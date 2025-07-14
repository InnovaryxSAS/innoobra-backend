##############################################################################
# 1) IAM Role & Policies
##############################################################################

data "aws_iam_policy_document" "assume_lambda" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}


resource "aws_iam_role" "lambda_exec" {
  name               = "lambda_exec_role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.assume_lambda.json
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy" "lambda_secrets" {
  name = "LambdaSecretsAccess-${var.environment}"
  role = aws_iam_role.lambda_exec.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["secretsmanager:GetSecretValue"]
      Resource = [var.db_secret_arn]
    }]
  })
}

##############################################################################
# 2) API Gateway HTTP
##############################################################################

resource "aws_apigatewayv2_api" "http_api" {
  name          = "http-api-${var.environment}"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

output "endpoint_url" {
  description = "URL base del HTTP API"
  value       = aws_apigatewayv2_api.http_api.api_endpoint
}

##############################################################################
# 3) Lambda Layer para utilidades comunes
##############################################################################

resource "aws_lambda_layer_version" "common" {
  layer_name          = "common-layer"
  compatible_runtimes = ["java21"]
  # path.root apunta al directorio raíz (infra/dev)
  filename         = null
  s3_bucket        = var.lambda_bucket
  s3_key           = "common-layers/common.zip"
  source_code_hash = filebase64sha256("${path.root}/${var.common.zip_path}")
}

##############################################################################
# 4) Funciones Lambda + VPC + DB
##############################################################################


locals {
  lambda_to_folder = {
    # Company
    "create_company"       = "company"
    "get_companies"        = "company"
    "update_company"       = "company"
    "delete_company"       = "company"

    # Project
    "create_project"       = "project"
    "get_projects"         = "project"
    "update_project"       = "project"
    "delete_project"       = "project"

    # ApuDetail
    "create_apudetail"     = "apudetail"
    "get_apudetails"       = "apudetail"
    "update_apudetail"     = "apudetail"
    "delete_apudetail"     = "apudetail"

    # Chapter
    "create_chapter"       = "chapter"
    "get_chapters"         = "chapter"
    "update_chapter"       = "chapter"
    "delete_chapter"       = "chapter"

    # Activity
    "create_activity"      = "activity"
    "get_activities"       = "activity"
    "update_activity"      = "activity"
    "delete_activity"      = "activity"

    # Attribute
    "create_attribute"     = "attribute"
    "get_attributes"       = "attribute"
    "update_attribute"     = "attribute"
    "delete_attribute"     = "attribute"

    # Role
    "create_role"          = "role"
    "get_roles"            = "role"
    "update_role"          = "role"
    "delete_role"          = "role"

    # User
    "create_user"          = "user"
    "get_users"            = "user"
    "update_user"          = "user"
    "delete_user"          = "user"

    # ActivityById etc., si tienes keys como get_activity_by_id:
    "get_activity_by_id"   = "activity"
    "get_chapter_by_id"    = "chapter"
    "get_project_by_id"    = "project"
    "get_role_by_id"       = "role"
    "get_user_by_id"       = "user"
    "get_attribute_by_id"  = "attribute"
    "get_apudetail_by_id"  = "apudetail"
    "get_company_by_id"    = "company"
    # …y así con TODAS tus keys en var.lambdas
  }
}

resource "aws_lambda_function" "this" {
  for_each         = var.lambdas
  function_name    = "${each.key}_${var.environment}"
  filename         = null
  s3_bucket        = var.lambda_bucket
  s3_key           = "lambdas/${each.key}.zip"
  handler          = each.value.handler
  runtime          = "java21"
  role             = aws_iam_role.lambda_exec.arn
  source_code_hash = filebase64sha256(
    "functions/${local.lambda_to_folder[each.key]}/${each.key}.zip"
  )
  layers           = [aws_lambda_layer_version.common.arn]

  vpc_config {
    subnet_ids         = var.vpc_subnet_ids
    security_group_ids = [var.lambda_sg_id]
  }

  environment {
    variables = {
      ENV            = var.environment
      DB_SECRET_ARN  = var.db_secret_arn
      DB_ENDPOINT    = var.db_endpoint
      DB_NAME        = var.db_name
      DB_USERNAME    = var.db_username
    }
  }

  memory_size = var.memory_size
  timeout     = var.timeout
}

##############################################################################
# 5) Integración con API Gateway y permisos
##############################################################################
resource "aws_apigatewayv2_integration" "lambda_integration" {
  for_each               = var.lambdas
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "AWS_PROXY"
  integration_method     = "POST"
  integration_uri        = aws_lambda_function.this[each.key].invoke_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "lambda_route" {
  for_each  = var.lambdas
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = each.value.route_key
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration[each.key].id}"
}

resource "aws_lambda_permission" "api_gw" {
  for_each      = var.lambdas
  statement_id  = "AllowExecutionFromAPIGateway-${each.key}-${var.environment}"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.this[each.key].function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http_api.execution_arn}/*/*"
}
