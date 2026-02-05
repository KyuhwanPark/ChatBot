package com.example.chatbot.repository;

import com.example.chatbot.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // 특정 유저의 대화 목록을 '최신순'으로 가져오기
    List<Conversation> findByUserIdOrderByCreatedAtDesc(Long userId);
}