package com.example.chatbot.service;

import com.example.chatbot.dto.AiResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.entity.User;
import com.example.chatbot.repository.ConversationRepository;
import com.example.chatbot.repository.MessageRepository;
import com.example.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    /**
     * 1. 대화방 생성
     */
    @Transactional
    public Conversation createConversation(Long userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(title);
        conversation.setCreatedAt(java.time.LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    /**
     * 2. 메시지 처리 (일반 단건 응답)
     */
    @Transactional
    public Message processMessage(Long conversationId, String userContent) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화방이 존재하지 않습니다. id=" + conversationId));

        Message userMessage = new Message(conversation, "user", userContent, 0, 0);
        messageRepository.save(userMessage);

        int N = 3;
        Pageable limit = PageRequest.of(0, N);
        List<Message> recentMessages = messageRepository.findRecentMessages(conversationId, limit);
        List<Message> history = new ArrayList<>(recentMessages);
        Collections.reverse(history);

        AiResponseDto aiResponse = openAIService.callGptApi(history);

        Message aiMessage = new Message(
                conversation,
                "assistant",
                aiResponse.content(),
                aiResponse.promptTokens(),
                aiResponse.completionTokens()
        );

        return messageRepository.save(aiMessage);
    }

    /**
     * 2-1. 메시지 처리 (스트리밍 방식 & 문맥 유지 저장)
     */
    @Transactional
    public Flux<String> processStreamMessage(Long conversationId, String userContent) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화방이 존재하지 않습니다. id=" + conversationId));

        // 1. 사용자 질문 DB 저장
        Message userMessage = new Message(conversation, "user", userContent, 0, 0);
        messageRepository.save(userMessage);

        // 2. 컨텍스트 조회 (최근 5개로 여유있게 설정)
        List<Message> recentMessages = messageRepository.findRecentMessages(conversationId, PageRequest.of(0, 5));
        List<Message> history = new ArrayList<>(recentMessages);
        Collections.reverse(history);

        // 3. AI 스트리밍 요청
        Flux<String> stream = openAIService.chatStream(history);

        // 4. 비동기로 AI 응답을 누적할 빌더 생성
        StringBuilder aiResponseContent = new StringBuilder();

        return stream.doOnNext(data -> {
            // JSON 응답에서 실시간 텍스트 조각만 추출하여 누적
            if (!"[DONE]".equals(data) && data != null) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(data);
                    com.fasterxml.jackson.databind.JsonNode choices = root.get("choices");
                    if (choices != null && choices.isArray() && choices.size() > 0) {
                        com.fasterxml.jackson.databind.JsonNode delta = choices.get(0).get("delta");
                        if (delta != null && delta.has("content")) {
                            aiResponseContent.append(delta.get("content").asText());
                        }
                    }
                } catch (Exception e) {
                    // 파싱 에러 무시
                }
            }
        }).doOnComplete(() -> {
            // 5. 스트리밍 종료 후 AI 답변 최종 DB 저장 (문맥 유지용)
            Message aiMessage = new Message(conversation, "assistant", aiResponseContent.toString(), 0, 0);
            messageRepository.save(aiMessage);
        });
    }

    /**
     * 3. 대화방 목록 조회 (전체)
     */
    @Transactional(readOnly = true)
    public List<Conversation> findAllConversations() {
        return conversationRepository.findAll();
    }

    /**
     * 4. 대화방 단건 조회 (방 정보만)
     */
    @Transactional(readOnly = true)
    public Conversation findConversationById(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("대화방이 존재하지 않습니다. id=" + id));
    }

    /**
     * 5. 대화방 내 메시지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Message> findMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    /**
     * 6. 대화방 삭제
     */
    @Transactional
    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }
}