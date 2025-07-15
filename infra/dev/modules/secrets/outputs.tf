// modules/secrets/outputs.tf
output "db_secret_arn" {
  description = "ARN del secreto RDS (usuario + contraseña)"
  value       = aws_secretsmanager_secret.db.arn
}

