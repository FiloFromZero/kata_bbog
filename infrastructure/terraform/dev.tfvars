# Terraform variables for the development environment
# Keep only env-specific settings, credentials will fall back to GitHub Secrets (TF_VAR_*)

aws_region    = "us-east-1"
instance_type = "t3.micro"
