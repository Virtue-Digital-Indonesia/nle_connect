#!/usr/bin/env groovy

pipeline {
    agent any
    environment {
        shortGitCommit = env.GIT_COMMIT.take(7)
    }
    stages {

        stage('check java') {
            steps {
                script {
                    sh "java -version"
                }
            }
        }

        stage('Bump Version') {
            steps {
                script {
                    sh "./mvnw versions:set -DnewVersion=${shortGitCommit}"
                }
            }
        }

        stage('clean') {
            steps {
                script {
                    sh "chmod +x mvnw"
                    sh "./mvnw -ntp clean"
                }
            }
        }

        stage('update secret') {
            steps {
                withCredentials([
                    string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'TRIGGER_URL', variable: 'TRIGGER_URL'),
                    string(credentialsId: 'TRIGGER_TOKEN', variable: 'TRIGGER_TOKEN'),
                    usernamePassword(credentialsId: 'FTPCredentials', passwordVariable: 'FTP_PASSWORD', usernameVariable: 'FTP_USERNAME')
                    ]) {
                    sh """
                        cd src/main/resources
                        export DB_PASSWORD=$DB_PASSWORD
                        export FTP_USERNAME=$FTP_USERNAME
                        export FTP_PASSWORD=$FTP_PASSWORD
                        export TRIGGER_URL=$TRIGGER_URL
                        export TRIGGER_TOKEN=$TRIGGER_TOKEN
                        envsubst < application.yml > application_tmp.yml
                        mv application_tmp.yml application.yml
                    """
                }
            }
        }

        stage('Packaging') {
            steps {
                script {
                    sh "./mvnw clean install -DskipTests"
                }
            }
        }

        stage('Build docker image & update compose file') {
            steps {
                withCredentials([string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD')]) {
                    sh """
                        cp Dockerfile target/
                        cd target/
                        docker image build --build-arg JAR_FILE=nlebackend.jar -t nlebackend:${shortGitCommit} .
                        cd ../
                        export VERSION=${shortGitCommit}
                        export DB_PASSWORD=$DB_PASSWORD
                        cd src/main/docker/
                        envsubst < docker-compose-template.yml > docker-compose.yml
                    """
                }
            }
        }

        stage('Stop current backend') {
            steps {
                script {
                    sh """
                        cd src/main/docker/
                        docker-compose down
                    """
                }
            }
        }

        stage('Start backend with new version') {
            steps {
                script {
                    sh """
                        cd src/main/docker/
                        docker-compose up -d
                    """
                }
            }
        }
    }
}
