pipeline {
    agent any

    stages {
        stage('Prepare') {
            steps {
                echo 'Checking out code...'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Build & Run') {
            steps {
                echo 'Building Docker Image...'
                sh 'docker build -t my-app:latest .'
                
                echo 'Running Container...'
                sh 'docker stop my-app || true'
                sh 'docker rm my-app || true'
                sh 'docker run -d --name my-app -p 8081:8080 my-app:latest'
            }
        }
    }
}