# 图书管理系统前端

本项目是图书管理系统的前端部分，基于 Vue 3、Element Plus 和 Vite 开发的现代化 SPA 应用。

## 功能特性

- 用户认证（登录、注册、退出）
- 管理员和普通用户不同角色权限
- 图书管理（浏览、搜索、添加、编辑、删除）
- 图书分类管理
- 借阅管理（借书、还书、延期）
- 用户管理
- 响应式设计，美观的 UI 界面

## 技术栈

- Vue 3 - 渐进式 JavaScript 框架
- Vue Router - 官方路由管理器
- Pinia - 状态管理库
- Element Plus - 基于 Vue 3 的组件库
- Axios - HTTP 客户端
- Vite - 前端构建工具

## 开发环境搭建

### 前提条件

- Node.js v14.0 或更高版本
- npm v6.0 或更高版本

### 安装依赖

```bash
cd library-ui
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
library-ui/
├── public/                # 静态资源
├── src/
│   ├── api/              # API请求模块
│   ├── assets/           # 资源文件(图片、样式等)
│   ├── components/       # 公共组件
│   ├── router/           # 路由配置
│   ├── store/            # Pinia状态管理
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── .gitignore            # Git忽略文件
├── index.html            # HTML模板
├── package.json          # 项目配置
├── README.md             # 项目说明
└── vite.config.js        # Vite配置
```

## 部署

1. 构建生产版本：`npm run build`
2. 将 dist 文件夹部署到 Web 服务器

## 后端 API

本前端项目连接到基于 Spring Boot 的后端 API，接口前缀为`/api`。后端仓库请参考主项目目录。

## 许可证

MIT
