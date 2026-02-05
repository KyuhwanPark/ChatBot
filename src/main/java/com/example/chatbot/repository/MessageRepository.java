package com.example.chatbot.repository;

import com.example.chatbot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지를 '과거 -> 최신' 순으로 가져오기
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}