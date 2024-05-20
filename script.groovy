def incrementVersion() {
    echo "incrementing the application version..."
    sh "npm version patch"
    def version = sh "jq -r '.version' package.json"
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