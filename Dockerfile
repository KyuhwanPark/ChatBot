# 1. Build Stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Maven 래퍼와 설정 파일 복사
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 의존성 먼저 다운로드 (캐시 활용)
RUN ./mvnw dependency:go-offline

# 소스 코드 복사 및 빌드 (테스트 생략)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일 복사
COPY --from=builder /app/target/*.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8081

# 컨테이너 실행 시 애플리케이션 구동
ENTRYPOINT ["java", "-jar", "app.jar"]