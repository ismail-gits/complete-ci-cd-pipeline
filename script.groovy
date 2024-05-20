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
        sh "echo $PASSWORD | docker login -u $USER --password-stdin $DOCKER_REPOSITORY"
        sh "docker push $IMAGE_NAME:$IMAGE_VERSION"
        sh "docker push $IMAGE_NAME:latest"
    }
}

return this