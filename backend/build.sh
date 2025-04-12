#!/bin/bash

# 腳本名稱: build.sh
# 用途: 自動化 Maven 專案打包並構建 Docker 鏡像，從 pom.xml 提取 artifactId 和 version，支援自定義環境變量檔案
# 使用方式: chmod +x build.sh && ./build.sh

# 配置參數
DOCKERFILE="Dockerfile"
ENV_FILE="../.backendEnv"  # 預設檢查上一層的 .backendEnv，可透過環境變量 ENV_FILE 覆蓋

# 顏色輸出函數
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'
echo_success() { echo -e "${GREEN}[SUCCESS] $1${NC}"; }
echo_error() { echo -e "${RED}[ERROR] $1${NC}"; }
echo_info() { echo -e "${YELLOW}[INFO] $1${NC}"; }

# 從 pom.xml 提取 artifactId 和 version
extract_pom_info() {
    echo_info "Extracting info from pom.xml..."
    if [ ! -f "pom.xml" ]; then
        echo_error "pom.xml not found in current directory"
        exit 1
    fi
    if command -v mvn &> /dev/null; then
        APP_NAME=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout 2>/dev/null)
        VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)
    fi
    if [ -z "$APP_NAME" ] && command -v xmllint &> /dev/null; then
        APP_NAME=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='artifactId']/text()" pom.xml 2>/dev/null)
        VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" pom.xml 2>/dev/null)
    fi
    if [ -z "$APP_NAME" ]; then
        APP_NAME=$(grep -A 1 "<project" pom.xml | grep "<artifactId>" | head -n 1 | sed -e 's/.*<artifactId>\(.*\)<\/artifactId>.*/\1/')
        VERSION=$(grep -A 3 "<project" pom.xml | grep "<version>" | head -n 1 | sed -e 's/.*<version>\(.*\)<\/version>.*/\1/')
    fi
    if [ -z "$APP_NAME" ] || [ -z "$VERSION" ]; then
        echo_error "Failed to extract artifactId or version from pom.xml"
        exit 1
    fi
    JAR_NAME="${APP_NAME}-${VERSION}.jar"
    IMAGE_NAME="${APP_NAME}:${VERSION}"
    echo_success "Extracted from pom.xml: APP_NAME=$APP_NAME, VERSION=$VERSION, JAR_NAME=$JAR_NAME"
}

# 檢查依賴
check_dependencies() {
    echo_info "Checking dependencies..."
    if ! command -v java &> /dev/null; then
        echo_error "Java not found. Please install OpenJDK 21 (e.g., sudo apt install openjdk-21-jdk)"
        exit 1
    fi
    JAVA_VERSION=$(java --version | head -n 1 | grep "21")
    if [ -z "$JAVA_VERSION" ]; then
        echo_error "Java 21 is required. Current version: $(java --version | head -n 1)"
        exit 1
    fi
    if ! command -v docker &> /dev/null; then
        echo_error "Docker not found. Please install Docker (e.g., sudo apt install docker.io)"
        exit 1
    fi
    if ! command -v mvn &> /dev/null; then
        echo_error "Maven not found. Please install Maven (e.g., sudo apt install maven)"
        exit 1
    fi
    echo_success "Dependencies check passed"
}

# 打包 Maven 專案
build_jar() {
    echo_info "Building Maven project..."
    mvn clean package -DskipTests || {
        echo_error "Maven build failed"
        exit 1
    }
    if [ ! -f "target/$JAR_NAME" ]; then
        echo_error "JAR file not found at target/$JAR_NAME"
        exit 1
    fi
    echo_success "JAR file generated: target/$JAR_NAME"
}

# 構建 Docker 鏡像
build_docker_image() {
    echo_info "Building Docker image: $IMAGE_NAME..."
    if [ ! -f "$DOCKERFILE" ]; then
        echo_error "Dockerfile not found: $DOCKERFILE"
        exit 1
    fi
    if [ ! -f "target/$JAR_NAME" ]; then
        echo_error "JAR file not found: target/$JAR_NAME"
        exit 1
    fi
    docker build -t "$IMAGE_NAME" -f "$DOCKERFILE" . || {
        echo_error "Docker build failed"
        exit 1
    }
    echo_success "Docker image built: $IMAGE_NAME"
}

# 驗證 Docker 鏡像
verify_image() {
    echo_info "Verifying Docker image..."
    if ! docker images -q "$IMAGE_NAME" &> /dev/null; then
        echo_error "Docker image $IMAGE_NAME not found"
        exit 1
    fi
    if [ -f "$ENV_FILE" ]; then
        echo_info "Loading environment variables from $ENV_FILE..."
    else
        echo_error "Environment file not found at $ENV_FILE. Please create it with DB_HOST, DB_USER, and DB_PASSWORD."
        exit 1
    fi
    echo_info "Starting test container..."
    CONTAINER_ID=$(docker run -d -p 8080:8080 --env-file "$ENV_FILE" "$IMAGE_NAME")
    sleep 15  # 增加延遲，適應外部 PostgreSQL 連線
    if [ "$(docker inspect -f '{{.State.Running}}' "$CONTAINER_ID")" != "true" ]; then
        echo_error "Container failed to start. Check logs:"
        docker logs "$CONTAINER_ID"
        docker stop "$CONTAINER_ID" &> /dev/null
        docker rm "$CONTAINER_ID" &> /dev/null
        exit 1
    fi
    if curl -s -f http://localhost:8080 &> /dev/null; then
        echo_success "Application is running successfully"
    else
        echo_info "Warning: Failed to access http://localhost:8080. Check if port, endpoint, or PostgreSQL connection is correct."
        docker logs "$CONTAINER_ID"
    fi
    docker stop "$CONTAINER_ID" &> /dev/null
    docker rm "$CONTAINER_ID" &> /dev/null
    echo_success "Docker image verified"
}

# 主流程
main() {
    echo_info "Starting build process..."
    # 允許環境變量 ENV_FILE 覆蓋預設值
    ENV_FILE=${ENV_FILE:-"../.backendEnv"}
    echo_info "Using environment file: $ENV_FILE"
    extract_pom_info
    echo_info "Building $APP_NAME:$VERSION..."
    check_dependencies
    build_jar
    build_docker_image
    verify_image
    echo_success "Build and packaging completed successfully!"
    echo_info "Image ready: $IMAGE_NAME"
    echo_info "Run with: docker run -d -p 8080:8080 --env-file \"$ENV_FILE\" $IMAGE_NAME"
    echo_info "Or use: docker-compose up -d"
}

# 執行主流程
main