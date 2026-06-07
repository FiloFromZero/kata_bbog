data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*-x86_64"]
  }
}

resource "aws_instance" "app_server" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = var.instance_type
  key_name               = var.key_pair_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name
  user_data_replace_on_change = true

  user_data = templatefile("${path.module}/user-data.sh", {
    s3_bucket         = aws_s3_bucket.artifacts.id
    db_user           = var.db_user
    db_password       = var.db_password
    jwt_secret        = var.jwt_secret
    basic_user        = var.basic_user
    basic_password    = var.basic_password
    spring_profile    = var.spring_profile
    app_port          = var.app_port
    duckdns_subdomain = var.duckdns_subdomain
    duckdns_token     = var.duckdns_token
    jar_version       = aws_s3_object.app_jar.etag
  })

  # Ensure the jar is uploaded before creating the instance so user-data doesn't fail
  depends_on = [aws_s3_object.app_jar]

  tags = {
    Name        = "kata-cloud-customers"
    Environment = "prod"
  }
}

# IAM Role for EC2 to access S3
resource "aws_iam_role" "ec2_s3_role" {
  name = "kata-cloud-ec2-s3-role-${random_id.bucket_suffix.hex}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# IAM Policy for S3 read access
resource "aws_iam_policy" "s3_read_policy" {
  name        = "kata-cloud-s3-read-policy-${random_id.bucket_suffix.hex}"
  description = "Allow EC2 to read artifacts from S3"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Effect = "Allow"
        Resource = [
          aws_s3_bucket.artifacts.arn,
          "${aws_s3_bucket.artifacts.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ec2_s3_attachment" {
  role       = aws_iam_role.ec2_s3_role.name
  policy_arn = aws_iam_policy.s3_read_policy.arn
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "kata-cloud-ec2-instance-profile-${random_id.bucket_suffix.hex}"
  role = aws_iam_role.ec2_s3_role.name
}
