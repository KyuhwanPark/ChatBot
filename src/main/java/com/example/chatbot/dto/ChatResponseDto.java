package com.example.chatbot.dto;

import com.example.chatbot.entity.Message;
import lombok.Getter;

@Getter
public class ChatResponseDto {
    private String role;
    private String content;

    public ChatResponseDto(Message message) {
        this.role = message.getRole();
        this.content = message.getContent();
    }
}