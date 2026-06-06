#!/bin/bash
# user-data.sh
# Update system
dnf update -y

# Install Java 21 (Corretto)
dnf install -y java-21-amazon-corretto

# Install and start Docker
dnf install -y docker
systemctl start docker
systemctl enable docker

# Run PostgreSQL in Docker
# Expose port 5432 and configure credentials
docker run -d \
  --name postgres-prod \
  --restart always \
  -p 5432:5432 \
  -e POSTGRES_DB=customers_prod \
  -e POSTGRES_USER="${db_user}" \
  -e POSTGRES_PASSWORD="${db_password}" \
  postgres:16-alpine

# Prepare app directory
mkdir -p /opt/app
cd /opt/app

# Wait for S3 bucket to be ready, then download the JAR
# Amazon Linux 2023 comes with the AWS CLI installed
aws s3 cp s3://${s3_bucket}/app.jar /opt/app/app.jar

# Run the Spring Boot App
# Configure active profile to 'prod', set host database, port, and security variables
java -jar /opt/app/app.jar \
  --spring.profiles.active=prod \
  --server.port=9090 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/customers_prod \
  --spring.datasource.username="${db_user}" \
  --spring.datasource.password="${db_password}" \
  --security.jwt.secret="${jwt_secret}" \
  --security.basic.username="${basic_user}" \
  --security.basic.password="${basic_password}" > /opt/app/app.log 2>&1 &
