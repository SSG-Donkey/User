# 기본 Java 환경을 사용하는 Docker 이미지
FROM openjdk:17-jdk

# 애플리케이션 JAR 파일을 이미지로 복사 #build/libs/ 경로는 Gradle 빌드 후 JAR 파일이 위치하는 곳입니다.
COPY build/libs/*.jar app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "/app.jar"]
