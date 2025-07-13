variable "public_subnet_id" {
  type        = string
  description = "ID of the public subnet for the NAT instance"
}

variable "private_route_table_id" {
  type        = string
  description = "ID of the private route table to add the NAT route"
}
