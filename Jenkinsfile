pipeline {
    agent any

    tools {
        jdk 'jdk11'
    }

    environment {
        DOCKER_HUB_USER = 'aericho'
        DOCKER_CRED_ID = 'aericho' 
    }

    stages {
        stage('Backend: Build & Push') {
            steps {
                dir('ci-cd-test-back') {
                    script {
                        sh "chmod +x gradlew"
                        sh "./gradlew clean build -x test" // clean 추가 추천

                        withCredentials([usernamePassword(credentialsId: "${DOCKER_CRED_ID}", usernameVariable: 'D_USER', passwordVariable: 'D_PASS')]) {
                            sh "docker login -u \$D_USER -p \$D_PASS"
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
                            sh "docker login -u \$D_USER -p \$D_PASS"
                            sh "docker build -t ${DOCKER_HUB_USER}/ci-cd-test-front:latest ."
                            sh "docker push ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                        }
                    }
                }
            }
        }

        stage('Local Deployment') {
            steps {
                script {
                    sh "docker network create my-network || true"

                    // 백엔드 실행
                    sh "docker stop back-container || true"
                    sh "docker rm back-container || true"
                    sh "docker run -d --name back-container --network my-network -p 8081:8080 ${DOCKER_HUB_USER}/ci-cd-test-back:latest"

                    // 프론트엔드 실행
                    sh "docker stop front-container || true"
                    sh "docker rm front-container || true"
                    sh "docker run -d --name front-container --network my-network -p 80:80 ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                }
            }
        }
    }
}