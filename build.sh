#!/bin/bash
# Java এবং Maven ইনস্টল করুন
apt-get update
apt-get install -y openjdk-17-jdk maven

# Spring Boot অ্যাপ্লিকেশন বিল্ড করুন
mvn clean package