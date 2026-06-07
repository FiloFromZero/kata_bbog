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
