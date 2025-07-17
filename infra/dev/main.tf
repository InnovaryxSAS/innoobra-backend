provider "aws" {
  region  = "us-east-1"
}

# 1) Secrets Manager (usuario + contraseña RDS)
module "secrets" {
  source      = "./modules/secrets"
  db_username = var.db_username  # sólo para inicializar
}

# 2) VPC + subnets + route tables
module "vpc" {
  source          = "./modules/vpc"
  vpc_cidr        = var.vpc_cidr
  az              = var.az
  public_subnets  = var.public_subnets
  private_subnets = var.private_subnets
  environment    = var.environment
  region         = var.region
}

# 3) NAT instance económica
module "nat" {
  source                 = "./modules/nat"
  public_subnet_id       = module.vpc.public_subnet_ids[0]
  private_route_table_id = module.vpc.private_route_table_id
}

# 4) Security Groups
module "security" {
  source = "./modules/security"
  vpc_id        = module.vpc.vpc_id
}

# 5) RDS Postgres en subred privada
module "rds" {
  source                 = "./modules/rds"
  db_name                = var.db_name
  db_username            = var.db_username
  db_password_secret_arn = module.secrets.db_secret_arn
  subnet_ids             = module.vpc.private_subnet_ids
  security_group_id      = module.security.rds_sg_id
  db_secret_arn          = module.secrets.db_secret_arn
}

# 6) Lambda + HTTP API + integración con RDS y Secrets
module "lambda" {
  source         = "./modules/lambda"

  # Variables propias del módulo
  environment    = var.environment
  lambdas        = var.lambdas
  common         = var.common

  # Networking
  vpc_subnet_ids = module.vpc.private_subnet_ids
  lambda_sg_id   = module.security.lambda_sg_id

  # Base de datos & secretos
  db_secret_arn  = module.secrets.db_secret_arn
  db_endpoint    = module.rds.db_address
  db_name        = var.db_name
  db_username    = var.db_username

  # Performance Lambdas
  memory_size    = var.lambda_memory
  timeout        = var.lambda_timeout
  lambda_bucket = var.lambda_bucket
  vpc_id        = module.vpc.vpc_id
  region        = var.region
}

module "endpoints" {
  source             = "./modules/endpoints"
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  lambda_sg_id       = module.security.lambda_sg_id
  region             = var.region
}


###########################################
# (Opcional) Outputs globales
###########################################

output "rds_endpoint" {
  description = "Endpoint de la base de datos RDS"
  value       = module.rds.db_address
}

output "api_url" {
  description = "URL pública del HTTP API Gateway"
  value       = module.lambda.endpoint_url
}

# VPC Endpoint para Secrets Manager
resource "aws_vpc_endpoint" "secretsmanager" {
  vpc_id            = module.vpc.vpc_id
  service_name      = "com.amazonaws.${var.region}.secretsmanager"
  vpc_endpoint_type = "Interface"
  subnet_ids        = module.vpc.private_subnet_ids
  security_group_ids = [module.security.lambda_sg_id]
  tags = {
    Name = "secretsmanager-endpoint-${var.environment}"
  }
}


































