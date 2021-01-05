package com.chess.socket;

import cn.hutool.json.JSONUtil;
import com.chess.bean.Board;
import com.chess.bean.Message;
import com.chess.bean.Player;
import com.chess.bean.PlayerStatus;
import com.chess.config.BusinessConfig;
import com.chess.exception.BusinessError;
import com.chess.exception.BusinessException;
import com.chess.pieces.Piece;
import com.chess.service.PlayerService;
import com.chess.utils.ApplicationContextUtil;
import com.chess.utils.TokenUtil;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{token}")
@Component
@Data
public class PVPController {
    private static PlayerService playerService;
    private static BusinessConfig businessConfig;

    /**
     * key是player的id
     */
    private static final Map<String, Session> PLAYER_SESSIONS = new ConcurrentHashMap<>();
    /**
     * key是两个对局player的id用逗号的拼接
     */
    private static final Map<String, Board> BOARDS = new ConcurrentHashMap<>();
    /**
     * key是玩家id,value是两个玩家id的拼接
     */
    private static final Map<String, String> BOARDS_KEYS = new ConcurrentHashMap<>();
    /**
     * 所有PVP的玩家 key为userId
     */
    private static final Map<String, Player> ALL_PVP_PLAYERS = new ConcurrentHashMap<>();

    /**
     * 待匹配的玩家 key为userId
     */
    private static final Map<String, Player> MATCHED_PLAYER = new ConcurrentHashMap<>();
    /**
     * 匹配的次数  key为userId
     */
    private static final Map<String, Integer> MATCHED_TIMES = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) throws BusinessException {
        PlayerService playerService = getPlayerService();
        BusinessConfig businessConfig = getBusinessConfig();
        TokenUtil.verifyToken(token, businessConfig.getJwtSecret(), playerService);
        String userId = TokenUtil.getClaim(token, businessConfig.getJwtSecret(), "id");
//        String userId = "2";
        PLAYER_SESSIONS.put(userId, session);
        //连进来表示想PVP，故将该玩家放入待匹配的列表中
        Player player = playerService.getPlayerById(Integer.parseInt(userId));
        MATCHED_PLAYER.put(player.getId(), player);
        MATCHED_TIMES.put(player.getId(), 0);
    }

    //关闭连接时调用
    @OnClose
    public void onClose(Session session,CloseReason closeReason1) {
        System.out.println(closeReason1);
        String sessionId = session.getId();
        //前端主动断开连接时 要把对应的玩家id从待匹配列表中移除
        for (String userId : PLAYER_SESSIONS.keySet()) {
            //如果session相同，则把玩家从对应的列表的移除
            Session userSession = PLAYER_SESSIONS.get(userId);
            if (userSession != null && userSession.getId().equals(sessionId)) {
                PLAYER_SESSIONS.remove(userId);
                MATCHED_PLAYER.remove(userId);
                MATCHED_TIMES.remove(userId);
                //若正在游戏的玩家断开了连接 则移除对局 默认另一个玩家胜利
                Player player = ALL_PVP_PLAYERS.get(userId);
                if (player != null) {
                    String linkId = BOARDS_KEYS.get(userId);
                    //移除对局
                    BOARDS.remove(linkId);
                    //给另一个玩家发送胜利信息
                    String anotherUserId = getAnotherUserId(linkId, userId);
                    Session anotherSession = PLAYER_SESSIONS.get(anotherUserId);
                    sendMessage(anotherSession, "{\"stage\":\"finished\",\"isWin\":true}");
                    //然后主动断开连接
                    try {
                        CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "另一位玩家掉线，你赢了");
                        anotherSession.close(closeReason);
                        PLAYER_SESSIONS.remove(anotherUserId);
                        ALL_PVP_PLAYERS.remove(userId);
                        ALL_PVP_PLAYERS.remove(anotherUserId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message) throws BusinessException {
        //发过来的信息以分号分割，第一个是token，第二个是step
        //其中step的格式为1,1,2,3 表示1,1处的棋子移动到2,3处
        String[] messages = message.split(";");
        String token = messages[0];
        String step = messages[1];
        String jwtSecret = getBusinessConfig().getJwtSecret();
        PlayerService playerService = getPlayerService();
        TokenUtil.verifyToken(token, jwtSecret, playerService);
        //从token中获取userId
        String userId = TokenUtil.getClaim(token, jwtSecret, "id");
        //获取棋子的原坐标和目的坐标
        String[] split = step.split(",");
        int x1 = Integer.parseInt(split[0]);
        int y1 = Integer.parseInt(split[1]);
        int x2 = Integer.parseInt(split[2]);
        int y2 = Integer.parseInt(split[3]);
        //根据id获取board
        String boardKey = BOARDS_KEYS.get(userId);
        Board board = BOARDS.get(boardKey);
        //判断是否能移动该棋子，若棋子阵营和玩家阵营相同，则可以移动
        Player player = ALL_PVP_PLAYERS.get(userId);
        Piece piece = board.getPieceByCoordinate(x1, y1);
        if (piece == null) {
            throw new BusinessException(BusinessError.NO_PIECE);
        }
        //仅当是自己回合，且移动的是自己的棋子才能移动棋子
        if (player.isCamp() == board.isCurrentCamp() && player.isCamp() == piece.isCamp()) {
            boolean result = board.movePiece(x1, y1, x2, y2);
            //获取另一个玩家的id  如boardKey="12,34" userId=12 则anotherUserId=34
            String anotherUserId = getAnotherUserId(boardKey,userId);
            //发送最后棋子的移动情况
            sendMessage(anotherUserId, "{\"stage\":\"move\",\"step\":\"" + step + "\"}");
            //如果棋局结束,则直接发送输赢信息
            if (result) {
                //stage:finished表示棋局结束阶段
                sendMessage(userId, "{\"stage\":\"finished\",\"finished\":true,\"isWin\":true}");
                sendMessage(anotherUserId, "{\"stage\":\"finished\",\"finished\":true,\"isWin\":false}");
                //把棋盘从容器中移除
                BOARDS_KEYS.remove(userId);
                BOARDS_KEYS.remove(anotherUserId);
                BOARDS.remove(boardKey);
                //从容器中移除用户
                ALL_PVP_PLAYERS.remove(userId);
                ALL_PVP_PLAYERS.remove(anotherUserId);
                //主动断开两个用户的连接
                try {
                    CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "对局结束！！");
                    Session anotherUserSession = PLAYER_SESSIONS.remove(anotherUserId);
                    anotherUserSession.close(closeReason);
                    Session userSession = PLAYER_SESSIONS.remove(userId);
                    userSession.close(closeReason);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //棋局未结束 向另一个玩家发送棋子移动的消息
                sendMessage(anotherUserId, "{\"stage\":\"move\",\"step\":\"" + step + "\"}");
            }
        } else {
            throw new BusinessException(BusinessError.NO_PERMISSION);
        }

    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable instanceof BusinessException) {
            BusinessError businessError = ((BusinessException) throwable).getBusinessError();
            Message message = new Message();
            message.setSuccess(false);
            message.setCode(businessError.getCode());
            message.setMessage(businessError.getMessage());
            sendMessage(session, JSONUtil.parse(message).toJSONString(0));
        } else if (throwable.getCause() instanceof BusinessException){
            BusinessError businessError = ((BusinessException) throwable.getCause()).getBusinessError();
            Message message = new Message();
            message.setSuccess(false);
            message.setCode(businessError.getCode());
            message.setMessage(businessError.getMessage());
            sendMessage(session, JSONUtil.parse(message).toJSONString(0));
        }else {
            throwable.printStackTrace();
        }
    }

    public void sendMessage(String userId, String message) {
        Session session = PLAYER_SESSIONS.get(userId);
        sendMessage(session, message);
    }

    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //每秒执行一次
    @Scheduled(cron = "0/1 * * * * ?")
    public void checkMatched() {
        //先将所有玩家的匹配次数加1
        for (String userId : MATCHED_PLAYER.keySet()) {
            Integer times = MATCHED_TIMES.get(userId);
            MATCHED_TIMES.put(userId, times + 1);
        }
        //如果待匹配的列表中有两个以上待匹配的玩家，则依次匹配
        if (MATCHED_PLAYER.size() >= 2) {
            Iterator<String> iterator = MATCHED_PLAYER.keySet().iterator();
            while (MATCHED_PLAYER.size() >= 2) {
                String userId1 = iterator.next();
                String userId2 = iterator.next();
                //从待匹配的列表中取出玩家
                Player player1 = MATCHED_PLAYER.get(userId1);
                Player player2 = MATCHED_PLAYER.get(userId2);
                //将玩家状态设置为PVP
                player1.setStatus(PlayerStatus.PVP);
                player2.setStatus(PlayerStatus.PVP);
                //给各个玩家分配阵营
                player1.setCamp(true);
                player2.setCamp(false);

                //初始化棋盘
                Board board = new Board(true);
                String boardId = userId1 + "," + userId2;
                BOARDS_KEYS.put(userId1, boardId);
                BOARDS_KEYS.put(userId2, boardId);
                BOARDS.put(boardId, board);
                //把已匹配的玩家放到容器中
                ALL_PVP_PLAYERS.put(userId1, player1);
                ALL_PVP_PLAYERS.put(userId2, player2);
                //把玩家从待匹配列表移除
                MATCHED_PLAYER.remove(userId1);
                MATCHED_PLAYER.remove(userId2);
                //将分配结果发给前端。stage表示当前阶段 distribution表示分配阶段
                sendMessage(userId1, "{\"stage\":\"distribution\",\"camp\":true,\"matchFlag\":true}");
                sendMessage(userId2, "{\"stage\":\"distribution\",\"camp\":false,\"matchFlag\":true}");
            }
        }
        //如果匹配次数超过一定次数，则移出匹配
        for (String userId : MATCHED_PLAYER.keySet()) {
//            System.out.println("----");
            Integer times = MATCHED_TIMES.get(userId);
            //超过10次,发送匹配失败的信息，移出匹配列表
            if (times >= 10) {
//                sendMessage(userId, "{matchFlag:false}");
                MATCHED_TIMES.remove(userId);
                ALL_PVP_PLAYERS.remove(userId);
                MATCHED_PLAYER.remove(userId);
                try {
                    //主动关闭连接
                    CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "未匹配到玩家，请稍后再试");
                    PLAYER_SESSIONS.get(userId).close(closeReason);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PlayerService getPlayerService() {
        if (playerService == null) {
            playerService = (PlayerService) ApplicationContextUtil.getBean("playerService");
        }
        return playerService;
    }

    private BusinessConfig getBusinessConfig() {
        if (businessConfig == null) {
            businessConfig = (BusinessConfig) ApplicationContextUtil.getBean("businessConfig");
        }
        return businessConfig;
    }

    /**
     * 获取另一个玩家的id   usersIdLink="12,34" userId=12 则anotherUserId=34
     *
     * @param usersIdLink 通过逗号连接的id
     * @param userId
     * @return 另一个玩家的id
     */
    public String getAnotherUserId(String usersIdLink, String userId) {
        String anotherUserId = "";
        int i = usersIdLink.indexOf(userId);
        if (i == 0) {
            anotherUserId = usersIdLink.substring(userId.length() + 1);
        } else {
            anotherUserId = usersIdLink.substring(0, usersIdLink.length() - userId.length() - 1);
        }
        return anotherUserId;
    }
}
