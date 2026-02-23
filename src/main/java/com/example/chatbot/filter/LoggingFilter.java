package com.example.chatbot.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        // 요청 로깅 (예: [REQUEST] GET /api/conversations - Client IP: 127.0.0.1)
        log.info("[REQUEST] {} {} - Client IP: {}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());

        // 다음 필터나 컨트롤러로 넘김
        chain.doFilter(request, response);

        // 처리 시간 계산 및 응답 로깅
        long duration = System.currentTimeMillis() - startTime;
        log.info("[RESPONSE] {} {} - Status: {} ({}ms)", req.getMethod(), req.getRequestURI(), res.getStatus(), duration);
    }
}