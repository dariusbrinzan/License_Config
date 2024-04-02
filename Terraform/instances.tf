resource "aws_instance" "ec2" {
  count         = 1
  ami           = var.ami_id
  instance_type = var.instance_type
  key_name      = var.key_name
  vpc_security_group_ids = [aws_security_group.allow_ssh.id]

  root_block_device {
    volume_size = 13
  }

  tags = {
    Name = "Instance ${count.index + 1}"
  }
}
