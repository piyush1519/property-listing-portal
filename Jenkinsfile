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

        stage('Deploy') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'mysql-db-password',
                        variable: 'DB_PASSWORD'
                    )
                ]) {
                    bat '''
                        echo Deploying environment: %DEPLOY_ENV%

                        if not exist "C:\\property-portal-deploy" mkdir "C:\\property-portal-deploy"

                        echo Stopping previous application...
                        powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-CimInstance Win32_Process -Filter \\"Name = 'java.exe'\\" | Where-Object { $_.CommandLine -like '*backend-0.0.1-SNAPSHOT.jar*' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }"

                        timeout /t 3 /nobreak >nul

                        echo Copying new application...
                        copy /Y "backend\\target\\backend-0.0.1-SNAPSHOT.jar" "C:\\property-portal-deploy\\backend.jar"

                        echo Starting Spring Boot application...
                        start "" /B powershell -NoProfile -ExecutionPolicy Bypass -Command "$env:DB_PASSWORD='%DB_PASSWORD%'; java -jar 'C:\\property-portal-deploy\\backend.jar' --server.port=8080 > 'C:\\property-portal-deploy\\backend.log' 2>&1"

                        echo Waiting for application startup...
                        timeout /t 15 /nobreak >nul
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                powershell '''
                    $url = "http://localhost:8088/api/properties"

                    Write-Host "Checking deployed application through Nginx..."
                    Write-Host "URL: $url"

                    try {
                        $response = Invoke-WebRequest `
                            -Uri $url `
                            -UseBasicParsing `
                            -TimeoutSec 10

                        Write-Host "HTTP Status: $($response.StatusCode)"

                        if ($response.StatusCode -ne 200) {
                            throw "Health check failed with HTTP status $($response.StatusCode)"
                        }

                        Write-Host "Deployment health check passed."
                    }
                    catch {
                        Write-Error "Deployment health check failed: $($_.Exception.Message)"
                        exit 1
                    }
                '''
            }
        }
    }

    post {
        success {
            echo "Deployment completed successfully for ${params.DEPLOY_ENV}"
        }

        failure {
            echo "Build or deployment failed"
        }
    }
}
