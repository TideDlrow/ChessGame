package com.chess.config;

import com.chess.service.PlayerService;
import com.chess.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Service
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    PlayerService playerService;
    @Autowired
    BusinessConfig businessConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中去取token
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
            if (passToken.required()) {
                return true;
            } else {
                return TokenUtil.verifyToken(token, businessConfig.getJwtSecret(), playerService);
            }
        }
        return false;
    }
}
