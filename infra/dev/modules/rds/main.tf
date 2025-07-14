data "aws_secretsmanager_secret_version" "db" {
  secret_id = module.secrets.aws_secretsmanager_secret.db.id
}

locals {
  db_creds = jsondecode(data.aws_secretsmanager_secret_version.db.secret_string)
}

resource "aws_db_subnet_group" "this" {
  name       = "dev-db-subnet-group"
  subnet_ids = var.subnet_ids
}

resource "aws_db_instance" "this" {
  identifier             = "innobra-dev-db"
  engine                 = "postgres"
  engine_version         = "13.4"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = var.db_name
  username               = var.db_username
  password               = local.db_creds["password"]
  publicly_accessible    = false
  vpc_security_group_ids = [var.security_group_id]
  db_subnet_group_name   = aws_db_subnet_group.this.name
  skip_final_snapshot    = true

  tags = {
    Environment = "dev"
  }
}
