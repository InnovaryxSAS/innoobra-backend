provider "aws" {
  region  = "us-east-1"
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda_exec_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
      Effect = "Allow"
      Sid    = ""
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}


resource "aws_apigatewayv2_api" "http_api" {
  name          = "http-api-budget"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

output "endpoint_url" {
  value = aws_apigatewayv2_api.http_api.api_endpoint
}

resource "aws_lambda_function" "lambda" {
  for_each      = var.lambdas
  function_name = "${each.key}_${var.environment}"
  handler       = each.value.handler
  runtime       = "java21"
  filename      = "${path.module}/${each.value.jar_path}"
  layers = [ aws_lambda_layer_version.commons.arn ]
  source_code_hash = filebase64sha256("${path.module}/${each.value.jar_path}")
  role          = aws_iam_role.lambda_exec.arn
  memory_size   = 128
  timeout       = 15
  environment {
    variables = {
      ENV = var.environment
    }
  }
}

resource "aws_lambda_layer_version" "commons" {
  layer_name          = "commons-layer"
  compatible_runtimes = ["java21"]
  filename            = "${path.module}/${var.commons.zip_path}"
  source_code_hash    = filebase64sha256("${path.module}/${var.commons.zip_path}")
}

resource "aws_apigatewayv2_route" "lambda_route" {
  for_each  = var.lambdas
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = each.value.route_key
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration[each.key].id}"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  for_each               = var.lambdas
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.lambda[each.key].invoke_arn
  integration_method     = "POST"               
  payload_format_version = "2.0"
}

resource "aws_lambda_permission" "api_gw" {
  for_each      = var.lambdas
  statement_id  = "AllowExecutionFromAPIGateway-${each.key}-${var.environment}"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda[each.key].function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http_api.execution_arn}/*/*"
}


