# 智慧管理平台设计系统 v1.0

> "反主流"医疗健康管理系统 - 避免千篇一律的 SaaS 模板

## 🎯 设计理念

**医疗 × 科技感 × 温度**
- 不是冰冷的数据面板，是有温度的健康助手
- 用色彩传递情绪，用动效增加生命力
- 追求专业但不刻板，现代但不浮夸

---

## 🎨 配色方案

### 核心色彩（Medical Vitality Palette）

```css
/* 主色调：生命绿 - 象征健康与活力 */
--primary: #0dbda8;           /* 青绿色 - 主要交互 */
--primary-light: #14d4bc;     /* 浅绿 - hover状态 */
--primary-dark: #0a9d8a;      /* 深绿 - 按下状态 */
--primary-glow: rgba(13, 189, 168, 0.15);

/* 辅助色彩 - 功能性色板 */
--accent-coral: #FF6B6B;      /* 珊瑚红 - 警告/紧急 */
--accent-amber: #FFB84D;      /* 琥珀黄 - 提醒/待办 */
--accent-sky: #4FC3F7;        /* 天空蓝 - 信息/数据 */
--accent-sage: #81C784;       /* 鼠尾草绿 - 成功/健康 */
--accent-lavender: #B39DDB;   /* 薰衣草 - 休息/康复 */
--accent-peach: #FFAB91;      /* 蜜桃橙 - 次要强调 */

/* 中性色彩 */
--gray-50: #FAFBFC;
--gray-100: #F4F6F8;
--gray-200: #E8ECF0;
--gray-300: #DFE3E8;
--gray-400: #C1C7CD;
--gray-500: #8F96A1;
--gray-600: #5E6670;
--gray-700: #3D4551;
--gray-800: #272E38;
--gray-900: #1A1F28;

/* 语义化颜色 */
--success: #81C784;
--warning: #FFB84D;
--error: #FF6B6B;
--info: #4FC3F7;
```

### 渐变组合

```css
/* 卡片渐变 - 不对称的活力 */
--gradient-health: linear-gradient(135deg, #0dbda8 0%, #14d4bc 100%);
--gradient-warm: linear-gradient(135deg, #FFB84D 0%, #FFAB91 100%);
--gradient-cool: linear-gradient(135deg, #4FC3F7 0%, #B39DDB 100%);
--gradient-vitality: linear-gradient(135deg, #81C784 0%, #0dbda8 100%);

/* 玻璃态背景 */
--glass-bg: rgba(255, 255, 255, 0.7);
--glass-border: rgba(255, 255, 255, 0.3);
--glass-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.1);
```

---

## ✍️ 字体系统

### 字体配对：现代 × 人文

```css
/* 标题字体：Outfit - 几何现代感 */
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&display=swap');

/* 正文字体：Inter - 清晰易读 */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');

/* 数据字体：JetBrains Mono - 等宽，适合数字 */
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;600;700&display=swap');

/* 中文字体：PingFang SC / Microsoft YaHei */
font-family: 'Outfit', 'PingFang SC', 'Microsoft YaHei', sans-serif;
```

### 字阶系统

```css
/* 超大标题 - 页面主标题 */
--text-5xl: 3rem;      /* 48px */
--text-4xl: 2.25rem;   /* 36px */
--text-3xl: 1.875rem;  /* 30px */

/* 标题 */
--text-2xl: 1.5rem;    /* 24px */
--text-xl: 1.25rem;    /* 20px */
--text-lg: 1.125rem;   /* 18px */

/* 正文 */
--text-base: 1rem;     /* 16px */
--text-sm: 0.875rem;   /* 14px */
--text-xs: 0.75rem;    /* 12px */

/* 权重 */
--font-normal: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
--font-extrabold: 800;
```

---

## 🎬 动效系统

### 动画时长

```css
--duration-fast: 150ms;
--duration-normal: 250ms;
--duration-slow: 350ms;
--duration-slower: 500ms;

/* 缓动函数 - 拒绝线性！*/
--ease-smooth: cubic-bezier(0.4, 0.0, 0.2, 1);
--ease-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
--ease-elastic: cubic-bezier(0.34, 1.56, 0.64, 1);
```

### 核心动画

```css
/* 页面进入动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 卡片悬停 */
@keyframes cardHover {
  from {
    transform: translateY(0) scale(1);
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  }
  to {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 12px 24px rgba(0,0,0,0.1);
  }
}

/* 脉冲效果 - 用于吸引注意力 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}
```

---

## 🧱 组件规范

### 玻璃态卡片（Glass Card）

```css
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  transition: all var(--duration-normal) var(--ease-smooth);
}

.glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 48px 0 rgba(31, 38, 135, 0.15);
}
```

### 按钮系统

```css
/* 主按钮 - 生命绿 */
.btn-primary {
  background: var(--gradient-health);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-smooth);
  box-shadow: 0 4px 12px var(--primary-glow);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px var(--primary-glow);
}

/* 次要按钮 */
.btn-secondary {
  background: white;
  color: var(--primary);
  border: 2px solid var(--primary);
  border-radius: 12px;
  padding: 10px 22px;
  font-weight: 600;
}
```

### 输入框

```css
.input-modern {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border: 2px solid var(--gray-200);
  border-radius: 12px;
  padding: 12px 16px;
  transition: all var(--duration-normal);
}

.input-modern:focus {
  border-color: var(--primary);
  background: white;
  box-shadow: 0 0 0 4px var(--primary-glow);
}
```

---

## 🖼️ 背景与纹理

### 噪点纹理（必须）

```css
/* 避免纯平背景 - 添加细微噪点 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='3.5' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.03'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 9999;
}
```

### 渐变背景

```css
.page-background {
  background: linear-gradient(135deg, #FAFBFC 0%, #F4F6F8 50%, #E8ECF0 100%);
  min-height: 100vh;
}
```

---

## 🎯 图标系统

**使用 Iconify** - https://iconify.design

推荐图标集：
- `mdi:` - Material Design Icons（医疗相关）
- `lucide:` - Lucide Icons（现代简洁）
- `carbon:` - IBM Carbon（数据可视化）

```html
<!-- 示例 -->
<iconify-icon icon="mdi:heart-pulse" width="24"></iconify-icon>
<iconify-icon icon="lucide:activity" width="24"></iconify-icon>
```

---

## 📏 布局原则

### 不对称布局

```
❌ 禁止：等宽三栏布局（太对称）
✅ 推荐：16:8 或 12:12 不对称分割
```

### 间距系统

```css
--space-xs: 4px;
--space-sm: 8px;
--space-md: 16px;
--space-lg: 24px;
--space-xl: 32px;
--space-2xl: 48px;
--space-3xl: 64px;
```

---

## 💬 文案规范

### 原则
1. **口语化**：像朋友聊天，不要官方腔
2. **简短**：每句话不超过15个字
3. **具体**：有数字、有场景

### 示例对比

| ❌ 避免使用 | ✅ 推荐使用 |
|-----------|-----------|
| "数据加载中，请稍候..." | "正在获取数据..." |
| "院领导驾驶舱可视化看板" | "院领导驾驶舱" |
| "暂无相关数据信息显示" | "暂无数据" |
| "确定要执行此操作吗？" | "确定这样做？" |

---

## 🚫 禁止项（红线）

### 配色禁止
- ❌ 紫色/靛蓝色 (#6366F1, #8B5CF6)
- ❌ 纯平背景（必须有纹理/渐变）
- ❌ Tailwind 默认色板

### 布局禁止
- ❌ Hero + 三卡片完美居中
- ❌ 等宽多栏

### 组件禁止
- ❌ Element Plus 默认样式（必须深度定制）
- ❌ Emoji 作为功能图标
- ❌ 线性动画（ease-in-out）

---

## 📦 实施检查清单

- [ ] 替换所有紫色为生命绿/珊瑚红等
- [ ] 添加噪点纹理到背景
- [ ] 引入 Outfit + Inter 字体
- [ ] 深度定制 Element Plus 组件
- [ ] 确保布局不对称
- [ ] 优化所有文案为口语化
- [ ] 使用 Iconify 图标替换 Element Plus 图标
- [ ] 添加微动效到交互元素

---

**最后更新**: 2026-02-05
**设计师**: Antigravity AI
