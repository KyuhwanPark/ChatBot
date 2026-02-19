package com.example.chatbot.config;

import com.example.chatbot.interceptor.ApiKeyInterceptor;
import com.example.chatbot.interceptor.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor apiKeyInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // OPTIONS 허용 중요
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. API Key 인증 (모든 /api/** 요청에 적용)
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/api/**");

        // 2. Rate Limiting (모든 /api/** 요청에 적용)
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**");
    }
}