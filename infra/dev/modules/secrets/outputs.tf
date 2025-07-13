output "secret_arn" {
  description = "ARN of the DB credentials secret"
  value       = aws_secretsmanager_secret.db.arn
}
