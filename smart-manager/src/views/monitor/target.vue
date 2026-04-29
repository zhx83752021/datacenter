<template>
    <div class="target-config-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header">
                <div class="title-with-icon">
                    <div class="icon-box">
                        <el-icon :size="20">
                            <Aim />
                        </el-icon>
                    </div>
                    <div class="text-group">
                        <span class="title">目标与预警管理</span>
                        <span class="subtitle">设定核心指标的基准值与预警阈值</span>
                    </div>
                </div>
                <div class="actions">
                    <el-button icon="Upload" round @click="importVisible = true">导入目标</el-button>
                    <el-button type="success" icon="Checked" round @click="saveAll" class="save-btn">批量保存</el-button>
                    <el-button icon="Bell" circle @click="pushConfigVisible = true" title="预警配置"></el-button>
                </div>
            </div>

            <div class="filter-bar glass-inner target-filter-bar">
                <el-form :inline="true" :model="filters" class="premium-form target-filter-form">
                    <el-form-item label="考核年度">
                        <el-date-picker v-model="filters.year" type="year" placeholder="选择年份" class="target-filter-year"
                            :clearable="false" />
                    </el-form-item>
                    <el-form-item label="指标名称">
                        <el-select v-model="filters.indicator" placeholder="请选择指标" class="target-filter-indicator">
                            <el-option label="门急诊总人次" value="1" />
                            <el-option label="医疗总收入" value="2" />
                            <el-option label="四级手术率" value="3" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" circle @click="handleSearch"></el-button>
                    </el-form-item>
                </el-form>
            </div>

            <div class="info-alert mb-4">
                <el-icon class="mr-2 text-primary">
                    <InfoFilled />
                </el-icon>
                <span>提示：目标值设定后将作为全院监控及绩效考核的基准，请谨慎修改。</span>
            </div>

            <!-- 移动端关闭 fixed：窄屏固定列占宽且 shadows 叠层；桌面保留左右固定便于览表 -->
            <div class="target-table-shell" :class="tableShellClass">
            <el-table :data="tableData" class="premium-table" style="width: 100%" height="100%">
                <el-table-column prop="deptName" label="考核科室" width="160" :fixed="fixedLeft">
                    <template #default="{ row }"><span class="fw-bold">{{ row.deptName }}</span></template>
                </el-table-column>
                <el-table-column label="历史参考值 (去年同期)" width="180" align="right">
                    <template #default="{ row }">
                        <span class="text-secondary font-mono">{{ row.historyVal }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="目标值 (Target)" min-width="160" align="center">
                    <template #default="{ row }">
                        <el-input-number v-model="row.targetVal" :precision="0" :step="100"
                            class="premium-input-number w-100" />
                    </template>
                </el-table-column>
                <el-table-column label="预警区间 (Warning)" min-width="260" align="center">
                    <template #header>
                        <div class="th-with-icon warning">
                            <span>预警区间</span>
                            <el-tooltip content="超出此范围将触发黄色预警"><el-icon>
                                    <QuestionFilled />
                                </el-icon></el-tooltip>
                        </div>
                    </template>
                    <template #default="{ row }">
                        <div class="range-group warning">
                            <el-input-number v-model="row.warnMin" :controls="false" placeholder="Min"
                                class="mini-input" />
                            <span class="divider">~</span>
                            <el-input-number v-model="row.warnMax" :controls="false" placeholder="Max"
                                class="mini-input" />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="危急值 (Critical)" min-width="260" align="center">
                    <template #header>
                        <div class="th-with-icon critical">
                            <span>危急值</span>
                            <el-tooltip content="超出此范围将触发红色高警"><el-icon>
                                    <QuestionFilled />
                                </el-icon></el-tooltip>
                        </div>
                    </template>
                    <template #default="{ row }">
                        <div class="range-group critical">
                            <el-input-number v-model="row.critMin" :controls="false" placeholder="Min"
                                class="mini-input" />
                            <span class="divider">~</span>
                            <el-input-number v-model="row.critMax" :controls="false" placeholder="Max"
                                class="mini-input" />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" :fixed="fixedRight" align="center">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="handleTrend(row)"><el-icon>
                                <TrendCharts />
                            </el-icon></el-button>
                    </template>
                </el-table-column>
            </el-table>
            </div>
        </div>

        <!-- Push Config Dialog -->
        <el-dialog v-model="pushConfigVisible" title="预警推送配置" width="500px" class="glass-dialog">
            <el-form :model="pushForm" label-position="top">
                <el-form-item label="推送渠道">
                    <div class="checkbox-pill-group">
                        <el-checkbox-group v-model="pushForm.channels">
                            <el-checkbox label="wechat" border>企业微信</el-checkbox>
                            <el-checkbox label="sms" border>短信通知</el-checkbox>
                            <el-checkbox label="mail" border>邮件</el-checkbox>
                        </el-checkbox-group>
                    </div>
                </el-form-item>
                <el-form-item label="推送频率">
                    <el-radio-group v-model="pushForm.frequency">
                        <el-radio label="realtime" border>实时推送</el-radio>
                        <el-radio label="daily" border>每日汇总 (08:00)</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="接收对象">
                    <el-select v-model="pushForm.receivers" multiple placeholder="选择接收角色" class="w-100 glass-select">
                        <el-option label="科主任" value="director" />
                        <el-option label="主管院长" value="dean" />
                        <el-option label="指标负责人" value="owner" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="pushConfigVisible = false">取消</el-button>
                    <el-button type="primary" @click="pushConfigVisible = false">保存配置</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 导入目标弹窗 -->
        <el-dialog v-model="importVisible" title="导入考核目标" width="460px" class="glass-dialog import-dialog">
            <div class="import-content">
                <div class="step-item">
                    <div class="step-num">1</div>
                    <div class="step-body">
                        <div class="step-title">下载导入模版</div>
                        <div class="step-desc">请先下载标准的导入模版，并按规范填写指标数值</div>
                        <el-button size="small" icon="Download" @click="downloadTemplate">下载 Excel 模版</el-button>
                    </div>
                </div>
                <div class="step-item mt-6">
                    <div class="step-num">2</div>
                    <div class="step-body">
                        <div class="step-title">上传数据文件</div>
                        <div class="step-desc">支持 .xlsx, .xls 格式文件，单次最大 10MB</div>
                        <el-upload class="target-uploader" drag action="#" :auto-upload="false"
                            :on-change="handleUploadSuccess" :show-file-list="false">
                            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                            <div class="el-upload__text">
                                将文件拖到此处，或 <em>点击上传</em>
                            </div>
                        </el-upload>
                    </div>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="importVisible = false">取消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Aim, Upload, Checked, Bell, Search, InfoFilled, QuestionFilled, TrendCharts, UploadFilled, Download } from '@element-plus/icons-vue'
import { useTableFixedColumns } from '@/composables/useTableFixedColumns'

const { fixedLeft, fixedRight, tableShellClass } = useTableFixedColumns()

const filters = reactive({ year: '2024', indicator: '1' })
const pushConfigVisible = ref(false)
const importVisible = ref(false)
const pushForm = reactive({ channels: ['wechat'], frequency: 'realtime', receivers: ['director', 'owner'] })

const tableData = ref([
    { id: 1, deptName: '心血管内科', historyVal: 1200, targetVal: 1300, warnMin: 1250, warnMax: 1350, critMin: 1100, critMax: 1400 },
    { id: 2, deptName: '呼吸内科', historyVal: 980, targetVal: 1050, warnMin: 1000, warnMax: 1100, critMin: 900, critMax: 1200 },
    { id: 3, deptName: '普外科', historyVal: 850, targetVal: 900, warnMin: 880, warnMax: 920, critMin: 800, critMax: 950 },
    { id: 4, deptName: '神经外科', historyVal: 420, targetVal: 450, warnMin: 440, warnMax: 460, critMin: 400, critMax: 500 },
    { id: 5, deptName: '骨科', historyVal: 760, targetVal: 800, warnMin: 780, warnMax: 820, critMin: 700, critMax: 900 },
])

const handleSearch = () => { ElMessage.success('查询成功') }
const saveAll = () => { ElMessage.success('批量保存成功') }
const handleTrend = (row: any) => { ElMessage.info(`查看 ${row.deptName} 历史趋势 (Mock)`) }

const downloadTemplate = () => {
    // 构造 CSV 内容
    const headers = ['考核年份', '指标名称', '考核科室', '历史参考值', '目标值(Target)', '预警显示低值', '预警显示高值', '危急显示低值', '危急显示高值']
    const sampleData = [
        ['2024', '门急诊总人次', '心血管内科', '1200', '1300', '1250', '1350', '1100', '1400'],
        ['2024', '门急诊总人次', '呼吸内科', '980', '1050', '1000', '1100', '900', '1200']
    ]

    const csvContent = [headers, ...sampleData].map(e => e.map(v => `"${v}"`).join(",")).join("\n")

    // 添加 BOM 防止 Excel 打开乱码
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement("a")
    const url = URL.createObjectURL(blob)

    link.setAttribute("href", url)
    link.setAttribute("download", `考核目标导入模版_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('模版已生成并开始下载')
}

const handleUploadSuccess = (file: any) => {
    const loading = ElLoading.service({
        target: '.import-dialog',
        text: '正在解析数据并校验合规性...',
        background: 'rgba(255, 255, 255, 0.7)'
    })

    setTimeout(() => {
        loading.close()
        importVisible.value = false
        ElMessage.success({
            message: `成功解析文件: ${file.name}，共更新 12 条科室指标目标值`,
            duration: 5000,
            showClose: true
        })
    }, 2000)
}
</script>

<style scoped lang="scss">
.target-config-container {
    height: 100%;
    padding-bottom: 20px;

    .glass-panel {
        background: #fff;
        border-radius: 20px;
        border: 1px solid #e2e8f0;
        padding: 24px;
        height: 100%;
        display: flex;
        flex-direction: column;
        transition: all 0.3s;

        &:hover {
            border-color: rgba(13, 189, 168, 0.3);
        }
    }

    .target-table-shell {
        flex: 1;
        min-height: 0;
        min-width: 0;
        width: 100%;
    }

    .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .title-with-icon {
            display: flex;
            align-items: center;
            gap: 12px;

            .icon-box {
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, #0dbda8, #4FC3F7);
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #fff;
                box-shadow: 0 4px 10px rgba(13, 189, 168, 0.3);
            }

            .text-group {
                display: flex;
                flex-direction: column;

                .title {
                    font-size: 18px;
                    font-weight: 700;
                    color: var(--text-primary);
                }

                .subtitle {
                    font-size: 12px;
                    color: var(--text-secondary);
                }
            }
        }

        .actions {
            display: flex;
            gap: 12px;

            .save-btn {
                box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
            }
        }
    }

    .filter-bar {
        background: #f8fafc;
        padding: 16px 20px;
        border-radius: 12px;
        margin-bottom: 16px;

        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 24px;
        }
    }

    .info-alert {
        background: #ecfdf5;
        border: 1px solid #d1fae5;
        border-radius: 8px;
        padding: 10px 16px;
        font-size: 13px;
        color: #047857;
        display: flex;
        align-items: center;
    }

    // Table Styling
    .fw-bold {
        font-weight: 600;
        color: var(--text-primary);
    }

    .font-mono {
        font-family: 'JetBrains Mono', monospace;
    }

    .text-secondary {
        color: var(--text-secondary);
    }

    .premium-input-number {
        :deep(.el-input__wrapper) {
            box-shadow: none !important;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            background: #fff;

            &:hover,
            &.is-focus {
                border-color: var(--primary-color);
            }
        }
    }

    .th-with-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;

        &.warning {
            color: #f59e0b;
        }

        &.critical {
            color: #ef4444;
        }
    }

    .range-group {
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f8fafc;
        padding: 4px;
        border-radius: 8px;
        border: 1px solid transparent;

        .mini-input {
            width: 80px;

            :deep(.el-input__wrapper) {
                box-shadow: none !important;
                background: transparent;
                padding: 0 8px;
            }

            :deep(input) {
                text-align: center;
            }
        }

        .divider {
            color: var(--text-secondary);
            margin: 0 4px;
        }

        &.warning {
            background: #fffbeb;
            border-color: #fef3c7;
        }

        &.critical {
            background: #fef2f2;
            border-color: #fee2e2;
        }
    }

    .w-100 {
        width: 100%;
    }

    .mb-4 {
        margin-bottom: 16px;
    }

    .mr-2 {
        margin-right: 8px;
    }

    .target-filter-year {
        width: 120px;
        max-width: 100%;
    }

    .target-filter-indicator {
        width: 220px;
        max-width: 100%;
    }

    @media (max-width: 768px) {
        .panel-header {
            flex-direction: column;
            align-items: stretch;
            gap: 14px;

            .actions {
                flex-wrap: wrap;
                justify-content: flex-end;
            }
        }

        .glass-panel {
            padding: 16px;
        }

        .filter-bar {
            padding: 12px 14px;

            .target-filter-form {
                :deep(.el-form-item) {
                    margin-right: 0;
                    margin-bottom: 12px;
                    display: block;

                    &:last-child {
                        margin-bottom: 0;
                    }
                }
            }
        }

        .target-filter-year,
        .target-filter-indicator {
            width: 100% !important;
        }

        .info-alert {
            align-items: flex-start;
            font-size: 12px;
            line-height: 1.5;
        }

        .target-table-shell {
            min-height: 320px;
        }
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
}

// 导入弹窗样式
.import-dialog {
    :deep(.el-dialog__body) {
        padding-top: 10px;
    }

    .step-item {
        display: flex;
        gap: 16px;

        .step-num {
            width: 24px;
            height: 24px;
            background: #0dbda8;
            color: #fff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: 700;
            flex-shrink: 0;
            margin-top: 2px;
        }

        .step-title {
            font-weight: 700;
            color: #1e293b;
            font-size: 15px;
            margin-bottom: 4px;
        }

        .step-desc {
            font-size: 12px;
            color: #94a3b8;
            margin-bottom: 12px;
        }
    }

    .target-uploader {
        :deep(.el-upload-dragger) {
            padding: 20px;
            border: 2px dashed #e2e8f0;
            background: #f8fafc;

            &:hover {
                border-color: #0dbda8;
            }

            .el-icon--upload {
                font-size: 32px;
                color: #94a3b8;
                margin-bottom: 8px;
            }

            .el-upload__text {
                font-size: 13px;
                color: #64748b;

                em {
                    color: #0dbda8;
                    font-weight: 600;
                    font-style: normal;
                }
            }
        }
    }
}
</style>
