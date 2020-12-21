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
    private PlayerStatus status;
}
