variable "aws_region" {
  description = "AWS region where resources will be deployed"
  default     = "eu-central-1"
}

variable "instance_type" {
  description = "Tipul instanței EC2"
  default     = "t3.large"
}

variable "ami_id" {
  description = "AMI ID for the EC2 instance"
  default = "ami-023adaba598e661ac"
}

variable "key_name" {
  # default = "/Users/brinzandarius/Downloads/access.pem"
  default = "access"
}