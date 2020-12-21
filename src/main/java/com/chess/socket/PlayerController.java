package com.chess.socket;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{userId}")
@Controller
public class PlayerController {
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) {
        System.out.println("成功连接");
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "userId") String userName) {
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message) {
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
    }

}
