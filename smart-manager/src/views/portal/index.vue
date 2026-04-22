<template>
  <div class="portal-container">

    <!-- 全院核心运营水位 (Macro KPIs) : 引入深度溯源 -->
    <el-row :gutter="24" class="macro-kpi-row" style="margin-bottom: 32px;">
      <el-col :xs="24" :sm="12" :md="6" v-for="(kpi, index) in macroKPIs" :key="kpi.label">
        <div class="macro-card animate-enter" :style="{ 'animation-delay': `${index * 0.1}s` }">
          <div class="kpi-header">
            <span class="label" style="display: flex; align-items: center; gap: 4px;">
              {{ kpi.label }}
              <!-- 嵌入指标知识溯源的玻璃气泡 -->
              <el-tooltip effect="light" placement="bottom-start" :show-after="150" popper-class="portal-glass-tooltip">
                <template #content>
                  <div class="indicator-tooltip-content">
                    <div class="tt-header">
                      <el-icon><Menu /></el-icon>
                      <span style="font-weight:600; font-size: 14px; color: var(--text-primary);">门户焦点指标库</span>
                    </div>
                    <div class="tt-body">
                      <div class="tt-item">
                        <span class="tt-label">🧮 聚合算式：</span>
                        <span class="tt-value" style="font-family: monospace;">{{ kpi.formula }}</span>
                      </div>
                      <div class="tt-item">
                        <span class="tt-label">🏷️ 归属管辖：</span>
                        <div class="tt-value">
                            <el-tag size="small" type="success" effect="light" style="border-radius:2px;">{{ kpi.policySource }}</el-tag>
                        </div>
                      </div>
                      <div class="tt-item">
                        <span class="tt-label" style="color:#ef4444">🚨 门户预警标准：</span>
                        <span class="tt-value" style="color:#ef4444; font-weight: 500;">{{ kpi.thresholdDesc }}</span>
                      </div>
                    </div>
                  </div>
                </template>
                <el-icon class="info-icon" style="cursor:help; color:#94a3b8; font-size:14px;"><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
            <div class="trend-icon" :class="kpi.status">
              <el-icon><component :is="kpi.status === 'up' ? 'TopRight' : 'BottomRight'" /></el-icon>
            </div>
          </div>
          <div class="kpi-body">
            <span class="value"><CountTo :endVal="kpi.value" :decimals="kpi.decimals" /></span>
            <span class="unit">{{ kpi.unit }}</span>
          </div>
          <div class="kpi-footer">
            <span class="compare">较上月 <span :class="kpi.status">{{ kpi.mom }}%</span></span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 中部：业务探索场景 (Business Scenarios) -->
    <div class="section-header-modern animate-enter" style="animation-delay: 0.1s">
      <div class="title-group">
        <div class="title">业务决策入口</div>
      </div>
    </div>
    <el-row :gutter="24" class="scenarios-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in businessScenarios" :key="item.id">
        <div class="scenario-card animate-enter"
          :style="{ '--accent-color': item.color, 'animation-delay': `${index * 0.1 + 0.3}s` }"
          @click="$router.push(item.path)">
          <div class="scenario-icon">
            <el-icon :size="24">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="scenario-info">
            <h3 class="scenario-title">{{ item.title }}</h3>
            <p class="scenario-desc">{{ item.desc }}</p>
          </div>
          <el-icon class="arrow-icon">
            <ArrowRight />
          </el-icon>
        </div>
      </el-col>
    </el-row>

    <!-- 下部：动态与工具 (Feeds & Tools) -->
    <el-row :gutter="24" class="mt-8">
      <!-- 工作动态 - 实时预警流 -->
      <el-col :xs="24" :lg="16">
        <div class="section-header-modern animate-enter" style="animation-delay: 0.45s">
          <div class="title-group">
            <div class="title">指标决策动态</div>
            <div class="subtitle">关键异常与系统推送</div>
          </div>
          <el-button text type="primary" size="small" class="view-all-btn" @click="router.push('/monitor/target')">
            查看全部 <el-icon class="el-icon--right">
              <ArrowRight />
            </el-icon>
          </el-button>
        </div>
        <div class="glass-panel feed-card animate-enter" style="animation-delay: 0.5s">
          <div class="alert-list-slim custom-scrollbar">
            <div class="alert-item-new" v-for="alert in alerts" :key="alert.id" :class="`alert-level-${alert.level}`">
              <div class="alert-type-icon">
                <el-icon>
                  <WarningFilled v-if="alert.level === 'critical'" />
                  <Warning v-else-if="alert.level === 'warning'" />
                  <BellFilled v-else />
                </el-icon>
              </div>
              <div class="alert-body">
                <div class="alert-top">
                  <span class="alert-tag">{{ alert.level === 'critical' ? '紧急' : '提示' }}</span>
                  <span class="alert-time-text">{{ alert.time }}</span>
                </div>
                <h4 class="alert-title-text">{{ alert.title }}</h4>
                <p class="alert-desc-text">{{ alert.desc }}</p>
              </div>
              <div class="alert-ops">
                <el-button type="primary" plain round size="small" @click="handleAlert(alert)">分析</el-button>
              </div>
            </div>
            <div class="empty-state" v-if="alerts.length === 0">
              <p>系统监控中，暂无异常动态</p>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 核心工具箱 (Tools Grid) -->
      <el-col :xs="24" :lg="8">
        <div class="section-header-modern animate-enter" style="animation-delay: 0.55s">
          <div class="title-group">
            <div class="title">核心工具箱</div>
          </div>
        </div>
        <div class="glass-panel tools-card animate-enter" style="animation-delay: 0.6s">
          <div class="tools-grid-new">
            <div class="tool-item-new" v-for="app in coreTools" :key="app.name" @click="app.action">
              <div class="tool-icon-wrapper" :style="{ background: `${app.color}15`, color: app.color }">
                <el-icon :size="20">
                  <component :is="app.icon" />
                </el-icon>
              </div>
              <div class="tool-name-new">{{ app.name }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import countTo from '../../components/common/CountTo.vue'
import { ArrowRight, WarningFilled, Warning, BellFilled, DataLine, Top, Bottom, Document, Edit, Bell, Collection, Setting, DataAnalysis, UserFilled, DataBoard, Search, QuestionFilled, Menu, TopRight, BottomRight } from '@element-plus/icons-vue'
import { getQuickStats, getAlerts } from '../../api/portal'
import { useRouter } from 'vue-router'

const router = useRouter()

// 门户顶层全院全景宏观水位（带政策解读）
const macroKPIs = ref([
  {
    label: '全院医疗总营收',
    value: 125642.8,
    unit: '万元',
    decimals: 1,
    status: 'up',
    mom: 8.5,
    formula: 'Σ 各类医疗计费明细及日终结账金额',
    policySource: '《现代医院管理制度核心指标评价体系》绩效项',
    thresholdDesc: '医院经济运行基盘数据，波动＞5%需立即彻查结算异常。'
  },
  {
    label: 'DRG/DIP 入组率',
    value: 99.4,
    unit: '%',
    decimals: 1,
    status: 'up',
    mom: 0.2,
    formula: '[成功发生医保DRG/DIP分组出院总人次] / [医保结算出院总人次] × 100',
    policySource: '《国家医疗保障按病种分值付费(DIP)技术规范》',
    thresholdDesc: '极严苛考评指标！入组率低于 95% 视为重度不合规，影响全院级资金拨付。'
  },
  {
    label: '百床并发症比率',
    value: 0.32,
    unit: '比',
    decimals: 2,
    status: 'down',
    mom: -5.4,
    formula: '[全院非预见性并发症发生人次] / [全院编设床位数] × 100',
    policySource: '《医疗质量安全核心制度（2025年版）》第九点',
    thresholdDesc: '核心安全高压线，此项连续上升三个月将触发卫健委直接巡查及问责。'
  },
  {
    label: '医疗核心指标达标率',
    value: 94.2,
    unit: '%',
    decimals: 1,
    status: 'up',
    mom: 2.1,
    formula: '通过国家级质控平台校验通过的条目数 / 总上报条目',
    policySource: '《医院信息化与标准化绩效考核规范》',
    thresholdDesc: '低于 90% 一票褫夺绩效评优与次年经费扩大申批资质。'
  }
])

// 核心绩效数据
const quickStats = ref<any[]>([])
const alerts = ref<any[]>([])

const quickSearchKey = ref('')
const handleQuickSearch = () => {
  if (quickSearchKey.value.trim()) {
    router.push({ path: '/dashboard/list', query: { search: quickSearchKey.value } })
  }
}

// 业务探索场景 (自定义聚合)
const businessScenarios = [
  {
    id: '1',
    title: '全院绩效指挥',
    desc: '实时全量指标监测，辅助决策。',
    icon: DataBoard,
    color: '#0dbda8',
    path: '/cockpit'
  },
  {
    id: '2',
    title: '异常指标穿透',
    desc: '自动定位偏离目标值的核心指标。',
    icon: DataAnalysis,
    color: '#4FC3F7',
    path: '/monitor'
  },
  {
    id: '3',
    title: '主题报表研制',
    desc: '沉浸式数据分析与决策周报生成。',
    icon: Document,
    color: '#FFB84D',
    path: '/report/advanced'
  },
  {
    id: '4',
    title: '临床路径效率',
    desc: '从患者维度洞察医疗组织效率。',
    icon: UserFilled,
    color: '#7986CB',
    path: '/analysis/theme'
  }
]

// 核心工具箱
const coreTools = [
  { name: '指标库', icon: Collection, color: '#4FC3F7', action: () => router.push('/monitor/lib') },
  { name: '数据反馈', icon: Edit, color: '#FFB84D', action: () => router.push('/monitor/feedback') },
  { name: '预警配置', icon: Bell, color: '#FF6B6B', action: () => router.push('/monitor/target') },
  { name: '系统安全', icon: Setting, color: '#8F96A1', action: () => router.push('/system/user') },
]

const fetchData = async () => {
  try {
    const alertsRes = await getAlerts()
    alerts.value = (alertsRes as any).slice(0, 5) // 仅显示前5条
  } catch (error) {
    console.error('Failed to fetch portal data:', error)
  }
}

onMounted(() => {
  fetchData()
})

const handleAlert = (alert: any) => {
  ElMessage.success(`正在跳转处理: ${alert.title}`)
  router.push('/monitor/target')
}
</script>

<style scoped lang="scss">
.portal-container {
  padding: 16px 16px 0;
  width: 100%;
  box-sizing: border-box;
}

// 0. Macro KPI Row
.macro-card {
  background: var(--bg-surface, #ffffff);
  border-radius: 12px;
  border: 1px solid var(--border-color-light, #f1f5f9);
  padding: 20px;
  box-shadow: 0 4px 16px -4px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.06);
    border-color: rgba(13, 189, 168, 0.3); // hover with our brand color hint
  }

  .kpi-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .label {
      font-size: 14px;
      color: #64748b;
      font-weight: 500;
    }

    .trend-icon {
      font-size: 16px;
      &.up { color: var(--success, #10b981); }
      &.down { color: var(--error, #ef4444); }
    }
  }

  .kpi-body {
    display: flex;
    align-items: baseline;
    gap: 4px;
    margin-bottom: 8px;

    .value {
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary, #1e293b);
      letter-spacing: -0.5px;
    }
    .unit {
      font-size: 13px;
      color: #94a3b8;
    }
  }

  .kpi-footer {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #94a3b8;

    .compare {
      span {
        font-weight: 600;
        margin-left: 4px;
        &.up { color: var(--success, #10b981); }
        &.down { color: var(--error, #ef4444); }
      }
    }
  }
}

// === Portal 特有的明色系高质感玻璃拟态 ===
:global(.portal-glass-tooltip) {
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(16px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(16px) saturate(180%) !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
  border-radius: 12px !important;
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.15) !important;
  padding: 12px 18px !important;
}

:global(.portal-glass-tooltip .el-popper__arrow::before) {
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
}

.indicator-tooltip-content {
  min-width: 250px;
  max-width: 360px;
  text-align: left;
  .tt-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e2e8f0;
    margin-bottom: 12px;
    .el-icon { font-size: 16px; color: #0dbda8; }
  }
  .tt-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
    font-size: 13px;
    line-height: 1.5;
    color: #475569;
    .tt-item {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .tt-label { color: #64748b; }
    .tt-value { word-break: break-all; }
  }
}

// 3. Scenario Cards
.scenarios-row {
  margin-bottom: 40px;

  .scenario-card {
    background: var(--bg-surface);
    padding: 16px 20px;
    border-radius: 12px;
    border: 1px solid var(--border-glass);
    display: flex;
    align-items: center;
    gap: 20px;
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      width: 4px;
      height: 100%;
      background: var(--accent-color);
      opacity: 0.5;
    }

    .scenario-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      background: var(--accent-color);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 10px -2px var(--accent-color);
    }

    .scenario-info {
      flex: 1;

      .scenario-title {
        font-size: 17px;
        font-weight: 700;
        margin: 0 0 4px 0;
        color: var(--text-primary);
      }

      .scenario-desc {
        font-size: 13px;
        color: var(--text-secondary);
        margin: 0;
        line-height: 1.4;
      }
    }

    .arrow-icon {
      opacity: 0;
      transform: translateX(-10px);
      transition: all 0.3s ease;
      color: var(--accent-color);
    }

    &:hover {
      background: #fff;
      transform: scale(1.02);
      box-shadow: var(--shadow-md);

      .arrow-icon {
        opacity: 1;
        transform: translateX(0);
      }
    }
  }
}

// 4. Feeds & Tools
.glass-panel {
  background: var(--bg-surface);
  border-radius: 12px;
  border: 1px solid var(--border-glass);
  padding: 16px;
  height: 400px;

  &.feed-card {
    display: flex;
    flex-direction: column;
  }
}

.alert-list-slim {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;

  .alert-item-new {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 8px;
    border-radius: 0;
    border-bottom: 1px solid var(--border-color-light, #f1f5f9);
    background: transparent;
    transition: background-color 0.2s;

    &:last-child {
      border-bottom: none;
    }

    .alert-type-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      flex-shrink: 0;
    }

    .alert-body {
      flex: 1;

      .alert-top {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .alert-tag {
          font-size: 11px;
          padding: 2px 6px;
          border-radius: 4px;
          font-weight: 600;
        }

        .alert-time-text {
          font-size: 12px;
          color: #94a3b8;
        }
      }

      .alert-title-text {
        font-size: 15px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 4px 0;
      }

      .alert-desc-text {
        font-size: 13px;
        color: #64748b;
        margin: 0;
      }
    }

    .alert-ops {
      flex-shrink: 0;

      ::v-deep(.el-button) {
        border-color: #cbd5e1;
        color: #475569;

        &:hover {
          background-color: #f8fafc;
          border-color: #94a3b8;
        }
      }
    }

    &.alert-level-critical {
      .alert-type-icon {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
      }

      .alert-tag {
        background: #ef4444;
        color: #fff;
      }
    }

    &.alert-level-warning {
      .alert-type-icon {
        background: rgba(245, 158, 11, 0.1);
        color: #f59e0b;
      }

      .alert-tag {
        background: #f59e0b;
        color: #fff;
      }
    }

    &:hover {
      background: #f8fafc;
    }
  }
}

.tools-grid-new {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .tool-item-new {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24px;
    border-radius: 20px;
    background: #f8fafc;
    cursor: pointer;
    transition: all 0.2s;

    .tool-icon-wrapper {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
    }

    .tool-name-new {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
    }

    &:hover {
      background: #fff;
      transform: translateY(-3px);
      box-shadow: var(--shadow-sm);
    }
  }
}

@keyframes status-pulse {
  0% {
    opacity: 1;
  }

  50% {
    opacity: 0.5;
  }

  100% {
    opacity: 1;
  }
}

.mt-8 {
  margin-top: 24px;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--border-color-base);
  border-radius: 2px;
}

.custom-search-input :deep(.el-input__wrapper) {
  background: #f8fafc;
  border-radius: 20px;
  box-shadow: none;
  border: 1px solid var(--border-color-light, #e2e8f0);
  transition: all 0.3s ease;

  &:hover,
  &.is-focus {
    background: #fff;
    border-color: #3b82f6;
  }
}
</style>
```
