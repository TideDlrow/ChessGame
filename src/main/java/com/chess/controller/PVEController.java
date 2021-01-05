package com.chess.controller;

import com.chess.service.HttpAPIService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/PVE")
public class PVEController {

    @Resource
    private HttpAPIService httpAPIService;

    private final String CHESS_DB_URL = "http://www.chessdb.cn/chessdb.php";

    /**
     *
     * @param FENString 局面表示的FEN串
     * @param camp 行动方阵营 true表示黑方 false表示红方
     * @return
     */
    @RequestMapping("/bestNext")
    @CrossOrigin
    public int[] bestNext(String FENString,boolean camp) throws Exception {
        System.out.println(FENString);
        System.out.println(camp);
        String moveCamp = "";
        if (camp){
            moveCamp = "b";
        }else {
            moveCamp = "w";
        }
        Map<String,String> params = new HashMap<>();
        params.put("action","querybest");
        params.put("board",FENString+" "+moveCamp);
        //以 | 号分隔的着法信息，常规着法为 move:[MOVE] ，残局库着法为 egtb:[MOVE] ，候选着法为 search:[MOVE]
        //若所查询的局面没有符合自动出步规则的着法，返回 nobestmove
        //其中[MOVE]的格式是b0c4这样的字符串
        String response = httpAPIService.doGet(CHESS_DB_URL,params);
        System.out.println(response);
        int[] step = parseICCS(response);
        if (step==null){
            String move = queryAll(FENString+" "+moveCamp);
            if (move==null){
                return null;
            }
            if (!move.equals("checkmate")){
                step = parseICCS(move);
            }else{
                //表示被将死
                return new int[]{-1,-1,-1,-1};
            }
        }
        System.out.println(Arrays.toString(step));
        return step;
    }

    /**
     * 查询所有着法,并取第一个着法
     * @param FENString 局面字符串加上b或w
     * @return move:[MOVE] 其中[MOVE]的格式为b0c4
     */
    public String queryAll(String FENString) throws Exception {
        Map<String,String> params = new HashMap<>();
        params.put("action","queryall");
        params.put("board",FENString);
        //以 | 号分隔的着法信息，每项包含以 , 号分隔的着法 (move)、分值 (score)、排序 (rank)、胜率 (winrate) 及备注 (note)
        //若局面代码错误，返回 invalid board ，若所查询的局面没有已知着法，返回 unknown ，若走棋方被将死或困毙，返回 checkmate / stalemate
        //例子如下：
        //move:b5e5,score:29999,rank:2,note:! (00-00)|move:e3e4,score:952,rank:0,note:?
        //(01-00),winrate:94.71|move:e3e5,score:447,rank:0,note:? (01-00),winrate:79.49|move:b5a5,score:421,rank:0,note:?
        //(01-00),winrate:78.17|move:b5i5,score:323,rank:0,note:? (01-00),winrate:72.69
        String response = httpAPIService.doGet(CHESS_DB_URL,params);
        if (response.contains("unknown")||response.contains("invalid")){
            return null;
        }else if (response.contains("checkmate") || response.contains("stalemate")){
            return "checkmate";
        }
        System.out.println("queryAll:"+response);
        String[] items = response.split("\\|");
        //一般是按score从大到小排列，取分数最大的一个即第一个
        String item = items[0];
        String[] datas = item.split(",");
        for (String data:datas){
            if (data.contains("move:")){
                return data;
            }
        }
        return null;
    }

    /**
     * 把ICCS(Internet Chinese Chess Server)的着法转换成本项目中的表示
     * @param ICCS 格式为move:b0c4  棋盘的纵线按从左到右分别标为a,b,c,d,e,f,g,h,i。
     *                             棋盘的横线从下往上分别标为0,1,2,3,4,5,6,7,8,9。
     *                             故b0c4的意思为把b0处的棋子移动到c4处
     * @return 格式为[2,3,4,5]棋子起始坐标2,3 棋子移动的终点坐标4,5
     */
    private int[] parseICCS(String ICCS){
        if (!ICCS.contains("move:")&&!ICCS.contains("egbt:")){
            return null;
        }
        String step = ICCS.split(":")[1];
        int[] steps = new int[4];
        for (int i = 0; i < 4; i++) {
            steps[i] = ICCSCharToInt(step.charAt(i));
        }
        return steps;
    }


    /**
     * 把字符转成x坐标。因为在该项目中左上角的车坐标是1,1  马是2,1
     * @param ICCSChar
     * @return
     */
    private int ICCSCharToInt(char ICCSChar){
        return switch (ICCSChar) {
            //‘a’表示第一列，‘9’表示第一行
            case 'a', '9' -> 1;
            case 'b', '8' -> 2;
            case 'c', '7' -> 3;
            case 'd', '6' -> 4;
            case 'e', '5' -> 5;
            case 'f', '4' -> 6;
            case 'g', '3' -> 7;
            case 'h', '2' -> 8;
            case 'i', '1' -> 9;
            case '0' ->10;
            default -> -1;
        };
    }
}
