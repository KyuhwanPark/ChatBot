package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequestDto;
import com.example.chatbot.dto.ChatResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.service.ChatService;
import com.example.chatbot.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "대화방 및 메시지 관리 API")
public class ChatController {

    private final ChatService chatService;
    private final OpenAIService openAIService;

    @Operation(summary = "대화방 생성", description = "새로운 챗봇 대화방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대화방 생성 성공 (대화방 ID 반환)"),
            @ApiResponse(responseCode = "401", description = "API Key 인증 실패")
    })
    @PostMapping
    public ResponseEntity<Long> createConversation(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long userId,
            @Parameter(description = "대화방 제목", example = "Java 학습 대화") @RequestParam String title) {
        Conversation conversation = chatService.createConversation(userId, title);
        return ResponseEntity.ok(conversation.getId());
    }

    @Operation(summary = "메시지 전송 (단건)", description = "대화방에 메시지를 보내고 AI의 답변을 받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (내용 없음)"),
            @ApiResponse(responseCode = "404", description = "대화방을 찾을 수 없음"),
            @ApiResponse(responseCode = "429", description = "Rate Limit 초과"),
            @ApiResponse(responseCode = "500", description = "OpenAI API 통신 오류 등 서버 내부 오류")
    })
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @Parameter(description = "대화방 ID") @PathVariable Long conversationId,
            @RequestBody ChatRequestDto requestDto) {
        Message aiMessage = chatService.processMessage(conversationId, requestDto.getContent());
        return ResponseEntity.ok(new ChatResponseDto(aiMessage));
    }

    @Operation(summary = "SSE 스트리밍 채팅", description = "OpenAI API를 활용해 실시간 스트리밍 답변을 제공합니다.")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
            @Parameter(description = "사용자 질문 내용") @RequestParam String message) {
        return openAIService.chatStream(message);
    }

    @Operation(summary = "전체 대화방 목록 조회", description = "모든 대화방 목록을 가져옵니다.")
    @GetMapping
    public ResponseEntity<List<Conversation>> getConversations() {
        return ResponseEntity.ok(chatService.findAllConversations());
    }

    @Operation(summary = "특정 대화방 상세 조회", description = "단일 대화방의 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.findConversationById(id));
    }

    @Operation(summary = "대화방 메시지 내역 조회", description = "해당 대화방에서 오고 간 모든 메시지를 조회합니다.")
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.findMessagesByConversationId(id));
    }

    @Operation(summary = "대화방 삭제", description = "대화방과 해당 방의 메시지를 모두 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return ResponseEntity.ok("대화방이 삭제되었습니다. id=" + id);
    }
}