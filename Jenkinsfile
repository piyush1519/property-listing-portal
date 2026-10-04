pipeline {
    agent any

    parameters {
        string(
            name: 'DEPLOY_ENV',
            defaultValue: 'local',
            description: 'Deployment environment'
        )

        string(
            name: 'DOCKER_PORT',
            defaultValue: '8090',
            description: 'Host port for Docker container'
        )
    }

    environment {
        IMAGE_NAME = 'property-portal'
        CONTAINER_NAME = 'property-portal-container'
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
                powershell '''
                    Write-Host "========================================"
                    Write-Host " Docker Environment Check"
                    Write-Host "========================================"

                    docker --version

                    Write-Host ""
                    Write-Host "Checking Docker daemon..."

                    docker info

                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker daemon is not accessible."
                        exit 1
                    }

                    Write-Host ""
                    Write-Host "Docker environment is ready."
                '''
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

        stage('Docker Build') {
            steps {
                powershell '''
                    Write-Host "========================================"
                    Write-Host " Docker Image Build"
                    Write-Host "========================================"

                    $imageTag = "$env:IMAGE_NAME`:$env:BUILD_NUMBER"

                    Write-Host "Building Docker image:"
                    Write-Host $imageTag

                    docker build `
                        -f docker/Dockerfile `
                        -t $imageTag `
                        .

                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker image build failed."
                        exit 1
                    }

                    Write-Host ""
                    Write-Host "Docker image built successfully."

                    Write-Host ""
                    Write-Host "Image details:"
                    docker images $env:IMAGE_NAME
                '''
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
                    powershell '''
                        Write-Host "========================================"
                        Write-Host " Docker Container Deployment"
                        Write-Host "========================================"

                        $imageTag = "$env:IMAGE_NAME`:$env:BUILD_NUMBER"

                        Write-Host "Image:"
                        Write-Host $imageTag

                        Write-Host ""
                        Write-Host "Container:"
                        Write-Host $env:CONTAINER_NAME

                        Write-Host ""
                        Write-Host "Host Port:"
                        Write-Host $env:DOCKER_PORT

                        Write-Host ""
                        Write-Host "Stopping previous container if it exists..."

                        docker stop $env:CONTAINER_NAME 2>$null

                        Write-Host "Removing previous container if it exists..."

                        docker rm $env:CONTAINER_NAME 2>$null

                        Write-Host ""
                        Write-Host "Starting fresh Docker container..."

                        docker run -d `
                            --name $env:CONTAINER_NAME `
                            -p "$env:DOCKER_PORT`:$env:APP_PORT" `
                            --add-host=host.docker.internal:host-gateway `
                            -e "DB_HOST=host.docker.internal" `
                            -e "DB_PASSWORD=$env:DB_PASSWORD" `
                            $imageTag

                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Docker container failed to start."
                            exit 1
                        }

                        Write-Host ""
                        Write-Host "Docker container started successfully."

                        Write-Host ""
                        Write-Host "Waiting for Spring Boot application..."

                        Start-Sleep -Seconds 15

                        Write-Host ""
                        Write-Host "Container status:"
                        docker ps -a --filter "name=$env:CONTAINER_NAME"

                        Write-Host ""
                        Write-Host "Recent container logs:"
                        docker logs --tail 50 $env:CONTAINER_NAME
                    '''
                }
            }
        }

        stage('Docker Health Check') {
            steps {
                powershell '''
                    Write-Host "========================================"
                    Write-Host " Docker Deployment Health Check"
                    Write-Host "========================================"

                    $url = "http://localhost:$env:DOCKER_PORT/api/properties"

                    Write-Host "Health Check URL:"
                    Write-Host $url

                    $healthy = $false

                    for ($i = 1; $i -le 6; $i++) {

                        Write-Host ""
                        Write-Host "Health check attempt $i of 6..."

                        try {

                            $response = Invoke-WebRequest `
                                -Uri $url `
                                -UseBasicParsing `
                                -TimeoutSec 10

                            Write-Host "HTTP Status: $($response.StatusCode)"

                            if ($response.StatusCode -eq 200) {
                                $healthy = $true
                                Write-Host ""
                                Write-Host "Docker deployment health check PASSED."
                                break
                            }

                        }
                        catch {

                            Write-Host "Application not ready yet."
                            Write-Host $_.Exception.Message
                        }

                        if ($i -lt 6) {
                            Write-Host "Waiting 5 seconds..."
                            Start-Sleep -Seconds 5
                        }
                    }

                    if (-not $healthy) {
                        Write-Error "Docker deployment health check FAILED."

                        Write-Host ""
                        Write-Host "Container logs:"
                        docker logs $env:CONTAINER_NAME

                        exit 1
                    }
                '''
            }
        }

        stage('Docker Deployment Verification') {
            steps {
                powershell '''
                    Write-Host "========================================"
                    Write-Host " Docker Deployment Verification"
                    Write-Host "========================================"

                    Write-Host ""
                    Write-Host "Running containers:"
                    docker ps

                    Write-Host ""
                    Write-Host "Deployed image:"
                    docker inspect `
                        --format='{{.Config.Image}}' `
                        $env:CONTAINER_NAME

                    Write-Host ""
                    Write-Host "Container port mapping:"
                    docker port $env:CONTAINER_NAME

                    Write-Host ""
                    Write-Host "Docker deployment verification completed."
                '''
            }
        }
    }

    post {

        success {
            echo "========================================"
            echo " WEEK 12 CONTINUOUS DELIVERY PASSED"
            echo "========================================"
            echo "Environment: ${params.DEPLOY_ENV}"
            echo "Docker Image: ${env.IMAGE_NAME}:${env.BUILD_NUMBER}"
            echo "Container: ${env.CONTAINER_NAME}"
            echo "Host Port: ${params.DOCKER_PORT}"
            echo "Health Check: PASSED"
            echo "Deployment completed successfully."
        }

        failure {
            echo "========================================"
            echo " BUILD / DEPLOYMENT FAILED"
            echo "========================================"
            echo "Check the failed Jenkins stage and console output."
        }

        always {
            echo "Jenkins pipeline execution completed."
        }
    }
}