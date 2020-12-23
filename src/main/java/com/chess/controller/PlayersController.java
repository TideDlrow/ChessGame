package com.chess.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chess.bean.Message;
import com.chess.bean.Player;
import com.chess.config.BusinessConfig;
import com.chess.config.PassToken;
import com.chess.exception.BusinessError;
import com.chess.exception.BusinessException;
import com.chess.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class PlayersController {
    @Autowired
    PlayerService playerService;
    @Autowired
    BusinessConfig businessConfig;
    /**
     * key是player的id
     */
    private Map<Integer, Player> players = new HashMap<>();

    @RequestMapping("/login")
    @PassToken
    public Message login(String username, String password) throws BusinessException {
        Player player = playerService.getPlayerByName(username);
        //验证用户名及密码
        if (!player.getPassword().equals(SecureUtil.sha1(password))) {
            throw new BusinessException(BusinessError.LOGIN_ERROR);
        }
        return generateToken(player);
    }

    @RequestMapping("/register")
    @PassToken
    public Message register(String username, String password) throws BusinessException {
        int playerNameNum = playerService.getPlayerNameNum(username);
        if (playerNameNum >= 1) {
            throw new BusinessException(BusinessError.USERNAME_EXITING);
        }
        Player player = new Player();
        player.setUserName(username);
        player.setPassword(SecureUtil.sha1(password));
        player = playerService.addPlayer(player);
        //token
        //过期时间
        return generateToken(player);
    }

    private Message generateToken(Player player) {
        long expirationTime = System.currentTimeMillis() + businessConfig.getValidTime();
        //token中添加uuid来确保不会多客户端登录
        String activeUUID = IdUtil.simpleUUID();
        playerService.setActiveUUID(Integer.parseInt(player.getId()), activeUUID);
        String token = JWT.create()
                //受众(好像没什么意义)
                .withAudience(player.getId())
                //过期时间
                .withExpiresAt(new Date(expirationTime))
                //自定义属性
                .withClaim("id",player.getId())
                .withClaim("activeUUID",activeUUID)
                //签名
                .sign(Algorithm.HMAC256(businessConfig.getJwtSecret()));
        Message message = new Message();
        message.setMessage(token);
        message.setSuccess(true);
        return message;
    }

    @RequestMapping("/changePassword")
    public Message changePassword(int playerId, String oldPassword, String newPassword) {
        return null;
    }

    @RequestMapping("/getPlayerById")
    public Player getPlayerById(int id) {
        return null;
    }

    @RequestMapping("/refreshToken")
    public Message refreshToken(HttpServletRequest request) throws BusinessException {
        //从请求头中去取token
        String token = request.getHeader("token");
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(businessConfig.getJwtSecret())).build();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BusinessException(BusinessError.TOKEN_INVALID);
        }
        String userId = decodedJWT.getClaim("id").asString();
        Player player = playerService.getPlayerById(Integer.parseInt(userId));
        long expirationTime = System.currentTimeMillis() + businessConfig.getValidTime();
        //仅修改过期时间
        token = JWT.create()
                .withAudience(player.getId())
                //过期时间
                .withExpiresAt(new Date(expirationTime))
                //自定义属性
                .withClaim("id",player.getId())
                .withClaim("activeUUID",player.getActiveUUID())
                .sign(Algorithm.HMAC256(businessConfig.getJwtSecret()));
        Message message = new Message();
        message.setMessage(token);
        message.setSuccess(true);
        return message;
    }

}
