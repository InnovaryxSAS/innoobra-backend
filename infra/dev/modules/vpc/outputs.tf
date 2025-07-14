output "vpc_id" {
  value = aws_vpc.this.id
}

output "public_subnet_ids" {
  value = values(aws_subnet.public)[*].id
  description = "IDs de las subnets públicas"
}

output "private_subnet_ids" {
  value = aws_subnet.private[*].id
  description = "IDs de las subnets privadas"
}

output "private_route_table_id" {
  value = aws_route_table.private.id
}
