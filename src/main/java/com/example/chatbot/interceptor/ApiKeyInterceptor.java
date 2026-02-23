package com.example.chatbot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${app.auth.api-key}")
    private String validApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS preflight 요청(OPTIONS)은 통과시킴
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        // 1. 헤더에서 API 키 확인
        String clientApiKey = request.getHeader("x-api-key");

        // 2. 헤더에 없으면 쿼리 파라미터에서 확인 (SSE EventSource 호환성용)
        if (clientApiKey == null) {
            clientApiKey = request.getParameter("apiKey");
        }

        if (clientApiKey == null || !clientApiKey.equals(validApiKey)) {
            throw new IllegalArgumentException("유효하지 않은 API Key입니다.");
        }

        return true;
    }
}