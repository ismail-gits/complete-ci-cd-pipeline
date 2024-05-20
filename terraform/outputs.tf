output "ec2_public_ip-1" {
    value = aws_instance.myapp-server-1.public_ip
}

output "ec2_public_ip-2" {
    value = aws_instance.myapp-server-2.public_ip
}