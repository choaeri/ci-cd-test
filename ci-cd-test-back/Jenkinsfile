pipeline {
    agent any
    
    tools {
        jdk 'jdk11'
    }

    stages {
        stage('Prepare') {
            steps {
				sh 'docker stop ci-cd-app || true'
                sh 'docker rm ci-cd-app || true'
                // 네트워크 생성 (DB와 연결용)
                sh 'docker network create my-network || true'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Deploy') {
            steps {
				sh 'docker stop ci-cd-app || true'
        		sh 'docker rm ci-cd-app || true'
        		
                sh 'docker build -t ci-cd-test:latest .'
                
                sh 'docker run -d --name ci-cd-app --network my-network -p 8081:8080 ci-cd-test:latest'
            }
        }
    }
}