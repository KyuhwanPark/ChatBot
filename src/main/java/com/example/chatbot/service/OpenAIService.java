package com.example.chatbot.service;

import com.example.chatbot.dto.AiResponseDto;
import com.example.chatbot.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // 추가됨
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent; // 추가됨
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // 1. 일반 질문 (동기 방식 - 유지)
    public AiResponseDto callGptApi(List<Message> history) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        List<Map<String, String>> messages = new ArrayList<>();
        for (Message msg : history) {
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("role", msg.getRole());
            messageMap.put("content", msg.getContent());
            messages.add(messageMap);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<String, Object> response = restTemplate.postForObject(OPENAI_URL, request, Map.class);

        if (response == null || !response.containsKey("choices")) {
            throw new RuntimeException("OpenAI API 응답이 올바르지 않습니다.");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) messageObj.get("content");

        Map<String, Object> usage = (Map<String, Object>) response.get("usage");
        int promptTokens = (int) usage.get("prompt_tokens");
        int completionTokens = (int) usage.get("completion_tokens");

        return new AiResponseDto(content, promptTokens, completionTokens);
    }

    // 2. 스트리밍 질문 (WebFlux - 수정됨!)
    public Flux<String> chatStream(String userMessage) {
        WebClient webClient = WebClient.builder()
                .baseUrl(OPENAI_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("stream", true);
        body.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                // [핵심 변경] String.class 대신 ServerSentEvent로 받아서 알맹이만 쏙 뺍니다.
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .map(sse -> {
                    String data = sse.data();
                    // 데이터가 없거나 끝났으면 그대로 반환
                    if (data == null || data.equals("[DONE]")) {
                        return "[DONE]";
                    }
                    // 순수 JSON 문자열만 반환 (Controller가 알아서 "data:" 붙여줌)
                    return data;
                });
    }
}