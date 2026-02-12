package com.example.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private String message; // 에러 메시지 (예: "대화방을 찾을 수 없습니다.")
    private String code;    // 에러 코드 (예: "NOT_FOUND")
}