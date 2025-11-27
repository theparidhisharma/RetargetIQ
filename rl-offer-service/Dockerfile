FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/rl-offer-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3007
ENTRYPOINT ["java","-jar","app.jar"]