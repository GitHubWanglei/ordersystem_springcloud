package com.cy.ordersystem.utils;

import cn.hutool.core.date.DateTime;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@WebFilter(urlPatterns = "/*")
public class LogginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String method = httpServletRequest.getMethod();
        if (!method.equals("OPTIONS")) {
            String url = httpServletRequest.getRequestURL().toString();
            String queryString = httpServletRequest.getQueryString();
            System.out.println("=============================================>>>");
            Date date = new DateTime();
            System.out.println(date);
            System.out.println("remote: "+servletRequest.getRemoteAddr()+":"+servletRequest.getRemotePort());
            if (queryString != null) {
                System.out.println(method+": "+ url +"?"+ queryString);
            } else {
                System.out.println(method+": "+ url);
            }
            String token = ((HttpServletRequest) servletRequest).getHeader("token");
            if (token != null) {
                System.out.println("token: "+token);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
