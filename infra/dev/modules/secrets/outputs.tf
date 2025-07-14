output "secret_arn" {
  description = "ARN of the DB credentials secret"
  value       = aws_secretsmanager_secret.db.arn
}
output "db_secret_arn" {
  description = "El ARN del secreto RDS"
  value       = aws_secretsmanager_secret.db.arn
}