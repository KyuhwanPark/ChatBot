package com.example.chatbot.service;

import com.example.chatbot.dto.AiResponseDto;
import com.example.chatbot.entity.Conversation;
import com.example.chatbot.entity.Message;
import com.example.chatbot.entity.User;
import com.example.chatbot.repository.ConversationRepository;
import com.example.chatbot.repository.MessageRepository;
import com.example.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 2. 메시지 처리 (질문 저장 -> AI 요청 -> 답변 저장)
     */
    @Transactional
    public Message processMessage(Long conversationId, String userContent) {
        // A. 대화방 조회
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("대화방이 존재하지 않습니다. id=" + conversationId));

        // B. 사용자 질문 DB 저장 (일단 토큰은 0으로 저장)
        Message userMessage = new Message(conversation, "user", userContent, 0, 0);
        messageRepository.save(userMessage);

        // C. 전체 대화 기록 가져오기 (문맥 유지를 위해 필수)
        // **중요**: 방금 저장한 userMessage도 포함해서 가져와야 AI가 질문을 인식함
        List<Message> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // D. AI 서비스 호출 (GPT API 통신)
        AiResponseDto aiResponse = openAIService.callGptApi(history);

        // E. AI 답변 DB 저장 (API에서 받은 토큰 사용량 기록)
        Message aiMessage = new Message(
                conversation,
                "assistant",
                aiResponse.content(),        // 답변 내용
                aiResponse.promptTokens(),   // 질문 토큰(비용)
                aiResponse.completionTokens()// 답변 토큰(비용)
        );

        return messageRepository.save(aiMessage);
    }
}