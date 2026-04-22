<template>
    <div class="feedback-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header">
                <div class="title-with-icon">
                    <div class="icon-box warning">
                        <el-icon :size="20">
                            <Message />
                        </el-icon>
                    </div>
                    <div class="text-group">
                        <span class="title">数据反馈管理</span>
                    </div>
                </div>
                <div class="actions">
                    <div class="radio-pill-group">
                        <span class="pill-option" :class="{ active: statusFilter === 'all' }"
                            @click="statusFilter = 'all'">全部</span>
                        <span class="pill-option" :class="{ active: statusFilter === '0' }"
                            @click="statusFilter = '0'">待处理</span>
                        <span class="pill-option" :class="{ active: statusFilter === '1' }"
                            @click="statusFilter = '1'">处理中</span>
                        <span class="pill-option" :class="{ active: statusFilter === '2' }"
                            @click="statusFilter = '2'">已解决</span>
                    </div>
                </div>
            </div>

            <el-table :data="filteredData" class="premium-table" style="width: 100%" height="calc(100vh - 280px)">
                <el-table-column prop="date" label="提交时间" width="180">
                    <template #default="{ row }"><span class="text-secondary text-sm font-mono">{{ row.date
                    }}</span></template>
                </el-table-column>
                <el-table-column prop="indicator" label="关联指标" width="180">
                    <template #default="{ row }"><span class="fw-bold">{{ row.indicator }}</span></template>
                </el-table-column>
                <el-table-column prop="user" label="提交人" width="120">
                    <template #default="{ row }">
                        <div class="user-cell">
                            <el-avatar :size="24" class="mr-2 avatar-bg">{{ row.user.charAt(0) }}</el-avatar>
                            <span>{{ row.user }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="问题类型" width="120">
                    <template #default="{ row }">
                        <el-tag :type="getIssueTypeTag(row.type)" effect="light" round size="small">{{ row.type
                        }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="desc" label="问题描述" min-width="240" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <div class="status-badge" :class="getStatusClass(row.status)">
                            <span class="dot"></span> {{ getStatusLabel(row.status) }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button v-if="row.status !== '2'" type="primary" link size="small"
                            @click="handleProcess(row)">
                            <el-icon class="mr-1">
                                <EditPen />
                            </el-icon> 处理
                        </el-button>
                        <el-button v-else type="success" link size="small" disabled>
                            <el-icon class="mr-1">
                                <Check />
                            </el-icon> 已完结
                        </el-button>
                        <el-button type="info" link size="small">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-footer mt-4">
                <el-pagination background layout="prev, pager, next, total" :total="filteredData.length" />
            </div>
        </div>

        <!-- Process Dialog -->
        <el-dialog v-model="processVisible" title="反馈处理" width="500px" class="glass-dialog">
            <el-form label-position="top">
                <el-form-item label="当前状态">
                    <div class="status-badge large" :class="getStatusClass(currentRow?.status)">
                        <span class="dot"></span> {{ getStatusLabel(currentRow?.status) }}
                    </div>
                </el-form-item>
                <el-form-item label="处理动作">
                    <el-radio-group v-model="processForm.action">
                        <el-radio label="1" border>转交核实</el-radio>
                        <el-radio label="2" border>标记已解决</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="处理意见">
                    <el-input type="textarea" v-model="processForm.comment" :rows="3" placeholder="请输入处理意见..."
                        class="glass-input" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="processVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmProcess">提交处理</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Message, EditPen, Check } from '@element-plus/icons-vue'
import { getFeedbackList, processFeedback } from '@/api/feedback'

const statusFilter = ref('all')
const processVisible = ref(false)
const currentRow = ref<any>(null)
const processForm = reactive({ action: '1', comment: '' })

/** 分页参数 */
const pagination = reactive({ pageNum: 1, pageSize: 20, total: 0 })
const tableData = ref<any[]>([])
const tableLoading = ref(false)

/** 加载反馈列表 */
const loadData = async () => {
    tableLoading.value = true
    try {
        const statusParam = statusFilter.value === 'all' ? undefined : Number(statusFilter.value)
        const res: any = await getFeedbackList({
            pageNum: pagination.pageNum,
            pageSize: pagination.pageSize,
            status: statusParam
        })
        // 后端返回 Page 对象，包含 records / total
        if (res && res.records) {
            const indicatorMap: any = {
                1: '门急诊收入',
                2: '住院人次',
                3: '药占比',
                4: '门急诊总人次',
                5: '四级手术率',
                6: '住院总收入',
                7: '平均住院日'
            }

            tableData.value = res.records.map((item: any) => {
                // 根据内容简单推断问题类型
                let inferredType = '其他问题'
                const content = item.content || ''
                if (content.includes('缺失') || content.includes('显示为0') || content.includes('缺少')) {
                    inferredType = '数据缺失'
                } else if (content.includes('错误') || content.includes('计算') || content.includes('偏高') || content.includes('偏低')) {
                    inferredType = '计算错误'
                } else if (content.includes('口径') || content.includes('定义') || content.includes('不一致')) {
                    inferredType = '逻辑口径'
                }

                return {
                    ...item,
                    date: item.createTime ? item.createTime.replace('T', ' ').substring(0, 19) : '-',
                    user: item.createBy || '系统',
                    indicator: indicatorMap[item.indicatorId] || `未知指标(${item.indicatorId})`,
                    type: inferredType,
                    desc: item.content,
                    status: String(item.status)
                }
            })
            pagination.total = res.total || 0
        }
    } catch (e) {
        console.error('加载反馈列表失败:', e)
    } finally {
        tableLoading.value = false
    }
}

const filteredData = computed(() => tableData.value)

const getStatusLabel = (status: string) => { const map: any = { '0': '待处理', '1': '处理中', '2': '已打回', '3': '已修复' }; return map[status] || status }
const getStatusClass = (status: string) => { const map: any = { '0': 'status-danger', '1': 'status-warning', '2': 'status-info', '3': 'status-success' }; return map[status] || 'status-info' }
const getIssueTypeTag = (type: string) => {
    const map: any = {
        '数据缺失': 'danger',
        '计算错误': 'warning',
        '逻辑口径': 'primary',
        '数据不一致': 'warning',
        '其他问题': 'info'
    }
    return map[type] || 'info'
}

const handleProcess = (row: any) => { currentRow.value = row; processForm.action = row.status === '0' ? '1' : '3'; processForm.comment = ''; processVisible.value = true }

/** 提交处理 */
const confirmProcess = async () => {
    if (!currentRow.value) return
    try {
        await processFeedback({
            id: currentRow.value.id,
            status: Number(processForm.action),
            processor: 'admin',
            resultMsg: processForm.comment
        })
        ElMessage.success('处理成功')
        processVisible.value = false
        loadData()
    } catch (e) {
        ElMessage.error('处理失败')
    }
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.feedback-container {
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

    .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        .title-with-icon {
            display: flex;
            align-items: center;
            gap: 12px;

            .icon-box {
                width: 40px;
                height: 40px;
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #fff;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);

                &.warning {
                    background: linear-gradient(135deg, #f59e0b, #fbbf24);
                    box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3);
                }
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
    }

    .radio-pill-group {
        background: #f1f5f9;
        padding: 4px;
        border-radius: 8px;
        display: flex;
        gap: 4px;

        .pill-option {
            padding: 6px 16px;
            border-radius: 6px;
            font-size: 12px;
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

    .user-cell {
        display: flex;
        align-items: center;

        .avatar-bg {
            background: var(--primary-color);
            font-size: 12px;
        }
    }

    .font-mono {
        font-family: 'JetBrains Mono', monospace;
    }

    .text-sm {
        font-size: 12px;
    }

    .fw-bold {
        font-weight: 600;
    }

    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 4px 10px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;

        .dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
        }

        &.status-danger {
            background: #fef2f2;
            color: #ef4444;

            .dot {
                background: #ef4444;
            }
        }

        &.status-warning {
            background: #fffbeb;
            color: #f59e0b;

            .dot {
                background: #f59e0b;
            }
        }

        &.status-success {
            background: #ecfdf5;
            color: #10b981;

            .dot {
                background: #10b981;
            }
        }

        &.large {
            padding: 6px 16px;
            font-size: 14px;

            .dot {
                width: 8px;
                height: 8px;
            }
        }
    }

    .glass-input {
        :deep(.el-input__wrapper) {
            box-shadow: none !important;
            border: 1px solid #e2e8f0;
            background: #f8fafc;

            &:hover,
            &.is-focus {
                background: #fff;
                border-color: var(--primary-color);
            }
        }
    }

    .mt-4 {
        margin-top: 20px;
    }

    .mr-2 {
        margin-right: 8px;
    }

    .mr-1 {
        margin-right: 4px;
    }

    .pagination-footer {
        margin-top: auto;
        display: flex;
        justify-content: flex-end;
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
}
</style>
