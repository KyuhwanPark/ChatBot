# 🤖 AI Chatbot REST API

Spring Boot AI 챗봇 REST API 서버입니다.
OpenAI의 GPT-3.5-turbo 모델을 활용하여 사용자와 실시간으로 대화할 수 있으며, 이전 대화 문맥을 기억하고 SSE(Server-Sent Events)를 통해 자연스러운 스트리밍 응답을 제공합니다.

## ✨ 주요 기능 (Features)

- 💬 **실시간 AI 채팅 (SSE 기반)**: ChatGPT처럼 글자가 실시간으로 타이핑되는 스트리밍 응답 지원
- 🧠 **대화 문맥 유지**: RDBMS(MySQL)에 대화 기록을 저장하여 이전 대화 흐름을 이해하고 답변
- 🔒 **API Key 인증 체계**: `x-api-key` 헤더를 통한 간단하고 강력한 API 접근 제어
- 🛡️ **Rate Limiting (트래픽 제어)**: Redis를 활용하여 특정 IP의 무분별한 API 요청 제한 (기본: 60초당 10회)

## 🛠 기술 스택 (Tech Stack)

- **Backend**: Spring Boot 3.4.1, Spring WebFlux, Java 17
- **Data/Cache**: MySQL 8.0, Spring Data JPA, Redis
- **Infra/Deploy**: Docker, Docker Compose, Railway
- **Docs**: Springdoc OpenAPI (Swagger UI)

## 🚀 시작하기 (Getting Started)
https://chatbot-production-2fea.up.railway.app/
