variable "environment"   { type = string }
variable "lambdas" {
  description = "Map de funciones Lambda (handler, jar_path, route_key)"
  type = map(object({
    handler   = string
    jar_path  = string
    route_key = string
  }))
}

variable "vpc_subnet_ids"{ type = list(string) }
variable "lambda_sg_id"  { type = string }
variable "db_secret_arn" { type = string }
variable "db_endpoint"   { type = string }
variable "db_name"       { type = string }
variable "db_username"   { type = string }
variable "common"        { type = object({ zip_path = string }) }
variable "memory_size" {
  type        = number
  default     = 128
}
variable "timeout" {
  type        = number
  default     = 30
}
variable "lambda_bucket" {
  description = "Bucket donde subimos los .zip de las Lambdas"
  type        = string
}