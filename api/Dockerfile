
# 베이스 이미지 설정 (예: OpenJDK 17)
FROM openjdk:17-jdk-slim

# JAR 파일을 /app 디렉토리에 복사
COPY build/libs/*.jar /app/app.jar

# 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
