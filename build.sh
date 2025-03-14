#!/bin/bash
# Install Maven
apt-get update
apt-get install -y maven

# Build the project
mvn clean package