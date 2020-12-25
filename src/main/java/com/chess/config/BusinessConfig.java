package com.chess.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BusinessConfig {

    private final long validTime = 10*60*1000;
    private String jwtSecret = "liu_yi_cheng";

}
