package com.chess.bean;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;

@Data
public class Token {
    private Player player;
    private long beginTimestamp;
    private long endTimestamp;

    public String getToken() {
        String token = "";
        token = JWT.create().withAudience(player.getId())
                .sign(Algorithm.HMAC256(player.getPassword()));
        return token;
    }
}
