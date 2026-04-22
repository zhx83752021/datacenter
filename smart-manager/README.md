# Smart Management Platform (智慧管理平台)

基于 Vue 3 + TypeScript + Vite + Element Plus 的现代化医院管理系统前端项目。

## 目录结构 (Directory Structure)

```
smart-manager/
├── src/
│   ├── assets/          # 静态资源
│   ├── layout/          # 布局组件 (Sidebar, Header)
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia 状态管理
│   ├── styles/          # 全局样式 (Sass)
│   ├── views/           # 页面视图
│   │   ├── monitor/     # 全院指标监控
│   │   ├── portal/      # 运营决策门户
│   │   ├── cockpit/     # 决策驾驶舱
│   │   └── reports/     # 数据分析报表
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
└── ...
```

## 快速开始 (Quick Start)

### 1. 安装依赖

```bash
cd smart-manager
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

### 3. 构建生产版本

```bash
npm run build
```

## 配置说明

### 关于图标 (Icons)
项目目前默认使用 **Element Plus Icons** 以确保开箱即用。
如需切换为 **Alibaba Iconfont** (按设计文档要求):
1. 在 `index.html` 的 `<head>` 中引入 FontClass CSS 链接：
   ```html
   <link rel="stylesheet" href="//at.alicdn.com/t/font_xxxxxx.css">
   ```
2. 在组件中将 `<el-icon><TrendCharts /></el-icon>` 替换为 `<i class="iconfont icon-trend-charts"></i>`。

### 主题色 (Theme Color)
全局主题色定义在 `src/styles/variables.scss` 中，默认为 `#0dbda8` (医疗绿)。

## 功能模块状态

- [x] **全院指标监控**: 已完成基础布局与卡片展示。
- [x] **决策驾驶舱**: 已集成 ECharts 图表渲染与 KPI 卡片。
- [x] **运营决策门户**: 已完成卡片式导航布局。
- [x] **数据分析报表**: 已完成基础表格展示。
