package com.chess.config;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    PlayerService playerService;
    @Autowired
    BusinessConfig businessConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中去除token
        String token = request.getHeader("token");
        //如果不是方法，则直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查是否有passToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (!passToken.required()) {
                if (token == null) {
                    throw new BusinessException(BusinessError.NO_TOKEN);
                }
                JWTVerifier verifier = JWT.require(Algorithm.HMAC256(businessConfig.getJwtSecret())).build();
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
            }
            return true;
        }
        return false;
    }
}
