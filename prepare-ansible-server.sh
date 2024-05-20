#!/usr/bin/env bash

sudo yum update
sudo yum install ansible -y
sudo yum install python3-pip -y
pip3 install boto3 botocore