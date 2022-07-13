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
                    sh "./mvnw -ntp clean -P-webapp"
                }
            }
        }

        stage('nohttp') {
            steps {
                script {
                    sh "./mvnw -ntp checkstyle:check"
                }
            }
        }

        stage('Backend Tests') {
            steps {
                script {
                    try {
                        sh "./mvnw -ntp verify -P-webapp"
                    } catch(err) {
                        throw err
                    } finally {
                        junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'
                    }
                }
            }
        }

        stage('Packaging') {
            steps {
                script {
                    sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    sh """
                        cp Dockerfile target/
                        cd target/
                        docker image build --build-arg JAR_FILE=nlebackend.jar -t nlebackend:${shortGitCommit} .
                    """
                }
            }
        }

        stage('Stop current backend') {
            steps {
                script {
                    sh """
                        docker ps -aq --filter "name=nlebackend" | grep -q . && docker stop nlebackend && docker rm -fv nlebackend
                    """
                }
            }
        }

        stage('Start backend with new version') {
            steps {
                script {
                    sh "docker container run --name nlebackend -p 8081:8080 nlebackend:${shortGitCommit}"
                }
            }
        }
    }
}
