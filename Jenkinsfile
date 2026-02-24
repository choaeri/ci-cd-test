pipeline {
    agent any
    
    tools {
        jdk 'jdk11'
    }

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
                sh 'docker build -t ci-cd-test:latest .'
                
                echo 'Running Container...'
                sh 'docker stop jenkins || true'
                sh 'docker rm jenkins || true'
                sh 'docker run -d --name jenkins -p 8081:8080 ci-cd-test:latest'
            }
        }
    }
}