FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
COPY src/main/resources/application-prod.yml /app/application-prod.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application-prod.yml"]
