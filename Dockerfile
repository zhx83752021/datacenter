# -----------------------------------------------------------------------------
# 多阶段构建：前端 (Vite) → 后端 (Maven) → 仅 JRE 运行
# 构建上下文必须为仓库根目录：docker build -t smart-manager .
# -----------------------------------------------------------------------------
# syntax=docker/dockerfile:1

# ---------- 阶段 1：构建 Vue 静态资源 ----------
FROM node:20-bookworm-slim AS frontend-build
WORKDIR /web

COPY smart-manager/package.json smart-manager/package-lock.json ./
RUN npm ci

COPY smart-manager/ ./
# 与后端同域，由 Spring 提供 /api，生产构建走相对路径
ENV VITE_APP_BASE_API=/api
RUN npm run build

# ---------- 阶段 2：Maven 打包 Spring Boot（嵌入上一步的 dist）----------
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /build

COPY smart-manager-backend/pom.xml .
COPY smart-manager-backend/src ./src
COPY smart-manager-backend/sql ./sql

COPY --from=frontend-build /web/dist/ ./src/main/resources/static/

RUN mvn -B -DskipTests package

# ---------- 阶段 3：仅运行 JRE，减小镜像体积 ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=backend-build /build/target/smart-manager-backend-0.0.1-SNAPSHOT.jar app.jar

# Railway 会注入 PORT；application.yml 使用 ${PORT:8081} 读取该变量
ENV PORT=8081
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
