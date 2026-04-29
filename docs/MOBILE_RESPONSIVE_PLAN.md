# 智慧管理平台 · 一套代码兼容 PC 与移动端

本文档描述 **单仓库、单前端工程（`smart-manager`）** 下的响应式策略与分阶段任务清单。目标：**同一套 Vue 代码、同一套路由与接口**，通过断点、布局分支与组件形态切换适配桌面与手机浏览器。

---

## 1. 原则

| 原则 | 说明 |
|------|------|
| 单代码库 | 不另建独立 H5/uni-app 工程；业务逻辑、权限、API 只维护一份 |
| 移动优先补洞 | 以桌面为默认实现，在窄屏上增量做「导航 / 表格 / 表单 / 图表」的适配分支 |
| 断点统一 | 全局使用同一套宽度约定（见下文），避免魔法数字散落 |
| 渐进增强 | P0 解决「能用」，P1/P2 解决「好用」 |

---

## 2. 断点约定

| 名称 | 条件 | 典型用途 |
|------|------|----------|
| **mobile** | `width ≤ 768px` | 抽屉导航、压缩顶栏、内容区缩小左右 padding |
| **tablet**（可选） | `769px ~ 1024px` | 过渡布局、表格横滚或简化列 |

实现：`src/composables/useBreakpoint.ts` 基于 `matchMedia` / `resize` 输出 `isMobile` 等，供布局与页面使用。

---

## 3. 分阶段任务清单

### P0 — 全局可用（导航 + 基础布局）

- [x] 编写本文档 `docs/MOBILE_RESPONSIVE_PLAN.md`
- [x] 新增 `useBreakpoint` 组合式函数
- [x] 主导航抽离为 `MainNavMenu.vue`，桌面横向菜单 + 移动端侧栏抽屉内纵向菜单
- [x] 顶栏：窄屏显示汉堡按钮；内容区窄屏减小 padding
- [x] 根容器：`min-height: 100dvh` 降级 `100vh`，缓解移动端地址栏导致的高度裁切
- [ ] 全站抽检：主要路由无整页横向滚动（按需后续页面排查）

### P1 — 数据与表单

- [x] 全局工具类 `table-responsive`（`src/styles/index.scss`）：表格区域横向滚动、触摸惯性滚动
- [x] 专项报表：列表表格包裹 + 窄屏筛选区/标题区纵向堆叠（`views/reports/index.vue`）
- [x] 用户管理：表格包裹 + 窄屏筛选/顶栏/弹窗宽度（`views/system/user.vue`）
- [x] 其余主要含 `el-table` 的页面：系统（参数/字典/日志/角色/菜单）、监测（知识库/反馈/目标预警/指标分析）、驾驶舱（院级排名表/科室医师表）按「fixed 列 → `sys-table-shell` / 无 fixed → `table-responsive`」包裹（见 `index.scss`）

- [x] ECharts：`resize`（驾驶舱 `cockpit/index`、`cockpit/department` 已监听 `window.resize`；旋转窄屏可正常重绘）


### P2 — 体验与可访问性

- [ ] 弹窗：窄屏接近全屏或底部抽屉，避免键盘遮挡（用户管理已做基础 `min(92vw)` 宽度，其余弹窗按页补）
- [ ] 触控热区 ≥ ~44px、`touch-action` 与图表滑动冲突处理
- [x] `prefers-reduced-motion` 下降级动画（`src/styles/index.scss`）
- [x] 安全区 `safe-area-inset-*`：内容区 `.content-wrapper.is-mobile` 底部内边距（`index.scss`）


### P3 — 性能（弱网 / 老机）

- [ ] 路由与组件懒加载盘点
- [ ] 列表虚拟滚动（大数据表格页）
- [ ] 非首屏图片/模块懒加载

---

## 4. 验收建议

- Chrome DevTools：375 / 390 / 414 / 768 宽度抽样
- 真机：至少各 1 台 iOS Safari、Android Chrome
- 旋转：竖屏为主；复杂报表可提示横屏查看

---

## 5. 变更记录

| 日期 | 说明 |
|------|------|
| 2026-04-29 | 初版：文档 + P0 布局/导航/断点/dvh |
| 2026-04-29 | P1 扩展：系统 config/menu/log/role、监测 lib/feedback/target、analysis，驾驶舱 index/department 表格区；字典页窄屏左右栏纵向堆叠；知识库表格外层 `table-responsive--grow` + `min-width` 横滑 |
