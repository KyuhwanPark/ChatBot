package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequestDto;
import com.example.chatbot.dto.ChatResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. 대화방 생성 (POST /api/conversations)
    @PostMapping
    public ResponseEntity<Long> createConversation(@RequestParam Long userId, @RequestParam String title) {
        Conversation conversation = chatService.createConversation(userId, title);
        return ResponseEntity.ok(conversation.getId());
    }

    // 2. 메시지 전송 (POST /api/conversations/{id}/messages)
    // 의미: {id}번 대화방에 메시지를 생성한다
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(@PathVariable Long conversationId, @RequestBody ChatRequestDto requestDto) {
        Message aiMessage = chatService.processMessage(conversationId, requestDto.getContent());
        return ResponseEntity.ok(new ChatResponseDto(aiMessage));
    }

    // 3. 대화방 목록 조회 (GET /api/conversations)
    @GetMapping
    public ResponseEntity<List<Conversation>> getConversations() {
        List<Conversation> conversations = chatService.findAllConversations();
        return ResponseEntity.ok(conversations);
    }

    // 4. 대화방 상세 조회 (GET /api/conversations/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        Conversation conversation = chatService.findConversationById(id);
        return ResponseEntity.ok(conversation);
    }

    // 5. 대화 내역 조회 (GET /api/conversations/{id}/messages)
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long id) {
        List<Message> messages = chatService.findMessagesByConversationId(id);
        return ResponseEntity.ok(messages);
    }

    // 6. 대화방 삭제 (DELETE /api/conversations/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return ResponseEntity.ok("대화방이 삭제되었습니다. id=" + id);
    }
}