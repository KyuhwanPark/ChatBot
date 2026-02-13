// ChatRequestDto.java 수정 예시
package com.example.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDto {
    @NotBlank(message = "메시지 내용은 비어있을 수 없습니다.")
    private String content;
}