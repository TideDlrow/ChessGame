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
    /**
     * 玩家阵营，true代表黑方，false代表红方
     */
    private boolean camp;

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
