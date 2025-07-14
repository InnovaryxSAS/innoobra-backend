// modules/secrets/outputs.tf
output "db_secret_arn" {
  description = "ARN del secreto RDS (usuario + contraseña)"
  value       = aws_secretsmanager_secret.db.arn
}

output "db_secret_version_id" {
  description = "Versión actual del secreto"
  value       = aws_secretsmanager_secret_version.db_version.version_id
}