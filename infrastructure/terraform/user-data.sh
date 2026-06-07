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

# Configure dynamic port and database name based on the active profile
PORT=${app_port}
if [ "${spring_profile}" = "dev" ]; then
  DB_NAME="customers_dev"
else
  DB_NAME="customers_prod"
fi

# Run PostgreSQL in Docker
# Expose port 5432 and configure credentials
docker run -d \
  --name "postgres-${spring_profile}" \
  --restart always \
  -p 5432:5432 \
  -e POSTGRES_DB="$${DB_NAME}" \
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
# Configure active profile, set host database, port, and security variables
java -jar /opt/app/app.jar \
  --spring.profiles.active="${spring_profile}" \
  --server.port="$${PORT}" \
  --spring.datasource.url="jdbc:postgresql://localhost:5432/$${DB_NAME}" \
  --spring.datasource.username="${db_user}" \
  --spring.datasource.password="${db_password}" \
  --security.jwt.secret="${jwt_secret}" \
  --security.basic.username="${basic_user}" \
  --security.basic.password="${basic_password}" > /opt/app/app.log 2>&1 &
