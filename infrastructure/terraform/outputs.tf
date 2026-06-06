output "ec2_public_ip" {
  value       = aws_instance.app_server.public_ip
  description = "Public IP address of the EC2 instance"
}

output "ec2_public_dns" {
  value       = aws_instance.app_server.public_dns
  description = "Public DNS of the EC2 instance"
}

output "s3_bucket_name" {
  value       = aws_s3_bucket.artifacts.id
  description = "Name of the S3 bucket created for artifacts"
}

output "app_url" {
  value       = "http://${aws_instance.app_server.public_ip}:9090"
  description = "URL of the deployed Spring Boot application"
}
