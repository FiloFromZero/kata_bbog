variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "AWS Region to deploy resource"
}

variable "instance_type" {
  type        = string
  default     = "t2.micro"
  description = "EC2 Instance type (Free Tier eligible)"
}

variable "key_pair_name" {
  type        = string
  description = "Name of the SSH Key Pair to access the EC2 instance"
}

variable "my_ip" {
  type        = string
  description = "My public IP in CIDR format (e.g. 192.168.1.1/32) to allow SSH access"
}

variable "db_user" {
  type        = string
  default     = "prod_user"
  description = "Database username for production"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Database password for production"
}

variable "jwt_secret" {
  type        = string
  sensitive   = true
  description = "JWT Secret Key for the application (minimum 256 bits)"
}

variable "basic_user" {
  type        = string
  default     = "admin-prod"
  description = "Basic auth username for /auth/login"
}

variable "basic_password" {
  type        = string
  sensitive   = true
  description = "Basic auth password for /auth/login"
}

variable "spring_profile" {
  type        = string
  default     = "prod"
  description = "Spring profile active (dev or prod)"
}

variable "app_port" {
  type        = number
  default     = 9090
  description = "Port on which the Spring Boot application runs"
}

