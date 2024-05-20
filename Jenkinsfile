#!/usr/bin/env groovy

def gv 

pipeline {
    agent any

    tools {
        nodejs 'nodejs'
    }

    environment {
        DOCKER_REPOSITORY = "590183817498.dkr.ecr.ap-south-1.amazonaws.com"
        IMAGE_NAME = "590183817498.dkr.ecr.ap-south-1.amazonaws.com/nodejs-app"
    }

    stages {
        stage('init') {
            steps {
                script {
                    gv = load "script.groovy"
                    echo "executing pipeline for branch $BRANCH_NAME"
                }
            }
        }

        stage('increment version') {
            steps {
                script {
                    gv.incrementVersion()
                }
            }
        }

        stage('test nodejs app') {
            steps {
                script {
                    gv.testNodeApp()
                }
            }
        }

        stage('build docker image') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    gv.buildDockerImage()
                }
            }
        }

        stage('push docker image') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    gv.pushImageToECR()
                }
            }
        }
    }
}