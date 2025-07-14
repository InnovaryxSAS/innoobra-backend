data "aws_secretsmanager_secret_version" "db" {
  secret_id = var.db_secret_arn
}

locals {
  db_creds = jsondecode(data.aws_secretsmanager_secret_version.db.secret_string)
}

resource "aws_db_subnet_group" "this" {
  name       = "dev-db-subnet-group"
  subnet_ids = var.subnet_ids
   tags = {
     Name = "${var.db_name}-db-subnet-group"
   }
 }

resource "aws_db_instance" "this" {
  identifier             = "innobra-dev-db"
  engine                 = "postgres"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = var.db_name
  username               = var.db_username
  password               = local.db_creds.password
  publicly_accessible    = false
  vpc_security_group_ids = [var.security_group_id]
  db_subnet_group_name   = aws_db_subnet_group.this.name
  skip_final_snapshot    = true

  tags = {
    Environment = "dev"
  }
}
