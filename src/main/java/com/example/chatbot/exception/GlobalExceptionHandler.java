package com.example.chatbot.exception;

import com.example.chatbot.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 컨트롤러의 에러를 감지함
public class GlobalExceptionHandler {

    // 1. 잘못된 요청 데이터 또는 존재하지 않는 데이터 (IllegalArgumentException)
    // ChatService에서 "대화방이 존재하지 않습니다"라고 던지면 여기서 잡아서 404로 바꿔줌
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), "BAD_REQUEST_OR_NOT_FOUND");

        // 상황에 따라 400(Bad Request) 또는 404(Not Found)를 줄 수 있는데,
        // ID 조회 실패는 보통 404가 적절해. 여기서는 메시지에 따라 유연하게 400으로 통일하거나 분기할 수 있어.
        // 일단 명확하게 404(NOT_FOUND)로 처리해볼게.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 2. 그 외 알 수 없는 모든 에러 (Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {
        e.printStackTrace(); // 서버 로그에 에러 내용 출력
        ErrorResponseDto response = new ErrorResponseDto("서버 내부 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}