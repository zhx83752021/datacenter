<template>
    <div class="dept-cockpit-container animate-enter">
        <!-- Header -->
        <div class="section-header-modern">
            <div class="title-group">
                <div class="title">科室运营驾驶舱</div>
            </div>
            <div class="actions">
                <div class="filter-group">
                    <span class="filter-label">时间范围:</span>
                    <el-radio-group v-model="timeRange" size="small" class="custom-radio">
                        <el-radio-button label="today">今日</el-radio-button>
                        <el-radio-button label="week">本周</el-radio-button>
                        <el-radio-button label="month">本月</el-radio-button>
                    </el-radio-group>
                </div>
                <div class="divider-v"></div>
                <el-select v-model="currentDept" class="glass-select" placeholder="选择科室" size="large">
                    <template #prefix><el-icon>
                            <OfficeBuilding />
                        </el-icon></template>
                    <el-option label="心血管内科" value="cardio" />
                    <el-option label="呼吸内科" value="resp" />
                    <el-option label="骨科" value="ortho" />
                    <el-option label="普外科" value="general" />
                </el-select>
                <div class="divider-v"></div>
                <span class="update-time">UPDATE: 14:00</span>
                <el-button circle icon="Back" class="back-btn" @click="$router.push('/cockpit')"
                    title="返回院级"></el-button>
            </div>
        </div>

        <!-- State Cards -->
        <el-row :gutter="20" class="mb-4">
            <el-col :xs="24" :sm="12" :md="6" v-for="(card, index) in stateCards" :key="card.title">
                <div class="glass-panel stat-card" :class="card.colorType"
                    :style="{ animationDelay: index * 0.1 + 's' }">
                    <div class="icon-box">
                        <el-icon>
                            <component :is="card.icon" />
                        </el-icon>
                    </div>
                    <div class="content">
                        <div class="label">{{ card.title }}</div>
                        <div class="value-row">
                            <span class="value metric-value">
                                <CountTo :endVal="parseFloat(card.value.replace(/,/g, ''))"
                                    :decimals="card.value.includes('.') ? 1 : 0" />
                            </span>
                            <span class="unit">{{ card.unit }}</span>
                        </div>
                    </div>

                    <!-- Tiny Sparkline Visual -->
                    <div class="mini-trend">
                        <svg viewBox="0 0 100 40" preserveAspectRatio="none">
                            <path :d="card.trendPath" fill="none" stroke-width="2.5" stroke-linecap="round"
                                stroke-linejoin="round" />
                        </svg>
                    </div>

                    <!-- Decor -->
                    <div class="card-decor"></div>
                </div>
            </el-col>
        </el-row>

        <!-- Main Charts Area (Trend, CMI, and Rankings in one row) -->
        <el-row :gutter="20" class="mb-4">
            <!-- Left: Trend Analysis -->
            <el-col :xs="24" :lg="10">
                <div class="glass-panel chart-panel">
                    <div class="panel-header">
                        <div class="title-with-icon">
                            <span class="title">近7日服务量趋势</span>
                        </div>
                        <div class="radio-pill-group">
                            <span class="pill-option" :class="{ active: trendType === 'outpatient' }"
                                @click="trendType = 'outpatient'">门诊</span>
                            <span class="pill-option" :class="{ active: trendType === 'inpatient' }"
                                @click="trendType = 'inpatient'">入院</span>
                        </div>
                    </div>
                    <div class="chart-container" ref="trendChartRef"></div>
                </div>
            </el-col>
            <!-- Middle: CMI Structure -->
            <el-col :xs="24" :lg="7">
                <div class="glass-panel chart-panel">
                    <div class="panel-header">
                        <div class="title-with-icon">
                            <span class="title">病种结构 (CMI Top 5)</span>
                        </div>
                    </div>
                    <div class="chart-container" ref="cmiChartRef"></div>
                </div>
            </el-col>
            <!-- Right: Department Rankings (Moved here) -->
            <el-col :xs="24" :lg="7">
                <div class="glass-panel chart-panel">
                    <div class="panel-header">
                        <div class="title-with-icon">
                            <span class="title">全院科室排名</span>
                        </div>
                        <el-dropdown trigger="click">
                            <span class="el-dropdown-link cursor-pointer">
                                {{ rankingMetric === 'income' ? '总收入' : (rankingMetric === 'cmi' ? 'CMI' : '效率') }}
                                <el-icon class="el-icon--right"><arrow-down /></el-icon>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item @click="rankingMetric = 'income'">总收入</el-dropdown-item>
                                    <el-dropdown-item @click="rankingMetric = 'cmi'">CMI指数</el-dropdown-item>
                                    <el-dropdown-item @click="rankingMetric = 'efficiency'">效率得分</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div class="chart-container" ref="rankChartRef"></div>
                </div>
            </el-col>
        </el-row>

        <!-- Bottom Row -->
        <el-row :gutter="20" class="mb-4">
            <!-- Income Structure -->
            <el-col :xs="24" :lg="8">
                <div class="glass-panel chart-panel">
                    <div class="panel-header">
                        <div class="title-with-icon">
                            <span class="title">科室收入构成</span>
                        </div>
                    </div>
                    <div class="chart-container" ref="incomeChartRef"></div>
                </div>
            </el-col>
            <!-- Doctor Performance -->
            <el-col :xs="24" :lg="16">
                <div class="glass-panel list-panel">
                    <div class="panel-header">
                        <div class="title-with-icon">
                            <span class="title">医师绩效排名</span>
                        </div>
                        <el-button link type="primary">查看全部 <el-icon>
                                <ArrowRight />
                            </el-icon></el-button>
                    </div>
                    <el-table :data="doctorData" class="premium-table" style="width: 100%" height="260px">
                        <el-table-column type="index" label="排名" width="60" align="center">
                            <template #default="{ $index }">
                                <div class="rank-badge" :class="'rank-' + ($index + 1)">{{ $index + 1 }}</div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="name" label="医师姓名" width="120">
                            <template #default="{ row }">
                                <div class="user-info">
                                    <el-avatar :size="24" class="avatar-tiny"
                                        :style="{ background: getRandomColor(row.name) }">{{ row.name.charAt(0)
                                        }}</el-avatar>
                                    <span class="name">{{ row.name }}</span>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="title" label="职称" width="100">
                            <template #default="{ row }"><el-tag size="small" effect="plain" round>{{ row.title
                            }}</el-tag></template>
                        </el-table-column>
                        <el-table-column prop="outpatient" label="门诊人次" align="right" sortable>
                            <template #default="{ row }"><span class="font-num">{{ row.outpatient }}</span></template>
                        </el-table-column>
                        <el-table-column prop="surgery" label="手术例数" align="right" sortable>
                            <template #default="{ row }"><span class="font-num">{{ row.surgery }}</span></template>
                        </el-table-column>
                        <el-table-column prop="income" label="创收 (万)" align="right" sortable>
                            <template #default="{ row }"><span class="font-num text-primary fw-bold">{{ row.income
                            }}</span></template>
                        </el-table-column>
                        <el-table-column label="效率评分" align="right">
                            <template #default>
                                <el-rate v-model="mockRate" disabled text-color="#ff9900" size="small" />
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-col>
        </el-row>


    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { OfficeBuilding, ArrowRight } from '@element-plus/icons-vue'
import CountTo from '../../components/common/CountTo.vue'
import { getDeptMetrics, getDeptTrend, getDoctorPerformance, getDeptRankings } from '../../api/cockpit'

const currentDept = ref('cardio')
const trendType = ref('outpatient')
const timeRange = ref('month')
const rankingMetric = ref('income')
const mockRate = ref(4.5)
const loading = ref(false)

const stateCards = ref<any[]>([])
const doctorData = ref<any[]>([])

const fetchDeptData = async () => {
    loading.value = true
    try {
        const [metrics, doctors] = await Promise.all([
            getDeptMetrics(currentDept.value),
            getDoctorPerformance(currentDept.value)
        ])
        stateCards.value = metrics as any
        doctorData.value = doctors as any
    } catch (error) {
        console.error('Failed to fetch department data:', error)
    } finally {
        loading.value = false
    }
}

// Updated to professional palette
const getRandomColor = (name: string) => {
    const colors = ['#60A5FA', '#34D399', '#FBBF24', '#A78BFA', '#F472B6', '#818CF8']
    return colors[name.charCodeAt(0) % colors.length]
}

// Refs & Charts
const trendChartRef = ref<HTMLElement | null>(null)
const cmiChartRef = ref<HTMLElement | null>(null)
const incomeChartRef = ref<HTMLElement | null>(null)
const rankChartRef = ref<HTMLElement | null>(null)
let trendChart: echarts.ECharts | null = null
let cmiChart: echarts.ECharts | null = null
let incomeChart: echarts.ECharts | null = null
let rankChart: echarts.ECharts | null = null

const initTrendChart = () => {
    if (!trendChartRef.value) return
    trendChart = echarts.init(trendChartRef.value)
    if (trendChart) {
        trendChart.setOption({
            tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', padding: [8, 12], textStyle: { color: '#333' } },
            grid: { top: 30, right: 20, bottom: 20, left: 40, containLabel: true },
            xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#94a3b8' } },
            yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }, axisLabel: { color: '#94a3b8' } },
            series: [{
                name: '人次', type: 'line', smooth: true, symbolSize: 8,
                data: [120, 132, 101, 134, 190, 230, 210],
                lineStyle: { width: 3, color: '#0dbda8' },
                itemStyle: { color: '#0dbda8' },
                areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(13, 189, 168, 0.2)' }, { offset: 1, color: 'rgba(13, 189, 168, 0)' }]) }
            }]
        })
    }
}

const initCmiChart = () => {
    if (!cmiChartRef.value) return
    cmiChart = echarts.init(cmiChartRef.value)
    if (cmiChart) {
        cmiChart.setOption({
            tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
            grid: { top: 10, right: 30, bottom: 20, left: 10, containLabel: true },
            xAxis: { type: 'value', splitLine: { show: false } },
            yAxis: { type: 'category', data: ['冠心病', '心力衰竭', '心律失常', '心肌梗死', '瓣膜病'], axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#64748b' } },
            series: [{
                type: 'bar', barWidth: 16,
                data: [0.8, 1.1, 1.2, 2.5, 3.1],
                itemStyle: { borderRadius: [0, 8, 8, 0], color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [{ offset: 0, color: '#F59E0B' }, { offset: 1, color: '#FDE68A' }]) },
                label: { show: true, position: 'right', color: '#F59E0B', fontWeight: 'bold' }
            }]
        })
    }
}

const initIncomeChart = () => {
    if (!incomeChartRef.value) return
    incomeChart = echarts.init(incomeChartRef.value)
    if (incomeChart) {
        incomeChart.setOption({
            tooltip: { trigger: 'item' },
            legend: { bottom: 0, icon: 'circle', itemWidth: 8, itemHeight: 8 },
            color: ['#0dbda8', '#3B82F6', '#F59E0B', '#FF6B6B'],
            series: [{
                type: 'pie', radius: ['45%', '70%'], center: ['50%', '40%'],
                itemStyle: { borderColor: '#fff', borderWidth: 2 },
                label: { show: true, formatter: '{b}:\n{d}%', color: '#475569' },
                data: [
                    { value: 1048, name: '医疗服务' }, { value: 735, name: '药品' },
                    { value: 580, name: '耗材' }, { value: 484, name: '检查检验' }
                ]
            }]
        })
    }
}

const initRankChart = () => {
    if (!rankChartRef.value) return
    if (rankChart) rankChart.dispose()
    const chart = echarts.init(rankChartRef.value)
    chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { top: 10, right: 30, bottom: 20, left: 10, containLabel: true },
        xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { type: 'dashed' } } },
        yAxis: {
            type: 'category',
            data: ['儿科', '普外科', '骨科', '呼吸内科', '心血管内科'].reverse(),
            axisLine: { show: false },
            axisTick: { show: false }
        },
        series: [{
            name: '指标值',
            type: 'bar',
            barWidth: 20,
            data: [85, 92, 110, 125, 140],
            itemStyle: {
                borderRadius: [0, 10, 10, 0],
                color: (params: any) => {
                    return params.name === '心血管内科' ? '#0dbda8' : '#cbd5e1'
                }
            },
            label: { show: true, position: 'right', color: '#64748b' }
        }]
    })
    rankChart = chart
}

const handleResize = () => {
    trendChart?.resize(); cmiChart?.resize(); incomeChart?.resize(); rankChart?.resize()
}

watch([trendType], async () => {
    const res = await getDeptTrend(currentDept.value, trendType.value)
    const data = res as any
    trendChart?.setOption({
        xAxis: { data: data.days },
        series: [{ data: data.values }]
    })
})

watch([rankingMetric], async () => {
    const res = await getDeptRankings(rankingMetric.value)
    const data = res as any
    rankChart?.setOption({
        yAxis: { data: data.categories.reverse() },
        series: [{ data: data.values }]
    })
})

watch(currentDept, async () => {
    fetchDeptData()
    // 重新获取并刷新所有图表数据
    const [trendRes] = await Promise.all([
        getDeptTrend(currentDept.value, trendType.value)
    ])

    // 刷新趋势图
    const trendData = trendRes as any
    trendChart?.setOption({
        xAxis: { data: trendData.days },
        series: [{ data: trendData.values }]
    })

    // 模拟刷新病种结构和收入构成（由 fetchDeptData 内部逻辑或此处补充）
    // 为了极致体验，科室切换时重置动画
    initCmiChart()
    initIncomeChart()
}, { immediate: true })

onMounted(() => {
    nextTick(() => {
        initTrendChart(); initCmiChart(); initIncomeChart(); initRankChart()
        window.addEventListener('resize', handleResize)
    })
})
onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    trendChart?.dispose(); cmiChart?.dispose(); incomeChart?.dispose(); rankChart?.dispose()
})
</script>

<style scoped lang="scss">
.dept-cockpit-container {
    padding-bottom: 20px;
}

// Reduced from 40px

.section-header-modern {
    display: flex;
    justify-content: space-between;
    align-items: center; // Changed from flex-end for better alignment with filter
    margin-bottom: 12px;
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
    }

    .actions {
        display: flex;
        align-items: center;
        gap: 16px;

        .filter-group {
            display: flex;
            align-items: center;
            gap: 8px;

            .filter-label {
                font-size: 13px;
                color: var(--text-secondary);
                font-weight: 500;
            }

            .custom-radio {
                :deep(.el-radio-button__inner) {
                    background: transparent;
                    border: 1px solid #e2e8f0;
                    color: var(--text-secondary);
                    padding: 5px 12px;
                    font-size: 12px;
                    border-radius: 6px;
                }

                :deep(.el-radio-button:first-child .el-radio-button__inner) {
                    border-radius: 6px 0 0 6px;
                }

                :deep(.el-radio-button:last-child .el-radio-button__inner) {
                    border-radius: 0 6px 6px 0;
                }

                :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
                    background: rgba(13, 189, 168, 0.1);
                    border-color: var(--primary-color);
                    color: var(--primary-color);
                }
            }
        }

        .glass-select {
            width: 140px;

            :deep(.el-input__wrapper) {
                background: #fff;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                border-radius: 12px;
                border: 1px solid #e2e8f0;
            }
        }

        .divider-v {
            width: 1px;
            height: 16px;
            background: #cbd5e1;
        }

        .update-time {
            font-size: 11px;
            color: var(--text-secondary);
            font-family: 'JetBrains Mono', monospace;
            opacity: 0.6;
        }

        .back-btn {
            background: #fff;
            border: 1px solid #e2e8f0;
            color: var(--text-secondary);

            &:hover {
                color: var(--primary-color);
                border-color: var(--primary-color);
            }
        }
    }
}

.contrast-panel {
    min-height: 240px;
    margin-top: 10px;

    .chart-container {
        min-height: 200px;
    }
}

.glass-panel {
    background: #fff;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
    padding: 20px;
    transition: all 0.3s;
    position: relative;
    overflow: hidden;

    &:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.05);
        border-color: var(--primary-color);
    }
}

.stat-card {
    display: flex;
    align-items: center;
    gap: 16px;
    height: 100px;
    position: relative; // Reduced height slightly if needed, kept 110px or 100px

    .icon-box {
        width: 56px;
        height: 56px;
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;
        z-index: 1;
    }

    .content {
        display: flex;
        flex-direction: column;
        z-index: 1;

        .label {
            font-size: 13px;
            color: var(--text-secondary);
            margin-bottom: 4px;
            font-weight: 500;
        }

        .value-row {
            display: flex;
            align-items: baseline;
            gap: 4px;

            .value {
                font-size: 28px;
                font-weight: 700;
                color: var(--text-primary);
                letter-spacing: -0.5px;
                line-height: 1;
            }

            .unit {
                font-size: 12px;
                color: var(--text-secondary);
            }
        }
    }

    // Tiny Sparkline
    .mini-trend {
        position: absolute;
        right: 24px;
        top: 50%;
        transform: translateY(-50%);
        width: 60px;
        height: 32px;
        opacity: 0.8;

        svg {
            width: 100%;
            height: 100%;
            overflow: visible;
        }

        path {
            fill: none;
            stroke-width: 2.5;
            stroke-linecap: round;
            vector-effect: non-scaling-stroke;
        }
    }

    .card-decor {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        width: 60%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8));
        opacity: 0.5;
        z-index: 0;
    }

    &.blue {
        .icon-box {
            background: linear-gradient(135deg, #3B82F6, #60A5FA);
            box-shadow: 0 8px 16px rgba(59, 130, 246, 0.25);
        }

        .mini-trend path {
            stroke: rgba(59, 130, 246, 0.3);
        }
    }

    &.green {
        .icon-box {
            background: linear-gradient(135deg, #10B981, #34D399);
            box-shadow: 0 8px 16px rgba(16, 185, 129, 0.25);
        }

        .mini-trend path {
            stroke: rgba(16, 185, 129, 0.3);
        }
    }

    &.orange {
        .icon-box {
            background: linear-gradient(135deg, #F59E0B, #FBBF24);
            box-shadow: 0 8px 16px rgba(245, 158, 11, 0.25);
        }

        .mini-trend path {
            stroke: rgba(245, 158, 11, 0.3);
        }
    }

    &.red {
        .icon-box {
            background: linear-gradient(135deg, #EF4444, #F87171);
            box-shadow: 0 8px 16px rgba(239, 68, 68, 0.25);
        }

        .mini-trend path {
            stroke: rgba(239, 68, 68, 0.3);
        }
    }
}

.chart-panel {
    min-height: 300px;
    display: flex;
    flex-direction: column;
}

// Reduced height from 380px
.panel-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .title-with-icon {
        display: flex;
        flex-direction: column;

        .title {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-primary);
        }

        // Subtitle removed
    }
}

.chart-container {
    flex: 1;
    width: 100%;
    min-height: 260px;
}

// Reduced from 280px

.radio-pill-group {
    background: #f1f5f9;
    padding: 3px;
    border-radius: 8px;
    display: flex;
    gap: 2px;

    .pill-option {
        padding: 4px 12px;
        font-size: 12px;
        border-radius: 6px;
        cursor: pointer;
        color: var(--text-secondary);
        transition: all 0.2s;

        &.active {
            background: #fff;
            color: var(--primary-color);
            font-weight: 600;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
    }
}

.cursor-pointer {
    cursor: pointer;
}

.el-dropdown-link {
    font-size: 13px;
    color: var(--primary-color);
    font-weight: 500;
    display: flex;
    align-items: center;
    background: rgba(13, 189, 168, 0.05);
    padding: 4px 10px;
    border-radius: 6px;
    transition: all 0.2s;

    &:hover {
        background: rgba(13, 189, 168, 0.1);
    }
}

.premium-table {
    :deep(th.el-table__cell) {
        background: transparent !important;
        color: var(--text-secondary);
        font-size: 12px;
        border-bottom: 1px solid #f1f5f9;
    }

    .rank-badge {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background: #f1f5f9;
        color: var(--text-secondary);
        font-size: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        margin: 0 auto;

        &.rank-1 {
            background: #FEF3C7;
            color: #F59E0B;
        }

        &.rank-2 {
            background: #E0F2FE;
            color: #0EA5E9;
        }

        &.rank-3 {
            background: #FFEDD5;
            color: #F97316;
        }
    }

    .user-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .avatar-tiny {
            font-size: 10px;
            color: #fff;
        }

        .name {
            font-weight: 500;
            font-size: 13px;
        }
    }

    .font-num {
        font-family: 'JetBrains Mono', monospace;
        font-size: 13px;
    }

    .text-primary {
        color: var(--primary-color);
    }

    .fw-bold {
        font-weight: 600;
    }
}

.mb-4 {
    margin-bottom: 12px;
}

// Reduced margin
.animate-enter {
    animation: fadeInUp 0.5s ease-out forwards;
    opacity: 0;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(15px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
