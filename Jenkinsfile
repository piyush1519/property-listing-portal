pipeline {
    agent any

    parameters {
        string(
            name: 'DEPLOY_ENV',
            defaultValue: 'docker',
            description: 'Deployment environment'
        )

        string(
            name: 'DOCKER_PORT',
            defaultValue: '8090',
            description: 'Host port for Docker application'
        )
    }

    environment {
        IMAGE_NAME = 'property-portal'
        CONTAINER_NAME = 'property-portal-container'
        MYSQL_CONTAINER = 'property-portal-mysql'
        APP_PORT = '8080'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Environment Check') {
            steps {
                bat '''
                    echo ===== Docker Version =====
                    docker --version

                    echo ===== Docker Info =====
                    docker info
                '''
            }
        }

        stage('Start Docker MySQL') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'mysql-db-password',
                        variable: 'MYSQL_ROOT_PASSWORD'
                    )
                ]) {
                    bat '''
                        echo ===== Starting Docker MySQL =====

                        set MYSQL_ROOT_PASSWORD=%MYSQL_ROOT_PASSWORD%

                        docker compose up -d mysql

                        echo ===== Waiting for MySQL to become healthy =====

                        powershell -NoProfile -Command ^
                          "$timeout = 120; $elapsed = 0; while ($elapsed -lt $timeout) { $status = docker inspect -f '{{.State.Health.Status}}' property-portal-mysql 2>$null; Write-Host ('MySQL health: ' + $status); if ($status -eq 'healthy') { exit 0 }; Start-Sleep -Seconds 5; $elapsed += 5 }; Write-Error 'MySQL did not become healthy'; exit 1"
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                bat '''
                    echo ===== Maven Build =====

                    mvn -f backend/pom.xml clean compile
                '''
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
                    bat '''
                        echo ===== Running Tests =====
                        echo Tests will use Docker MySQL through localhost:3307

                        set DB_HOST=localhost
                        set DB_PORT=3307
                        set DB_USERNAME=root
                        set DB_PASSWORD=%DB_PASSWORD%

                        mvn -f backend/pom.xml test
                    '''
                }
            }
        }

        stage('Package') {
            steps {
                bat '''
                    echo ===== Packaging Application =====

                    mvn -f backend/pom.xml package -DskipTests
                '''
            }
        }

        stage('Archive Artifact') {
            steps {
                echo '===== Archiving JAR ====='

                archiveArtifacts artifacts: 'backend/target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    def imageTag = "${env.IMAGE_NAME}:${env.BUILD_NUMBER}"

                    bat """
                        echo ===== Building Docker Image =====

                        docker build ^
                          -f docker/Dockerfile ^
                          -t ${imageTag} ^
                          .
                    """
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'mysql-db-password',
                        variable: 'DB_PASSWORD'
                    )
                ]) {
                    script {
                        def imageTag = "${env.IMAGE_NAME}:${env.BUILD_NUMBER}"

                        bat """
                            echo ===== Stopping Previous Application Container =====

                            docker rm -f ${env.CONTAINER_NAME} 2>nul || exit /b 0

                            echo ===== Starting New Application Container =====

                            docker run -d ^
                              --name ${env.CONTAINER_NAME} ^
                              --network property-listing-portal_default ^
                              -p ${params.DOCKER_PORT}:${env.APP_PORT} ^
                              -e DB_HOST=mysql ^
                              -e DB_PORT=3306 ^
                              -e DB_USERNAME=root ^
                              -e DB_PASSWORD=%DB_PASSWORD% ^
                              ${imageTag}

                            echo ===== Waiting For Application Startup =====

                            powershell -NoProfile -Command "Start-Sleep -Seconds 15"

                            echo ===== Container Status =====

                            docker ps -a --filter "name=${env.CONTAINER_NAME}"

                            echo ===== Application Logs =====

                            docker logs ${env.CONTAINER_NAME}
                        """
                    }
                }
            }
        }

        stage('Docker Health Check') {
            steps {
                script {
                    bat """
                        echo ===== Checking Application Health =====

                        powershell -NoProfile -Command ^
                          "\\$url = 'http://localhost:${params.DOCKER_PORT}/api/properties'; \\$success = \\$false; for (\\$i = 1; \\$i -le 12; \\$i++) { try { \\$response = Invoke-WebRequest -Uri \\$url -UseBasicParsing -TimeoutSec 5; Write-Host ('Attempt ' + \\$i + ': HTTP ' + \\$response.StatusCode); if (\\$response.StatusCode -eq 200) { \\$success = \\$true; break } } catch { Write-Host ('Attempt ' + \\$i + ': application not ready') }; Start-Sleep -Seconds 5 }; if (-not \\$success) { Write-Error 'Docker deployment health check failed'; exit 1 }"

                        echo ===== Docker Deployment Health Check Passed =====
                    """
                }
            }
        }

        stage('Docker Deployment Verification') {
            steps {
                bat '''
                    echo ===== Docker Containers =====

                    docker ps

                    echo ===== Application Container =====

                    docker inspect property-portal-container --format "{{.Config.Image}}"

                    echo ===== Port Mapping =====

                    docker port property-portal-container

                    echo ===== Application API =====

                    powershell -NoProfile -Command ^
                      "Invoke-WebRequest -Uri 'http://localhost:8090/api/properties' -UseBasicParsing | Select-Object StatusCode"
                '''
            }
        }
    }

    post {

        success {
            echo "=============================================="
            echo "DOCKER CONTINUOUS DELIVERY SUCCESS"
            echo "=============================================="
            echo "Application deployed successfully."
            echo "Environment: ${params.DEPLOY_ENV}"
            echo "Application URL: http://localhost:${params.DOCKER_PORT}/api/properties"
            echo "Docker Image: ${env.IMAGE_NAME}:${env.BUILD_NUMBER}"
        }

        failure {
            echo "=============================================="
            echo "DOCKER CONTINUOUS DELIVERY FAILED"
            echo "=============================================="
            echo "Check the Jenkins console output for the failed stage."
        }

        always {
            echo "Jenkins Docker pipeline completed."
        }
    }
}