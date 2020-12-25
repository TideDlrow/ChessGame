package com.chess.socket;

import cn.hutool.json.JSONUtil;
import com.chess.bean.Board;
import com.chess.bean.Message;
import com.chess.bean.Player;
import com.chess.bean.PlayerStatus;
import com.chess.config.BusinessConfig;
import com.chess.exception.BusinessError;
import com.chess.exception.BusinessException;
import com.chess.service.PlayerService;
import com.chess.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{token}")
@Component
public class PVPController {
    @Autowired
    PlayerService playerService;
    @Autowired
    BusinessConfig businessConfig;
    /**
     * key是player的id
     */
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    /**
     * key是两个对局player的id用逗号的拼接
     */
    private final Map<String, Board> boards = new HashMap<>();

    /**
     * 所有PVP的玩家 key为userId
     */
    private final Map<String, Player> allPVPPlayers = new ConcurrentHashMap<>();

    /**
     * 待匹配的玩家 key为userId
     */
    private final Map<String, Player> matchedPlayer = new ConcurrentHashMap<>();
    /**
     * 匹配的次数  key为userId
     */
    private final Map<String, Integer> matchedTimes = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) throws BusinessException {
        TokenUtil.verifyToken(token, businessConfig.getJwtSecret(), playerService);
        String userId = TokenUtil.getClaim(token, businessConfig.getJwtSecret(), "id");
        userSessions.put(userId, session);
        //连进来表示想PVP，故将该玩家放入待匹配的列表中
        Player player = playerService.getPlayerById(Integer.parseInt(userId));
        matchedPlayer.put(player.getId(), player);
        allPVPPlayers.put(player.getId(), player);
        matchedTimes.put(player.getId(), 0);
    }

    //关闭连接时调用
    @OnClose
    public void onClose() {
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message) {
        //发过来的信息以逗号分割，第一个是token，第二个是step
        //其中step的格式为1,1,2,3 表示1,1处的棋子移动到2,3处
        String[] messages = message.split(",");
        String token = messages[0];
        String step = messages[1];

    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        if (throwable instanceof BusinessException){
            BusinessError businessError = ((BusinessException) throwable).getBusinessError();
            Message message = new Message();
            message.setSuccess(false);
            message.setCode(businessError.getCode());
            message.setMessage(businessError.getMessage());
            sendMessage(session,JSONUtil.parse(message).toJSONString(0));
        }else {
            throwable.printStackTrace();
        }
    }

    public void sendMessage(String userId, String message) {
        Session session = userSessions.get(userId);
        sendMessage(session,message);
    }

    public void sendMessage(Session session,String message){
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
        for (String userId : matchedPlayer.keySet()) {
            Integer times = matchedTimes.get(userId);
            matchedTimes.put(userId, times + 1);
        }
        //如果待匹配的列表中有两个以上待匹配的玩家，则依次匹配
        if (matchedPlayer.size() >= 2) {
            Iterator<String> iterator = matchedPlayer.keySet().iterator();
            while (matchedPlayer.size() >= 2) {
                String userId1 = iterator.next();
                String userId2 = iterator.next();
                Player player1 = matchedPlayer.get(userId1);
                Player player2 = matchedPlayer.get(userId2);
                matchedPlayer.remove(userId1);
                matchedPlayer.remove(userId2);
                player1.setStatus(PlayerStatus.PVP);
                player2.setStatus(PlayerStatus.PVP);
                //分配阵营
                sendMessage(userId1, "{camp:true,matchFlag:true}");
                sendMessage(userId2, "{camp:false,matchFlag:true}");
            }
        }
        //如果匹配次数超过一定次数，则移出匹配
        for (String userId : matchedPlayer.keySet()) {
            Integer times = matchedTimes.get(userId);
            //超过10次,发送匹配失败的信息，移出匹配列表
            if (times >= 10) {
                sendMessage(userId, "{matchFlag:false}");
                matchedTimes.remove(userId);
                allPVPPlayers.remove(userId);
                matchedPlayer.remove(userId);
            }
        }
    }

}
