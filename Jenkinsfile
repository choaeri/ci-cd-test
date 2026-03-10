pipeline {
    agent any

    tools {
        jdk 'jdk11'
        dockerTool 'docker' 
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
                        // 프론트엔드도 로그인이 필요하다면 push 전에 로그인 체크
                        sh "docker build -t ${DOCKER_HUB_USER}/ci-cd-test-front:latest ."
                        sh "docker push ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                    }
                }
            }
        }

        stage('Local Deployment') {
            steps {
                script {
                    // 기존 컨테이너 정리 시 에러 무시
                    sh "docker network create my-network || true"
                    
                    // 백엔드 재시작
                    sh "docker rm -f back-container || true"
                    sh "docker run -d --name back-container --network my-network -p 8081:8080 ${DOCKER_HUB_USER}/ci-cd-test-back:latest"

                    // 프론트엔드 재시작
                    sh "docker rm -f front-container || true"
                    sh "docker run -d --name front-container --network my-network -p 80:80 ${DOCKER_HUB_USER}/ci-cd-test-front:latest"
                }
            }
        }
    }
}