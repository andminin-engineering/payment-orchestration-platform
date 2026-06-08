locals {
  name_prefix = "${var.project_name}-${var.environment}"
  tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    },
    var.common_tags
  )
}

resource "aws_vpc" "main" {
  count                = var.enable_network ? 1 : 0
  cidr_block           = var.base_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-vpc"
  })
}

resource "aws_subnet" "private_a" {
  count             = var.enable_network ? 1 : 0
  vpc_id            = aws_vpc.main[0].id
  availability_zone = "${var.aws_region}a"
  cidr_block        = cidrsubnet(var.base_cidr, 8, 1)

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-private-a"
    Tier = "private"
  })
}

resource "aws_subnet" "private_b" {
  count             = var.enable_network ? 1 : 0
  vpc_id            = aws_vpc.main[0].id
  availability_zone = "${var.aws_region}b"
  cidr_block        = cidrsubnet(var.base_cidr, 8, 2)

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-private-b"
    Tier = "private"
  })
}

resource "aws_security_group" "rds" {
  count       = var.enable_network && var.enable_rds ? 1 : 0
  name        = "${local.name_prefix}-rds-sg"
  description = "Security group for PostgreSQL"
  vpc_id      = aws_vpc.main[0].id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [var.base_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-rds-sg"
  })
}

resource "aws_db_subnet_group" "main" {
  count      = var.enable_network && var.enable_rds ? 1 : 0
  name       = "${replace(local.name_prefix, "_", "-")}-db-subnets"
  subnet_ids = [aws_subnet.private_a[0].id, aws_subnet.private_b[0].id]

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-db-subnets"
  })
}

resource "random_password" "db_password" {
  count   = var.enable_secrets ? 1 : 0
  length  = 24
  special = true
}

resource "aws_secretsmanager_secret" "db_credentials" {
  count       = var.enable_secrets ? 1 : 0
  name        = "${local.name_prefix}/database"
  description = "Database credentials for payment orchestration platform"

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-db-secret"
  })
}

resource "aws_secretsmanager_secret_version" "db_credentials" {
  count     = var.enable_secrets ? 1 : 0
  secret_id = aws_secretsmanager_secret.db_credentials[0].id
  secret_string = jsonencode({
    username = var.db_username
    password = random_password.db_password[0].result
    db_name  = var.db_name
  })
}

resource "aws_db_instance" "postgres" {
  count                   = var.enable_network && var.enable_rds ? 1 : 0
  identifier              = "${replace(local.name_prefix, "_", "-")}-pg"
  engine                  = "postgres"
  engine_version          = "16.3"
  instance_class          = "db.t4g.micro"
  allocated_storage       = 20
  max_allocated_storage   = 100
  db_name                 = var.db_name
  username                = var.db_username
  password                = var.enable_secrets ? random_password.db_password[0].result : "ChangeMeImmediately123!"
  db_subnet_group_name    = aws_db_subnet_group.main[0].name
  vpc_security_group_ids  = [aws_security_group.rds[0].id]
  publicly_accessible     = false
  deletion_protection     = false
  skip_final_snapshot     = true
  backup_retention_period = 7

  tags = merge(local.tags, {
    Name = "${local.name_prefix}-postgres"
  })
}

resource "aws_cloudwatch_log_group" "ecs" {
  count             = var.enable_ecs ? 1 : 0
  name              = "/ecs/${local.name_prefix}"
  retention_in_days = 14

  tags = local.tags
}

resource "aws_ecs_cluster" "main" {
  count = var.enable_ecs ? 1 : 0
  name  = "${local.name_prefix}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = local.tags
}
