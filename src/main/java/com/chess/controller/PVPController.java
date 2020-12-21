package com.chess.controller;

import com.chess.bean.Board;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class PVPController {
    /**
     * key是两个对局player的id用逗号的拼接
     */
    private Map<String, Board> boards = new HashMap<>();
    /**
     * key是player的id
     */
    private  Map<Integer, Session> sessions= new HashMap<>();
}
