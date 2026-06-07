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
  value       = "http://${aws_instance.app_server.public_ip}:${var.app_port}"
  description = "URL of the deployed Spring Boot application"
}

output "frontend_url" {
  value       = "http://${aws_s3_bucket_website_configuration.frontend_website.website_endpoint}"
  description = "URL pública del Front-End desplegado en S3"
}

output "frontend_bucket_name" {
  value       = aws_s3_bucket.frontend.id
  description = "Nombre del bucket S3 del Front-End (usado por el pipeline)"
}
