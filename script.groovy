def incrementVersion() {
    echo "Incrementing the application version..."

    sh "git config --global user.email jenkins@gmail.com"
    sh "git config --global user.name jenkins"
    
    // Increment the version using npm
    sh "npm version patch"
    
    // Read the updated version from package.json
    def version = sh(script: 'cat package.json | jq -r ".version"', returnStdout: true).trim()
    
    echo "Updated version: $version"
    env.IMAGE_VERSION = "$version-$BUILD_NUMBER"
}

def installDependencies() {
    echo "installing dependencies of nodejs application..."
    sh "npm install"
}

def testNodeApp() {
    echo "testing the nodejs application..."
    sh "npm test"
}

def buildDockerImage() {
    echo "building the docker image..."
    sh "docker build -t $IMAGE_NAME:$IMAGE_VERSION ."
    sh "docker tag $IMAGE_NAME:$IMAGE_VERSION $IMAGE_NAME:latest"
}

def pushImageToECR() {
    echo "pushing docker image to ECR private registry..."
    withCredentials([usernamePassword(
        credentialsId: 'aws-ecr-credentials',
        usernameVariable: 'USER',
        passwordVariable: 'PASSWORD'
    )]) {
        sh 'echo $PASSWORD | docker login -u $USER --password-stdin $DOCKER_REPOSITORY'
        sh "docker push $IMAGE_NAME:$IMAGE_VERSION"
        sh "docker push $IMAGE_NAME:latest"
    }
}

def provisionServer() {
    echo "provisioning ec2 servers using terraform..."
    dir("terraform") {
        sh "terraform init"
        sh "terraform apply --auto-approve"

        env.SERVER_PUBLIC_IP_1 = sh(
            script: "terraform output ec2_public_ip-1",
            returnStdout: true
        ).trim()
        env.SERVER_PUBLIC_IP_2 = sh(
            script: "terraform output ec2_public_ip-2",
            returnStdout: true
        ).trim()
    }

    echo "Server-1 public ip: $SERVER_PUBLIC_IP_1"
    echo "Server-2 public ip: $SERVER_PUBLIC_IP_2"

    // echo "waiting for EC2 server to initialize..."
    // sleep(time: 60, unit: "SECONDS")
}

def configureServers() {
    echo "executing ansible-playbook to configure servers..."

    echo "copying all necessary files to ansible control node server"

    sshagent(['ansible-server-key']) {
        sh "scp -o StrictHostKeyChecking=no ansible/* ec2-user@${ANSIBLE_SERVER}:~/"

        withCredentials([sshUserPrivateKey(
            credentialsId: 'ansible-server-key',
            keyFileVariable: 'keyfile',
            usernameVariable: 'user'
        )]) {
        sh 'scp $keyfile ec2-user@$ANSIBLE_SERVER:~/ansible-jenkins.pem'
        }
    }

    def remote = [:]
    remote.name = "ansible"
    remote.host = env.ANSIBLE_SERVER
    remote.allowAnyHosts = true

    withCredentials([sshUserPrivateKey(
        credentialsId: 'ansible-server-key', 
        keyFileVariable: 'keyfile',
        usernameVariable: 'user'
    )]) {
        remote.user = user
        remote.identityFile = keyfile

        sshCommand remote: remote, command: "ls -l"
        sshScript remote: remote, script: "prepare-ansible-server.sh"
        sshCommand remote: remote, command: "ansible-inventory --graph"
        sshCommand remote: remote, command: "ansible-playbook playbook.yaml"
    }
}

def versionBump() {
    echo "committing version update to git repository..."

    sh "git status"
    sh "git branch"

    withCredentials([usernamePassword(
        credentialsId: '<git-credentials>',
        usernameVariable: 'USER',
        passwordVariable: 'PASSWORD'
    )]) {
        sh "git remote set-url origin https://$USER:$PASSWORD@<git-repo-url>"
    }

    sh "git add ."
    sh "git commit -m 'jenkins-ci: version bump'"
    sh "git push origin HEAD:main"
}

return this