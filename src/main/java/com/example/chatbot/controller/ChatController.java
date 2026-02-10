package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequestDto;
import com.example.chatbot.dto.ChatResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. 대화방 생성 API (테스트를 위해 userId=1로 고정하거나 파라미터로 받음)
    @PostMapping("/conversation")
    public Long createConversation(@RequestParam Long userId, @RequestParam String title) {
        Conversation conversation = chatService.createConversation(userId, title);
        return conversation.getId(); // 생성된 대화방 ID 반환
    }

    // 2. 메시지 전송 API (DTO 적용)
    @PostMapping("/{conversationId}")
    public ChatResponseDto sendMessage(@PathVariable Long conversationId, @RequestBody ChatRequestDto requestDto) {
        Message aiMessage = chatService.processMessage(conversationId, requestDto.getContent());
        return new ChatResponseDto(aiMessage);
    }
}