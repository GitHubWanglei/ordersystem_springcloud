package com.cy.ordersystem.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.cy.ordersystem.entity.User;
import com.cy.ordersystem.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Autowired
    private UserRepository userRepository;

    private static JwtUtil INSTANCE;

    private static String secret = "dhfpaosiHF(S^&*F)*DF&S(F*&AS(AS)DAS++++===09sdf90dsdyufoasjdflsjadfhasdlkhfs&F(SDUFOISDJOF";

    @PostConstruct
    public void init() {
        INSTANCE = this;
    }

    public static String generateToken(String username, String password) {
        return JWT.create().withAudience(username)
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.offsetDay(new Date(), 30))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(secret));
    }

    public static boolean validateToken(String token) {
        try {
            JWTVerifier verfifier = JWT.require(Algorithm.HMAC256(secret)).build();
            verfifier.verify(token);
            // 校验成功，再判断是否已经退出登录
            String username = JWT.decode(token).getAudience().get(0);
            List<User> userList = INSTANCE.userRepository.findByUsername(username);
            if (userList.isEmpty()) { // 理论不可达
                return false;
            }
            if (userList.get(0).getToken() == null || userList.get(0).getToken().isEmpty()) { // 已退出登录
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
