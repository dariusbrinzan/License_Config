variable "aws_region" {
  description = "AWS region where resources will be deployed"
  default     = "eu-central-1"
}

variable "instance_type" {
  description = "Tipul instanței EC2"
  default     = "t2.micro"
}

variable "ami_id" {
  description = "ami-0183b16fc359a89dd"
}

variable "key_name" {
  # default = "/Users/brinzandarius/Downloads/access.pem"
  default = "access.pem"
}