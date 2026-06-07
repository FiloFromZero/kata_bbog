terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
  backend "s3" {}
}

provider "aws" {
  region = var.aws_region
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket" "artifacts" {
  bucket        = "kata-cloud-artifacts-${random_id.bucket_suffix.hex}"
  force_destroy = true

  tags = {
    Name        = "kata-cloud-artifacts"
    Environment = "prod"
  }
}

resource "aws_s3_object" "app_jar" {
  bucket = aws_s3_bucket.artifacts.id
  key    = "app.jar"
  source = "${path.module}/../../Back-End/target/app.jar"
  etag   = fileexists("${path.module}/../../Back-End/target/app.jar") ? filemd5("${path.module}/../../Back-End/target/app.jar") : null
}

# --- Front-End: S3 Static Website Hosting ---

resource "aws_s3_bucket" "frontend" {
  bucket        = "kata-cloud-frontend-${var.spring_profile}-${random_id.bucket_suffix.hex}"
  force_destroy = true
  tags = {
    Name        = "kata-cloud-frontend"
    Environment = var.spring_profile
  }
}

resource "aws_s3_bucket_website_configuration" "frontend_website" {
  bucket = aws_s3_bucket.frontend.id
  index_document { suffix = "index.html" }
  error_document { key    = "index.html" }  # SPA fallback — rutas del router
}

resource "aws_s3_bucket_public_access_block" "frontend_public" {
  bucket                  = aws_s3_bucket.frontend.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "frontend_policy" {
  bucket     = aws_s3_bucket.frontend.id
  depends_on = [aws_s3_bucket_public_access_block.frontend_public]
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "PublicReadGetObject"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:GetObject"
      Resource  = "${aws_s3_bucket.frontend.arn}/*"
    }]
  })
}
