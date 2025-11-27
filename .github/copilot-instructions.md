Welcome! These instructions help an AI coding agent (and contributors) quickly become productive in the RetargetIQ repository.

Key design summary
- This is a set of small Spring Boot microservices wired together with Apache Kafka and Docker Compose.
- Services are independent Maven projects under the repo root (e.g. `user-activity-service`, `order-service`, `event-processor-service`, `analytics-service`, `feature-store-service`, `rl-offer-service`, `ranking-service`, `retrieval-service`).
- Communication is asynchronous through Kafka topics — producers call `KafkaTemplate` and consumers use `@KafkaListener`.

Where to look first (fast wins)
- Top-level orchestrator: `docker-compose.yml` — use this to spin up kafka, postgres, and several services for integration testing.
- High-level architecture: `README.md` (root) explains the event flow and default topics.
- Example producer: `order-service/src/main/java/com/example/orderservice/KafkaProducer.java`.
- Example consumer: `event-processor-service/src/main/java/com/example/orderservice/eventprocessor/KafkaConsumerService.java` and `feature-store-service/src/main/java/com/retargetiq/featurestore/kafka/EventConsumer.java`.

Common patterns and conventions to preserve
- Kafka bootstrap address: services use `spring.kafka.bootstrap-servers` with the Docker default `kafka:9092`. When running locally outside compose, you may need `localhost:9092` instead.
- Topic names and env var pattern: many services read topic names from `application.yml`/`application.properties` or environment variables, e.g. `FEATURESTORE_KAFKA_USER_TOPIC` (default `user-events`). Always check the service's `src/main/resources` for defaults.
- Port conventions (used by compose): user-activity 8080, event-processor 8081, analytics 8082, order 8083, rl-offer-service 3007. Confirm service-level `application.yml`/`application.properties` before changing ports.
- Packages vary across services (`com.example.*`, `com.retarget.*`, `com.retargetiq.*`) — prefer editing within the service's own package and keeping package scope consistent.

Developer workflows / exact commands
- Spin up the full dev stack (Docker + Kafka + services):
  docker-compose up --build

- Build a single service (fast local iteration):
  cd <service>
  mvn clean package -DskipTests

- Run a service locally without Docker:
  cd <service>
  mvn spring-boot:run

- Check logs from compose (follow):
  docker-compose logs -f <service-name>

What agents should do first (step-by-step)
1. Read `README.md` for the big picture and `docker-compose.yml` to understand what compose will start.
2. Search the repo for `@KafkaListener` and `KafkaTemplate` to find producers/consumers for the change you're implementing.
3. Make small, self-contained edits scoped to one service. Update that service's `application.properties`/`application.yml` and `Dockerfile` only when necessary.
4. Build and run the affected service locally (`mvn package` or `spring-boot:run`) and/or run the system via compose to validate integration.

Small examples to reference in PRs
- If changing a topic name, update: `feature-store-service/src/main/resources/application.yml` and the consumer annotation `@KafkaListener(topics = "${featurestore.kafka.user-topic:user-events}")`.
- If adding an API endpoint, mirror patterns in `user-activity-service/src/main/java/com/example/orderservice/ActivityController.java` and `order-service/src/main/java/com/example/orderservice/OrderController.java`.

Pitfalls and gotchas
- Don't assume `localhost:9092` when the service runs in compose — service containers use `kafka:9092`.
- Not every service is included in `docker-compose.yml`. Feature-store, ranking, retrieval and RL services might require extra compose entries or manual run commands (see each service's README).
- Tests are sparse in some services; prefer quick local run + integration via compose for validation.

If anything in these instructions seems incomplete, tell me what's failing or which service you want more detail on and I’ll update this file with more examples.

— RetargetIQ Copilot guidance (generated)
