# Terraform Base - Payment Orchestration Platform

This base package provisions a minimal dev foundation for AWS:
- VPC and private subnets
- PostgreSQL RDS
- ECS cluster and log group
- Secrets Manager database secret

## Quick Start

```bash
cd infra/terraform
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform plan
```

By default, resource creation is controlled by feature flags:
- enable_network
- enable_rds
- enable_ecs
- enable_secrets

Set them to true in terraform.tfvars to provision resources.
