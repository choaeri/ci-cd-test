pipeline {
    agent any

    tools {
        jdk 'jdk11'
    }

    environment {
        DOCKER_HUB_USER = 'aericho'
        DOCKER_ID = 'aericho'
        DOCKER_HOST = 'tcp://127.0.0.1:2375'
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
                          sh "echo \$D_PASS | docker -H ${DOCKER_HOST} login -u \$D_USER --password-stdin"
                          sh "docker -H ${DOCKER_HOST} build -t ${DOCKER_HUB_USER}/ci-cd-test-back:latest ."
                          sh "docker -H ${DOCKER_HOST} push ${DOCKER_HUB_USER}/ci-cd-test-back:latest"
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
                    // 기존 컨테이너가 없어도 에러나지 않게 || true 추가
                    sh "docker stop back-container || true"
                    sh "docker rm back-container || true"
                    sh "docker run -d --name back-container -p 8080:8080 ${DOCKER_HUB_USER}/ci-cd-test-back:latest"

                    sh "docker stop front-container || true"
                    sh "docker rm front-container || true"
                    sh "docker run -d --name front-container -p 80:80 ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                }
            }
        }
    }
}