# 书籍订阅系统

书籍订阅系统是一个基于 Spring Boot 和 Vue 3 开发的现代化图书借阅和管理平台，实现了图书借阅、归还、用户管理、信用积分管理等核心功能。系统分为用户端和管理员端两个界面，为图书馆提供全面的数字化管理解决方案。

## 项目结构

本项目由两部分组成：

- `library`: 后端服务，基于 Spring Boot 开发
- `library-ui`: 前端应用，基于 Vue 3 开发

## 功能特性

### 用户端功能

- 账户注册与登录
- 图书浏览和搜索
- 图书分类浏览
- 图书详情查看
- 图书借阅和归还
- 借阅历史查询
- 借阅延期申请
- 个人信用积分查看

### 管理员功能

- 图书管理（新增、编辑、删除）
- 图书分类管理
- 用户管理
- 借阅记录管理
- 信用积分管理
- 系统统计信息

## 技术栈

### 后端技术栈

- Spring Boot 2.x: 应用程序框架
- Spring Security: 认证和授权
- MyBatis Plus: ORM 框架
- MySQL: 关系型数据库
- Druid: 数据库连接池
- Swagger/Knife4j: API 文档生成工具
- lombok: 减少模板代码

### 前端技术栈

- Vue 3: 渐进式 JavaScript 框架
- Vue Router: 前端路由
- Element Plus: UI 组件库
- Axios: HTTP 客户端
- Pinia: 状态管理

## 数据库设计

系统包含以下主要数据表：

- `bs_user`: 用户表
- `bs_book`: 图书表
- `bs_book_type`: 图书类型表
- `bs_book_type_relation`: 图书与类型关联表
- `bs_borrow`: 借阅记录表
- `bs_credit_record`: 信用记录表
- `bs_comment`: 留言表

## 安装部署

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Node.js 14+
- npm 6+

### 后端部署

1. 克隆仓库到本地
2. 配置 MySQL 数据库，创建名为`library_db`的数据库
3. 修改`library/src/main/resources/application.yml`中的数据库连接信息
4. 进入`library`目录，运行以下命令：

```bash
# Windows
mvnw spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

服务将在默认的 8080 端口启动

### 前端部署

1. 进入`library-ui`目录
2. 安装依赖：

```bash
npm install
```

3. 开发模式运行：

```bash
npm run dev
```

4. 构建生产版本：

```bash
npm run build
```

构建产物将保存在`dist`目录，可部署到任意 Web 服务器

## 信用积分规则

系统实现了信用积分管理机制：

- 新用户初始积分为 100 分
- 按时归还图书：+2 分
- 逾期归还图书：-10 分
- 图书逾期未归还：-5 分/本/次（系统自动检测）
- 积分过低可能限制借阅权限

## 项目截图

(此处可添加系统主要功能界面截图)

## 许可证

MIT License
