pipeline {
    agent any

    environment {
        IMAGE_NAME = "wellnesshub-backend"
        IMAGE_TAG = "latest" // You can also use ${BUILD_NUMBER}
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build Docker image using Dockerfile
                bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }
    }

    post {
        success {
            echo '✅ Build, tests, and Docker image succeeded!'
        }
        failure {
            echo '❌ Build or tests failed. Check logs!'
        }
    }
}
