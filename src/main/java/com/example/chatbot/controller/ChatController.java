package com.example.chatbot.controller;

import com.example.chatbot.entity.Message;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 예시 엔드포인트
    @PostMapping("/{conversationId}")
    public Message sendMessage(@PathVariable Long conversationId, @RequestBody String content) {
        return chatService.processMessage(conversationId, content);
    }
}
