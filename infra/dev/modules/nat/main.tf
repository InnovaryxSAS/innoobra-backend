data "aws_ami" "nat" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["amzn-ami-vpc-nat*"]
  }
}

resource "aws_eip" "nat" {
  domain = "vpc"
}

resource "aws_instance" "this" {
  ami                         = data.aws_ami.nat.id
  instance_type               = "t3.nano"
  subnet_id                   = var.public_subnet_id
  associate_public_ip_address = true
  source_dest_check           = false
  tags = { Name = "dev-nat-instance" }
}

resource "aws_route" "private_to_nat" {
  route_table_id         = var.private_route_table_id
  destination_cidr_block = "0.0.0.0/0"
  network_interface_id   = aws_instance.this.primary_network_interface_id
}
