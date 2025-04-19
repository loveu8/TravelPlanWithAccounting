#!/bin/bash

# 腳本名稱: build.sh
# 用途: 使用 maven:3.9.9-eclipse-temurin-21 容器打包 Maven 專案，構建 Docker 鏡像，支援 monorepo 結構查找 pom.xml 和 Dockerfile，動態查找 .backendEnv
# 使用方式: chmod +x build.sh && ./build.sh

# 配置參數
DOCKERFILE="Dockerfile"
MAVEN_IMAGE="maven:3.9.9-eclipse-temurin-21"

# 顏色輸出函數
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'
echo_success() { echo -e "${GREEN}[SUCCESS] $1${NC}"; }
echo_error() { echo -e "${RED}[ERROR] $1${NC}"; }
echo_info() { echo -e "${YELLOW}[INFO] $1${NC}"; }

# 從 pom.xml 提取 artifactId 和 version，並檢查 Dockerfile
extract_pom_info() {
    echo_info "Extracting info from pom.xml and locating Dockerfile..."
    POM_PATH=""
    DOCKERFILE_PATH=""
    
    if [ -f "pom.xml" ]; then
        POM_PATH="pom.xml"
        DOCKERFILE_PATH="$DOCKERFILE"
    elif [ -f "backend/pom.xml" ]; then
        POM_PATH="backend/pom.xml"
        DOCKERFILE_PATH="backend/$DOCKERFILE"
    else
        echo_error "pom.xml not found in current directory or backend/"
        exit 1
    fi

    if [ ! -f "$DOCKERFILE_PATH" ]; then
        echo_error "Dockerfile not found at $DOCKERFILE_PATH"
        exit 1
    fi

    echo_info "Using pom.xml at: $POM_PATH"
    echo_info "Using Dockerfile at: $DOCKERFILE_PATH"

    if [ "$POM_PATH" = "pom.xml" ]; then
        CONTAINER_WORKDIR="/app"
        HOST_DIR="$(pwd)"
        JAR_DIR="target"
    else
        CONTAINER_WORKDIR="/app/backend"
        HOST_DIR="$(pwd)"
        JAR_DIR="backend/target"
    fi

    APP_NAME=$(docker run --rm -v "$HOST_DIR:/app" -w "$CONTAINER_WORKDIR" $MAVEN_IMAGE mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout 2>/dev/null)
    VERSION=$(docker run --rm -v "$HOST_DIR:/app" -w "$CONTAINER_WORKDIR" $MAVEN_IMAGE mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2>/dev/null)

    if [ -z "$APP_NAME" ] && command -v xmllint &> /dev/null; then
        APP_NAME=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='artifactId']/text()" "$POM_PATH" 2>/dev/null)
        VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" "$POM_PATH" 2>/dev/null)
    fi

    if [ -z "$APP_NAME" ]; then
        APP_NAME=$(grep -A 1 "<project" "$POM_PATH" | grep "<artifactId>" | head -n 1 | sed -e 's/.*<artifactId>\(.*\)<\/artifactId>.*/\1/')
        VERSION=$(grep -A 3 "<project" "$POM_PATH" | grep "<version>" | head -n 1 | sed -e 's/.*<version>\(.*\)<\/version>.*/\1/')
    fi

    if [ -z "$APP_NAME" ] || [ -z "$VERSION" ]; then
        echo_error "Failed to extract artifactId or version from $POM_PATH"
        exit 1
    fi

    JAR_NAME="${APP_NAME}-${VERSION}.jar"
    IMAGE_NAME="${APP_NAME}:${VERSION}"
    echo_success "Extracted from $POM_PATH: APP_NAME=$APP_NAME, VERSION=$VERSION, JAR_NAME=$JAR_NAME"
}

# 檢查依賴
check_dependencies() {
    echo_info "Checking dependencies..."
    if ! command -v docker &> /dev/null; then
        echo_error "Docker not found. Please install Docker (e.g., sudo apt install docker.io)"
        exit 1
    fi
    echo_success "Dependencies check passed"
}

# 打包 Maven 專案
build_jar() {
    echo_info "Building Maven project in Docker container..."
    docker run --rm -v "$HOST_DIR:/app" -w "$CONTAINER_WORKDIR" $MAVEN_IMAGE mvn clean package -DskipTests || {
        echo_error "Maven build failed in container"
        exit 1
    }
    if [ ! -f "$JAR_DIR/$JAR_NAME" ]; then
        echo_error "JAR file not found at $JAR_DIR/$JAR_NAME"
        exit 1
    fi
    echo_success "JAR file generated: $JAR_DIR/$JAR_NAME"
}

# 構建 Docker 鏡像
build_docker_image() {
    echo_info "Building Docker image: $IMAGE_NAME..."
    if [ ! -f "$DOCKERFILE_PATH" ]; then
        echo_error "Dockerfile not found at $DOCKERFILE_PATH"
        exit 1
    fi
    if [ ! -f "$JAR_DIR/$JAR_NAME" ]; then
        echo_error "JAR file not found at $JAR_DIR/$JAR_NAME"
        exit 1
    fi
    DOCKERFILE_DIR="."
    if [ "$POM_PATH" = "backend/pom.xml" ]; then
        DOCKERFILE_DIR="backend"
    fi
    docker build -t "$IMAGE_NAME" -f "$DOCKERFILE_PATH" "$DOCKERFILE_DIR" || {
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
    CONTAINER_ID=$(docker run -d -p 9000:9000 --env-file "$ENV_FILE" "$IMAGE_NAME")
    sleep 15
    if [ "$(docker inspect -f '{{.State.Running}}' "$CONTAINER_ID")" != "true" ]; then
        echo_error "Container failed to start. Check logs:"
        docker logs "$CONTAINER_ID"
        docker stop "$CONTAINER_ID" &> /dev/null
        docker rm "$CONTAINER_ID" &> /dev/null
        exit 1
    fi
    if curl -s -f http://localhost:9000 &> /dev/null; then
        echo_success "Application is running successfully"
    else
        echo_info "Warning: Failed to access http://localhost:9000. Check if port, endpoint, or PostgreSQL connection is correct."
        docker logs "$CONTAINER_ID"
    fi
    docker stop "$CONTAINER_ID" &> /dev/null
    docker rm "$CONTAINER_ID" &> /dev/null
    echo_success "Docker image verified"
}

# 主流程
main() {
    echo_info "Starting build process..."
    # 動態設置 ENV_FILE
    SCRIPT_DIR="$(dirname "$(realpath "$0")")"
    if [ "$(basename "$SCRIPT_DIR")" = "backend" ]; then
        ENV_FILE="$SCRIPT_DIR/../.backendEnv"
    else
        ENV_FILE="$SCRIPT_DIR/.backendEnv"
    fi
    ENV_FILE=${ENV_FILE:-"$SCRIPT_DIR/.backendEnv"}  # 允許環境變量覆蓋
    # 正規化路徑，移除多餘的 ../
    ENV_FILE=$(realpath -m "$ENV_FILE" 2>/dev/null || echo "$ENV_FILE")
    echo_info "Using environment file: $ENV_FILE"
    extract_pom_info
    echo_info "Building $APP_NAME:$VERSION..."
    check_dependencies
    build_jar
    build_docker_image
    verify_image
    echo_success "Build and packaging completed successfully!"
    echo_info "Image ready: $IMAGE_NAME"
    echo_info "Run with: docker run -d -p 9000:9000 --env-file \"$ENV_FILE\" $IMAGE_NAME"
    COMPOSE_DIR="."
    if [ "$POM_PATH" = "backend/pom.xml" ]; then
        COMPOSE_DIR="backend"
    fi
    echo_info "Or use: cd $COMPOSE_DIR && docker-compose up -d"
}

main