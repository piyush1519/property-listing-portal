pipeline {

    agent any

    options {
        skipDefaultCheckout(true)
    }

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
                    echo ==========================================
                    echo Docker Environment Check
                    echo ==========================================

                    docker --version
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
                        echo ==========================================
                        echo Starting Docker MySQL
                        echo ==========================================

                        set MYSQL_ROOT_PASSWORD=%MYSQL_ROOT_PASSWORD%

                        docker compose -f docker-compose.yml up -d mysql

                        if errorlevel 1 exit /b 1

                        echo.
                        echo Waiting for MySQL to become healthy...
                        echo.

                        powershell -NoProfile -Command ^
                          "$timeout = 120; $elapsed = 0; while ($elapsed -lt $timeout) { $status = docker inspect -f '{{.State.Health.Status}}' property-portal-mysql 2>$null; Write-Host ('MySQL health: ' + $status); if ($status -eq 'healthy') { exit 0 }; Start-Sleep -Seconds 5; $elapsed += 5 }; Write-Error 'MySQL did not become healthy within 120 seconds'; exit 1"
                    '''
                }
            }
        }

        stage('Build') {
            steps {

                bat '''
                    echo ==========================================
                    echo Maven Build
                    echo ==========================================

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
                        echo ==========================================
                        echo Running Tests
                        echo ==========================================

                        echo.
                        echo Database:
                        echo Host = localhost
                        echo Port = 3307
                        echo Database = property_portal
                        echo.

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
                    echo ==========================================
                    echo Packaging Application
                    echo ==========================================

                    mvn -f backend/pom.xml package -DskipTests
                '''
            }
        }

        stage('Archive Artifact') {
            steps {

                echo '=========================================='
                echo 'Archiving JAR Artifact'
                echo '=========================================='

                archiveArtifacts artifacts: 'backend/target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {

                script {

                    def imageTag = "${env.IMAGE_NAME}:${env.BUILD_NUMBER}"

                    bat """
                        echo ==========================================
                        echo Building Docker Image
                        echo ==========================================
                        echo Image: ${imageTag}
                        echo.

                        docker build -f docker/Dockerfile -t ${imageTag} .
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
                            echo ==========================================
                            echo Docker Deployment
                            echo ==========================================
                            echo Image: ${imageTag}
                            echo Container: ${env.CONTAINER_NAME}
                            echo Port: ${params.DOCKER_PORT}
                            echo.

                            echo Stopping old application container...

                            docker rm -f ${env.CONTAINER_NAME} 2>nul || echo No existing application container found.

                            echo.

                            echo Starting new application container...
                            echo.

                            docker run -d --name ${env.CONTAINER_NAME} --network property-listing-portal-pipeline_default -p ${params.DOCKER_PORT}:${env.APP_PORT} -e DB_HOST=mysql -e DB_PORT=3306 -e DB_USERNAME=root -e DB_PASSWORD=%DB_PASSWORD% ${imageTag}

                            echo.

                            echo Waiting for application startup...

                            powershell -NoProfile -Command "Start-Sleep -Seconds 15"

                            echo.

                            echo Container Status:

                            docker ps -a --filter "name=${env.CONTAINER_NAME}"

                            echo.

                            echo Application Logs:

                            docker logs ${env.CONTAINER_NAME}
                        """
                    }
                }
            }
        }

        stage('Docker Health Check') {
            steps {

                script {

                    writeFile file: 'health-check.ps1', text: """

\$url = 'http://localhost:${params.DOCKER_PORT}/api/properties'

\$success = \$false

Write-Host '=========================================='
Write-Host 'Docker Application Health Check'
Write-Host '=========================================='

Write-Host "URL: \$url"

Write-Host ''

for (\$i = 1; \$i -le 12; \$i++) {

    try {

        \$response = Invoke-WebRequest `
            -Uri \$url `
            -UseBasicParsing `
            -TimeoutSec 5

        \$statusCode = \$response.StatusCode

        Write-Host "Attempt \$i : HTTP \$statusCode"

        if (\$statusCode -eq 200) {

            \$success = \$true

            break
        }
    }

    catch {

        Write-Host "Attempt \$i : application not ready"
    }

    Start-Sleep -Seconds 5
}

if (-not \$success) {

    Write-Error 'Docker deployment health check failed'

    exit 1
}

Write-Host ''

Write-Host 'Docker deployment health check passed.'

"""

                    bat '''
                        powershell -NoProfile -ExecutionPolicy Bypass -File health-check.ps1
                    '''
                }
            }
        }

        stage('Docker Deployment Verification') {
            steps {

                bat """

                    echo ==========================================
                    echo Docker Deployment Verification
                    echo ==========================================

                    echo.

                    echo ===== Docker Containers =====

                    echo.

                    docker ps

                    echo.

                    echo ===== Application Container =====

                    echo.

                    docker inspect property-portal-container --format "{{.Config.Image}}"

                    echo.

                    echo ===== Port Mapping =====

                    echo.

                    docker port property-portal-container

                    echo.

                    echo ===== Application API =====

                    echo.

                    powershell -NoProfile -Command "Invoke-WebRequest -Uri 'http://localhost:${params.DOCKER_PORT}/api/properties' -UseBasicParsing | Select-Object StatusCode"

                """
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

            echo "=============================================="
        }

        failure {

            echo "=============================================="
            echo "DOCKER CONTINUOUS DELIVERY FAILED"
            echo "=============================================="

            echo "Check the Jenkins console output for the failed stage."

            echo "=============================================="
        }

        always {

            echo "Jenkins Docker pipeline completed."
        }
    }
}