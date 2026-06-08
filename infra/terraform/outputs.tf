output "vpc_id" {
  description = "VPC identifier"
  value       = var.enable_network ? aws_vpc.main[0].id : null
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value = var.enable_network ? [
    aws_subnet.private_a[0].id,
    aws_subnet.private_b[0].id
  ] : []
}

output "rds_endpoint" {
  description = "RDS endpoint"
  value       = var.enable_network && var.enable_rds ? aws_db_instance.postgres[0].endpoint : null
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = var.enable_ecs ? aws_ecs_cluster.main[0].name : null
}

output "db_secret_arn" {
  description = "Secrets Manager ARN for DB credentials"
  value       = var.enable_secrets ? aws_secretsmanager_secret.db_credentials[0].arn : null
}
