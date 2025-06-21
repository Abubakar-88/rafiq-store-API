
# Build Stage
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Rafiq-print-store-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app/app.jar"]
