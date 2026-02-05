package com.example.chatbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 대화방 주제 (예: "여행 계획 짜줘")

    private LocalDateTime createdAt;

    // [추가된 부분] N : 1 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키 컬럼명 지정
    private User user;

    // Conversation : Message = 1 : N (기존 구조 유지)
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    // 편의 메서드 (연관관계 세팅용)
    public void setUser(User user) {
        this.user = user;
        user.getConversations().add(this);
    }
}