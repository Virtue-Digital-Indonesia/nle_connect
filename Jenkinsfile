#!/usr/bin/env groovy

node {
    environment {
        shortGitCommit = env.GIT_COMMIT.take(7)
    }

    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh "java -version"
    }

    stage('Bump Version') {
        steps {
            script {
                sh "./mvnw versions:set -DnewVersion=${shortGitCommit}"
            }
        }
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw -ntp clean -P-webapp"
    }

    stage('nohttp') {
        sh "./mvnw -ntp checkstyle:check"
    }

    stage('Backend Tests') {
        try {
            sh "./mvnw -ntp verify -P-webapp"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'
        }
    }

    stage('Packaging') {
        sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('Build docker image') {
        sh "docker image build --build-arg JAR_FILE=nlebackend-${shortGitCommit}.jar -t nlebackend:${shortGitCommit} ."
    }

    stage('Stop current backend') {
        sh """
            docker container stop nlebackend
            docker container rm nlebackend
        """
    }

    stage('Start backend with new version') {
        sh "docker container run --name nlebackend -p 8081:8080 nlebackend:${shortGitCommit}"
    }

}
