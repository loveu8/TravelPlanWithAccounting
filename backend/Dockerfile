# 構建階段
FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /application/jar
COPY target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# 運行階段
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache bash
WORKDIR /application
# 複製分層構建的結果
COPY --from=builder /application/jar/dependencies/ ./
COPY --from=builder /application/jar/spring-boot-loader/ ./
COPY --from=builder /application/jar/snapshot-dependencies/ ./
COPY --from=builder /application/jar/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]