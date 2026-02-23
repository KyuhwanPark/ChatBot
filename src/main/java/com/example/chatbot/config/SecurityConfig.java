package com.example.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST API이므로 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 일단 모든 요청 통과 (인증은 ApiKeyInterceptor가 처리)
                )
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 끄기
                .httpBasic(basic -> basic.disable()); // 기본 HTTP Basic 인증 끄기

        return http.build();
    }
}