package com.chess.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessException extends Exception{
    private BusinessError businessError;
}
