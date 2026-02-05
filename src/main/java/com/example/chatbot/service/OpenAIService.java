package com.example.chatbot.service;

import com.example.chatbot.dto.AiResponseDto;
import com.example.chatbot.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public AiResponseDto callGptApi(List<Message> history) {
        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 2. 바디 설정 (GPT에게 보낼 메시지 리스트 변환)
        List<Map<String, String>> messages = new ArrayList<>();
        for (Message msg : history) {
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("role", msg.getRole());
            messageMap.put("content", msg.getContent());
            messages.add(messageMap);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo"); // 또는 gpt-4
        body.put("messages", messages);

        // 3. 요청 전송
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 실제 API 호출 (응답을 Map으로 받음, 실제론 별도 DTO 클래스를 만드는 게 더 좋습니다)
        Map<String, Object> response = restTemplate.postForObject(OPENAI_URL, request, Map.class);

        // 4. 응답 파싱 (내용 및 토큰 수 추출)
        if (response == null || !response.containsKey("choices")) {
            throw new RuntimeException("OpenAI API 응답이 올바르지 않습니다.");
        }

        // 내용 추출
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) messageObj.get("content");

        // 토큰 사용량 추출 (Usage)
        Map<String, Object> usage = (Map<String, Object>) response.get("usage");
        int promptTokens = (int) usage.get("prompt_tokens");
        int completionTokens = (int) usage.get("completion_tokens");

        return new AiResponseDto(content, promptTokens, completionTokens);
    }
}