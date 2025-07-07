terraform {
  required_version = ">= 1.0"

  backend "s3" {
    bucket = "mi-bucket-terraform-state-${var.environment}"   # bucket exclusivo para el estado de dev
    key    = "innoobra/${var.environment}/terraform.tfstate"     
    region = "us-east-1"
  }
}