pipeline {
    agent any

    parameters {
        string(
            name: 'DEPLOY_ENV',
            defaultValue: 'local',
            description: 'Deployment environment'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -f backend/pom.xml clean compile'
            }
        }

        stage('Test') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'mysql-db-password',
                        variable: 'DB_PASSWORD'
                    )
                ]) {
                    bat 'mvn -f backend/pom.xml test'
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -f backend/pom.xml package -DskipTests'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts(
                    artifacts: 'backend/target/*.jar',
                    fingerprint: true
                )
            }
        }
    }

    post {
        success {
            echo "Build completed successfully for ${params.DEPLOY_ENV}"
        }

        failure {
            echo "Build failed"
        }
    }
}
