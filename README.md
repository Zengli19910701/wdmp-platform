# WDMP 工业互联网平台 — 后端（wdmp-server）

> 基于 Spring Boot 3 + MyBatis-Plus + JWT 构建的工业互联网平台后端服务

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.3 |
| MyBatis-Plus | 3.5.7 |
| Spring Security | 6.x |
| JJWT | 0.12.6 |
| Quartz | 2.3.x |
| MySQL | 8.0 |
| Lombok | 最新 |

## 项目结构

```
src/main/java/com/wmmp/
├── auth/          # JWT 认证模块（登录/登出/Token 刷新）
├── system/        # 系统管理（用户/角色/菜单/部门/配置）
├── file/          # 文件管理（上传/下载/删除）
├── report/        # 报表设计与查询（支持 SQL 动态报表）
├── task/          # 定时任务（Quartz 动态调度）
├── push/          # 消息推送（全体/用户/角色定向推送）
├── org/           # 组织架构（部门树管理）
└── common/        # 公共模块（Result/异常/工具类/配置）
```

## 快速启动

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE wdmp DEFAULT CHARACTER SET utf8mb4;

-- 执行初始化脚本
mysql -u root -p wdmp < src/main/resources/db/init.sql
```

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml`，将数据库密码替换为实际密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wdmp?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: YOUR_DB_PASSWORD   # ← 替换为实际密码
```

### 3. 启动服务

```bash
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8080`

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | Admin@123 | 超级管理员 |

## 主要接口

| 模块 | 前缀 | 说明 |
|------|------|------|
| 认证 | `/api/auth` | 登录、登出、获取用户信息 |
| 用户管理 | `/api/system/users` | CRUD + 角色分配 |
| 角色管理 | `/api/system/roles` | CRUD + 菜单权限 |
| 菜单管理 | `/api/system/menus` | 菜单树 CRUD |
| 部门管理 | `/api/system/depts` | 部门树 CRUD |
| 系统配置 | `/api/system/configs` | 键值对配置 |
| 文件管理 | `/api/files` | 上传/下载/删除 |
| 报表 | `/api/reports` | 报表设计与数据查询 |
| 定时任务 | `/api/tasks` | 动态 Quartz 任务管理 |
| 消息推送 | `/api/push` | 消息发送与历史查询 |
| 组织架构 | `/api/org` | 组织节点管理 |

## 运行测试

```bash
# 运行所有单元测试（不需要数据库连接）
mvn test
```

测试覆盖：认证、用户管理、菜单管理、文件管理、报表服务，共 11 个测试类。

## 文件上传说明

- 上传目录：`uploads/`（可在 `application.yml` 中配置 `app.upload.path`）
- 允许类型：`jpg, jpeg, png, gif, pdf, doc, docx, xls, xlsx, zip, rar, txt`
- 下载地址：`GET /api/files/{id}/download`

## 前端项目

前端项目地址：[wdmp-client](https://github.com/Zengli19910701/wdmp-client)
