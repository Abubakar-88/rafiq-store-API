
# Base image হিসেবে OpenJDK 17 ব্যবহার
FROM openjdk:17-jdk-slim

# JAR ফাইল কপি করুন
WORKDIR /app
COPY target/Rafiq-print-store-0.0.1-SNAPSHOT.jar app.jar

# অ্যাপ্লিকেশন চালানোর কমান্ড
CMD ["java", "-jar", "app.jar"]
