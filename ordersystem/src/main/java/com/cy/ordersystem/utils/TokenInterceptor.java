package com.cy.ordersystem.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("token");
        if (!StrUtil.isBlank(token)) {
            boolean isValidated = JwtUtil.validateToken(token);
            if (isValidated) {
                return true;
            }
        }
        response.setContentType("application/json; charset=utf-8");
        try {
            JSONObject json = new JSONObject();
            json.putOpt("code", 3);
            json.putOpt("message", "登录已过期，请重新登录!");
            response.getWriter().append(json.toString());
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
