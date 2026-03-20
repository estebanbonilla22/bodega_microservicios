# Multi-módulo genérico (CI o build manual). En local, Compose usa el Dockerfile de cada servicio.
# Ejemplo:
#   docker build --build-arg MODULE=auth-service --build-arg EXPOSE_PORT=8081 -t bodega-auth .
FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG MODULE
WORKDIR /w
COPY pom.xml .
COPY discovery-service discovery-service
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY catalog-service catalog-service
COPY inventory-service inventory-service
COPY dispatch-service dispatch-service
RUN mvn -q -pl "${MODULE}" -am package -DskipTests

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
ARG MODULE
COPY --from=build /w/${MODULE}/target/*.jar /app/app.jar
ARG EXPOSE_PORT=8080
EXPOSE ${EXPOSE_PORT}
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
