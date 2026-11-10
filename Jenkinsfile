pipeline {
    agent any

    environment {
        SERVICE = 'gateway'
    }

    stages {
        stage('Dependencies') {
            steps {
                build job: 'auth', wait: true
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Build & Push Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credential',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'TOKEN'
                )]) {
                    sh '''
                        echo "$TOKEN" | docker login -u "$USERNAME" --password-stdin
                        docker buildx create --use \
                          --platform=linux/arm64,linux/amd64 \
                          --node "multi-platform-builder-$SERVICE" \
                          --name "multi-platform-builder-$SERVICE"
                        docker buildx build \
                          --platform=linux/arm64,linux/amd64 \
                          --push \
                          --tag "$USERNAME/$SERVICE:latest" \
                          --tag "$USERNAME/$SERVICE:$BUILD_ID" \
                          -f Dockerfile .
                        docker buildx rm --force "multi-platform-builder-$SERVICE"
                    '''
                }
            }
        }
    }
}
