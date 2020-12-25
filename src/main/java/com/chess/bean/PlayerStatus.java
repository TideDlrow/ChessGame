package com.chess.bean;

public enum PlayerStatus {
    LOGOUT(0),//未登录
    LOGIN(1),//仅处于登录状态
    PVE(2),//与电脑对战中
    SEARCH_PLAYER(3),//正在搜寻对手
    PVP(4);//与玩家对战中

    private int status;
    PlayerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
