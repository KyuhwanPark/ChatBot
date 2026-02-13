package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequestDto;
import com.example.chatbot.dto.ChatResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.service.ChatService;
import com.example.chatbot.service.OpenAIService; // 추가됨
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType; // 추가됨
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux; // 추가됨

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final OpenAIService openAIService; // 직접 호출을 위해 추가됨

    // 1. 대화방 생성 (POST /api/conversations)
    @PostMapping
    public ResponseEntity<Long> createConversation(@RequestParam Long userId, @RequestParam String title) {
        Conversation conversation = chatService.createConversation(userId, title);
        return ResponseEntity.ok(conversation.getId());
    }

    // 2. 메시지 전송 (기존 단건 응답 방식)
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(@PathVariable Long conversationId, @RequestBody ChatRequestDto requestDto) {
        Message aiMessage = chatService.processMessage(conversationId, requestDto.getContent());
        return ResponseEntity.ok(new ChatResponseDto(aiMessage));
    }

    // [New] SSE 스트리밍 엔드포인트
    // 사용법: GET /api/conversations/stream?message=안녕
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message) {
        return openAIService.chatStream(message);
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