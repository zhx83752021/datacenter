<template>
    <div class="analysis-container">
        <!-- Header: Indicator Summary -->
        <div class="glass-panel indicator-header animate-enter">
            <div class="header-left">
                <el-button link icon="ArrowLeft" @click="$router.push('/monitor')" class="back-btn">返回监控</el-button>
                <div class="info-block">
                    <div class="icon-wrap" :style="{ backgroundColor: 'rgba(13, 189, 168, 0.1)', color: '#0dbda8' }">
                        <el-icon :size="32">
                            <component :is="indicatorInfo.icon" />
                        </el-icon>
                    </div>
                    <div class="title-block">
                        <div class="top-row">
                            <h1>{{ indicatorInfo.name }}</h1>
                            <span class="status-badge">正常</span>
                        </div>
                        <div class="tags">
                            <span class="tag-item">{{ indicatorInfo.category }}</span>
                            <span class="tag-item">月度指标</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="header-right">
                <div class="kpi-group">
                    <div class="kpi-item">
                        <label>当前值</label>
                        <div class="value-box">
                            <span class="value metric-value">{{ indicatorInfo.value }}</span>
                            <span class="unit">{{ indicatorInfo.unit }}</span>
                        </div>
                    </div>
                    <div class="divider-v"></div>
                    <div class="kpi-item">
                        <label>目标值</label>
                        <div class="value-box">
                            <span class="value target">45,000</span>
                        </div>
                    </div>
                    <div class="divider-v"></div>
                    <div class="kpi-item">
                        <label>完成率</label>
                        <div class="value-box">
                            <span class="value rate up">100.5%</span>
                        </div>
                    </div>
                    <div class="divider-v"></div>
                    <div class="kpi-action">
                        <el-button type="warning" plain icon="Warning" circle size="large"
                            @click="feedbackVisible = true" title="数据存疑反馈"></el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Content: Tabs and Charts -->
        <div class="analysis-body mt-4 animate-enter" style="animation-delay: 0.1s">
            <el-row :gutter="20">
                <el-col :span="24">
                    <div class="glass-panel chart-panel">
                        <div class="panel-header">
                            <div class="custom-tabs">
                                <div class="tab-item" :class="{ active: activeTab === 'trend' }"
                                    @click="activeTab = 'trend'">趋势分析</div>
                                <div class="tab-item" :class="{ active: activeTab === 'structure' }"
                                    @click="activeTab = 'structure'">结构分解</div>
                                <div class="tab-item" :class="{ active: activeTab === 'ranking' }"
                                    @click="activeTab = 'ranking'">科室排名</div>
                            </div>
                            <div class="tools">
                                <div class="radio-pill-group">
                                    <span class="pill-option" :class="{ active: dateRange === 'near6' }"
                                        @click="dateRange = 'near6'">近6月</span>
                                    <span class="pill-option" :class="{ active: dateRange === 'near12' }"
                                        @click="dateRange = 'near12'">近1年</span>
                                </div>
                                <!-- <el-divider direction="vertical" /> -->
                                <el-button icon="Download" circle class="ml-4 action-btn"></el-button>
                            </div>
                        </div>
                        <div class="chart-container" ref="chartRef"></div>
                    </div>
                </el-col>
            </el-row>

            <!-- Detailed Table -->
            <el-row :gutter="20" class="mt-4 animate-enter" style="animation-delay: 0.2s">
                <el-col :span="24">
                    <div class="glass-panel">
                        <div class="panel-header simple mb-4">
                            <div class="title-with-icon">
                                <el-icon class="mr-2 text-primary">
                                    <List />
                                </el-icon>
                                <span class="title">明细数据</span>
                            </div>
                            <el-button type="primary" link icon="Download">导出Excel</el-button>
                        </div>
                        <el-table :data="tableData" class="premium-table" style="width: 100%">
                            <el-table-column prop="date" label="时间" width="180" />
                            <el-table-column prop="dept" label="科室" width="180">
                                <template #default="{ row }">
                                    <span style="font-weight: 500">{{ row.dept }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="value" label="数值" align="right">
                                <template #default="{ row }">
                                    <span class="metric-value text-sm">{{ row.value }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="target" label="目标值" align="right" />
                            <el-table-column prop="rate" label="完成率" align="right">
                                <template #default="scope">
                                    <div class="rate-cell">
                                        <span class="val">{{ scope.row.rate }}</span>
                                        <div class="mini-progress">
                                            <div class="bar"
                                                :style="{ width: Math.min(scope.row.percentage, 100) + '%', background: scope.row.percentage >= 100 ? 'var(--success)' : 'var(--primary)' }">
                                            </div>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- Feedback Dialog -->
        <el-dialog v-model="feedbackVisible" title="数据存疑反馈" width="500px" class="glass-dialog">
            <el-form :model="feedbackForm" label-position="top">
                <el-form-item label="问题类型">
                    <el-select v-model="feedbackForm.type" class="w-100 glass-select">
                        <el-option label="数据缺失" value="missing" />
                        <el-option label="计算错误" value="calc" />
                        <el-option label="口径疑问" value="definition" />
                        <el-option label="其他" value="other" />
                    </el-select>
                </el-form-item>
                <el-form-item label="问题描述">
                    <el-input v-model="feedbackForm.desc" type="textarea" :rows="4" placeholder="请详细描述您遇到的问题..."
                        class="glass-input" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="feedbackVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, reactive, nextTick, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { ArrowLeft, UserFilled, Warning, Download, List } from '@element-plus/icons-vue'

const route = useRoute()
const activeTab = ref('trend')
const dateRange = ref('near6')
const chartRef = ref<HTMLElement | null>(null)
let myChart: echarts.ECharts | null = null

// Feedback
const feedbackVisible = ref(false)
const feedbackForm = reactive({ type: 'missing', desc: '' })

const submitFeedback = () => {
    ElMessage.success('反馈已提交，管理员将尽快处理')
    feedbackVisible.value = false
    feedbackForm.desc = ''
}

// Mock Info
const indicatorInfo = ref({
    id: 1,
    name: '门急诊总人次',
    category: '运营效率',
    value: '45,231',
    unit: '人次',
    color: '#0dbda8',
    icon: 'UserFilled'
})

const tableData = ref([
    { date: '2024-05', dept: '内科', value: '12,000', target: '11,000', rate: '109%', percentage: 100 },
    { date: '2024-05', dept: '外科', value: '9,500', target: '10,000', rate: '95%', percentage: 95 },
    { date: '2024-05', dept: '儿科', value: '5,000', target: '4,500', rate: '111%', percentage: 100 },
    { date: '2024-05', dept: '妇产科', value: '4,200', target: '4,000', rate: '105%', percentage: 100 },
    { date: '2024-05', dept: '五官科', value: '3,000', target: '3,200', rate: '93%', percentage: 93 },
])

const initChart = () => {
    if (!chartRef.value) return
    if (myChart) myChart.dispose()

    myChart = echarts.init(chartRef.value)
    let option = {}

    const commonGrid = { top: '15%', right: '2%', bottom: '10%', left: '3%', containLabel: true }

    if (activeTab.value === 'trend') {
        option = {
            tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', textStyle: { color: '#333' } },
            legend: { data: ['本期值', '去年同期', '目标值'], top: 0, icon: 'circle' },
            grid: commonGrid,
            xAxis: {
                type: 'category',
                data: ['12月', '1月', '2月', '3月', '4月', '5月'],
                axisLine: { show: false },
                axisTick: { show: false },
                axisLabel: { color: '#94a3b8' }
            },
            yAxis: {
                type: 'value',
                splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }
            },
            series: [
                {
                    name: '本期值',
                    type: 'line',
                    smooth: true,
                    data: [38000, 39000, 35000, 41000, 43000, 45231],
                    itemStyle: { color: '#0dbda8' },
                    symbolSize: 8,
                    areaStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(13, 189, 168,0.2)' }, { offset: 1, color: 'rgba(13, 189, 168,0)' }])
                    }
                },
                {
                    name: '去年同期',
                    type: 'line',
                    smooth: true,
                    data: [35000, 36000, 33000, 38000, 40000, 41000],
                    itemStyle: { color: '#94a3b8' },
                    lineStyle: { type: 'dashed' },
                    symbol: 'none'
                },
                {
                    name: '目标值',
                    type: 'line',
                    data: [40000, 40000, 40000, 40000, 42000, 45000],
                    itemStyle: { color: '#FFB84D' },
                    lineStyle: { width: 2 },
                    symbol: 'none'
                }
            ]
        }
    } else if (activeTab.value === 'structure') {
        option = {
            tooltip: { trigger: 'item' },
            legend: { orient: 'vertical', left: 'right', top: 'center' },
            series: [
                {
                    name: '科室构成',
                    type: 'pie',
                    radius: ['50%', '75%'],
                    center: ['40%', '50%'],
                    itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                    data: [
                        { value: 12000, name: '内科', itemStyle: { color: '#0dbda8' } },
                        { value: 9500, name: '外科', itemStyle: { color: '#4FC3F7' } },
                        { value: 5000, name: '儿科', itemStyle: { color: '#FFB84D' } },
                        { value: 4200, name: '妇产科', itemStyle: { color: '#FF6B6B' } },
                        { value: 14531, name: '其他', itemStyle: { color: '#94a3b8' } }
                    ]
                }
            ]
        }
    } else if (activeTab.value === 'ranking') {
        option = {
            tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
            grid: commonGrid,
            xAxis: { type: 'value', splitLine: { show: false } },
            yAxis: { type: 'category', data: ['五官科', '妇产科', '儿科', '外科', '内科'], axisLine: { show: false }, axisTick: { show: false } },
            series: [
                {
                    name: '总人次',
                    type: 'bar',
                    data: [3000, 4200, 5000, 9500, 12000],
                    barWidth: 20,
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [{ offset: 0, color: '#0dbda8' }, { offset: 1, color: 'rgba(13, 189, 168, 0.4)' }]),
                        borderRadius: [0, 4, 4, 0]
                    },
                    label: { show: true, position: 'right', color: '#64748b' }
                }
            ]
        }
    }

    myChart?.setOption(option)
}

watch([activeTab, dateRange], () => {
    initChart()
})

onMounted(() => {
    nextTick(() => {
        initChart()
        window.addEventListener('resize', () => myChart?.resize())
    })
})
onUnmounted(() => {
    window.removeEventListener('resize', () => myChart?.resize())
    myChart?.dispose()
})
</script>

<style scoped lang="scss">
.analysis-container {
    padding-bottom: 40px;

    // Header Styles
    .indicator-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 30px;

        .header-left {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .back-btn {
                font-size: 14px;
                color: var(--text-secondary);
                margin-left: -8px;

                &:hover {
                    color: var(--primary-color);
                }
            }

            .info-block {
                display: flex;
                align-items: center;
                gap: 20px;

                .icon-wrap {
                    width: 56px;
                    height: 56px;
                    border-radius: 14px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .title-block {
                    .top-row {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        margin-bottom: 6px;

                        h1 {
                            font-size: 20px;
                            font-weight: 700;
                            color: var(--text-primary);
                            margin: 0;
                        }

                        .status-badge {
                            background: #ecfdf5;
                            color: var(--success);
                            font-size: 12px;
                            padding: 2px 8px;
                            border-radius: 4px;
                            font-weight: 600;
                        }
                    }

                    .tags {
                        display: flex;
                        gap: 8px;

                        .tag-item {
                            font-size: 12px;
                            color: var(--text-secondary);
                            background: #f1f5f9;
                            padding: 2px 8px;
                            border-radius: 4px;
                        }
                    }
                }
            }
        }

        .header-right {
            .kpi-group {
                display: flex;
                align-items: center;
                gap: 24px;

                .divider-v {
                    width: 1px;
                    height: 32px;
                    background: #e2e8f0;
                }

                .kpi-item {
                    label {
                        font-size: 12px;
                        color: var(--text-secondary);
                        margin-bottom: 4px;
                        display: block;
                    }

                    .value-box {
                        display: flex;
                        align-items: baseline;
                        gap: 4px;

                        .value {
                            font-size: 24px;
                            font-weight: 700;
                            color: var(--text-primary);
                            letter-spacing: -0.5px;

                            &.metric-value {
                                font-family: var(--font-family-base);
                            }

                            // Uses global font
                            &.rate.up {
                                color: var(--success);
                            }
                        }

                        .unit {
                            font-size: 12px;
                            color: var(--text-secondary);
                        }
                    }
                }
            }
        }
    }

    // Chart Panel
    .chart-panel {
        padding: 0; // Reset p-24 from glass-panel if needed, but usually we want padding.
        // We will keep default glass-panel padding and style header inside

        .panel-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;

            .custom-tabs {
                display: flex;
                background: #f1f5f9;
                padding: 4px;
                border-radius: 10px;

                .tab-item {
                    padding: 8px 24px;
                    font-size: 14px;
                    color: var(--text-secondary);
                    cursor: pointer;
                    border-radius: 8px;
                    font-weight: 500;
                    transition: all 0.3s;

                    &.active {
                        background: #fff;
                        color: var(--primary-color);
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                        font-weight: 600;
                    }
                }
            }

            .tools {
                display: flex;
                align-items: center;

                .radio-pill-group {
                    display: flex;
                    border: 1px solid #e2e8f0;
                    border-radius: 8px;
                    overflow: hidden;

                    .pill-option {
                        padding: 6px 16px;
                        font-size: 12px;
                        cursor: pointer;
                        background: #fff;
                        color: var(--text-secondary);
                        border-right: 1px solid #e2e8f0;
                        transition: all 0.2s;

                        &:last-child {
                            border-right: none;
                        }

                        &.active {
                            background: var(--primary-bg);
                            color: var(--primary-color);
                            font-weight: 600;
                        }

                        &:hover:not(.active) {
                            background: #f8fafc;
                        }
                    }
                }

                .action-btn {
                    border-color: #e2e8f0;
                    color: var(--text-secondary);

                    &:hover {
                        color: var(--primary-color);
                        border-color: var(--primary-color);
                        background: var(--primary-bg);
                    }
                }
            }
        }

        .chart-container {
            height: 420px;
            width: 100%;
        }
    }

    // Table
    .premium-table {
        .rate-cell {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 8px;

            .val {
                width: 40px;
                text-align: right;
            }

            .mini-progress {
                width: 60px;
                height: 6px;
                background: #f1f5f9;
                border-radius: 3px;
                overflow: hidden;

                .bar {
                    height: 100%;
                    border-radius: 3px;
                }
            }
        }
    }

    .text-primary {
        color: var(--primary-color);
    }

    .mb-4 {
        margin-bottom: 16px;
    }

    .ml-4 {
        margin-left: 16px;
    }

    .mr-2 {
        margin-right: 8px;
    }
}

.glass-panel {
    background: #fff;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
    padding: 24px;
    margin-bottom: 24px;
    transition: all 0.3s;

    &:hover {
        border-color: rgba(13, 189, 168, 0.3);
        box-shadow: 0 10px 30px -10px rgba(13, 189, 168, 0.05);
    }
}

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
