# Base image হিসেবে OpenJDK 17 ব্যবহার
FROM openjdk:17-jdk-slim

# ওয়ার্কিং ডিরেক্টরি সেট করুন
WORKDIR /app

# Maven এবং তার ডিপেন্ডেন্সি সেটআপ
COPY pom.xml .
COPY src ./src

# Maven ইনস্টল করুন এবং অ্যাপ বিল্ড করুন
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && mv target/Rafiq-print-store-0.0.1-SNAPSHOT.jar app.jar

# JAR ফাইল চালানোর জন্য কমান্ড
CMD ["java", "-jar", "/app/app.jar"]

