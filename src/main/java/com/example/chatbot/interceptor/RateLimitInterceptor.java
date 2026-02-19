package com.example.chatbot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.rate-limit.capacity}")
    private int capacity;

    @Value("${app.rate-limit.duration}")
    private int duration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;

        // Redis 카운트 증가
        Long count = redisTemplate.opsForValue().increment(key);

        // 첫 요청 시 만료 시간 설정
        if (count != null && count == 1) {
            redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        }

        // 제한 초과 체크
        if (count != null && count > capacity) {
            throw new IllegalStateException("요청 횟수 초과! (Redis on Docker)");
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}