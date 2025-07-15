resource "aws_ssm_parameter" "db_host" {
  name  = "/innobra/dev/db/host"         # O "prod" según el ambiente
  type  = "String"
  value = var.db_host_value

  tags = {
    Environment = "dev"
    Service     = "database"
    Project     = "innobra"
  }
}
