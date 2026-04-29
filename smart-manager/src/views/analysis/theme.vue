<template>
    <div class="analysis-container">
        <div class="header-section animate-enter">
            <div class="title-group">
                <div class="icon-box">
                    <el-icon :size="20" color="#fff">
                        <DataAnalysis />
                    </el-icon>
                </div>
                <div class="text-col">
                    <span class="main">运营主题分析 - {{ getCurrentThemeName() }}</span>
                </div>
            </div>
            <div class="actions">
                <el-button round :icon="Printer" :loading="exportLoading" @click="handleExport">导出报告</el-button>
                <el-button type="primary" round :icon="Plus" @click="handleNew">新建分析</el-button>
            </div>
        </div>

        <div class="content-wrapper animate-enter" style="animation-delay: 0.1s">
            <!-- Left: Theme Navigation -->
            <div class="theme-sidebar glass-panel">
                <div class="sb-header">
                    <span class="lbl">分析主题</span>
                    <el-icon style="cursor: pointer; color: #94a3b8">
                        <Setting />
                    </el-icon>
                </div>
                <div class="theme-list">
                    <div v-for="theme in themes" :key="theme.id" class="theme-item"
                        :class="{ active: currentTheme === theme.id }" @click="currentTheme = theme.id">
                        <el-icon class="t-icon">
                            <component :is="theme.icon" />
                        </el-icon>
                        <div class="t-info">
                            <span class="t-name">{{ theme.name }}</span>
                            <span class="t-desc">{{ theme.charts }}个图表</span>
                        </div>
                        <el-icon class="arrow" v-if="currentTheme === theme.id">
                            <ArrowRight />
                        </el-icon>
                    </div>
                </div>
            </div>

            <!-- Right: Analysis Board -->
            <div class="analysis-board">
                <!-- Filter Bar -->
                <div class="filter-bar glass-panel">
                    <div class="f-group">
                        <span class="label">时间范围:</span>
                        <el-radio-group v-model="timeRange" size="small" class="glass-radio">
                            <el-radio-button label="month">本月</el-radio-button>
                            <el-radio-button label="quarter">本季</el-radio-button>
                            <el-radio-button label="year">本年</el-radio-button>
                        </el-radio-group>
                    </div>
                    <div class="divider"></div>
                    <div class="f-group">
                        <span class="label">对比维度:</span>
                        <el-select v-model="compareDim" placeholder="选择维度" size="small"
                            class="glass-select theme-compare-select">
                            <el-option label="科室环比" value="dept" />
                            <el-option label="去年同比" value="yoy" />
                            <el-option label="目标差距" value="target" />
                        </el-select>
                    </div>
                    <div class="spacer"></div>
                    <div class="update-time">数据更新: 2024-05-14 12:00</div>
                </div>

                <!-- Charts Grid -->
                <div class="charts-grid custom-scrollbar">
                    <!-- AI Insight Card -->
                    <div class="insight-card animate-enter" style="animation-delay: 0.2s">
                        <div class="card-icon"><el-icon>
                                <Cpu />
                            </el-icon></div>
                        <div class="card-content">
                            <div class="h-row">
                                <span class="h-title">智能洞察</span>
                                <el-tag size="small" effect="dark" type="success" round>耗时 0.4s</el-tag>
                            </div>
                            <p class="desc" v-html="aiInsight">
                            </p>
                        </div>
                    </div>

                    <!-- Chart Items -->
                    <div class="chart-row">
                        <div class="glass-panel chart-box big">
                            <div class="c-header">
                                <span class="title">趋势分析</span>
                                <el-button link type="primary" size="small">详情</el-button>
                            </div>
                            <div class="chart-content" ref="trendChartRef"></div>
                        </div>
                        <div class="glass-panel chart-box small">
                            <div class="c-header">
                                <span class="title">构成分布</span>
                            </div>
                            <div class="chart-content" ref="pieChartRef"></div>
                        </div>
                    </div>

                    <div class="chart-row">
                        <div class="glass-panel chart-box medium">
                            <div class="c-header">
                                <span class="title">科室排名前 10</span>
                            </div>
                            <div class="chart-content" ref="barChartRef"></div>
                        </div>
                        <div class="glass-panel chart-box medium">
                            <div class="c-header">
                                <span class="title">目标达成率</span>
                            </div>
                            <div class="chart-content" ref="gaugeChartRef"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 新建分析向导 -->
        <el-dialog v-model="createDialogVisible" title="新建运营主题分析" width="600px" append-to-body destroy-on-close
            class="premium-dialog theme-analysis-dialog-responsive">
            <el-form :model="newAnalysisForm" :rules="newAnalysisRules" ref="createFormRef" label-position="top">
                <el-form-item label="分析主题" prop="name">
                    <el-input v-model="newAnalysisForm.name" placeholder="输入分析报告名称，如：2024Q3耗材效率专项分析" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="指标领域" prop="category">
                            <el-select v-model="newAnalysisForm.category" placeholder="选择分析领域" style="width: 100%">
                                <el-option label="手术效率" value="surgical" />
                                <el-option label="收益评估" value="income" />
                                <el-option label="医疗质量" value="quality" />
                                <el-option label="资源调度" value="resource" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="对比维度" prop="dimension">
                            <el-select v-model="newAnalysisForm.dimension" placeholder="选择对比维度" style="width: 100%">
                                <el-option label="环比增长" value="mom" />
                                <el-option label="同比分析" value="yoy" />
                                <el-option label="标杆对标" value="bench" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="分析思路说明">
                    <el-input v-model="newAnalysisForm.description" type="textarea" :rows="3"
                        placeholder="简述本次分析的核心关注点（系统将据此生成专项洞察）" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div style="display: flex; justify-content: space-between; align-items: center; width: 100%">
                    <span style="font-size: 12px; color: #94a3b8">预计生成耗时：约 3 秒</span>
                    <div style="display: flex; gap: 12px">
                        <el-button @click="createDialogVisible = false" round>取消</el-button>
                        <el-button type="primary" @click="submitNewAnalysis" round :loading="creating">立即生成</el-button>
                    </div>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { DataAnalysis, Setting, ArrowRight, TrendCharts, Money, FirstAidKit, Cpu, Printer, Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getSurgicalEfficiency, getIncomeAnalysis, getQualityMonitor } from '@/api/analysis'

const currentTheme = ref('surgical')
const timeRange = ref('month')
const compareDim = ref('yoy')
const loading = ref(false)
const aiInsight = ref('')
const exportLoading = ref(false)
const lastLoadedData = ref<any>(null)

// 新建相关
const createDialogVisible = ref(false)
const creating = ref(false)
const createFormRef = ref<any>(null)
const newAnalysisForm = ref({
    name: '',
    category: 'surgical',
    dimension: 'yoy',
    description: ''
})

const newAnalysisRules = {
    name: [{ required: true, message: '请输入报告名称', trigger: 'blur' }],
    category: [{ required: true, message: '请选择领域', trigger: 'change' }]
}

const handleExport = () => {
    if (!lastLoadedData.value) {
        import('element-plus').then(({ ElMessage }) => ElMessage.warning('暂无数据可供导出'))
        return
    }

    exportLoading.value = true
    setTimeout(() => {
        exportLoading.value = false

        let csvContent = "\ufeff指标分析报告\n";
        csvContent += `报告主题,${getCurrentThemeName()}\n`;
        csvContent += `生成时间,${new Date().toLocaleString()}\n\n`;

        // 核心指标
        csvContent += "核心指标,本期数值,同期数值,增长率\n";
        csvContent += `${getCurrentThemeName()},2540,2100,20.9%\n\n`;

        // 趋势数据
        if (lastLoadedData.value.trend) {
            csvContent += "月份趋势,本期值,同期值\n";
            const trend = lastLoadedData.value.trend;
            trend.xAxis.forEach((month: string, idx: number) => {
                csvContent += `${month},${trend.current[idx]},${trend.previous[idx]}\n`;
            });
            csvContent += "\n";
        }

        // 构成分布
        if (lastLoadedData.value.composition) {
            csvContent += "构成分布,数值\n";
            lastLoadedData.value.composition.forEach((item: any) => {
                csvContent += `${item.name},${item.value}\n`;
            });
        }

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement("a");
        const url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", `智慧管理平台_${getCurrentThemeName()}_全量数据报告.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        import('element-plus').then(({ ElMessage }) => {
            ElMessage({
                message: '报告导出成功，已包含趋势与明细数据。',
                type: 'success',
                duration: 3000
            })
        })
    }, 1200)
}

const handleNew = () => {
    newAnalysisForm.value = {
        name: '',
        category: currentTheme.value,
        dimension: compareDim.value,
        description: ''
    }
    createDialogVisible.value = true
}

const submitNewAnalysis = async () => {
    if (!createFormRef.value) return
    await createFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            creating.value = true
            // 模拟 AI 计算
            setTimeout(() => {
                creating.value = false
                createDialogVisible.value = false
                currentTheme.value = newAnalysisForm.value.category
                compareDim.value = newAnalysisForm.value.dimension
                import('element-plus').then(({ ElNotification }) => {
                    ElNotification({
                        title: '分析生成成功',
                        message: `已基于您的需求生成了《${newAnalysisForm.value.name}》主题报告及智能洞察。`,
                        type: 'success',
                        duration: 5000
                    })
                })
                loadThemeData()
            }, 2500)
        }
    })
}

const themes = [
    { id: 'surgical', name: '手术效率分析', icon: FirstAidKit, charts: 6 },
    { id: 'income', name: '医疗收入分析', icon: Money, charts: 4 },
    { id: 'quality', name: '医疗质量监控', icon: TrendCharts, charts: 5 },
]

const getCurrentThemeName = () => {
    return themes.find(t => t.id === currentTheme.value)?.name || ''
}




// Chart Refs
const trendChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)
const gaugeChartRef = ref<HTMLElement | null>(null)

let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null
let gaugeChart: echarts.ECharts | null = null

const loadThemeData = async () => {
    loading.value = true
    try {
        let apiCall;
        if (currentTheme.value === 'surgical') apiCall = getSurgicalEfficiency();
        else if (currentTheme.value === 'income') apiCall = getIncomeAnalysis();
        else apiCall = getQualityMonitor();

        const res: any = await apiCall;
        if (res) {
            lastLoadedData.value = res;
            aiInsight.value = res.insight;
            updateCharts(res);
        }
    } catch (e) {
        console.error('Failed to load theme data:', e)
    } finally {
        loading.value = false
    }
}

const updateCharts = (data: any) => {
    if (trendChart && data.trend) {
        trendChart.setOption({
            xAxis: { data: data.trend.xAxis },
            series: [
                { name: '本期', data: data.trend.current },
                { name: '同期', data: data.trend.previous }
            ]
        })
    }
    if (pieChart && data.composition) {
        pieChart.setOption({
            series: [{ data: data.composition }]
        })
    }
    if (barChart && data.ranking) {
        barChart.setOption({
            yAxis: { data: data.ranking.yAxis },
            series: [{ data: data.ranking.values }]
        })
    }
    if (gaugeChart) {
        gaugeChart.setOption({
            series: [{ data: [{ value: data.targetRate }] }]
        })
    }
}

const initCharts = () => {
    // 1. Trend Chart (Line)
    if (trendChartRef.value) {
        const chart = echarts.init(trendChartRef.value)
        chart.setOption({
            color: ['#0dbda8', '#3b82f6'],
            tooltip: {
                trigger: 'axis',
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                borderWidth: 0,
                boxShadow: '0 0 10px rgba(0,0,0,0.1)',
                textStyle: { color: '#1e293b' }
            },
            legend: { bottom: 0, data: ['本期', '同期'], icon: 'circle' },
            grid: { top: 40, right: 30, bottom: 40, left: 20, containLabel: true },
            xAxis: {
                type: 'category',
                data: [],
                axisLine: { lineStyle: { color: '#e2e8f0' } },
                axisTick: { show: false }
            },
            yAxis: {
                type: 'value',
                splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } },
                axisLabel: { color: '#94a3b8' }
            },
            series: [
                {
                    name: '本期',
                    type: 'line',
                    smooth: true,
                    symbol: 'circle',
                    symbolSize: 8,
                    areaStyle: {
                        opacity: 0.15,
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#0dbda8' },
                            { offset: 1, color: 'transparent' }
                        ])
                    },
                    data: []
                },
                {
                    name: '同期',
                    type: 'line',
                    smooth: true,
                    showSymbol: false,
                    data: [],
                    lineStyle: { type: 'dashed', width: 2, opacity: 0.6 }
                }
            ]
        })
        trendChart = chart
    }

    // 2. Pie Chart
    if (pieChartRef.value) {
        const chart = echarts.init(pieChartRef.value)
        chart.setOption({
            color: ['#0dbda8', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6'],
            tooltip: {
                trigger: 'item',
                backgroundColor: 'rgba(255, 255, 255, 0.98)',
                padding: [12, 16],
                borderRadius: 12,
                borderWidth: 0,
                boxShadow: '0 8px 30px rgba(0,0,0,0.12)',
                formatter: (params: any) => {
                    return `<div style="font-weight:700;margin-bottom:4px;color:#1e293b">${params.name}</div>
                            <div style="display:flex;justify-content:space-between;gap:20px;font-size:12px;color:#64748b">
                                <span>分布数值:</span>
                                <span style="font-weight:600;color:#1e293b">${params.value}</span>
                            </div>
                            <div style="display:flex;justify-content:space-between;gap:20px;font-size:12px;color:#64748b">
                                <span>占比:</span>
                                <span style="font-weight:600;color:#0dbda8">${params.percent}%</span>
                            </div>`
                }
            },
            legend: {
                orient: 'vertical',
                right: '5%',
                top: 'middle',
                itemWidth: 8,
                itemHeight: 8,
                itemGap: 14,
                textStyle: {
                    color: '#64748b',
                    fontSize: 12
                }
            },
            series: [
                {
                    type: 'pie',
                    center: ['35%', '50%'],
                    radius: ['52%', '78%'],
                    avoidLabelOverlap: true,
                    itemStyle: {
                        borderRadius: 10,
                        borderColor: '#fff',
                        borderWidth: 3
                    },
                    label: {
                        show: true,
                        position: 'outside',
                        formatter: '{b}\n{c}',
                        color: '#475569',
                        fontSize: 12,
                        fontWeight: 500,
                        lineHeight: 16
                    },
                    labelLine: {
                        show: true,
                        length: 15,
                        length2: 10,
                        smooth: true,
                        lineStyle: {
                            color: '#cbd5e1',
                            width: 1
                        }
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: 14,
                            fontWeight: 'bold',
                            formatter: '{b}\n{c}\n({d}%)',
                            color: '#1e293b'
                        }
                    },
                    data: []
                }
            ]
        })
        pieChart = chart
    }

    // 3. Bar Chart
    if (barChartRef.value) {
        const chart = echarts.init(barChartRef.value)
        chart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { top: 10, right: 60, bottom: 20, left: 20, containLabel: true },
            xAxis: {
                type: 'value',
                splitLine: { show: false },
                axisLabel: { show: false },
                axisLine: { show: false }
            },
            yAxis: {
                type: 'category',
                data: [],
                axisLine: { show: false },
                axisTick: { show: false },
                axisLabel: { color: '#1e293b', fontWeight: 500 }
            },
            series: [
                {
                    type: 'bar',
                    data: [],
                    itemStyle: {
                        borderRadius: [0, 20, 20, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                            { offset: 0, color: '#6366f1' },
                            { offset: 1, color: '#8b5cf6' }
                        ])
                    },
                    label: {
                        show: true,
                        position: 'right',
                        color: '#6366f1',
                        fontWeight: 700,
                        formatter: '{c}'
                    },
                    barWidth: 14
                }
            ]
        })
        barChart = chart
    }

    // 4. Gauge Chart
    if (gaugeChartRef.value) {
        const chart = echarts.init(gaugeChartRef.value)
        chart.setOption({
            series: [
                {
                    type: 'gauge',
                    startAngle: 210,
                    endAngle: -30,
                    radius: '90%',
                    center: ['50%', '55%'],
                    axisLine: {
                        lineStyle: {
                            width: 12,
                            color: [
                                [0.3, '#ef4444'],
                                [0.7, '#f59e0b'],
                                [1, '#10b981']
                            ]
                        }
                    },
                    progress: { show: true, width: 12, itemStyle: { color: '#0dbda8' } },
                    pointer: { show: true, length: '60%', width: 5 },
                    axisTick: { show: false },
                    splitLine: { show: true, length: 3, lineStyle: { width: 2, color: '#999' } },
                    axisLabel: { show: true, distance: 15, color: '#94a3b8', fontSize: 10 },
                    anchor: { show: true, showAbove: true, size: 12, itemStyle: { borderWidth: 3, borderColor: '#0dbda8' } },
                    title: { show: true, offsetCenter: [0, '70%'], color: '#64748b', fontSize: 13, fontWeight: 500 },
                    detail: {
                        fontSize: 28,
                        offsetCenter: [0, '35%'],
                        formatter: '{value}%',
                        color: '#1e293b',
                        fontWeight: 700
                    },
                    data: [{ value: 0, name: '年度目标达成控制' }]
                }
            ]
        })
        gaugeChart = chart
    }
}

watch(currentTheme, () => {
    loadThemeData()
})

const resizeCharts = () => {
    trendChart?.resize()
    pieChart?.resize()
    barChart?.resize()
    gaugeChart?.resize()
}

onMounted(() => {
    nextTick(() => {
        initCharts()
        loadThemeData()
        window.addEventListener('resize', resizeCharts)
    })
})
</script>

<style scoped lang="scss">
.analysis-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;
    overflow-x: hidden;
    overflow-y: auto;
}

.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-shrink: 0;
    gap: 16px;

    .title-group {
        display: flex;
        gap: 12px;
        align-items: center;
        min-width: 0;
        flex: 1;

        .icon-box {
            width: 44px;
            height: 44px;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
        }

        .text-col {
            display: flex;
            flex-direction: column;

            .main {
                font-size: 20px;
                font-weight: 700;
                color: #1e293b;
                line-height: 1.3;
            }

            .sub {
                font-size: 13px;
                color: #64748b;
                font-family: 'JetBrains Mono';
            }
        }
    }

    .actions {
        display: flex;
        gap: 12px;
    }
}

.content-wrapper {
    flex: 1;
    display: flex;
    gap: 24px;
    min-height: 0;
    overflow-x: hidden;
    overflow-y: visible;
}

.theme-sidebar {
    width: 260px;
    padding: 0;
    display: flex;
    flex-direction: column;
    overflow: visible;
    flex-shrink: 0;

    .sb-header {
        padding: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #f1f5f9;

        .lbl {
            font-weight: 700;
            color: #334155;
        }
    }

    .theme-list {
        flex: 0 0 auto;
        padding: 12px;
        overflow: visible;

        .theme-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px;
            margin-bottom: 4px;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.2s;
            color: #64748b;

            &:hover {
                background: #f8fafc;
                color: #1e293b;
            }

            &.active {
                background: #eef2ff;
                color: #6366f1;

                .t-icon {
                    background: #fff;
                    color: #6366f1;
                    box-shadow: 0 2px 6px rgba(99, 102, 241, 0.15);
                }
            }

            .t-icon {
                width: 32px;
                height: 32px;
                border-radius: 8px;
                background: #f1f5f9;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.2s;
            }

            .t-info {
                flex: 1;
                display: flex;
                flex-direction: column;

                .t-name {
                    font-weight: 600;
                    font-size: 14px;
                }

                .t-desc {
                    font-size: 11px;
                    opacity: 0.7;
                }
            }

            .arrow {
                font-size: 12px;
            }
        }
    }
}

.analysis-board {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-width: 0;
}

.filter-bar {
    padding: 12px 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    flex-shrink: 0;

    .f-group {
        display: flex;
        align-items: center;
        gap: 8px;

        .label {
            font-size: 13px;
            color: #64748b;
            font-weight: 500;
        }
    }

    .divider {
        width: 1px;
        height: 20px;
        background: #e2e8f0;
    }

    .spacer {
        flex: 1;
    }

    .update-time {
        font-size: 12px;
        color: #94a3b8;
        font-family: 'JetBrains Mono';
    }

    .glass-radio {
        --el-fill-color-blank: #f8fafc;
        --el-border-color: #e2e8f0;
    }
}

.charts-grid {
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding-right: 4px;

    .insight-card {
        background: linear-gradient(135deg, #1e293b, #0f172a);
        border-radius: 16px;
        padding: 20px;
        min-height: 100px;
        display: flex;
        gap: 20px;
        align-items: flex-start;
        margin-bottom: 24px;

        .card-icon {
            width: 48px;
            height: 48px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #0dbda8;
            font-size: 24px;
        }

        .card-content {
            flex: 1;
            color: #fff;

            .h-row {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 8px;
            }

            .h-title {
                font-weight: 700;
                font-size: 16px;
            }

            .desc {
                font-size: 14px;
                line-height: 1.6;
                color: #cbd5e1;
                opacity: 0.9;
            }
        }
    }

    .chart-row {
        display: flex;
        gap: 16px;
        min-height: 320px;

        .chart-box {
            display: flex;
            flex-direction: column;
            padding: 20px;

            .c-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16px;

                .title {
                    font-weight: 700;
                    color: #1e293b;
                }
            }

            .chart-content {
                flex: 1;
                min-height: 0;
                width: 100%;
            }

            &.big {
                flex: 2;
            }

            &.small {
                flex: 1;
            }

            &.medium {
                flex: 1;
            }
        }
    }
}

.glass-panel {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    transition: all 0.3s;

    &:hover {
        border-color: #cbd5e1;
        box-shadow: 0 4px 12px -2px rgba(0, 0, 0, 0.05);
    }
}

.glass-select {
    :deep(.el-input__wrapper) {
        background: #f8fafc;
        box-shadow: none;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
    }
}

.theme-compare-select {
    width: 120px;
    max-width: 100%;
}

.animate-enter {
    animation: fadeIn 0.5s ease-out forwards;
    opacity: 0;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@media (max-width: 768px) {
    .analysis-container {
        overflow: auto;
        min-height: 0;
        padding-bottom: 16px;
    }

    .header-section {
        flex-direction: column;
        align-items: stretch;
        margin-bottom: 14px;

        .title-group {
            align-items: flex-start;

            .text-col {
                min-width: 0;

                .main {
                    font-size: clamp(14px, 4.2vw, 18px);
                    font-weight: 700;
                    line-height: 1.3;
                    overflow-wrap: anywhere;
                }
            }
        }

        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            width: 100%;

            :deep(.el-button) {
                flex: 1;
                min-width: 0;
            }
        }
    }

    .content-wrapper {
        flex-direction: column;
        gap: 14px;
        overflow: visible;
        min-height: auto;
    }

    .theme-sidebar {
        width: 100%;
    }

    .theme-sidebar .theme-list {
        overflow: visible;
    }

    .analysis-board {
        min-height: 0;
        width: 100%;
    }

    .filter-bar {
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
        padding: 14px 16px;

        .divider,
        .spacer {
            display: none;
        }

        .f-group {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
            width: 100%;

            .label {
                white-space: normal;
                line-height: 1.4;
            }

            .glass-radio {
                width: 100%;

                :deep(.el-radio-group) {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 8px;
                }
            }
        }

        .theme-compare-select {
            width: 100% !important;
        }

        .update-time {
            width: 100%;
            text-align: center;
            font-size: 11px;
        }
    }

    .charts-grid {
        padding-right: 0;

        .insight-card {
            flex-direction: column;
            align-items: stretch;
            gap: 14px;
            padding: 16px;
            margin-bottom: 16px;

            .card-icon {
                width: 44px;
                height: 44px;
            }
        }

        .chart-row {
            flex-direction: column;
            min-height: auto;
            gap: 12px;

            .chart-box {
                flex: none !important;
                width: 100%;
                min-height: 260px;
                padding: 16px;

                .chart-content {
                    min-height: 220px;
                }
            }
        }
    }

    :deep(.theme-analysis-dialog-responsive.el-dialog) {
        width: min(100vw - 32px, 600px) !important;
        margin: 2vh auto !important;
    }
}
</style>
