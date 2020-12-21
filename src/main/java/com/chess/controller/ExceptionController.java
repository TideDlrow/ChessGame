package com.chess.controller;

import com.chess.bean.Message;
import com.chess.exception.BusinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public Message exceptionHandler(BusinessException e){
        Message message = new Message();
        message.setSuccess(false);
        message.setCode(e.getBusinessError().getCode());
        message.setMessage(e.getBusinessError().getMessage());
        return message;
    }
}
