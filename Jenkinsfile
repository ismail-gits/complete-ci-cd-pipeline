#!/usr/bin/env groovy

def gv 

pipeline {
    agent any

    tools {
        nodejs 'nodejs'
    }

    environment {
        DOCKER_REPOSITORY = "<docker-repo>"
        IMAGE_NAME = "<image-name>"
        ANSIBLE_SERVER = "<ansible-control-node-server>"
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

        stage('install dependencies') {
            steps {
                script {
                    gv.installDependencies()
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

        stage('provision server') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
            environment {
                AWS_ACCESS_KEY_ID = credentials('aws_access_key_id')
                AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
            }
            steps {
                script {
                    gv.provisionServer()
                }
            }
        }

        stage('configure server') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    gv.configureServers()
                }
            }
        }

        stage('version bump') {
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    gv.versionBump()
                }
            }
        }
    }
}