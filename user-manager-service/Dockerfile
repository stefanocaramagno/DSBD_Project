# Fase 1: build del JAR con Maven
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copiamo solo il necessario per il build
COPY pom.xml .
COPY src ./src

# Compiliamo il progetto (senza test, per velocizzare)
RUN mvn clean package -DskipTests

# Fase 2: immagine runtime "leggera"
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiamo il JAR generato nella fase di build
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]