resource "aws_instance" "example" {
  count         = 2
  ami           = var.ami_id
  instance_type = var.instance_type
  key_name = var.key_name

  tags = {
    Name = "Instance ${count.index + 1}"
  }
}
