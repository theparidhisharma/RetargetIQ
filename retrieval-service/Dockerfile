FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/retrieval-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3005
ENTRYPOINT ["java","-jar","app.jar"]
