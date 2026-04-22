<template>
    <div class="ai-diagnosis-container animate-enter">
        <!-- Header -->
        <div class="header-section">
            <div class="title-group">
                <div class="icon-box pulse">
                    <el-icon>
                        <Cpu />
                    </el-icon>
                </div>
                <div>
                    <div class="main-title">智能辅助诊疗系统</div>
                    <div class="sub-title">AI Clinical Decision Support System</div>
                </div>
            </div>
            <div class="actions">
                <el-button round icon="Timer" @click="historyVisible = true">历史记录</el-button>
                <el-button type="primary" round icon="Plus" class="glow-btn" @click="handleNew">新会诊</el-button>
            </div>
        </div>

        <div class="main-content">
            <!-- Left: Case Input -->
            <div class="left-panel glass-panel">
                <div class="panel-title">
                    <el-icon>
                        <Document />
                    </el-icon> 病历概要
                </div>
                <div class="form-scroll custom-scrollbar">
                    <el-form label-position="top">
                        <el-form-item label="主诉 (Chief Complaint)">
                            <el-input type="textarea" :rows="2" v-model="form.complaint"
                                placeholder="例如：持续性胸痛2小时，向左肩放射..." />
                        </el-form-item>
                        <el-form-item label="现病史 (HPI)">
                            <el-input type="textarea" :rows="4" v-model="form.history"
                                placeholder="详细描述发病过程、诱因、缓解因素..." />
                        </el-form-item>
                        <el-row :gutter="12">
                            <el-col :span="12">
                                <el-form-item label="体温 (℃)">
                                    <el-input v-model="form.temp" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="血压 (mmHg)">
                                    <el-input v-model="form.bp" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="既往史">
                            <el-checkbox-group v-model="form.past" class="past-history-group">
                                <el-checkbox label="高血压" />
                                <el-checkbox label="糖尿病" />
                                <el-checkbox label="冠心病" />
                                <el-checkbox label="吸烟史" />
                            </el-checkbox-group>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="panel-footer">
                    <el-button class="w-100" type="primary" size="large" :loading="analyzing" @click="handleAnalyze">
                        <el-icon class="mr-2">
                            <MagicStick />
                        </el-icon> 开始智能分析
                    </el-button>
                </div>
            </div>

            <!-- Right: AI Analysis -->
            <div class="right-panel">
                <div class="result-card glass-panel" v-loading="analyzing" element-loading-text="AI引擎正在运算中...">
                    <template v-if="result">
                        <div class="result-header">
                            <div class="score-box">
                                <span class="label">置信度</span>
                                <span class="val">{{ result.confidence }}%</span>
                            </div>
                            <div class="diag-name">{{ result.diagnosis }}</div>
                            <el-tag type="danger" effect="dark" round>高风险</el-tag>
                        </div>

                        <div class="result-body custom-scrollbar">
                            <!-- Probability Chart -->
                            <div class="section">
                                <div class="sec-title">鉴别诊断 (Differential Diagnosis)</div>
                                <div class="prog-row" v-for="item in result.diffs" :key="item.name">
                                    <div class="info">
                                        <span>{{ item.name }}</span>
                                        <span>{{ item.prob }}%</span>
                                    </div>
                                    <el-progress :percentage="item.prob" :color="item.color" :show-text="false"
                                        stroke-width="8" />
                                </div>
                            </div>

                            <!-- Recommendations -->
                            <div class="section">
                                <div class="sec-title">检查建议</div>
                                <div class="tags">
                                    <el-tag v-for="test in result.tests" :key="test" class="mr-2 mb-2" effect="plain">{{
                                        test }}</el-tag>
                                </div>
                            </div>

                            <!-- Treatment -->
                            <div class="section">
                                <div class="sec-title">治疗方案建议</div>
                                <div class="markdown-text">{{ result.treatment }}</div>
                            </div>

                            <!-- Reference -->
                            <div class="citation">
                                <el-icon>
                                    <Collection />
                                </el-icon> 参考指南：{{ result.ref }}
                            </div>
                        </div>
                    </template>
                    <div class="empty-state" v-else>
                        <el-icon size="64" color="#cbd5e1">
                            <Cpu />
                        </el-icon>
                        <p>请输入病历信息并点击分析</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 历史记录抽屉 -->
    <el-drawer v-model="historyVisible" title="历史诊断记录" size="400px">
        <div class="history-list custom-scrollbar">
            <div v-for="item in mockHistory" :key="item.id" class="history-item" @click="selectHistory(item)">
                <div class="h-header">
                    <span class="h-date">{{ item.date }}</span>
                    <el-tag size="small" :type="item.confidence > 90 ? 'danger' : 'warning'">{{ item.confidence }}%
                        置信度</el-tag>
                </div>
                <div class="h-title">{{ item.diagnosis }}</div>
                <div class="h-desc">{{ item.complaint }}</div>
            </div>
        </div>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Cpu, Document, MagicStick, Timer, Plus, Collection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const analyzing = ref(false)
const historyVisible = ref(false)
const form = reactive({
    complaint: '突发胸骨后压榨性疼痛2小时，向左肩背部放射，伴大汗、恶心。',
    history: '患者2小时前劳累后突发胸痛，程度剧烈，含服硝酸甘油不缓解。无呼吸困难，无意识丧失。',
    temp: '36.8',
    bp: '150/90',
    past: ['高血压', '吸烟史']
})

const result = ref<any>(null)

// Mock 历史数据
const mockHistory = [
    { id: 101, date: '2024-03-04 14:20', diagnosis: '急性心肌梗死 (AMI)', confidence: 94, complaint: '突发胸骨后压榨性疼痛2小时...', history: '患者2小时前劳累后突发胸痛...', temp: '36.8', bp: '150/90', past: ['高血压', '吸烟史'] },
    { id: 102, date: '2024-03-02 09:15', diagnosis: '社区获得性肺炎', confidence: 88, complaint: '发热伴咳嗽、咳痰3天...', history: '3天前受凉后出现发热，最高38.5℃...', temp: '38.2', bp: '125/82', past: [] },
    { id: 103, date: '2024-02-28 16:40', diagnosis: '带状疱疹', confidence: 92, complaint: '左侧胸背部皮疹伴剧烈刺痛...', history: '2天前出现红斑，后转化为水疱...', temp: '37.0', bp: '132/85', past: ['糖尿病'] }
]

const handleNew = () => {
    form.complaint = ''
    form.history = ''
    form.temp = ''
    form.bp = ''
    form.past = []
    result.value = null
    ElMessage.info('已开启新会诊会话')
}

const selectHistory = (item: any) => {
    Object.assign(form, {
        complaint: item.complaint,
        history: item.history,
        temp: item.temp,
        bp: item.bp,
        past: item.past
    })
    result.value = null // 重新点击分析
    historyVisible.value = false
    ElMessage.success('已加载历史病历')
}

const handleAnalyze = () => {
    analyzing.value = true
    setTimeout(() => {
        analyzing.value = false
        result.value = {
            diagnosis: '急性心肌梗死 (AMI)',
            confidence: 94,
            diffs: [
                { name: '急性心肌梗死', prob: 94, color: '#ef4444' },
                { name: '不稳定型心绞痛', prob: 65, color: '#f59e0b' },
                { name: '主动脉夹层', prob: 30, color: '#3b82f6' }
            ],
            tests: ['12导联心电图', '心肌损伤标志物(肌钙蛋白)', '超声心动图', '血常规+凝血'],
            treatment: '立即给予阿司匹林300mg嚼服，氯吡格雷300mg口服。建立静脉通道，吸氧，心电监护。评估急诊PCI指征。',
            ref: '《急性ST段抬高型心肌梗死诊断和治疗指南(2019)》'
        }
    }, 2000)
}
</script>

<style scoped lang="scss">
.ai-diagnosis-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;
}

.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .title-group {
        display: flex;
        gap: 16px;
        align-items: center;

        .icon-box {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 24px;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);

            &.pulse {
                animation: pulse 2s infinite;
            }
        }

        .main-title {
            font-size: 20px;
            font-weight: 700;
            color: #1e293b;
        }

        .sub-title {
            font-size: 13px;
            color: #64748b;
            font-family: 'JetBrains Mono';
        }
    }
}

.main-content {
    flex: 1;
    display: flex;
    gap: 24px;
    min-height: 0;
}

.left-panel {
    width: 400px;
    display: flex;
    flex-direction: column;
    padding: 20px;

    .panel-title {
        font-weight: 700;
        color: #334155;
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 16px;
    }

    .form-scroll {
        flex: 1;
        overflow-y: auto;
        padding-right: 8px;
    }

    .panel-footer {
        margin-top: 16px;
    }

    .past-history-group {
        display: flex;
        flex-direction: column;
        gap: 8px;

        :deep(.el-checkbox) {
            margin-right: 0;
            height: auto;
            padding: 8px 12px;
            border-radius: 8px;
            border: 1px solid #f1f5f9;
            background: #f8fafc;
            transition: all 0.2s;
            width: 100%;

            &:hover {
                background: #f1f5f9;
                border-color: #e2e8f0;
            }

            &.is-checked {
                background: rgba(99, 102, 241, 0.05);
                border-color: rgba(99, 102, 241, 0.2);
            }

            .el-checkbox__label {
                font-size: 14px;
                color: #475569;
            }
        }
    }
}

.right-panel {
    flex: 1;
    display: flex;
    flex-direction: column;

    .result-card {
        flex: 1;
        padding: 0;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        border-color: #6366f1; // Highlight
        background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.05), transparent 40%), #fff;
    }

    .result-header {
        padding: 24px;
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        align-items: center;
        gap: 16px;
        background: linear-gradient(to bottom, #f8fafc, #fff);

        .score-box {
            display: flex;
            flex-direction: column;
            align-items: center;
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 4px 12px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

            .label {
                font-size: 10px;
                color: #64748b;
            }

            .val {
                font-size: 18px;
                font-weight: 800;
                color: #6366f1;
            }
        }

        .diag-name {
            font-size: 24px;
            font-weight: 700;
            color: #1e293b;
            flex: 1;
        }
    }

    .result-body {
        flex: 1;
        padding: 24px;
        overflow-y: auto;

        .section {
            margin-bottom: 32px;
        }

        .sec-title {
            font-size: 14px;
            font-weight: 700;
            color: #334155;
            margin-bottom: 12px;
            border-left: 3px solid #6366f1;
            padding-left: 8px;
        }

        .prog-row {
            margin-bottom: 12px;

            .info {
                display: flex;
                justify-content: space-between;
                font-size: 13px;
                margin-bottom: 4px;
                color: #475569;
            }
        }

        .markdown-text {
            font-size: 14px;
            line-height: 1.6;
            color: #334155;
            background: #f8fafc;
            padding: 16px;
            border-radius: 8px;
            border: 1px solid #e2e8f0;
        }

        .citation {
            margin-top: auto;
            padding-top: 20px;
            border-top: 1px dashed #e2e8f0;
            font-size: 12px;
            color: #94a3b8;
            display: flex;
            align-items: center;
            gap: 6px;
        }
    }

    .empty-state {
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 16px;
        color: #94a3b8;
    }
}

.glass-panel {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.glow-btn {
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    background: #6366f1;
    border-color: #6366f1;

    &:hover {
        background: #4f46e5;
        border-color: #4f46e5;
    }
}

@keyframes pulse {
    0% {
        transform: scale(1);
        box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.7);
    }

    70% {
        transform: scale(1.05);
        box-shadow: 0 0 0 10px rgba(99, 102, 241, 0);
    }

    100% {
        transform: scale(1);
        box-shadow: 0 0 0 0 rgba(99, 102, 241, 0);
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

.w-100 {
    width: 100%;
}

.mr-2 {
    margin-right: 8px;
}

.mb-2 {
    margin-bottom: 8px;
}

.history-list {
    padding: 10px;

    .history-item {
        padding: 16px;
        border-radius: 12px;
        border: 1px solid #f1f5f9;
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
            border-color: #6366f1;
            background: #f8fafc;
            transform: scale(1.02);
        }

        .h-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .h-date {
                font-size: 12px;
                color: #94a3b8;
            }
        }

        .h-title {
            font-size: 15px;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 6px;
        }

        .h-desc {
            font-size: 12px;
            color: #64748b;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    }
}
</style>
