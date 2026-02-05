package com.example.chatbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users") // DB 예약어 'user'와 충돌 방지
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 로그인 ID 역할

    @Column(nullable = false)
    private String password;

    private String username;

    // User : Conversation = 1 : N
    // 유저 한 명이 여러 개의 대화방을 가질 수 있음
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Conversation> conversations = new ArrayList<>();
}