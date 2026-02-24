package com.example.chatbot;

import com.example.chatbot.entity.User;
import com.example.chatbot.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    // 서버가 실행될 때 자동으로 실행되는 코드
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            // DB에 유저가 한 명도 없다면 테스트 유저(ID: 1)를 자동 생성
            if (userRepository.count() == 0) {
                User user = new User();
                user.setEmail("test@example.com");
                user.setPassword("1234");
                user.setUsername("kyuhwan");
                userRepository.save(user);
            }
        };
    }
}