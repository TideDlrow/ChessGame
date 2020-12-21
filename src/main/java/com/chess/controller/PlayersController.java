package com.chess.controller;

import com.chess.bean.Message;
import com.chess.bean.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayersController {
    /**
     * key是player的id
     */
    private Map<Integer, Player> players = new HashMap<>();

    public Message login(String username,String password){
        return null;
    }
}
