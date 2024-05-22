# Complete CI/CD Pipeline for Node.js Application with Jenkins, Terraform, Ansible and Docker

This project contains a Node.js application integrated with a complete CI/CD pipeline using Jenkins, Ansible, Docker, and Terraform. Below are the details of each component and how to get started.

# Directory Structure

```plaintext
completely ci-cd pipeline
├── ansible
│ ├── ansible.cfg
│ ├── docker-compose.yaml
│ ├── inventory_aws_ec2.yaml
│ ├── playbook-vars.yaml
│ └── playbook.yaml
├── app
│ ├── images
│ │ ├── profile-1.jpg
│ │ └── profile-2.jpg
│ ├── app.test.js
│ ├── index.html
│ └── server.js
├── terraform
│ ├── main.tf
│ ├── outputs.tf
│ ├── terraform.tfvars
│ └── variables.tf
├── Dockerfile
├── Jenkinsfile
├── package.json
└── prepare-ansible-server.sh
```

# Setup and Usage

## Prerequisites
- Jenkins: Jenkins server with necessary plugins.
- AWS CLI: AWS CLI configured with appropriate permissions.
- Docker: Docker installed on build agents.

## Jenkins Pipeline
The Jenkins pipeline (Jenkinsfile) is divided into several stages:

1. **Initialization:** Load the Groovy script.
2. **Install Dependencies:** Install Node.js dependencies.
3. **Increment Version:** Increment the application version.
4. **Test Node.js App:** Run tests using Jest.
5. **Build Docker Image:** Build Docker image (only on main branch).
6. **Push Docker Image:** Push Docker image to AWS ECR (only on main branch).
7. **Install Terraform:** Install Terraform on the Jenkins server.
8. **Provision Server:** Use Terraform to provision EC2 instances (only on main branch).
9. **Configure Server:** Configure servers using Ansible (only on main branch).
10. **Version Bump:** Commit and push version updates to the Git repository (only on main branch).

## Ansible Configuration
- **ansible.cfg:** Configures Ansible to use the specified inventory and disable host key checking.
- **inventory_aws_ec2.yaml:** Defines the AWS EC2 instances for Ansible.
- **playbook.yaml:** Ansible playbook to install Docker, Docker Compose, and configure the Node.js application.

## Terraform Configuration
- **main.tf:** Provisions VPC, subnets, internet gateway, route tables, security groups, and EC2 instances.
- **variables.tf:** Defines variables for Terraform.
- **terraform.tfvars:** Sets values for Terraform variables.
- **outputs.tf:** Outputs the public IPs of the provisioned EC2 instances.

## Docker Configuration
- **Dockerfile:** Defines the Docker image for the Node.js application.
- **docker-compose.yaml:** Docker Compose file to run the Node.js application on the EC2 instance.

## Node.js Application
- **server.js:** Node.js server script serving HTML and images.
- **app.test.js:** Contains tests for the Node.js application.
- **index.html:** HTML file served by the application.

## Getting Started

### Step 1: Manually Prepare Ansible Control Node

1. Set up a dedicated server to act as the Ansible control node.
2. Manually install Ansible and Python on this server.
3. Ensure the server has network access to the Jenkins server and the target EC2 instances.
4. Configure SSH keys and permissions to allow Ansible to manage the target EC2 instances.

### Step 2: Configure Jenkins

1. Install Jenkins on a server with sufficient resources.
2. Install the necessary Jenkins plugins:
   - Git Plugin
   - Docker Plugin
   - Ansible Plugin
   - AWS Credentials Plugin
3. Set up the required credentials in Jenkins:
   - **AWS credentials**: For Terraform to provision AWS resources.
   - **Docker repository credentials**: For pushing Docker images.
   - **Git repository credentials**: For accessing your code repository.
   - **SSH credentials**: For connecting to the Ansible control node and the target EC2 instances.
4. Create a new Jenkins pipeline job and link it to your repository containing the `Jenkinsfile`.

### Step 3: Prepare the Repository

1. Clone the repository to your local machine.
2. Ensure the directory structure is correct as per the repository layout described.
3. Verify that all required configuration files (Ansible, Terraform, Docker, Node.js application) are present and correctly set up.
4. Commit and push any changes to the repository.

### Step 4: Jenkins Pipeline Configuration

1. Access your Jenkins instance and navigate to the pipeline job created.
2. Configure the pipeline to use the `Jenkinsfile` from your repository.
3. Ensure that the Jenkinsfile includes stages for:
   - Loading the Groovy script
   - Installing dependencies
   - Running tests
   - Building and pushing Docker images
   - Installing and running Terraform
   - Configuring servers with Ansible

### Step 5: Execute the Pipeline

1. Manually trigger the pipeline from the Jenkins dashboard.
2. Monitor the pipeline execution to ensure each stage completes successfully.
3. If any stage fails, review the logs to diagnose and fix the issues.

### Step 6: Verify Infrastructure and Deployment

1. Once the pipeline completes, verify that the Terraform provisioning has created the necessary AWS infrastructure.
2. Check the Ansible configuration to ensure the EC2 instances are correctly set up.
3. Confirm that the Docker containers are running the Node.js application on the EC2 instances.

### Step 7: Access the Node.js Application

1. Retrieve the public IP addresses of the provisioned EC2 instances from the Terraform output.
2. Open a web browser and navigate to the public IP address of one of the EC2 instances to access the Node.js application at port 3000.
3. Verify that the application is running correctly and serving the expected content.