resource "aws_security_group" "app_sg" {
  name        = "kata-cloud-sg-${random_id.bucket_suffix.hex}"
  description = "Security group for kata cloud application"

  # SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
    description = "Allow SSH from my IP"
  }

  # App port
  ingress {
    from_port   = var.app_port
    to_port     = var.app_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow access to the Spring Boot App"
  }

  # Egress
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name        = "kata-cloud-sg"
    Environment = "prod"
  }
}
