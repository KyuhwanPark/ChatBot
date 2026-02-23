package com.example.chatbot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Re:Pair 기반 AI 챗봇 API")
                        .version("v1.0.0")
                        .description("Spring Boot 기반 AI 챗봇 REST API 문서입니다.")
                        .contact(new Contact().name("kyuhwan").email("kyuhwan@example.com")))
                .components(new Components()
                        .addSecuritySchemes("API_KEY", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("x-api-key")
                                .description("인증을 위한 API Key를 헤더에 포함해주세요.")))
                .addSecurityItem(new SecurityRequirement().addList("API_KEY"));
    }
}