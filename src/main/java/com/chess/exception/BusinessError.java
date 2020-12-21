package com.chess.exception;

import lombok.Getter;

@Getter
public enum BusinessError {
    NOT_LOGIN(401, "用户未登录"),
    LOGIN_ERROR(402, "用户名或密码错误");

    private final int code;
    private final String message;

    BusinessError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
