# STAGE 1: Build the application
# We use a Docker image that already has Maven installed to save time
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only the pom.xml first. 
# This allows Docker to cache dependencies. If you don't change pom.xml,
# it won't re-download all JARs next time you build.
COPY ./pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build
COPY ./src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Run the application
# We switch to a smaller, runtime-only image for production
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the compiled JAR from the 'builder' stage above
# UPDATED: Changed jar name to match event-processor-service
COPY --from=builder /app/target/event-processor-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]