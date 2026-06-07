# Terraform variables for the development environment
# Duplicate of production variables but with dev‑specific values.
# These values are injected from GitHub Secrets (prefixed with DEV).

# AWS region – same as prod
aws_region = "us-east-1"

# EC2 instance – can be cheaper for dev (e.g., t3.micro)
instance_type = "t3.micro"

# SSH key pair name (same as prod)
key_pair_name = "kata-ssh-key"

# Your public IP – keep the same or use a broader CIDR for dev testing
my_ip = "0.0.0.0/0"

# Development database credentials
db_user     = "dev_user"
# The value will be read from the secret DB_PASSWORD_DEV
# Example: db_password = var.db_password_dev (set via -var "db_password_dev=${{ secrets.DB_PASSWORD_DEV }}")

# Development JWT secret
jwt_secret = "dev-secret-key-minimum-256-bits-long-for-hs256-testing-purposes-only-12345"

# Basic auth credentials for dev
basic_user     = "admin-dev"
basic_password = "dev-password-123"
