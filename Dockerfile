# Dockerfile
# Stage 1: build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
# Copy Maven files first to leverage caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn -B -f pom.xml -q dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built jar and rename to app.jar
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

# Commands to rebuild & restart (run in PowerShell)
# docker rm -f wallet-service
# docker-compose up -d --build
