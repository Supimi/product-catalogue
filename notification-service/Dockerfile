
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_FILE=target/*.jar

# Copy the jar file from the host machine into the container
COPY ${JAR_FILE} /app/notification-service.jar

# Expose the port the application will run on

EXPOSE 8082

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]