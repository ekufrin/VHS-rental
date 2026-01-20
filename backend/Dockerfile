# Build and run Spring Boot jar
FROM eclipse-temurin:25-jre
WORKDIR /app
# Expect the application jar built into target/
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
