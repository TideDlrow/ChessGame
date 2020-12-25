package com.chess.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chess.bean.Player;
import com.chess.exception.BusinessError;
import com.chess.exception.BusinessException;
import com.chess.service.PlayerService;

import java.util.Map;

public class TokenUtil {

    /**
     * 验证token
     * @param token token
     * @param jwtSecret 用于签名的字符串
     * @param playerService
     * @return true表示验证通过 false表示验证未通过
     * @throws BusinessException
     */
    public static boolean verifyToken(String token, String jwtSecret, PlayerService playerService) throws BusinessException {
        if (token == null) {
            throw new BusinessException(BusinessError.NO_TOKEN);
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BusinessException(BusinessError.TOKEN_INVALID);
        }
        Map<String, Claim> claims = decodedJWT.getClaims();
        String userId = claims.get("id").asString();
        long expirationTime = decodedJWT.getExpiresAt().getTime();
        String activeUUID = claims.get("activeUUID").asString();
        long currentTime = System.currentTimeMillis();
        //查看token是否过期
        if (currentTime > expirationTime) {
            throw new BusinessException(BusinessError.TOKEN_EXPIRATION);
        }
        Player player = playerService.getPlayerById(Integer.parseInt(userId));
        if (!player.getActiveUUID().equals(activeUUID)) {
            throw new BusinessException(BusinessError.MULTI_LOGIN);
        }
        return true;
    }

    public static String getClaim(String token,String jwtSecret,String key) throws BusinessException {
        if (token == null) {
            throw new BusinessException(BusinessError.NO_TOKEN);
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BusinessException(BusinessError.TOKEN_INVALID);
        }
        Map<String, Claim> claims = decodedJWT.getClaims();
        return claims.get(key).asString();
    }
}
