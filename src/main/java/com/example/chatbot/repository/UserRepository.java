package com.example.chatbot.repository;

import com.example.chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 회원 찾기
    Optional<User> findByEmail(String email);
}