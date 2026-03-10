pipeline {
    agent any

    tools {
        jdk 'jdk11'
    }

    environment {
        DOCKER_HUB_USER = 'aericho'
        DOCKER_ID = 'aericho'
        DOCKER_HOST = 'tcp://192.168.50.1:2375'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend: Build & Push') {
            steps {
                dir('ci-cd-test-back') {
                    script {
                        sh "chmod +x gradlew"
                        sh "./gradlew build -x test"

                        // 플러그인 문법 대신 sh 명령어로 직접 처리
                        withCredentials([usernamePassword(credentialsId: "${DOCKER_ID}", usernameVariable: 'D_USER', passwordVariable: 'D_PASS')]) {
                          sh "echo \$D_PASS | docker login -u \$D_USER --password-stdin"
                          sh "docker build -t ${DOCKER_HUB_USER}/ci-cd-test-back:latest ."
                          sh "docker push ${DOCKER_HUB_USER}/ci-cd-test-back:latest"
                      }
                    }
                }
            }
        }

        stage('Frontend: Build & Push') {
            steps {
                dir('ci-cd-test-front') {
                    script {
                        withCredentials([usernamePassword(credentialsId: "${DOCKER_ID}", usernameVariable: 'D_USER', passwordVariable: 'D_PASS')]) {
                            sh "docker build -t ${DOCKER_HUB_USER}/ci-cd-test-front:latest ."
                            sh "docker push ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                        }
                    }
                }
            }
        }

        stage('Local Test Run') {
            steps {
                script {
                    // 1. 네트워크 생성 (이미 있으면 무시)
                    sh "docker network create my-network || true"

                    // 2. 백엔드 컨테이너 실행
                    sh "docker stop back-container || true"
                    sh "docker rm back-container || true"
                    // 말씀하신 대로 직관적인 docker run 명령어 사용
                    sh "docker run -d --name back-container --network my-network -p 8081:8080 ${DOCKER_HUB_USER}/ci-cd-test-back:latest"

                    // 3. 프론트엔드 컨테이너 실행
                    sh "docker stop front-container || true"
                    sh "docker rm front-container || true"
                    sh "docker run -d --name front-container --network my-network -p 80:80 ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                }
            }
        }
    }
}