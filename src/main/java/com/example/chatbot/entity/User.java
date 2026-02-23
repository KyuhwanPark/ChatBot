package com.example.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인은 비밀번호가 없으므로 nullable = false 조건을 제거합니다.
    private String password;

    private String username;

    // [New] 소셜 로그인 제공자 (예: "google", "kakao")
    private String provider;

    // [New] 소셜 로그인 제공자에서 발급한 고유 ID
    private String providerId;

    // [New] 권한 (기본값 설정)
    private String role = "ROLE_USER";

    // User : Conversation = 1 : N
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // 순환 참조 끊기
    private List<Conversation> conversations = new ArrayList<>();
}