variable "project_name" {
  description = "Project identifier for tags and resource names"
  type        = string
  default     = "payment-orchestration-platform"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
  default     = "us-east-1"
}

variable "base_cidr" {
  description = "Base CIDR block for VPC"
  type        = string
  default     = "10.30.0.0/16"
}

variable "db_name" {
  description = "RDS database name"
  type        = string
  default     = "paymentdb"
}

variable "db_username" {
  description = "RDS master username"
  type        = string
  default     = "payment_admin"
}

variable "enable_network" {
  description = "Whether to create VPC and private subnets"
  type        = bool
  default     = false
}

variable "enable_rds" {
  description = "Whether to create PostgreSQL RDS"
  type        = bool
  default     = false
}

variable "enable_ecs" {
  description = "Whether to create ECS cluster and log group"
  type        = bool
  default     = false
}

variable "enable_secrets" {
  description = "Whether to create Secrets Manager secret for DB credentials"
  type        = bool
  default     = false
}

variable "common_tags" {
  description = "Common tags to be merged into all resources"
  type        = map(string)
  default     = {}
}
