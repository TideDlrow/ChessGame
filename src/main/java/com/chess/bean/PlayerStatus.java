package com.chess.bean;

public enum PlayerStatus {
    LOGIN,//仅处于登录状态
    PVE,//与电脑对战中
    SEARCH_PLAYER,//正在搜寻对手
    PVP,//与玩家对战中
}
