pipeline {
    agent any
    
    tools {
        jdk 'jdk11'
        dockerTool 'docker'
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
                sh 'docker stop ci-cd-app || true'
                sh 'docker rm ci-cd-app || true'
                sh 'docker run -d --name ci-cd-app -p 8081:8080 ci-cd-test:latest'
            }
        }
    }
}