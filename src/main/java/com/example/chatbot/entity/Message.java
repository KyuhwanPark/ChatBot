package com.example.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // 1. 이 줄 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // 2. 이 줄 추가! (여기가 핵심입니다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Column(nullable = false)
    private String role; // user or assistant

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer promptTokens;

    private Integer completionTokens;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Message(Conversation conversation, String role, String content, Integer promptTokens, Integer completionTokens) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
    }
}