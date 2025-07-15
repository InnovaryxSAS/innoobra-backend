resource "aws_secretsmanager_secret" "db" {
  name        = "innobra-dev-db-credentials"
  description = "Dev RDS credentials"
  tags = {
    Environment = "dev"
  }
}
