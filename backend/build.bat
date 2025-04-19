@echo off
SETLOCAL EnableDelayedExpansion

echo [INFO] Starting Maven project and Docker image build...

REM Check pom.xml location
if exist "pom.xml" (
    set "POM_PATH=pom.xml"
    set "DOCKERFILE_PATH=Dockerfile"
    set "CONTAINER_WORKDIR=/app"
    set "JAR_DIR=target"
    set "DOCKER_BUILD_DIR=."
) else if exist "backend\pom.xml" (
    set "POM_PATH=backend\pom.xml"
    set "DOCKERFILE_PATH=backend\Dockerfile"
    set "CONTAINER_WORKDIR=/app/backend" 
    set "JAR_DIR=backend\target"
    set "DOCKER_BUILD_DIR=backend"
) else (
    echo [ERROR] Cannot find pom.xml file
    goto :end
)

echo [INFO] Using pom.xml: %POM_PATH%
echo [INFO] Using Dockerfile: %DOCKERFILE_PATH%

REM Check if Docker is installed
docker --version > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker not found. Make sure Docker Desktop is installed and running
    goto :end
)

echo [INFO] Extracting Maven project information...

REM Get current working directory absolute path
for %%i in ("%CD%") do set "HOST_DIR=%%~fi"

REM Convert Windows path to Docker path format
set "HOST_DIR_DOCKER=%HOST_DIR:\=/%"

REM Use Docker to extract artifactId
for /f "tokens=*" %%a in ('docker run --rm -v "%HOST_DIR_DOCKER%:/app" -w "%CONTAINER_WORKDIR%" maven:3.9.9-eclipse-temurin-21 mvn help:evaluate -Dexpression^=project.artifactId -q -DforceStdout') do (
    set "APP_NAME=%%a"
)

REM Use Docker to extract version
for /f "tokens=*" %%a in ('docker run --rm -v "%HOST_DIR_DOCKER%:/app" -w "%CONTAINER_WORKDIR%" maven:3.9.9-eclipse-temurin-21 mvn help:evaluate -Dexpression^=project.version -q -DforceStdout') do (
    set "VERSION=%%a"
)

if "!APP_NAME!" == "" (
    echo [ERROR] Failed to extract artifactId
    goto :end
)

if "!VERSION!" == "" (
    echo [ERROR] Failed to extract version
    goto :end
)

set "JAR_NAME=!APP_NAME!-!VERSION!.jar"
set "IMAGE_NAME=!APP_NAME!:!VERSION!"

echo [SUCCESS] Project info: name=!APP_NAME!, version=!VERSION!, JAR=!JAR_NAME!

echo [INFO] Building Maven project...
docker run --rm -v "%HOST_DIR_DOCKER%:/app" -w "%CONTAINER_WORKDIR%" maven:3.9.9-eclipse-temurin-21 mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed
    goto :end
)

REM Check if JAR file exists
if not exist "%JAR_DIR%\%JAR_NAME%" (
    echo [ERROR] Cannot find generated JAR file: %JAR_DIR%\%JAR_NAME%
    goto :end
)

echo [SUCCESS] JAR file generated: %JAR_DIR%\%JAR_NAME%

echo [INFO] Building Docker image: %IMAGE_NAME%...
docker build -t "%IMAGE_NAME%" -f "%DOCKERFILE_PATH%" "%DOCKER_BUILD_DIR%"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker image build failed
    goto :end
)

echo [SUCCESS] Docker image built successfully: %IMAGE_NAME%

REM Set environment file path
if "%POM_PATH%" == "backend\pom.xml" (
    for %%i in ("%CD%") do set "ENV_FILE=%%~dpi.backendEnv"
) else (
    set "ENV_FILE=%CD%\.backendEnv"
)

echo [INFO] Environment file location: %ENV_FILE%
echo [SUCCESS] Build process completed!
echo [INFO] Run command: docker run -d -p 9000:9000 --env-file "%ENV_FILE%" %IMAGE_NAME%

if "%POM_PATH%" == "backend\pom.xml" (
    echo [INFO] Or use: cd backend ^&^& docker-compose up -d
) else (
    echo [INFO] Or use: docker-compose up -d
)

:end
echo.
echo Press any key to exit...
pause > nul