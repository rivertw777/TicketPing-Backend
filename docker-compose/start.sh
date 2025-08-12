#!/bin/bash

echo "1. Starting Postgres services..."
docker compose -f docker-compose-postgres.yml up -d
sleep 5

echo "2. Starting Redis services..."
docker compose -f docker-compose-redis.yml up -d
sleep 5

echo "3. Starting Kafka services..."
docker compose -f docker-compose-kafka.yml up -d
sleep 5

echo "4. Starting monitoring services..."
docker compose -f docker-compose-monitoring.yml up -d
sleep 5

echo "5. Building Gradle project..."
cd ..
./gradlew build -x test
cd docker-compose

echo "6. Building Docker images..."
docker compose -f docker-compose-build.yml build
sleep 5

echo "7. Starting application services..."
docker compose -f docker-compose-spring.yml up -d

echo "All steps completed successfully!"
