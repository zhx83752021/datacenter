<template>
    <div class="patient-wrapper">
        <!-- Background Decor -->
        <div class="bg-decor-1"></div>
        <div class="bg-decor-2"></div>

        <div class="patient-container animate-fade-in">
            <!-- Header Section -->
            <div class="header-section glass-effect">
                <div class="title-group">
                    <div class="icon-box pulse-subtle">
                        <el-icon :size="24" color="#fff">
                            <UserFilled />
                        </el-icon>
                    </div>
                    <div class="text-col">
                        <span class="main">患者360全景视图</span>
                    </div>
                </div>
                <div class="search-actions">
                    <el-input v-model="searchId" placeholder="搜索住院号/姓名..." prefix-icon="Search"
                        class="glass-input hover-expand" clearable @keyup.enter="handleSearch" />
                    <el-divider direction="vertical" class="divider-glass" />
                    <el-button type="primary" circle plain icon="RefreshRight" @click="refreshData" :loading="loading"
                        class="action-btn" />
                    <el-button type="warning" circle plain icon="Bell" class="action-btn has-dot" />
                </div>
            </div>

            <!-- Contextual Drill Down Alert -->
            <div v-if="isDrillDown" class="drill-context-banner mb-4 animate-fade-in"
                style="margin-top: 12px; margin-bottom: 20px;">
                <el-alert :title="`正在展示指标【${drillIndicator}】的底层患者长明细`" type="warning" show-icon :closable="false"
                    class="glass-alert">
                    <template #default>
                        <div style="margin-top: 4px; line-height: 1.5;">
                            您已脱离宏观看板，进入 <strong>患者360视图</strong>
                            下钻分析模式。当前列表已自动过滤并呈现对该异常/重点指标有直接贡献的原始患者病历数据。由于数据合规原因，此处演示仅显示占位虚拟病历。
                        </div>
                    </template>
                </el-alert>
            </div>

            <!-- Content Grid -->
            <div class="content-grid" v-loading="loading" element-loading-background="rgba(255, 255, 255, 0.6)">

                <!-- Left Column: Identity & Risks -->
                <div class="col-left">
                    <!-- Profile Card -->
                    <div class="glass-panel profile-card hover-lift">
                        <div class="profile-header">
                            <div class="avatar-wrapper">
                                <el-avatar :size="88"
                                    src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                                    class="avatar-img" />
                                <div class="status-indicator online"></div>
                            </div>
                            <div class="name-box">
                                <div class="name-row">
                                    <span class="name">张建国</span>
                                    <div class="vip-badge" v-if="patient.isVip">VIP</div>
                                </div>
                                <div class="tags-row">
                                    <span class="meta-tag gender"><el-icon>
                                            <Male />
                                        </el-icon> 男</span>
                                    <span class="meta-tag age">65岁</span>
                                    <span class="meta-tag code font-mono">{{ patient.id }}</span>
                                </div>
                            </div>
                        </div>

                        <!-- Quick Stats Grid -->
                        <div class="stats-grid">
                            <div class="stat-item">
                                <span class="label">住院天数</span>
                                <span class="value font-mono">12<small>天</small></span>
                            </div>
                            <div class="stat-item">
                                <span class="label">护理等级</span>
                                <span class="value text-accent">二级</span>
                            </div>
                            <div class="stat-item">
                                <span class="label">费别</span>
                                <span class="value">医保</span>
                            </div>
                            <div class="stat-item">
                                <span class="label">预交金</span>
                                <span class="value font-mono">¥5k</span>
                            </div>
                        </div>

                        <!-- Risk Alerts -->
                        <div class="risk-section">
                            <div class="section-header">
                                <span class="title">风险评估</span>
                                <el-icon class="info-icon">
                                    <InfoFilled />
                                </el-icon>
                            </div>
                            <div class="risk-tags">
                                <div class="risk-tag high">
                                    <el-icon>
                                        <Warning />
                                    </el-icon> 高血压 III级
                                </div>
                                <div class="risk-tag medium">跌倒风险</div>
                                <div class="risk-tag alert">
                                    <el-icon>
                                        <WarningFilled />
                                    </el-icon> 青霉素过敏
                                </div>
                            </div>
                        </div>

                        <!-- Current Vitals Mini -->
                        <div class="vitals-mini">
                            <div class="vital-row">
                                <div class="label">血压 BP</div>
                                <div class="val text-danger">145/95 <span class="unit">mmHg</span></div>
                            </div>
                            <div class="vital-row">
                                <div class="label">心率 HR</div>
                                <div class="val">78 <span class="unit">bpm</span></div>
                            </div>
                            <div class="vital-row">
                                <div class="label">血氧 SpO2</div>
                                <div class="val text-success">98 <span class="unit">%</span></div>
                            </div>
                        </div>
                    </div>

                    <!-- Contact/Family Info Helper -->
                    <div class="glass-panel contact-card mt-3 hover-lift">
                        <div class="row">
                            <span class="label">联系电话</span>
                            <span class="val font-mono">138****8888</span>
                        </div>
                        <div class="row">
                            <span class="label">责任护士</span>
                            <span class="val">王小云</span>
                        </div>
                        <div class="row">
                            <span class="label">主治医师</span>
                            <span class="val">李主任</span>
                        </div>
                    </div>
                </div>

                <!-- Middle Column: Clinical Journey -->
                <div class="col-mid">
                    <div class="glass-panel main-panel">
                        <div class="panel-tabs">
                            <div class="tab-item" :class="{ active: activeTab === 'timeline' }"
                                @click="activeTab = 'timeline'">
                                <el-icon>
                                    <Timer />
                                </el-icon> 诊疗时间轴
                            </div>
                            <div class="tab-item" :class="{ active: activeTab === 'orders' }"
                                @click="activeTab = 'orders'">
                                <el-icon>
                                    <Tickets />
                                </el-icon>
                                今日医嘱 <span class="badge font-mono">{{ orders.length }}</span>
                            </div>
                            <div class="tab-item" :class="{ active: activeTab === 'reports' }"
                                @click="activeTab = 'reports'">
                                <el-icon>
                                    <Document />
                                </el-icon> 检查报告
                            </div>
                        </div>

                        <div class="panel-content scroll-styled">
                            <!-- Timeline View -->
                            <div v-show="activeTab === 'timeline'" class="timeline-view animate-slide-up">
                                <el-timeline>
                                    <el-timeline-item v-for="(activity, index) in timeline" :key="index"
                                        :type="activity.type" :color="activity.color" :hollow="activity.hollow"
                                        :timestamp="activity.timestamp" placement="top">
                                        <div class="timeline-card">
                                            <div class="card-header">
                                                <span class="activity-title">{{ activity.title }}</span>
                                                <el-tag v-if="activity.tag" size="small" effect="plain" round>{{
                                                    activity.tag
                                                    }}</el-tag>
                                            </div>
                                            <p class="activity-desc" v-if="activity.content">{{ activity.content }}</p>
                                            <div class="activity-footer" v-if="activity.user">
                                                <el-icon>
                                                    <User />
                                                </el-icon> {{ activity.user }}
                                            </div>
                                        </div>
                                    </el-timeline-item>
                                </el-timeline>
                            </div>

                            <!-- Orders View -->
                            <div v-show="activeTab === 'orders'" class="orders-view animate-slide-up">
                                <el-table :data="orders" style="width: 100%" class="modern-table"
                                    :header-cell-style="{ background: 'transparent' }">
                                    <el-table-column width="40">
                                        <template #default="{ row }">
                                            <div class="status-dot-pulse" :class="row.status"></div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="type" label="类别" width="80">
                                        <template #default="{ row }">
                                            <el-tag size="small" :type="row.type === '长期' ? 'primary' : 'warning'"
                                                effect="light">{{ row.type }}</el-tag>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="name" label="医嘱名称" min-width="180">
                                        <template #default="{ row }">
                                            <span class="font-medium text-slate-700">{{ row.name }}</span>
                                            <div class="text-xs text-slate-400" v-if="row.spec">{{ row.spec }}</div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="dose" label="剂量" width="100" />
                                    <el-table-column prop="freq" label="频次" width="80" />
                                    <el-table-column prop="startTime" label="开始时间" width="100"
                                        class-name="font-mono text-xs" />
                                </el-table>
                            </div>
                            <!-- Reports View (Placeholder) -->
                            <div v-show="activeTab === 'reports'" class="reports-view animate-slide-up">
                                <div class="empty-state">
                                    <el-empty description="暂无新报告" :image-size="100"></el-empty>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Analytics & AI -->
                <div class="col-right">
                    <!-- Diagnosis Card -->
                    <div class="glass-panel diag-panel mb-3 hover-lift">
                        <div class="panel-header-mini">
                            <span class="title">主要诊断</span>
                            <el-button link type="primary" size="small">详情</el-button>
                        </div>
                        <div class="diag-list">
                            <div class="diag-item main">
                                <div class="code-box font-mono">I10.x00</div>
                                <div class="info">
                                    <span class="name">原发性高血压</span>
                                    <span class="time">2024-05-10 确诊</span>
                                </div>
                            </div>
                            <div class="diag-item sub">
                                <div class="code-box font-mono">E11.900</div>
                                <div class="info">
                                    <span class="name">2型糖尿病</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Chart Card -->
                    <div class="glass-panel chart-panel mb-3">
                        <div class="panel-header-mini">
                            <span class="title">关键指标趋势</span>
                            <div class="chart-switcher">
                                <span v-for="t in ['血压', '血糖', '体温']" :key="t" class="switch-item"
                                    :class="{ active: chartType === t }" @click="chartType = t">
                                    {{ t }}
                                </span>
                            </div>
                        </div>
                        <div class="chart-wrapper">
                            <div class="chart-container" ref="chartRef"></div>
                        </div>
                    </div>

                    <!-- AI Insight Card -->
                    <div class="ai-insight-card hover-glow">
                        <div class="card-bg-effect"></div>
                        <div class="ai-header">
                            <div class="ai-badge">
                                <el-icon class="is-loading">
                                    <Cpu />
                                </el-icon>
                                AI Assistant
                            </div>
                            <el-tag size="small" effect="dark" type="success" class="score-tag">健康评分 85</el-tag>
                        </div>
                        <div class="ai-body">
                            <p class="insight-text">
                                <span class="highlight">分析报告：</span>
                                患者近期血压波动较大（<span class="warn">↑15%</span>），建议关注夜间血压监测结果。用药依从性良好，未发现药物相互作用风险。
                            </p>
                            <div class="action-suggestions">
                                <div class="suggestion-pill">增加夜间巡查</div>
                                <div class="suggestion-pill">复查肾功能</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import {
    UserFilled, Warning, Cpu, InfoFilled,
    Male, Timer, Tickets, Document, User, WarningFilled
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const route = useRoute()

// Drill down context
const isDrillDown = ref(false)
const drillIndicator = ref('')

// State
const loading = ref(false)
const searchId = ref('')
const activeTab = ref('timeline')
const chartType = ref('血压')
const chartRef = ref<HTMLElement | null>(null)

// Mock Data
const patient = reactive({
    id: '202405001',
    name: '张建国',
    age: 65,
    gender: '男',
    isVip: false
})

const timeline = ref([
    { timestamp: '05-13 09:30', title: '主治医师查房', content: '患者自述头晕缓解，调整降压药剂量', type: 'primary', color: '#0dbda8', user: '李主任', tag: '查房' },
    { timestamp: '05-13 08:00', title: '空腹血糖检测', content: '结果: 6.8 mmol/L (正常范围)', color: '#4FC3F7', hollow: true, tag: '检验' },
    { timestamp: '05-12 16:00', title: '长期医嘱执行', content: '硝苯地平控释片 30mg 口服', color: '#0dbda8', hollow: true, user: '王小云' },
    { timestamp: '05-12 10:00', title: '入院评估', content: '初步诊断：高血压III级，建议二级护理', type: 'warning', color: '#F59E0B', tag: '评估' },
])

const orders = ref([
    { status: 'active', type: '长期', name: '0.9%氯化钠注射液', spec: '100ml:0.9g', dose: '100ml', freq: 'qd', startTime: '05-10 10:00' },
    { status: 'active', type: '长期', name: '硝苯地平控释片', spec: '30mg*7片', dose: '30mg', freq: 'qd', startTime: '05-10 10:00' },
    { status: 'active', type: '长期', name: '二甲双胍片', spec: '0.5g*20片', dose: '0.5g', freq: 'tid', startTime: '05-10 11:00' },
    { status: 'pending', type: '临时', name: '阿司匹林肠溶片', spec: '100mg*10片', dose: '100mg', freq: 'qd', startTime: '05-13 09:00' },
    { status: 'stopped', type: '临时', name: '葡萄糖注射液', spec: '500ml:25g', dose: '500ml', freq: 'st', startTime: '05-12 14:00' },
])

// Actions
const handleSearch = () => {
    refreshData()
}

const refreshData = () => {
    loading.value = true
    setTimeout(() => {
        loading.value = false
        // Simulate chart update or data refresh
        initChart()
    }, 800)
}

// Chart Logic
const initChart = () => {
    if (!chartRef.value) return
    const myChart = echarts.init(chartRef.value)

    // Gradient definitions
    const gradientColor = new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(13, 189, 168, 0.5)' },
        { offset: 1, color: 'rgba(13, 189, 168, 0.05)' }
    ])

    const option = {
        grid: { top: 30, right: 10, bottom: 20, left: 35 },
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            borderColor: '#e2e8f0',
            textStyle: { color: '#1e293b' }
        },
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#94a3b8', fontSize: 10 }
        },
        yAxis: {
            type: 'value',
            splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } },
            axisLabel: { color: '#94a3b8', fontSize: 10 }
        },
        series: [
            {
                name: 'Systolic',
                data: [140, 142, 138, 145, 148, 135, 130],
                type: 'line',
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                itemStyle: { color: '#0dbda8', borderColor: '#fff', borderWidth: 2 },
                lineStyle: { width: 3, color: '#0dbda8' },
                areaStyle: { color: gradientColor }
            },
            {
                name: 'Diastolic',
                data: [90, 92, 88, 95, 96, 85, 82],
                type: 'line',
                smooth: true,
                symbol: 'none',
                lineStyle: { width: 2, color: '#94a3b8', type: 'dashed' }
            }
        ]
    }
    myChart.setOption(option)

    window.addEventListener('resize', () => myChart.resize())
}

onMounted(() => {
    if (route.query.drillDown === 'true') {
        isDrillDown.value = true
        drillIndicator.value = route.query.indicator ? decodeURIComponent(route.query.indicator as string) : '某重点监控指标'
    }
    refreshData() // Initial load
})
</script>

<style scoped lang="scss">
@use "sass:color";

/* --- Variables & Mixins --- */
$primary: #0dbda8;
$primary-light: color.adjust($primary, $lightness: 45%);
$dark-bg: #0f172a;
$card-bg: rgba(255, 255, 255, 0.85);
$glass-border: 1px solid rgba(255, 255, 255, 0.6);
$shadow-soft: 0 8px 30px rgba(0, 0, 0, 0.04);
$shadow-hover: 0 20px 40px rgba(0, 0, 0, 0.08);

.patient-wrapper {
    position: relative;
    height: 100%;
    width: 100%;
    background: #f1f5f9;
    overflow: hidden;
    font-family: 'Inter', system-ui, sans-serif;
}

/* Background Decorations */
.bg-decor-1 {
    position: absolute;
    top: -10%;
    left: -5%;
    width: 50%;
    height: 50%;
    background: radial-gradient(circle, rgba(13, 189, 168, 0.08) 0%, transparent 70%);
    filter: blur(60px);
    z-index: 0;
}

.bg-decor-2 {
    position: absolute;
    bottom: -10%;
    right: -5%;
    width: 60%;
    height: 60%;
    background: radial-gradient(circle, rgba(79, 195, 247, 0.08) 0%, transparent 70%);
    filter: blur(60px);
    z-index: 0;
}

.patient-container {
    position: relative;
    z-index: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 16px 24px;
    box-sizing: border-box;
}

/* --- Header --- */
.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 12px 20px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(12px);
    border: $glass-border;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

    .title-group {
        display: flex;
        align-items: center;
        gap: 16px;

        .icon-box {
            width: 44px;
            height: 44px;
            background: linear-gradient(135deg, $primary, color.adjust($primary, $hue: 15deg));
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(13, 189, 168, 0.3);
        }

        .text-col {
            display: flex;
            flex-direction: column;

            .main {
                font-size: 18px;
                font-weight: 700;
                color: #1e293b;
                line-height: 1.2;
            }
        }
    }

    .search-actions {
        display: flex;
        align-items: center;
        gap: 12px;

        .glass-input {
            width: 200px;
            transition: width 0.3s ease;

            &.hover-expand:hover,
            &:focus-within {
                width: 260px;
            }

            :deep(.el-input__wrapper) {
                border-radius: 20px;
                box-shadow: none;
                background: rgba(255, 255, 255, 0.5);
                border: 1px solid transparent;
                transition: all 0.3s;

                &:hover,
                &.is-focus {
                    background: #fff;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
                }
            }
        }

        .divider-glass {
            height: 18px;
            border-color: #cbd5e1;
            margin: 0 8px;
        }

        .action-btn {
            border: none;
            background: rgba(255, 255, 255, 0.5);
            transition: all 0.2s;

            &:hover {
                background: #fff;
                transform: scale(1.05);
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            }

            &.has-dot {
                position: relative;

                &::after {
                    content: '';
                    position: absolute;
                    top: 8px;
                    right: 8px;
                    width: 6px;
                    height: 6px;
                    background: #ff4757;
                    border-radius: 50%;
                }
            }
        }
    }
}

/* --- Grid Layout --- */
.content-grid {
    flex: 1;
    display: flex;
    gap: 24px;
    min-height: 0;
    overflow: hidden;
}

.col-left {
    flex: 0 0 290px;
    display: flex;
    flex-direction: column;
}

.col-right {
    flex: 0 0 340px;
    display: flex;
    flex-direction: column;
}

.col-mid {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
}

/* --- Component: Glass Panel --- */
.glass-panel {
    background: $card-bg;
    border-radius: 20px;
    border: $glass-border;
    backdrop-filter: blur(12px);
    box-shadow: $shadow-soft;
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

    &.hover-lift:hover {
        transform: translateY(-4px);
        box-shadow: $shadow-hover;
    }
}

/* --- Left Column Components --- */
.profile-card {
    padding: 24px 20px;
    margin-bottom: 20px;

    .profile-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 24px;

        .avatar-wrapper {
            position: relative;
            margin-bottom: 16px;

            .avatar-img {
                border: 4px solid #fff;
                box-shadow: 0 8px 16px rgba(0, 0, 0, 0.05);
            }

            .status-indicator {
                position: absolute;
                bottom: 6px;
                right: 6px;
                width: 14px;
                height: 14px;
                background: #10B981;
                border: 2px solid #fff;
                border-radius: 50%;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
        }

        .name-box {
            text-align: center;

            .name-row {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                margin-bottom: 8px;

                .name {
                    font-size: 22px;
                    font-weight: 700;
                    color: #1e293b;
                }

                .vip-badge {
                    background: linear-gradient(135deg, #F59E0B, #ffc107);
                    color: #fff;
                    font-size: 10px;
                    padding: 2px 6px;
                    border-radius: 8px;
                    font-weight: 700;
                }
            }

            .tags-row {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
                justify-content: center;

                .meta-tag {
                    font-size: 12px;
                    color: #64748b;
                    background: #f1f5f9;
                    padding: 4px 10px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    gap: 4px;

                    &.code {
                        color: #475569;
                        letter-spacing: 0.5px;
                    }
                }
            }
        }
    }

    .stats-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 12px;
        background: rgba(255, 255, 255, 0.5);
        border-radius: 16px;
        padding: 16px;
        margin-bottom: 24px;

        .stat-item {
            display: flex;
            flex-direction: column;

            .label {
                font-size: 11px;
                color: #94a3b8;
                margin-bottom: 4px;
            }

            .value {
                font-size: 14px;
                font-weight: 600;
                color: #334155;
            }

            .text-accent {
                color: $primary;
            }

            small {
                font-size: 10px;
                font-weight: 400;
                margin-left: 2px;
            }
        }
    }

    .risk-section {
        margin-bottom: 24px;

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;

            .title {
                font-size: 13px;
                font-weight: 700;
                color: #475569;
            }

            .info-icon {
                font-size: 14px;
                color: #cbd5e1;
                cursor: pointer;
            }
        }

        .risk-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .risk-tag {
                font-size: 11px;
                font-weight: 600;
                padding: 4px 10px;
                border-radius: 6px;
                display: flex;
                align-items: center;
                gap: 4px;

                &.high {
                    background: #fef2f2;
                    color: #ef4444;
                    border: 1px solid #fee2e2;
                }

                &.medium {
                    background: #fffbeb;
                    color: #f59e0b;
                    border: 1px solid #fef3c7;
                }

                &.alert {
                    background: #f5f5f5;
                    color: #475569;
                }
            }
        }
    }

    .vitals-mini {
        border-top: 1px solid #f1f5f9;
        padding-top: 20px;
        display: flex;
        flex-direction: column;
        gap: 12px;

        .vital-row {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .label {
                font-size: 11px;
                color: #94a3b8;
                font-weight: 600;
                text-transform: uppercase;
            }

            .val {
                font-size: 15px;
                font-weight: 700;
                color: #334155;
            }

            .unit {
                font-size: 10px;
                color: #94a3b8;
                font-weight: 400;
            }

            .text-danger {
                color: #ef4444;
            }

            .text-success {
                color: #10b981;
            }
        }
    }
}

.contact-card {
    padding: 16px 20px;
    flex: 1; // Fill remaining space if any

    .row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;

        &:last-child {
            margin-bottom: 0;
        }

        .label {
            font-size: 12px;
            color: #94a3b8;
        }

        .val {
            font-size: 13px;
            color: #475569;
            font-weight: 500;
        }
    }
}

/* --- Middle Column Components --- */
.main-panel {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 0;
    overflow: hidden;

    .panel-tabs {
        display: flex;
        background: rgba(241, 245, 249, 0.5);
        padding: 6px;
        gap: 6px;
        border-bottom: 1px solid #e2e8f0;

        .tab-item {
            flex: 1;
            padding: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            border-radius: 12px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 600;
            color: #64748b;
            transition: all 0.2s;

            &:hover {
                background: rgba(255, 255, 255, 0.6);
                color: #475569;
            }

            &.active {
                background: #fff;
                color: $primary;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }

            .badge {
                background: #fef2f2;
                color: #ef4444;
                padding: 1px 6px;
                border-radius: 10px;
                font-size: 10px;
            }
        }
    }

    .panel-content {
        flex: 1;
        padding: 24px;
        overflow-y: auto;
    }
}

.timeline-view {
    .timeline-card {
        background: #fff;
        border: 1px solid #f1f5f9;
        border-radius: 12px;
        padding: 12px 16px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
        transition: all 0.2s;

        &:hover {
            border-color: #e2e8f0;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.04);
        }

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .activity-title {
                font-weight: 600;
                color: #334155;
                font-size: 14px;
            }
        }

        .activity-desc {
            font-size: 13px;
            color: #64748b;
            line-height: 1.5;
            margin-bottom: 8px;
        }

        .activity-footer {
            font-size: 11px;
            color: #94a3b8;
            display: flex;
            align-items: center;
            gap: 4px;
        }
    }
}

.orders-view {
    .modern-table {
        background: transparent;
        --el-table-tr-bg-color: transparent;
        --el-table-header-bg-color: transparent;

        .status-dot-pulse {
            width: 8px;
            height: 8px;
            border-radius: 50%;

            &.active {
                background: #10B981;
                box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
            }

            &.pending {
                background: #F59E0B;
            }

            &.stopped {
                background: #94a3b8;
            }
        }
    }
}

/* --- Right Column Components --- */
.diag-panel {
    padding: 16px;

    .panel-header-mini {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .title {
            font-size: 13px;
            font-weight: 700;
            color: #1e293b;
        }
    }

    .diag-list {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .diag-item {
            display: flex;
            gap: 12px;
            padding: 10px;
            border-radius: 10px;
            background: #f8fafc;

            &.main {
                background: #fff1f2;
                border: 1px solid #fecdd3;

                .code-box {
                    color: #e11d48;
                }
            }

            .code-box {
                font-weight: 700;
                font-size: 12px;
            }

            .info {
                display: flex;
                flex-direction: column;

                .name {
                    font-size: 13px;
                    font-weight: 600;
                    color: #334155;
                }

                .time {
                    font-size: 10px;
                    color: #94a3b8;
                    margin-top: 2px;
                }
            }
        }
    }
}

.chart-panel {
    padding: 16px;
    flex: 1; // Stretch to fill
    display: flex;
    flex-direction: column;

    .panel-header-mini {
        margin-bottom: 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
            font-weight: 700;
            font-size: 13px;
        }
    }

    .chart-switcher {
        display: flex;
        gap: 4px;
        background: #f1f5f9;
        padding: 2px;
        border-radius: 6px;

        .switch-item {
            font-size: 10px;
            padding: 2px 8px;
            border-radius: 4px;
            cursor: pointer;
            color: #64748b;

            &.active {
                background: #fff;
                color: $primary;
                font-weight: 600;
                box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
            }
        }
    }

    .chart-wrapper {
        flex: 1;
        min-height: 180px;
        position: relative;

        .chart-container {
            width: 100%;
            height: 100%;
        }
    }
}

.ai-insight-card {
    position: relative;
    background: linear-gradient(135deg, #1e293b, #0f172a);
    border-radius: 16px;
    padding: 20px;
    color: #fff;
    overflow: hidden;
    cursor: default;
    box-shadow: 0 10px 30px rgba(15, 23, 42, 0.2);

    .card-bg-effect {
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(13, 189, 168, 0.15) 0%, transparent 60%);
        animation: rotateBg 10s linear infinite;
        pointer-events: none;
    }

    .ai-header {
        position: relative;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;

        .ai-badge {
            display: flex;
            align-items: center;
            gap: 6px;
            font-weight: 700;
            font-size: 13px;
            color: $primary;

            .el-icon {
                font-size: 16px;
            }
        }

        .score-tag {
            background: rgba(16, 185, 129, 0.2);
            border-color: rgba(16, 185, 129, 0.3);
            color: #34d399;
        }
    }

    .ai-body {
        position: relative;

        .insight-text {
            font-size: 13px;
            line-height: 1.6;
            color: #cbd5e1;
            margin-bottom: 12px;

            .highlight {
                color: #fff;
                font-weight: 600;
            }

            .warn {
                color: #f87171;
                font-weight: 700;
            }
        }

        .action-suggestions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;

            .suggestion-pill {
                font-size: 10px;
                color: $primary-light;
                border: 1px solid rgba(13, 189, 168, 0.3);
                padding: 2px 8px;
                border-radius: 12px;
                background: rgba(13, 189, 168, 0.1);
                cursor: pointer;
                transition: all 0.2s;

                &:hover {
                    background: rgba(13, 189, 168, 0.2);
                    color: #fff;
                }
            }
        }
    }
}

/* --- Utils & Animations --- */
.font-mono {
    font-family: 'JetBrains Mono', 'Roboto Mono', monospace;
}

.text-slate-700 {
    color: #334155;
}

.text-slate-400 {
    color: #94a3b8;
}

.text-xs {
    font-size: 12px;
}

.font-medium {
    font-weight: 500;
}

.mb-3 {
    margin-bottom: 12px;
}

.mt-3 {
    margin-top: 12px;
}

.scroll-styled {
    &::-webkit-scrollbar {
        width: 4px;
    }

    &::-webkit-scrollbar-track {
        background: transparent;
    }

    &::-webkit-scrollbar-thumb {
        background: #e2e8f0;
        border-radius: 2px;
    }

    &:hover::-webkit-scrollbar-thumb {
        background: #cbd5e1;
    }
}

@keyframes rotateBg {
    from {
        transform: rotate(0deg);
    }

    to {
        transform: rotate(360deg);
    }
}

@keyframes pulse-subtle {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.05);
    }

    100% {
        transform: scale(1);
    }
}

.pulse-subtle {
    animation: pulse-subtle 3s infinite ease-in-out;
}

.animate-fade-in {
    animation: fadeIn 0.6s ease-out forwards;
}

.animate-slide-up {
    animation: slideUp 0.4s ease-out forwards;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Responsive — 移动端：三栏纵向堆叠，顶栏搜索占满宽 */
@media (max-width: 768px) {
    .patient-container {
        padding: 12px;
    }

    .header-section {
        flex-direction: column;
        align-items: stretch;
        gap: 14px;

        .title-group .text-col {
            min-width: 0;

            .main {
                font-size: clamp(16px, 4.2vw, 18px);
            }
        }

        .search-actions {
            flex-wrap: wrap;
            width: 100%;
            gap: 10px;

            .glass-input {
                width: 100% !important;
                flex: 1 1 100%;
                min-width: 0;

                &.hover-expand:hover,
                &:focus-within {
                    width: 100% !important;
                }
            }

            .divider-glass {
                display: none;
            }

            .action-btn {
                flex-shrink: 0;
            }
        }
    }

    .content-grid {
        flex-direction: column;
        overflow: visible;
        gap: 16px;
        min-height: auto;
    }

    .col-left,
    .col-mid,
    .col-right {
        flex: none !important;
        width: 100%;
        min-width: 0;
    }
}

@media (max-width: 1400px) and (min-width: 769px) {
    .content-grid {
        gap: 16px;
    }

    .col-left {
        flex: 0 0 260px;
    }

    .col-right {
        flex: 0 0 300px;
    }
}
</style>
