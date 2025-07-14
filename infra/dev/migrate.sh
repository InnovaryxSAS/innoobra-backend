#!/usr/bin/env bash
set -euo pipefail

terraform init -input=false >/dev/null
echo "→ Leyendo recursos actuales…"
mapfile -t R < <(terraform state list)

# 1) Mueve las Lambdas
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_lambda_function\.lambda\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_lambda_function.this[\"$fn\"]"
    echo "→ Mover $src → $tgt"
    terraform state mv -allow-missing "$src" "$tgt"
  fi
done

# 2) Mueve los permisos
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_lambda_permission\.api_gw\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_lambda_permission.api_gw[\"$fn\"]"
    echo "→ Mover $src → $tgt"
    terraform state mv -allow-missing "$src" "$tgt"
  fi
done

# 3) Mueve las integraciones
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_apigatewayv2_integration\.lambda_integration\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_apigatewayv2_integration.lambda_integration[\"$fn\"]"
    echo "→ Mover $src → $tgt"
    terraform state mv -allow-missing "$src" "$tgt"
  fi
done

# 4) Mueve las rutas
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_apigatewayv2_route\.lambda_route\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_apigatewayv2_route.lambda_route[\"$fn\"]"
    echo "→ Mover $src → $tgt"
    terraform state mv -allow-missing "$src" "$tgt"
  fi
done

# 5) Layer, API y Stage (pueden no existir)
for res in \
  "aws_lambda_layer_version.common module.lambda.aws_lambda_layer_version.common" \
  "aws_apigatewayv2_api.http_api module.lambda.aws_apigatewayv2_api.http_api" \
  "aws_apigatewayv2_stage.default module.lambda.aws_apigatewayv2_stage.default"
do
  set -- $res
  src=$1; tgt=$2
  echo "→ Mover $src → $tgt (si existe)"
  terraform state mv -allow-missing "$src" "$tgt" || echo "   • no existe, salto…"
done

echo "✅ Migración de state completada."
