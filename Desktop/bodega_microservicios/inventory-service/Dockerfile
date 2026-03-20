# Build from monorepo root: docker compose build inventory-service
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /w
COPY pom.xml .
COPY discovery-service discovery-service
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY catalog-service catalog-service
COPY inventory-service inventory-service
COPY dispatch-service dispatch-service
RUN mvn -q -pl inventory-service -am package -DskipTests

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /w/inventory-service/target/*.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
