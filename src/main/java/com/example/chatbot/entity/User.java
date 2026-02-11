package com.example.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- [1] import 추가
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

    @Column(nullable = false)
    private String password;

    private String username;

    // User : Conversation = 1 : N
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // 순환 참조 끊기
    private List<Conversation> conversations = new ArrayList<>();
}