output "instance_ids" {
  value = aws_instance.ec2[*].id
}

output "instance_public_ips" {
  value = aws_instance.ec2[*].public_ip
}
