# Smart Manager Backend

## 简介
这是智慧管理系统的后端服务，基于 Spring Boot 3.2.1 开发。

## 环境要求
- **JDK**: 17 或更高版本 (必需，因为使用了 Spring Boot 3)
- **Maven**: 3.6+
- **MySQL**: 8.0+

## 快速开始

### 1. 数据库初始化
请在 MySQL 中执行 `sql/init.sql` 脚本以创建数据库和初始化表结构。
默认管理员账号：
- 用户名: `admin`
- 密码: `password`

### 2. 配置数据库连接
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_manager_db?...
    username: root
    password: password # 请修改为你本地数据库密码
```

### 3. 启动服务
在项目根目录下运行：
```bash
mvn spring-boot:run
```

或者使用 IDE (IntelliJ IDEA) 直接运行 `SmartManagerBackendApplication.java`。

服务启动后将监听端口 `8080` (默认)。

## 常见问题
- **Java版本错误**: 如果遇到类文件版本错误，请确保使用了 JDK 17。
- **连接被拒绝**: 请检查 MySQL 服务是否已启动。
