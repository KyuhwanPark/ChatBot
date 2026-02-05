package com.example.chatbot.dto; // 또는 package com.example.chatbot.dto;

public record AiResponseDto(
        String content,
        int promptTokens,
        int completionTokens
) {}