FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/feature-store-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3004
ENTRYPOINT ["java","-jar","app.jar"]
