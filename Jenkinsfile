pipeline {
    agent any

    tools {
        jdk 'jdk11'
        // 추가: 위에서 설정한 Name과 동일하게 입력
        docker 'docker' 
    }

    environment {
        DOCKER_HUB_USER = 'aericho'
        DOCKER_ID = 'aericho'
    }

    stages {
        stage('Backend: Build & Push') {
            steps {
                dir('ci-cd-test-back') {
                    script {
                        sh "chmod +x gradlew"
                        sh "./gradlew build -x test"

                        withCredentials([usernamePassword(credentialsId: "${DOCKER_ID}", usernameVariable: 'D_USER', passwordVariable: 'D_PASS')]) {
                            // 이제 docker 명령어가 인식됩니다!
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