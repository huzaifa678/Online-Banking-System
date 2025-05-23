pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/huzaifa678/Online-Banking-System.git'
        BRANCH = 'CI/CD'
        PATH = "/usr/local/bin:${env.PATH}"
        DOCKER_REGISTRY = 'huzaifagill234'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_TOKEN_ID = 'docker-token'
        KUBECONFIG_PATH = 'KUBE-CONFIG'
        HELM_CHART_PATH = './k8s/applications-chart'
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -Dorg.slf4j.simpleLogger.showDateTime=true -Djava.awt.headless=true'
        SPRING_PROFILES_ACTIVE = 'test'
    }

    tools {
        maven 'MAVEN'
    }

    stages {

        stage('Debug') {
               steps {
                   sh 'env | sort'
                   sh 'mvn -v'
                   sh 'java -version'
                   sh 'docker version'
               }
        }

        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Maven Build') {
            steps {
                script {
                    withCredentials([string(credentialsId: "${DOCKER_TOKEN_ID}", variable: 'DOCKER_TOKEN')]) {
                        withMaven(maven: 'MAVEN') {
                            sh "mvn clean install -DskipTests -DdockerPassword=${DOCKER_TOKEN}"
                        }
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    withCredentials([string(credentialsId: "${DOCKER_TOKEN_ID}", variable: 'DOCKER_TOKEN')]) {
                        withMaven(maven: 'MAVEN') {
                            sh "mvn test -pl account-service -DdockerPassword=${DOCKER_TOKEN}"
                        }
                    }

                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'

                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '**/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    withCredentials([
                        usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}",
                                         usernameVariable: 'DOCKER_USERNAME',
                                         passwordVariable: 'DOCKER_PASSWORD'),
                        string(credentialsId: "${DOCKER_TOKEN_ID}", variable: 'DOCKER_TOKEN')
                    ]) {
                        sh """
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                            mvn spring-boot:build-image -DskipTests -DdockerPassword=${DOCKER_TOKEN} \
                                -Dspring-boot.build-image.imageName=$DOCKER_USERNAME/backend:${BUILD_NUMBER}

                            docker push "$DOCKER_USERNAME/backend:${BUILD_NUMBER}"
                        """

                        sh """
                            cd ./frontend/my-app
                            docker build -t "$DOCKER_USERNAME"/frontend:${BUILD_NUMBER} .
                            docker push "$DOCKER_USERNAME"/frontend:${BUILD_NUMBER}
                        """
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_PATH')]) {
                        sh """
                            export KUBECONFIG=${KUBECONFIG_PATH}
                            helm dependency update ${HELM_CHART_PATH}
                            helm upgrade --install online-banking-system ${HELM_CHART_PATH} \
                                --set backend.image.repository=${DOCKER_REGISTRY}/backend \
                                --set backend.image.tag=${BUILD_NUMBER} \
                                --set frontend.image.repository=${DOCKER_REGISTRY}/frontend \
                                --set frontend.image.tag=${BUILD_NUMBER}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}