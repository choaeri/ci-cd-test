pipeline {
    agent any

    tools {
        jdk 'jdk11'
    }

    environment {
        DOCKER_HUB_USER = 'aericho' 
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // 1. 백엔드 빌드 및 배포 (Java 11 + Gradle)
        stage('Backend: Build & Push') {
            steps {
                dir('ci-cd-test-back') {
                    script {
                        sh "chmod +x gradlew"
                        sh "./gradlew build -x test"

                        // Docker Hub 로그인 및 푸시
                        docker.withRegistry('https://index.docker.io/v1/', 'aericho') {
                            def backImage = docker.build("${DOCKER_HUB_USER}/ci-cd-test-back:latest", ".")
                            backImage.push()
                        }
                    }
                }
            }
        }

        // 2. 프론트엔드 빌드 및 배포 (Vite + Docker)
        stage('Frontend: Build & Push') {
            steps {
                dir('ci-cd-test-front') {
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'aericho') {
                            def frontImage = docker.build("${DOCKER_HUB_USER}/ci-cd-test-front:latest", ".")
                            frontImage.push()
                        }
                    }
                }
            }
        }

        // 3. 로컬 테스트 서버에서 컨테이너 실행
        stage('Local Test Run') {
            steps {
                script {
                    // 기존 컨테이너 정리 후 새 이미지로 실행
                    // 백엔드 실행
                    sh "docker ps -q --filter name=back-container | xargs -r docker stop"
                    sh "docker ps -a -q --filter name=back-container | xargs -r docker rm"
                    sh "docker run -d --name back-container -p 8080:8080 ${DOCKER_HUB_USER}/ci-cd-test-back:latest"

                    // 프론트엔드 실행
                    sh "docker ps -q --filter name=front-container | xargs -r docker stop"
                    sh "docker ps -a -q --filter name=front-container | xargs -r docker rm"
                    sh "docker run -d --name front-container -p 80:80 ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                }
            }
        }
    }
}