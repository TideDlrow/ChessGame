package com.chess.exception;

import lombok.Getter;

@Getter
public enum BusinessError {
    NOT_LOGIN(401, "用户未登录"),
    LOGIN_ERROR(402, "用户名或密码错误"),
    USERNAME_EXITING(403,"用户名已存在"),
    NO_TOKEN(404,"未携带token"),
    TOKEN_EXPIRATION(405,"token已过期，请重新登录"),
    TOKEN_INVALID(406,"token无效"),
    MULTI_LOGIN(407,"已在别处登录");

    private final int code;
    private final String message;

    BusinessError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
