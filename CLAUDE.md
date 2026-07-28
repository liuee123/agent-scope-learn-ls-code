# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是 **AgentScope 课程** 的配套代码项目，按章节（section）递进，逐步演示 AgentScope AI 框架的使用。当前处于第 2 节：第一个 Agent。

- **作者**: AI-小沛
- **后端**: Spring Boot 4.0.4 + Java 21 + Maven，位于 `backend/`
- **前端**: React 19 + TypeScript + Vite + Tailwind CSS v4 + Assistant UI，位于 `frontend/`
- **仓库**: `https://github.com/liuee123/agent-scope-learn-ls-code.git`

## 构建与运行

### 后端

项目使用 Maven Wrapper，无需预先安装 Maven：

```bash
# 编译
./mvnw compile

# 运行（启动在 http://localhost:8080）
./mvnw spring-boot:run

# 运行测试
./mvnw test
```

### 前端

```bash
cd frontend

# 安装依赖
npm install

# 开发模式（启动在 http://localhost:5173）
npm run dev

# 构建
npm run build
```

## 架构

```
backend/
├── mvnw, mvnw.cmd            # Maven Wrapper（版本 3.9.9）
├── pom.xml                    # 项目定义
└── src/main/
    ├── java/io/agentscope/course/
    │   ├── Application.java                # Spring Boot 入口（纯入口，无业务逻辑）
    │   └── ai/
    │       ├── config/
    │       │   ├── AgentScopeModelConfig.java  # 模型注册（DeepSeek + DashScope）
    │       │   ├── AgentConfig.java            # Agent 实例 + AG-UI 注册
    │       │   ├── CorsConfig.java             # CORS 配置（允许 localhost:5173）
    │       │   └── GlobalExceptionHandler.java  # 全局异常拦截 → 标准 HTTP 错误
    │       ├── controller/
    │       │   └── ChatController.java    # REST API（同步 + SSE 流式）
    │       ├── dto/
    │       │   ├── ChatRequest.java       # 请求 DTO
    │       │   └── ChatResponse.java      # 响应 DTO
    │       └── service/
    │           └── ChatService.java       # Agent 封装与对话逻辑
    └── resources/
        └── application.yml                # 端口、AG-UI Starter 配置
```

```
frontend/
├── index.html                 # 入口 HTML
├── vite.config.ts             # Vite + React + Tailwind CSS 插件
├── tsconfig.json              # TypeScript 配置
└── src/
    ├── main.tsx               # React 入口
    ├── App.tsx                # 根组件（AgUiRuntimeProvider → ThreadLayout）
    ├── index.css              # Tailwind CSS + 全局样式
    ├── providers/
    │   └── AgUiRuntimeProvider.tsx  # AG-UI 运行时（HttpAgent → /agui/run）
    ├── components/
    │   ├── assistant-ui/      # Assistant UI 组件（thread, composer, reasoning…）
    │   └── ui/                # 基础 UI 组件（button, tooltip, avatar…）
    └── lib/
        └── utils.ts           # cn() 工具函数
```

前端通过 `@ag-ui/client` 的 `HttpAgent` 连接后端 `POST /agui/run`，使用 `@assistant-ui/react` 渲染对话界面。线程管理纯前端（内存），后端不持久化会话。

## API 端点

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/chat` | 同步对话 |
| POST | `/api/v1/chat/stream` | 流式对话（SSE） |
| POST | `/agui/run` | AG-UI 协议端点（AgentScope Starter 自动暴露） |

### 传统 REST 请求格式

```json
{
  "message": "你好",
  "model": "deepseek",
  "sessionId": "optional-session-id"
}
```

### AG-UI 端点

由 `agentscope-spring-boot-agui-starter` 根据 `AgentConfig.aguiAgentRegistryCustomizer()` 注册的 Agent 自动暴露。前端 Assistant UI 直接对接该端点，无需手动处理 SSE 协议。

## 课程结构

项目按章节递进，每节对应 AgentScope 课程的一个主题。代码变更通过 Git tag 标记：

```bash
git tag section-01  # 第 1 节：课程导览与环境搭建
git tag section-02  # 第 2 节：第一个 Agent
```

新增章节代码时，在 `io.agentscope.course.ai` 下创建对应的子包。

## Java 版本

项目要求 **Java 21**。`backend/pom.xml` 中 `<java.version>21</java.version>` 控制编译器版本。

## Agent Skills

### Issue tracker

Issues 以本地 markdown 文件形式存放在 `.scratch/` 目录下。详见 `docs/agents/issue-tracker.md`。

### Domain docs

单上下文（single-context）—— `CONTEXT.md` + `docs/adr/` 位于仓库根目录。详见 `docs/agents/domain.md`。

## 网络代理
用户有代理 地址是 127.0.0.1:7897