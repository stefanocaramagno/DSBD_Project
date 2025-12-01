# Fase 1: build del JAR con Maven
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Fase 2: immagine runtime
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
