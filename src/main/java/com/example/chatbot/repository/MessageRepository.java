package com.example.chatbot.repository;

import com.example.chatbot.entity.Message;
import org.springframework.data.domain.Pageable; // 추가됨
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 기존 메서드 (전체 조회용 - 필요하다면 남겨둠)
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    // [New] 최근 메시지 N개를 가져오는 메서드 (페이징 사용)
    // 최신순(Desc)으로 가져와야 가장 최근 대화를 자를 수 있습니다.
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findRecentMessages(@Param("conversationId") Long conversationId, Pageable pageable);
}