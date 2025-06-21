
# Base image হিসেবে OpenJDK 17 ব্যবহার করুন
FROM openjdk:17-jdk-slim
## ওয়ার্কিং ডিরেক্টরি সেট করুন
WORKDIR /app
# pom.xml এবং src ফোল্ডার কপি করু
COPY pom.xml .
COPY src ./src
 # Maven ব্যবহার করে অ্যাপ্লিকেশন বিল্ড করুন (টেস্ট স্কিপ করুন)
 RUN mvn clean package -DskipTests/
 # JAR ফাইলটি রান করার কমান্ড
 CMD ["java", "-jar", "target/Rafiq-print-store-0.0.1-SNAPSHOT.jar"]

