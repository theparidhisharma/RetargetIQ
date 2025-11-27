FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . /app
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests
EXPOSE 8082
CMD ["java", "-jar", "target/analytics-service-1.0-SNAPSHOT.jar"]