<template>
    <div class="report-container animate-enter">
        <!-- Header -->
        <div class="page-header">
            <div class="title-block">
                <div class="icon-wrapper pulse">
                    <el-icon>
                        <Share />
                    </el-icon>
                </div>
                <div>
                    <h1 class="main-title">智能报告分发</h1>
                </div>
            </div>
            <div class="actions">
                <el-button round icon="DocumentAdd" class="create-btn" @click="handleCreate">新建任务</el-button>
            </div>
        </div>

        <!-- Stats Row -->
        <div class="stats-row">
            <div class="glass-stat-card" v-for="(stat, idx) in stats" :key="idx"
                :style="{ animationDelay: idx * 0.1 + 's' }">
                <div class="stat-icon" :class="stat.color">
                    <el-icon>
                        <component :is="stat.icon" />
                    </el-icon>
                </div>
                <div class="stat-info">
                    <div class="label">{{ stat.label }}</div>
                    <div class="num font-mono">{{ stat.value }}</div>
                </div>
            </div>
        </div>

        <!-- Main Content: Master-Detail -->
        <div class="main-layout">
            <!-- Left: Task List -->
            <div class="task-list glass-panel">
                <div class="list-header">
                    <div class="search-box">
                        <el-input v-model="searchKey" placeholder="搜索报告任务..." :prefix-icon="Search" clearable />
                    </div>
                    <div class="filter-tabs">
                        <span class="tab active">全部</span>
                        <span class="tab">运行中</span>
                    </div>
                </div>

                <div class="list-scroll custom-scrollbar">
                    <div v-for="task in filteredTasks" :key="task.id" class="task-item"
                        :class="{ active: currentTask?.id === task.id }" @click="selectTask(task)">
                        <div class="task-status-line" :class="task.status ? 'on' : 'off'"></div>
                        <div class="task-main">
                            <div class="t-row1">
                                <span class="t-name">{{ task.name }}</span>
                                <el-tag size="small" :type="task.type === 'daily' ? '' : 'warning'" effect="light"
                                    round>
                                    {{ task.typeLabel }}
                                </el-tag>
                            </div>
                            <div class="t-row2">
                                <div class="cron-info">
                                    <el-icon>
                                        <Clock />
                                    </el-icon> {{ task.schedule }}
                                </div>
                                <el-switch v-model="task.status" size="small" @click.stop />
                            </div>
                        </div>
                        <el-icon class="arrow">
                            <ArrowRight />
                        </el-icon>
                    </div>
                </div>
            </div>

            <!-- Right: Details -->
            <div class="task-detail glass-panel">
                <template v-if="currentTask">
                    <div class="detail-header">
                        <div class="dh-left">
                            <div class="dh-title">{{ currentTask.name }}</div>
                            <div class="dh-meta">
                                <span class="meta-tag"><el-icon>
                                        <User />
                                    </el-icon> {{ currentTask.owner }}</span>
                                <span class="meta-tag"><el-icon>
                                        <Timer />
                                    </el-icon> 上次运行: {{ currentTask.lastRun }}</span>
                            </div>
                        </div>
                        <div class="dh-right">
                            <el-button circle icon="Edit" />
                            <el-button circle icon="Delete" type="danger" plain />
                        </div>
                    </div>

                    <div class="detail-body custom-scrollbar">
                        <!-- Preview Card -->
                        <div class="section-card preview-section">
                            <div class="sc-title">报告预览 (Snapshot)</div>
                            <div class="mock-preview">
                                <div class="mp-header">
                                    <div class="mp-logo">SMART REPORT</div>
                                    <div class="mp-date">2024-05-20</div>
                                </div>
                                <div class="mp-chart-area">
                                    <div class="mp-bar" style="height: 60%"></div>
                                    <div class="mp-bar" style="height: 80%"></div>
                                    <div class="mp-bar" style="height: 45%"></div>
                                    <div class="mp-bar" style="height: 90%"></div>
                                    <div class="mp-line"></div>
                                </div>
                                <div class="mp-text">
                                    <h3>运营摘要</h3>
                                    <p>本周全院门诊量环比上升 <strong>5.2%</strong>，运营效率显著提升...</p>
                                </div>
                            </div>
                        </div>

                        <!-- Config Info -->
                        <div class="row-grids">
                            <div class="section-card">
                                <div class="sc-title">分发配置</div>
                                <div class="config-list">
                                    <div class="cfg-item">
                                        <span class="lbl">推送渠道</span>
                                        <span class="val"><el-icon color="#07C160">
                                                <ChatDotRound />
                                            </el-icon> 企业微信</span>
                                    </div>
                                    <div class="cfg-item">
                                        <span class="lbl">接收对象</span>
                                        <div class="avatars">
                                            <el-avatar :size="20" class="av" v-for="i in 3" :key="i">U{{ i
                                                }}</el-avatar>
                                            <span class="more">+12</span>
                                        </div>
                                    </div>
                                    <div class="cfg-item">
                                        <span class="lbl">发送周期</span>
                                        <span class="val font-mono">0 0 8 * * ?</span>
                                    </div>
                                </div>
                            </div>

                            <div class="section-card">
                                <div class="sc-title">最近记录</div>
                                <el-timeline style="padding-left: 0">
                                    <el-timeline-item v-for="(log, idx) in currentTask.logs" :key="idx"
                                        :type="log.status === 'success' ? 'success' : 'danger'" :timestamp="log.time"
                                        hide-timestamp>
                                        <div class="log-item">
                                            <span class="log-time">{{ log.time }}</span>
                                            <span class="log-res" :class="log.status">{{ log.msg }}</span>
                                        </div>
                                    </el-timeline-item>
                                </el-timeline>
                            </div>
                        </div>
                    </div>
                </template>

                <div class="empty-placeholder" v-else>
                    <el-icon size="64">
                        <Document />
                    </el-icon>
                    <p>请选择左侧任务查看详情</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
    Share, Search, Clock, ArrowRight, User, Timer,
    ChatDotRound, Document, Checked, Promotion
} from '@element-plus/icons-vue'

const searchKey = ref('')

const stats = [
    { label: '运行中任务', value: '12', icon: Clock, color: 'blue' },
    { label: '累计发送', value: '8,542', icon: Promotion, color: 'green' },
    { label: '发送成功率', value: '99.8%', icon: Checked, color: 'orange' }
]

const tasks = ref([
    {
        id: 1, name: '全院晨报自动推送', type: 'daily', typeLabel: '日报', schedule: '每天 08:00', status: true, owner: 'Admin', lastRun: '今日 08:00',
        logs: [
            { time: '05-20 08:00', status: 'success', msg: '发送成功 (245人)' },
            { time: '05-19 08:00', status: 'success', msg: '发送成功 (244人)' },
            { time: '05-18 08:00', status: 'success', msg: '发送成功 (242人)' }
        ]
    },
    {
        id: 2, name: '科室运营周报', type: 'weekly', typeLabel: '周报', schedule: '每周一 09:00', status: true, owner: '王主任', lastRun: '05-13 09:00',
        logs: [
            { time: '05-13 09:00', status: 'success', msg: '发送成功 (12个科室)' },
            { time: '05-06 09:00', status: 'fail', msg: '数据生成超时' }
        ]
    },
    {
        id: 3, name: '月度质控分析报告', type: 'monthly', typeLabel: '月报', schedule: '每月1日 10:00', status: false, owner: '李科长', lastRun: '05-01 10:00',
        logs: [
            { time: '05-01 10:00', status: 'success', msg: '发送成功' }
        ]
    },
    {
        id: 4, name: '院长特别关注指标推送', type: 'custom', typeLabel: '自定义', schedule: '每小时', status: true, owner: 'Admin', lastRun: '14:00',
        logs: []
    }
])

const currentTask = ref<any>(tasks.value[0])

const filteredTasks = computed(() => {
    return tasks.value.filter(t => t.name.includes(searchKey.value))
})

const selectTask = (task: any) => {
    currentTask.value = task
}

const handleCreate = () => {
    // Logic for create
}
</script>

<style scoped lang="scss">
.report-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;
    gap: 20px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title-block {
        display: flex;
        gap: 16px;
        align-items: center;

        .icon-wrapper {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #8B5CF6, #D8B4FE);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 24px;
            box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);

            &.pulse {
                animation: pulse 2s infinite;
            }
        }

        .main-title {
            margin: 0;
            font-size: 20px;
            color: #1e293b;
        }

        .sub-title {
            margin: 0;
            font-size: 13px;
            color: #64748b;
            font-family: 'JetBrains Mono';
        }
    }

    .create-btn {
        background: #0dbda8;
        border-color: #0dbda8;
        color: #fff;

        &:hover {
            background: #0b9e8d;
            border-color: #0b9e8d;
            box-shadow: 0 4px 12px rgba(13, 189, 168, 0.3);
        }
    }
}

.stats-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    .glass-stat-card {
        background: #fff;
        border: 1px solid #e2e8f0;
        border-radius: 16px;
        padding: 20px;
        display: flex;
        align-items: center;
        gap: 16px;
        transition: all 0.3s;

        &:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
        }

        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;

            &.blue {
                background: #eff6ff;
                color: #3b82f6;
            }

            &.green {
                background: #ecfdf5;
                color: #10b981;
            }

            &.orange {
                background: #fff7ed;
                color: #f59e0b;
            }
        }

        .stat-info {
            .label {
                font-size: 13px;
                color: #64748b;
                margin-bottom: 4px;
            }

            .num {
                font-size: 24px;
                font-weight: 700;
                color: #1e293b;
            }
        }
    }
}

.main-layout {
    flex: 1;
    display: flex;
    gap: 20px;
    min-height: 0;

    .task-list {
        width: 320px;
        display: flex;
        flex-direction: column;
        padding: 0;

        .list-header {
            padding: 16px;
            border-bottom: 1px solid #f1f5f9;

            .filter-tabs {
                margin-top: 12px;
                display: flex;
                gap: 12px;

                .tab {
                    font-size: 13px;
                    color: #64748b;
                    cursor: pointer;
                    padding: 4px 8px;
                    border-radius: 6px;
                    transition: all 0.2s;

                    &:hover {
                        background: #f1f5f9;
                    }

                    &.active {
                        background: #eef2ff;
                        color: #6366f1;
                        font-weight: 600;
                    }
                }
            }
        }

        .list-scroll {
            flex: 1;
            overflow-y: auto;
            padding: 12px;
        }

        .task-item {
            padding: 12px;
            border-radius: 12px;
            margin-bottom: 8px;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 12px;
            position: relative;
            overflow: hidden;
            background: #fff;
            border: 1px solid transparent;
            transition: all 0.2s;

            &:hover {
                background: #f8fafc;
            }

            &.active {
                background: #f0f9ff;
                border-color: #bae6fd;

                .task-status-line {
                    opacity: 1;
                }
            }

            .task-status-line {
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                width: 4px;
                background: #cbd5e1;
                opacity: 0;

                &.on {
                    background: #10b981;
                }

                &.off {
                    background: #94a3b8;
                }
            }

            .task-main {
                flex: 1;

                .t-row1 {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 6px;

                    .t-name {
                        font-weight: 600;
                        font-size: 14px;
                        color: #1e293b;
                    }
                }

                .t-row2 {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;

                    .cron-info {
                        font-size: 12px;
                        color: #64748b;
                        display: flex;
                        align-items: center;
                        gap: 4px;
                    }
                }
            }

            .arrow {
                font-size: 12px;
                color: #94a3b8;
            }
        }
    }

    .task-detail {
        flex: 1;
        padding: 0;
        display: flex;
        flex-direction: column;
        overflow: hidden;

        .detail-header {
            padding: 24px;
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #fafafa;

            .dh-title {
                font-size: 20px;
                font-weight: 700;
                color: #1e293b;
                margin-bottom: 8px;
            }

            .dh-meta {
                display: flex;
                gap: 16px;

                .meta-tag {
                    font-size: 12px;
                    color: #64748b;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }
            }
        }

        .detail-body {
            flex: 1;
            overflow-y: auto;
            padding: 24px;

            .section-card {
                background: #fff;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 20px;

                .sc-title {
                    font-size: 14px;
                    font-weight: 700;
                    color: #334155;
                    margin-bottom: 16px;
                    border-left: 3px solid #6366f1;
                    padding-left: 8px;
                }
            }

            .preview-section {
                .mock-preview {
                    background: linear-gradient(to bottom, #f8fafc, #fff);
                    border: 1px solid #e2e8f0;
                    border-radius: 8px;
                    padding: 24px;

                    .mp-header {
                        display: flex;
                        justify-content: space-between;
                        border-bottom: 2px solid #000;
                        padding-bottom: 12px;
                        margin-bottom: 20px;

                        .mp-logo {
                            font-weight: 900;
                            font-size: 18px;
                            letter-spacing: 2px;
                        }

                        .mp-date {
                            font-family: 'JetBrains Mono';
                        }
                    }

                    .mp-chart-area {
                        height: 120px;
                        display: flex;
                        align-items: flex-end;
                        gap: 20px;
                        padding: 0 40px;
                        margin-bottom: 20px;
                        border-left: 1px solid #ccc;
                        border-bottom: 1px solid #ccc;

                        .mp-bar {
                            flex: 1;
                            background: #6366f1;
                            opacity: 0.8;
                            border-radius: 4px 4px 0 0;
                        }
                    }

                    .mp-text {
                        background: #f1f5f9;
                        padding: 12px;
                        border-radius: 6px;

                        h3 {
                            font-size: 14px;
                            margin: 0 0 8px 0;
                        }

                        p {
                            font-size: 12px;
                            color: #475569;
                            margin: 0;
                            line-height: 1.6;
                        }
                    }
                }
            }

            .row-grids {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .config-list {
                .cfg-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 12px 0;
                    border-bottom: 1px dashed #f1f5f9;

                    &:last-child {
                        border-bottom: none;
                    }

                    .lbl {
                        color: #64748b;
                        font-size: 13px;
                    }

                    .val {
                        font-weight: 600;
                        display: flex;
                        align-items: center;
                        gap: 6px;
                    }

                    .avatars {
                        display: flex;
                        align-items: center;

                        .av {
                            border: 2px solid #fff;
                            margin-left: -8px;

                            &:first-child {
                                margin-left: 0;
                            }

                            background: #cbd5e1;
                        }

                        .more {
                            font-size: 11px;
                            color: #94a3b8;
                            margin-left: 6px;
                        }
                    }
                }
            }

            .log-item {
                display: flex;
                justify-content: space-between;
                font-size: 12px;

                .log-time {
                    color: #94a3b8;
                    font-family: 'JetBrains Mono';
                }

                .log-res {
                    &.success {
                        color: #10b981;
                    }

                    &.fail {
                        color: #ef4444;
                    }
                }
            }
        }

        .empty-placeholder {
            height: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: #94a3b8;
            gap: 16px;
        }
    }
}

.glass-panel {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

@keyframes pulse {
    0% {
        transform: scale(1);
        box-shadow: 0 0 0 0 rgba(139, 92, 246, 0.7);
    }

    70% {
        transform: scale(1.05);
        box-shadow: 0 0 0 10px rgba(139, 92, 246, 0);
    }

    100% {
        transform: scale(1);
        box-shadow: 0 0 0 0 rgba(139, 92, 246, 0);
    }
}

.animate-enter {
    animation: fadeInUp 0.5s ease-out;
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

/* Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 2px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}

.font-mono {
    font-family: 'JetBrains Mono', monospace;
}
</style>
