resource "aws_secretsmanager_secret" "db" {
  name        = "innobra-dev-db-credentials"
  description = "Dev RDS credentials"
  tags = {
    Environment = "dev"
  }
}

resource "aws_secretsmanager_secret_version" "db_version" {
  secret_id     = aws_secretsmanager_secret.db.id
  secret_string = jsonencode({
    username = var.db_username
    password = var.db_password
  })
}
