#!/usr/bin/env bash
set -euo pipefail

# 0) Asegúrate de que el backend y providers estén inicializados
terraform init -input=false >/dev/null

echo "→ Leyendo recursos actuales…"
mapfile -t R < <(terraform state list)

# 1) Mueve las funciones Lambda
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_lambda_function\.lambda\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_lambda_function.this[\"$fn\"]"
    echo "  → Mover $src → $tgt"
    if terraform state mv "$src" "$tgt"; then
      echo "    ✓ OK"
    else
      echo "    • no existe, salto…"
    fi
  fi
done

# 2) Mueve los permisos de API Gateway
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_lambda_permission\.api_gw\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_lambda_permission.api_gw[\"$fn\"]"
    echo "  → Mover $src → $tgt"
    if terraform state mv "$src" "$tgt"; then
      echo "    ✓ OK"
    else
      echo "    • no existe, salto…"
    fi
  fi
done

# 3) Mueve las integraciones de API Gateway
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_apigatewayv2_integration\.lambda_integration\[\\"(.+)\\"\]$ ]]; then
    fn=${BASH_REMATCH[1]}
    tgt="module.lambda.aws_apigatewayv2_integration.lambda_integration[\"$fn\"]"
    echo "  → Mover $src → $tgt"
    if terraform state mv "$src" "$tgt"; then
      echo "    ✓ OK"
    else
      echo "    • no existe, salto…"
    fi
  fi
done

# 4) Mueve las rutas de API Gateway
for src in "${R[@]}"; do
  if [[ $src =~ ^aws_apigatewayv2_route\.(lambda_route|default_route)\[\\"(.+)\\"\]$ ]]; then
    kind=${BASH_REMATCH[1]}
    fn=${BASH_REMATCH[2]}
    tgt="module.lambda.aws_apigatewayv2_route.${kind}[\"$fn\"]"
    echo "  → Mover $src → $tgt"
    if terraform state mv "$src" "$tgt"; then
      echo "    ✓ OK"
    else
      echo "    • no existe, salto…"
    fi
  fi
done



# 5) Layer, API y Stage (pueden o no existir)
for res in \
  "aws_lambda_layer_version.common module.lambda.aws_lambda_layer_version.common" \
  "aws_apigatewayv2_api.http_api module.lambda.aws_apigatewayv2_api.http_api" \
  "aws_apigatewayv2_stage.default module.lambda.aws_apigatewayv2_stage.default"
do
  set -- $res; src=$1; tgt=$2
  echo "  → Mover $src → $tgt (si existe)"
  if terraform state mv "$src" "$tgt"; then
    echo "    ✓ OK"
  else
    echo "    • no existe, salto…"
  fi
done

echo "✅ Migración de state completada."
