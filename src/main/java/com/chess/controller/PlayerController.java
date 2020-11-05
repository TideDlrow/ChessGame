package com.chess.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{userId}")
@Component
public class PlayerController {
    private Map<String, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) {

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
