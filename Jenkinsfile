pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }
    }

    post {
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo ' Échec du build, vérifie les logs.'
        }
    }
}