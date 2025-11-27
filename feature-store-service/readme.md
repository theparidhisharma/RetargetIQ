Feature Store Service
---------------------

Purpose:
- Consume user events (topic: user-events)
- Consume order events (topic: order-events)
- Update per-user features in Postgres
- Expose GET /features/{userId}

Run locally (from project root):
- Ensure postgres and redis containers exist (docker compose)
- Build: cd feature-store-service && mvn clean package -DskipTests
- Start: docker compose up --build feature-store-service

Env vars:
- SPRING_DATASOURCE_URL (default: jdbc:postgresql://postgres:5432/retargetdb)
- FEATURESTORE_KAFKA_USER_TOPIC (default: user-events)
- FEATURESTORE_KAFKA_ORDER_TOPIC (default: order-events)
- REDIS_HOST (default: redis)
