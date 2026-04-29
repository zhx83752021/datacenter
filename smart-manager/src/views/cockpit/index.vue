<template>
  <div class="cockpit-container">
    <div class="section-header-modern">
      <div class="title-group">
        <div class="title">院领导驾驶舱</div>
      </div>
      <div class="actions">
        <!-- Date Picker or Filter could go here -->
        <span class="update-time">数据更新于: 12:00</span>
        <el-button type="primary" plain round size="small" @click="$router.push('/cockpit/dept')">
          <el-icon class="nr">
            <Switch />
          </el-icon> 切换科室
        </el-button>
      </div>
    </div>

    <!-- KPI Cards: High Impact -->
    <el-row :gutter="20" class="top-cards animate-enter">
      <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in kpiList" :key="item.label">
        <div class="kpi-card glass-panel" :class="item.type" :style="{ 'animation-delay': `${index * 0.1}s` }">
          <div class="kpi-top">
            <span class="label" style="display: flex; align-items: center; gap: 4px;">
              {{ item.label }}
              <el-tooltip effect="light" placement="bottom-start" :show-after="150" popper-class="cockpit-glass-tooltip">
                <template #content>
                  <div class="indicator-tooltip-content">
                    <div class="tt-header">
                      <el-icon><Menu /></el-icon>
                      <span style="font-weight:600; font-size: 14px; color: var(--text-primary);">大屏指标深度溯源</span>
                    </div>
                    <div class="tt-body">
                      <div class="tt-item">
                        <span class="tt-label">🧮 聚合算式：</span>
                        <span class="tt-value" style="font-family: monospace;">{{ item.formula }}</span>
                      </div>
                      <div class="tt-item">
                        <span class="tt-label">🏷️ 核准政策：</span>
                        <div class="tt-value">
                            <el-tag size="small" type="success" effect="light" style="border-radius:2px;">{{ item.policySource }}</el-tag>
                        </div>
                      </div>
                      <div class="tt-item">
                        <span class="tt-label">📡 治理中枢提取网闸：</span>
                        <span class="tt-value">{{ item.dataSource }}</span>
                      </div>
                      <div class="tt-item">
                        <span class="tt-label" style="color:#ef4444">🚨 宏观红线解构：</span>
                        <span class="tt-value" style="color:#ef4444; font-weight: 500;">{{ item.thresholdDesc }}</span>
                      </div>
                    </div>
                  </div>
                </template>
                <el-icon class="info-icon" style="cursor:help; color:#94a3b8;"><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
            <div class="badge" :class="item.status">
              <span class="rate">{{ item.rate }}</span>
              <el-icon v-if="item.status === 'up'">
                <TopRight />
              </el-icon>
              <el-icon v-else>
                <BottomRight />
              </el-icon>
            </div>
          </div>
          <div class="kpi-main">
            <div class="value-group">
              <span class="currency" v-if="item.prefix">{{ item.prefix }}</span>
              <span class="value metric-value">
                <CountTo :endVal="parseFloat(item.value.replace(/,/g, ''))"
                  :decimals="item.value.includes('.') ? 1 : 0" />
              </span>
              <span class="unit">{{ item.unit }}</span>
            </div>
            <!-- Decorative Trend Line (SVG) -->
            <div class="trend-visual">
              <svg viewBox="0 0 100 24" preserveAspectRatio="none" class="trend-svg">
                <path :d="item.path" fill="none" :stroke="item.chartColor" stroke-width="2"
                  vector-effect="non-scaling-stroke" stroke-linecap="round" />
                <path :d="item.areaPath" :fill="item.chartColor" fill-opacity="0.1" stroke="none" />
              </svg>
            </div>
          </div>
          <div class="kpi-footer">
            <span class="sub-label">目标达成</span>
            <el-progress :percentage="item.percentage" :color="item.chartColor" :show-text="true" :stroke-width="6"
              class="kpi-progress" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Core Charts: Glass Panels which are slightly flatter now -->
    <el-row :gutter="20" class="chart-section mt-6 animate-enter" style="animation-delay: 0.3s">
      <el-col :xs="24" :lg="16">
        <div class="glass-panel text-panel">
          <div class="panel-header">
            <div class="title-group">
              <span class="title">收支趋势分析</span>
            </div>
            <div class="controls">
              <el-radio-group v-model="trendType" size="small" class="custom-radio">
                <el-radio-button label="income">收入</el-radio-button>
                <el-radio-button label="cost">支出</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container" ref="lineChartRef"></div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div class="glass-panel text-panel">
          <div class="panel-header">
            <div class="title-group">
              <span class="title">收入构成</span>
            </div>
          </div>
          <div class="chart-wrapper circle-layout">
            <!-- Custom Donut Implementation for sharper look -->
            <div class="donut-group">
              <div class="donut-chart-box">
                <el-progress type="dashboard" :percentage="45" color="var(--primary)" :width="140" :stroke-width="10">
                  <template #default>
                    <div class="center-text">
                      <span class="val metric-value">
                        <CountTo :endVal="45" suffix="%" />
                      </span>
                      <span class="lbl">药品</span>
                    </div>
                  </template>
                </el-progress>
                <!-- Background rings for decoration -->
                <div class="ring-bg"></div>
              </div>
            </div>
            <div class="legend-list">
              <div class="legend-item" v-for="l in legends" :key="l.name">
                <span class="dot" :style="{ background: l.color }"></span>
                <span class="name">{{ l.name }}</span>
                <span class="val metric-value">{{ l.value }}%</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Bottom Section: Detailed Lists -->
    <el-row :gutter="20" class="bottom-section mt-6 animate-enter" style="animation-delay: 0.5s">
      <el-col :xs="24" :lg="12">
        <div class="glass-panel list-panel">
          <div class="panel-header">
            <div class="title-group">
              <span class="title">门诊人次 TOP 5</span>
            </div>
          </div>
          <div class="table-wrapper table-responsive">
            <el-table :data="rankData" style="width: 100%" class="premium-table-clean">
              <el-table-column type="index" label="排名" width="60">
                <template #default="{ $index }">
                  <div class="rank-num" :class="`top-${$index + 1}`">0{{ $index + 1 }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="dept" label="科室名称" />
              <el-table-column prop="value" label="门诊人次" align="right">
                <template #default="{ row }">
                  <span class="metric-value font-medium">
                    <CountTo :endVal="parseFloat(row.value.replace(/,/g, ''))" />
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="yoy" label="同比" align="right" width="120">
                <template #default="{ row }">
                  <div class="trend-tag" :class="row.yoy > 0 ? 'up' : 'down'">
                    {{ Math.abs(row.yoy) }}%
                    <el-icon>
                      <component :is="row.yoy > 0 ? 'TopRight' : 'BottomRight'" />
                    </el-icon>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="glass-panel grid-panel">
          <div class="panel-header">
            <div class="title-group">
              <span class="title">关键效率指标</span>
            </div>
          </div>
          <div class="kpi-grid">
            <div class="mini-stat-card" v-for="stat in efficiencyStats" :key="stat.label">
              <div class="card-left">
                <div class="header-row">
                  <div class="icon-layer" :class="stat.colorType">
                    <el-icon>
                      <component :is="stat.icon" />
                    </el-icon>
                  </div>
                  <div class="lbl">{{ stat.label }}</div>
                </div>
                <div class="val">
                  <CountTo :endVal="parseFloat(stat.value)" :suffix="stat.value.includes('%') ? '%' : ''"
                    :decimals="stat.value.includes('.') ? 1 : 0" />
                </div>
              </div>
              <div class="card-right">
                <el-progress type="circle" :percentage="stat.percentValue" :width="52" :stroke-width="5"
                  :color="stat.colorCode" :show-text="false" />
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { Switch, TopRight, BottomRight, QuestionFilled, Menu } from '@element-plus/icons-vue'
import CountTo from '../../components/common/CountTo.vue'
import { getPresidentKPIs, getPresidentTrend, getIncomeComposition, getOutpatientRankings, getEfficiencyStats } from '../../api/cockpit'

const trendType = ref('income')
const lineChartRef = ref<HTMLElement | null>(null)
let lineChart: echarts.ECharts | null = null

const kpiList = ref<any[]>([])
const legends = ref<any[]>([])
const rankData = ref<any[]>([])
const efficiencyStats = ref<any[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const [kpis, composition, rankings, efficiency] = await Promise.all([
      getPresidentKPIs(),
      getIncomeComposition(),
      getOutpatientRankings(),
      getEfficiencyStats()
    ])

    // 注入大屏宏观专属的政策溯源 Mock
    kpiList.value = (kpis as any).map((k: any) => {
        let policySource = k.label.includes('收入') || k.label.includes('均次') ? '《公立医疗机构经济管理年活动方案》' : '《三级公立医院绩效考核国家规范（国考）》';
        let formula = k.label.includes('收入') ? '[门诊收现] + [住院收现] + [医保拨付决算]' : (k.label.includes('门急诊') ? 'HIS挂号及就诊工作站记录聚合峰值' : '直连核心交易库基础表单汇聚');
        let dataSource = k.label.includes('收入') ? 'HRP财务业财一体化平台 / HIS结算系统' : 'HIS临床工作站 / 电子病历中心';
        let thresholdDesc = k.label.includes('收入') ? '全院资金链生命线。跌破同期预警线将立刻启动经管委风险质询。' : '宏观诊疗服务产能关键指标，影响全院绩效资金池分配基盘。';
        return { ...k, policySource, formula, dataSource, thresholdDesc }
    })

    legends.value = composition as any
    rankData.value = rankings as any
    efficiencyStats.value = efficiency as any
    updateChart()
  } catch (error) {
    console.error('Failed to fetch president data:', error)
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    updateChart()
  }
}

const updateChart = async () => {
  if (!lineChart) return

  const isIncome = trendType.value === 'income'
  const primaryColor = isIncome ? '#0dbda8' : '#F59E0B'
  const secondaryColor = isIncome ? '#3B82F6' : '#EF4444'

  try {
    const res = await getPresidentTrend(trendType.value)
    const chartData = res as any

    const legendName = isIncome ? '本期收入' : '本期支出'
    const legendLast = isIncome ? '同期收入' : '同期支出'

    const option = {
      grid: { top: 40, right: 30, bottom: 20, left: 40, containLabel: true },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        padding: [8, 12],
        textStyle: { color: '#1e293b', fontSize: 13 },
        extraCssText: 'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); border-radius: 8px;'
      },
      legend: {
        top: 0,
        right: 0,
        itemWidth: 8,
        itemHeight: 8,
        textStyle: { color: '#94a3b8' }
      },
      xAxis: {
        type: 'category',
        data: chartData.xAxis,
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#94a3b8', fontSize: 12, margin: 12 }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } },
        axisLabel: { color: '#94a3b8', fontSize: 12 }
      },
      series: [
        {
          name: legendName,
          data: chartData.current,
          type: 'line',
          smooth: true,
          showSymbol: false,
          symbolSize: 8,
          itemStyle: { color: primaryColor },
          lineStyle: { width: 3, color: primaryColor },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: isIncome ? 'rgba(13, 189, 168, 0.2)' : 'rgba(245, 158, 11, 0.2)' },
              { offset: 1, color: isIncome ? 'rgba(13, 189, 168, 0)' : 'rgba(245, 158, 11, 0)' }
            ])
          }
        },
        {
          name: legendLast,
          data: chartData.last,
          type: 'line',
          smooth: true,
          showSymbol: false,
          lineStyle: { width: 3, color: secondaryColor, type: 'dotted' },
          itemStyle: { color: secondaryColor }
        }
      ]
    }
    lineChart.setOption(option, true)
  } catch (err) {
    console.error('Update chart error:', err)
  }
}

watch(trendType, () => {
  updateChart()
})

onMounted(() => {
  nextTick(async () => {
    await fetchData()
    initCharts()
    window.addEventListener('resize', handleResize)
  })
})

const handleResize = () => { lineChart?.resize() }
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
})
</script>

<style scoped lang="scss">
.cockpit-container {
  padding-bottom: 20px;
}

.nr {
  margin-right: 4px;
}

.ml-3 {
  margin-left: 12px;
}

.mt-6 {
  margin-top: 20px;
}

// --- Header ---
.section-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding: 0 4px;

  .title-group {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .title {
      font-size: 24px;
      font-weight: 700;
      color: var(--text-primary);
      line-height: 1.2;
    }

    .subtitle {
      font-size: 13px;
      color: var(--text-secondary);
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 16px;

    .update-time {
      font-size: 13px;
      color: #64748b;
      font-weight: 500;
      background: rgba(241, 245, 249, 0.8);
      padding: 4px 12px;
      border-radius: 20px;
      border: 1px solid rgba(226, 232, 240, 0.8);
    }
  }
}

// --- KPI Cards ---
.kpi-card {
  padding: 16px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid var(--border-color-light);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 160px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  // Decorate Background
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 100px;
    height: 100px;
    background: radial-gradient(circle at top right, var(--kpi-glow), transparent 70%);
    opacity: 0.1;
    z-index: 0;
  }

  // Type Colors (Updated)
  &.kpi-blue {
    --kpi-glow: var(--info);
  }

  &.kpi-teal {
    --kpi-glow: var(--success);
  }

  &.kpi-orange {
    --kpi-glow: var(--warning);
  }

  &.kpi-coral {
    --kpi-glow: var(--error);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.05);
    border-color: var(--primary-color);
  }

  .kpi-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;

    .label {
      font-size: 14px;
      color: var(--text-secondary);
      font-weight: 600;
    }

    .badge {
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: 12px;
      font-weight: 700;
      padding: 2px 6px;
      border-radius: 6px;

      &.up {
        color: var(--success);
        background: rgba(16, 185, 129, 0.1);
      }

      &.down {
        color: var(--error);
        background: rgba(239, 68, 68, 0.1);
      }
    }
  }

  .kpi-main {
    position: relative;
    z-index: 1;
    margin: 8px 0;

    .value-group {
      display: flex;
      align-items: baseline;
      gap: 2px;

      .currency {
        font-size: 16px;
        color: var(--text-secondary);
        font-weight: 600;
      }

      .value {
        font-size: 28px;
        color: var(--text-primary);
        font-weight: 700;
        letter-spacing: -0.5px;
      }

      .unit {
        font-size: 13px;
        color: var(--text-secondary);
        margin-left: 2px;
      }
    }

    .trend-visual {
      position: absolute;
      bottom: 4px;
      right: -10px;
      width: 80px;
      height: 24px;
      opacity: 0.8;

      .trend-svg {
        width: 100%;
        height: 100%;
      }
    }
  }

  .kpi-footer {
    position: relative;
    z-index: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;

    .sub-label {
      font-size: 12px;
      color: var(--text-placeholder);
    }

    .kpi-progress {
      flex: 1;

      :deep(.el-progress__text) {
        font-size: 12px !important;
      }
    }
  }
}

// --- Common Glass Panels ---
.glass-panel {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid var(--border-color-light);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  padding: 16px;
  height: 100%;
}

.text-panel {
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.list-panel {
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.grid-panel {
  min-height: 300px;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .title {
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
    display: block;
  }



  .custom-radio {
    :deep(.el-radio-button__inner) {
      background: transparent;
      border: 1px solid #e2e8f0;
      color: var(--text-secondary);
      padding: 6px 16px;
      box-shadow: none !important;
    }

    :deep(.el-radio-button:first-child .el-radio-button__inner) {
      border-radius: 8px 0 0 8px;
    }

    :deep(.el-radio-button:last-child .el-radio-button__inner) {
      border-radius: 0 8px 8px 0;
    }

    :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
      background: rgba(13, 189, 168, 0.1);
      border-color: var(--primary-color);
      color: var(--primary-color);
    }
  }
}

.chart-container {
  flex: 1;
  width: 100%;
}

// --- Donut Chart Layout ---
.chart-wrapper.circle-layout {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 32px;

  .donut-group {
    position: relative;

    .donut-chart-box {
      position: relative;
      z-index: 2;

      .center-text {
        display: flex;
        flex-direction: column;
        align-items: center;

        .val {
          font-size: 24px;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1;
        }

        .lbl {
          font-size: 12px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }

    .ring-bg {
      position: absolute;
      top: -10px;
      left: -10px;
      right: -10px;
      bottom: -10px;
      border-radius: 50%;
      border: 1px dashed #e2e8f0;
      z-index: 1;
      animation: spin 30s linear infinite;
    }
  }

  .legend-list {
    width: 100%;
    display: flex;
    justify-content: space-around;

    .legend-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 6px;

      .dot {
        width: 8px;
        height: 8px;
        border-radius: 2px;
      }

      .name {
        font-size: 12px;
        color: var(--text-secondary);
      }

      .val {
        font-size: 14px;
        font-weight: 700;
        color: var(--text-primary);
      }
    }
  }
}

// --- Premium Table ---
.premium-table-clean {
  :deep(th.el-table__cell) {
    background: transparent !important;
    color: var(--text-secondary);
    font-weight: 600;
    font-size: 12px;
    padding: 8px 0;
    border-bottom: 1px solid #f1f5f9;
  }

  :deep(td.el-table__cell) {
    border-bottom: 1px solid #f8fafc;
    padding: 12px 0;
  }

  :deep(tr:hover > td.el-table__cell) {
    background: #f8fafc !important;
  }

  .rank-num {
    font-size: 12px;
    font-weight: 700;
    color: var(--text-placeholder);

    &.top-1 {
      color: #d97706;
    }

    &.top-2 {
      color: #0284c7;
    }

    &.top-3 {
      color: #ea580c;
    }
  }

  .trend-tag {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 2px;
    font-size: 13px;
    font-weight: 600;

    &.up {
      color: var(--success);
    }

    &.down {
      color: var(--error);
    }
  }
}

// --- Mini Stats Grid ---
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  flex: 1;

  .mini-stat-card {
    background: var(--gray-50);
    border: 1px solid var(--border-color-light);
    border-radius: 12px;
    padding: 16px;
    display: flex;
    flex-direction: row; // Changed to row for horizontal layout
    justify-content: space-between; // Space between content and chart
    align-items: center;
    gap: 12px;
    transition: all 0.3s;

    &:hover {
      background: #fff;
      border-color: var(--primary-color);
      transform: translateY(-2px);
      box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.05);
    }

    .card-left {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .header-row {
        display: flex;
        align-items: center;
        gap: 8px;

        .icon-layer {
          width: 32px;
          height: 32px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: white; // Added color

          &.blue {
            background: #eff6ff;
            color: #3B82F6;
          }

          &.coral {
            background: #fff1f1;
            color: #FF6B6B;
          }

          &.teal {
            background: #f0fdfa;
            color: #0dbda8;
          }

          &.orange {
            background: #fff7ed;
            color: #F59E0B;
          }
        }

        .lbl {
          font-size: 13px;
          color: var(--text-secondary);
          font-weight: 500;
        }
      }

      .val {
        font-size: 24px;
        font-weight: 700;
        color: var(--text-primary);
        letter-spacing: -0.5px;
      }
    }

    .card-right {
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0.8;
    }
  }
}

// Animation
@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.animate-enter {
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// Dark Mode Overrides
[data-theme='dark'] {

  .kpi-card,
  .glass-panel,
  .mini-stat-card {
    background: #1e293b;
    border-color: #334155;

    &:hover {
      background: #334155;
    }

    &::before {
      display: none;
    }

    // Remove radial gradient in dark mode
  }

  .mini-stat-card {
    background: #1e293b;

    .icon-layer.blue {
      background: rgba(59, 130, 246, 0.2);
    }

    .icon-layer.coral {
      background: rgba(255, 107, 107, 0.2);
    }

    .icon-layer.teal {
      background: rgba(13, 189, 168, 0.2);
    }

    .icon-layer.orange {
      background: rgba(245, 158, 11, 0.2);
    }
  }

  .premium-table-clean {
    :deep(th.el-table__cell) {
      border-bottom-color: #334155;
    }

    :deep(td.el-table__cell) {
      border-bottom-color: #334155;
    }

    :deep(tr:hover > td.el-table__cell) {
      background: #334155 !important;
    }
  }
}

// === Cockpit 大屏专属浅色毛玻璃气泡 ===
:global(.cockpit-glass-tooltip) {
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(16px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(16px) saturate(180%) !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
  border-radius: 12px !important;
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.15) !important;
  padding: 12px 18px !important;
  color: #1e293b !important;
}

:global(.cockpit-glass-tooltip .el-popper__arrow::before) {
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
}

.indicator-tooltip-content {
  min-width: 270px;
  max-width: 400px;
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

@media (max-width: 768px) {
  .section-header-modern {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    margin-bottom: 16px;
    padding: 0;

    .title-group .title {
      font-size: 16px;
      font-weight: 600;
      line-height: 1.35;
      letter-spacing: 0;
    }

    .actions {
      flex-wrap: wrap;
      align-items: center;
      gap: 10px;
      width: 100%;
    }
  }

  /* gutter 仅水平方向，纵向堆叠时卡片会贴在一起 */
  .cockpit-container .top-cards :deep(.el-col:not(:last-child)),
  .cockpit-container .chart-section :deep(.el-col:not(:last-child)),
  .cockpit-container .bottom-section :deep(.el-col:not(:last-child)) {
    margin-bottom: 16px;
  }
}
</style>
