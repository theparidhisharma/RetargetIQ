FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/recommend-api-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3000
ENTRYPOINT ["java","-jar","app.jar"]