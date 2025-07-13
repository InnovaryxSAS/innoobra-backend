variable "db_name" {
  type        = string
  description = "Name of the PostgreSQL database"
}
variable "db_username" {
  type        = string
  description = "Master username for RDS"
}
variable "db_password_secret_arn" {
  type        = string
  description = "ARN of the Secrets Manager secret containing the password"
}
variable "subnet_ids" {
  type        = list(string)
  description = "List of private subnet IDs for RDS subnet group"
}
variable "security_group_id" {
  type        = string
  description = "Security Group ID allowing Lambda access"
}
