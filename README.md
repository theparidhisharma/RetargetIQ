# RetargetIQ
Learning Project: Event-Driven Microservices using Apache Kafka

## Overview

RetargetIQ is a learning-focused backend project that demonstrates how modern platforms process real-time user behavior and transactional data using an event-driven microservices architecture.

This project was built to apply backend engineering concepts learned during my internship at Deutsche Telekom Digital Labs (DTDL). The primary goal was to understand how multiple independent services communicate asynchronously using Apache Kafka, and how such systems are structured in real-world backend environments.

The system simulates user activity, orders, recommendations, ranking, and analytics flowing through multiple backend services using Kafka topics as the communication layer.

---

## Key Concepts Demonstrated

- Microservices-based system design
- Event-driven architecture using Apache Kafka
- Asynchronous communication between services
- Loose coupling and separation of concerns
- Multiple consumers reacting to the same event
- Containerized local orchestration using Docker Compose

---

## Services in the System

The system is composed of multiple small, focused services. Each service owns a single responsibility and communicates via Kafka topics instead of direct REST calls.

---

### 1. User Activity Service

Responsibility:
Simulates real-time user interactions such as views and clicks.

Behavior:
- Exposes a REST API
- Publishes user activity events to Kafka

Kafka Topic:
user-activity-topic

Example Endpoint:
POST http://localhost:8081/api/users/activity

---

### 2. Order Service

Responsibility:
Simulates product order placements.

Behavior:
- Exposes a REST API
- Publishes order events to Kafka

Kafka Topic:
order-topic

Example Endpoint:
POST http://localhost:8082/api/orders/place

---

### 3. Event Processor Service

Responsibility:
Acts as a centralized processing layer for raw events.

Behavior:
- Consumes events from multiple Kafka topics
- Performs basic processing and structured logging

Consumes From:
- user-activity-topic
- order-topic

Note:
In real-world systems, this layer could handle validation, enrichment, or routing of events.

---

### 4. Analytics Service

Responsibility:
Consumes processed events and simulates analytics behavior.

Behavior:
- Listens to Kafka topics
- Logs events that could later be used for dashboards or metrics

Purpose:
Represents how event data is typically consumed for analytics and monitoring pipelines.

---

### 5. Feature Store Service

Responsibility:
Maintains derived or aggregated user features.

Behavior:
- Consumes user and order events
- Computes basic feature representations (simulated)
- Makes features available for downstream services

Purpose:
Represents how feature stores are used in recommendation and personalization systems.

---

### 6. Retrieval Service

Responsibility:
Retrieves candidate items for recommendation.

Behavior:
- Consumes processed events
- Fetches candidate data based on user activity

Purpose:
Simulates the retrieval stage in recommendation pipelines.

---

### 7. Ranking Service

Responsibility:
Ranks retrieved items based on simple logic.

Behavior:
- Consumes candidate data events
- Applies ranking rules (simulated scoring)
- Publishes ranked results

Purpose:
Represents the ranking layer commonly used in recommendation systems.

---

### 8. Recommendation API Service

Responsibility:
Exposes recommendations via a REST API.

Behavior:
- Consumes ranked recommendation events
- Serves final recommendations to clients

Purpose:
Acts as the API layer between backend recommendation logic and frontend consumers.

---

### 9. RL Offer Service

Responsibility:
Simulates offer selection logic.

Behavior:
- Consumes user activity and ranking events
- Generates personalized offer responses (rule-based simulation)

Purpose:
Represents how offer or decision services consume events to personalize outputs.

---

### 10. Frontend UI Service

Responsibility:
Provides a simple user interface to visualize interactions.

Behavior:
- Displays simulated user activity and recommendations
- Interacts with backend APIs

Purpose:
Demonstrates end-to-end data flow from backend services to a UI.

---

## Architecture Overview

The system follows a publish–subscribe model:

- Producer services publish events to Kafka topics
- Kafka acts as a central message broker
- Multiple consumer services independently process the same events
- Services remain loosely coupled and independently deployable

An architecture diagram is included in the repository as architecture.png.

## Architecture Diagram
![RetargetIQ Architecture](architecture.png)

---

## Tech Stack

- Java 17
- Spring Boot
- Apache Kafka
- Docker and Docker Compose
- Maven
- SLF4J and Lombok

---

## Running the Project Locally

Prerequisites:
- Docker
- Docker Compose

Steps:

git clone https://github.com/theparidhisharma/RetargetIQ.git
cd RetargetIQ
docker-compose up --build

This will start Kafka, Zookeeper, and all backend services locally.

---

## Testing the Event Flow

Trigger events using REST calls:

Trigger User Activity Event:
POST http://localhost:8081/api/users/activity

Trigger Order Event:
POST http://localhost:8082/api/orders/place

Each request sends a randomly generated payload to Kafka, which is then consumed by multiple downstream services.

---

## Viewing Logs

- event-processor-service logs processed events
- analytics-service logs analytics data
- other services log their respective event handling

Logs can be viewed using:
docker logs <container_name>

---

## What I Learned From This Project

- How event-driven systems differ from synchronous REST-based architectures
- How Kafka enables decoupled communication between independent services
- How multiple backend services can react to the same event
- How complex backend systems are composed of many small services
- How Docker Compose simplifies local orchestration of microservices

This project helped me connect internship concepts with hands-on backend system design.

---

## Possible Future Improvements

- Add persistence using MongoDB or PostgreSQL
- Implement schema validation and retry mechanisms
- Improve error handling and fault tolerance
- Add monitoring and metrics
- Enhance the frontend UI
- Write unit and integration tests

---

## Author

Paridhi Sharma  
B.Tech (2nd Year) – IGDTUW  
Former Backend Engineering Intern – Deutsche Telekom Digital Labs  

GitHub: https://github.com/theparidhisharma  
LinkedIn: https://www.linkedin.com/in/theparidhisharma  
Email: paridhi0203sharmaaa@gmail.com
