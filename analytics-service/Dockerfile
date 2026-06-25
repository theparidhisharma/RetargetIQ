FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . /app

RUN apt-get update && \
    apt-get install -y maven

RUN mvn clean package -DskipTests

EXPOSE 8084

CMD ["java","-jar","target/analytics-service-1.0.0.jar"]