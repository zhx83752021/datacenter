@echo off
chcp 65001 >nul
echo ========================================================
echo   Smart Manager Platform - 全栈自动化构建与部署脚本
echo ========================================================

set BACKEND_STATIC_DIR=smart-manager-backend\src\main\resources\static
set FRONTEND_DIR=smart-manager
set BACKEND_DIR=smart-manager-backend

echo.
echo [1/3] 清理旧版本的静态资源文件...
if exist %BACKEND_STATIC_DIR% (
    rmdir /s /q %BACKEND_STATIC_DIR%
)
mkdir %BACKEND_STATIC_DIR%

echo.
echo [2/3] 开始构建前端 Vue3 纯静态产物...
cd %FRONTEND_DIR%
echo - 正在安装 npm 依赖...
call npm install
echo - 正在进行生产环境编译...
call npm run build
echo - 正在迁移编译结果到 Spring Boot resources 目录...
xcopy /e /y dist\* ..\%BACKEND_STATIC_DIR%\ >nul
cd ..

echo.
echo [3/3] 开始构建后端 Spring Boot 微服务...
cd %BACKEND_DIR%
echo - 正在执行 Maven 打包 (跳过单元测试)...
call mvn clean package -DskipTests
cd ..

echo.
echo ========================================================
echo   构建全部完成！ 🎉
echo ========================================================
echo   输出路径: %BACKEND_DIR%\target\smart-manager-backend-1.0.jar
echo   启动命令: java -jar %BACKEND_DIR%\target\smart-manager-backend-1.0.jar
echo   无需单独启动 Nginx，Spring Boot 已内嵌前端文件直达
echo ========================================================
pause
