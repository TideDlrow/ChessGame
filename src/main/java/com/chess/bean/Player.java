package com.chess.bean;

import lombok.Data;

/**
 * 玩家
 */
@Data
public class Player {
    private String id;
    private String userName;
    private String password;
    private String activeUUID;
    private PlayerStatus status = PlayerStatus.LOGIN;

    public Player() {
    }

    public Player(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public Player(String userName, String password, PlayerStatus status) {
        this.userName = userName;
        this.password = password;
        this.status = status;
    }
}
