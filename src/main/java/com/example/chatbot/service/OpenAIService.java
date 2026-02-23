package com.example.chatbot.service;

import com.example.chatbot.dto.AiResponseDto;
import com.example.chatbot.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
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

    // 1. 일반 질문 (동기 방식 - 단건 응답용)
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

    // 2. 스트리밍 질문 - 단일 문자열 버전 (ChatController의 /stream API 용)
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
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .map(sse -> {
                    String data = sse.data();
                    if (data == null || data.equals("[DONE]")) {
                        return "[DONE]";
                    }
                    return data;
                });
    }

    // 3. 스트리밍 질문 - 대화 기록 리스트 버전 (ChatService의 컨텍스트 스트리밍 용)
    public Flux<String> chatStream(List<Message> history) {
        WebClient webClient = WebClient.builder()
                .baseUrl(OPENAI_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        List<Map<String, String>> messages = new ArrayList<>();
        for (Message msg : history) {
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("role", msg.getRole());
            messageMap.put("content", msg.getContent());
            messages.add(messageMap);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("stream", true);
        body.put("messages", messages);

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .map(sse -> {
                    String data = sse.data();
                    if (data == null || data.equals("[DONE]")) {
                        return "[DONE]";
                    }
                    return data;
                });
    }
}