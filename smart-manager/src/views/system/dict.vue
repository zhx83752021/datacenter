<template>
    <div class="dict-container animate-enter">
        <div class="content-layout">
            <!-- 左侧：字典类型列表 -->
            <div class="glass-panel left-panel">
                <div class="panel-header">
                    <div class="title-with-icon">
                        <div class="icon-box">
                            <el-icon :size="18">
                                <Notebook />
                            </el-icon>
                        </div>
                        <span class="title">字典分类</span>
                    </div>
                    <el-button link type="primary" size="small" @click="handleAddType">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-button>
                </div>

                <div class="search-box">
                    <el-input v-model="typeSearch" placeholder="搜索字典..." prefix-icon="Search" class="clean-input"
                        clearable />
                </div>

                <div class="type-list custom-scrollbar">
                    <div class="type-item" v-for="item in filteredTypes" :key="item.dictId"
                        :class="{ active: currentType?.dictId === item.dictId }" @click="selectType(item)">
                        <div class="type-info">
                            <span class="type-name">{{ item.dictName }}</span>
                            <span class="type-code">{{ item.dictType }}</span>
                        </div>
                        <el-tag size="small" round effect="light" :type="item.status === '0' ? 'success' : 'info'">
                            {{ item.status === '0' ? '正常' : '停用' }}
                        </el-tag>
                    </div>
                    <div class="empty-tip" v-if="filteredTypes.length === 0">暂无数据</div>
                </div>
            </div>

            <!-- 右侧：字典数据列表 -->
            <div class="glass-panel right-panel">
                <div class="panel-header">
                    <div class="title-with-icon">
                        <span class="title" v-if="currentType">{{ currentType.dictName }} - 字典数据</span>
                        <span class="title" v-else>请选择左侧字典分类</span>
                    </div>
                    <div class="actions" v-if="currentType">
                        <el-button type="primary" icon="Plus" round size="small" @click="handleAddData">新增数据</el-button>
                    </div>
                </div>

                <!-- 桌面 sys-table-shell；窄屏关 fixed 后用 table-responsive -->
                <div class="table-wrapper" :class="tableShellClass" v-if="currentType">
                    <el-table :data="currentDictData" class="premium-table" style="width: 100%">
                        <el-table-column prop="dictSort" label="排序" width="70" align="center" />
                        <el-table-column prop="dictLabel" label="字典标签" min-width="160">
                            <template #default="{ row }">
                                <el-tag :type="getTagType(row.listClass)" effect="light" round>
                                    {{ row.dictLabel }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="dictValue" label="字典键值" width="120">
                            <template #default="{ row }">
                                <span class="font-mono">{{ row.dictValue }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="cssClass" label="样式属性" width="120">
                            <template #default="{ row }">
                                <span class="css-class" v-if="row.cssClass">{{ row.cssClass }}</span>
                                <span class="text-placeholder" v-else>-</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="status" label="状态" width="80" align="center">
                            <template #default="{ row }">
                                <div class="status-dot" :class="row.status === '0' ? 'success' : 'fail'">
                                    <span class="dot"></span>
                                    {{ row.status === '0' ? '正常' : '停用' }}
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
                        <el-table-column label="操作" width="140" align="center" :fixed="fixedRight">
                            <template #default="{ row }">
                                <el-button link type="primary" size="small" @click="handleEditData(row)">编辑</el-button>
                                <el-button link type="danger" size="small" @click="handleDeleteData(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 空状态 -->
                <div class="empty-state" v-else>
                    <el-icon :size="64" color="#e2e8f0">
                        <Notebook />
                    </el-icon>
                    <p>请从左侧选择一个字典分类</p>
                </div>
            </div>
        </div>

        <!-- 字典类型编辑对话框 -->
        <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="480px" align-center
            class="sys-dialog-responsive">
            <el-form :model="typeForm" label-position="top">
                <el-form-item label="字典名称" required>
                    <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
                </el-form-item>
                <el-form-item label="字典类型" required>
                    <el-input v-model="typeForm.dictType" placeholder="请输入字典类型，如 sys_xxx_xxx" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="typeForm.status">
                        <el-radio label="0" border>正常</el-radio>
                        <el-radio label="1" border>停用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="typeForm.remark" type="textarea" :rows="2" resize="none" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="sys-dialog-footer">
                    <el-button @click="typeDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveType">保存</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 字典数据编辑对话框 -->
        <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="480px" align-center
            class="sys-dialog-responsive">
            <el-form :model="dataForm" label-position="top">
                <el-form-item label="字典标签" required>
                    <el-input v-model="dataForm.dictLabel" placeholder="请输入标签名称" />
                </el-form-item>
                <el-row :gutter="16">
                    <el-col :span="12" :xs="24">
                        <el-form-item label="字典键值" required>
                            <el-input v-model="dataForm.dictValue" placeholder="如 0, 1, 2..." />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <el-form-item label="显示排序">
                            <el-input-number v-model="dataForm.dictSort" :min="0" :max="999" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="16">
                    <el-col :span="12" :xs="24">
                        <el-form-item label="标签样式">
                            <el-select v-model="dataForm.listClass" placeholder="选择样式">
                                <el-option label="默认(default)" value="" />
                                <el-option label="主要(primary)" value="primary" />
                                <el-option label="成功(success)" value="success" />
                                <el-option label="警告(warning)" value="warning" />
                                <el-option label="危险(danger)" value="danger" />
                                <el-option label="信息(info)" value="info" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <el-form-item label="状态">
                            <el-radio-group v-model="dataForm.status">
                                <el-radio label="0">正常</el-radio>
                                <el-radio label="1">停用</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="备注">
                    <el-input v-model="dataForm.remark" type="textarea" :rows="2" resize="none" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="sys-dialog-footer">
                    <el-button @click="dataDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveData">保存</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Notebook, Plus, Search } from '@element-plus/icons-vue'
import { useTableFixedColumns } from '@/composables/useTableFixedColumns'

const { fixedRight, tableShellClass } = useTableFixedColumns()

const typeSearch = ref('')
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('新增字典类型')
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('新增字典数据')
const currentType = ref<any>(null)

// 字典类型数据
const dictTypes = ref([
    { dictId: 1, dictName: '用户性别', dictType: 'sys_user_sex', status: '0', remark: '用户性别列表' },
    { dictId: 2, dictName: '系统状态', dictType: 'sys_normal_disable', status: '0', remark: '正常/停用状态列表' },
    { dictId: 3, dictName: '操作类型', dictType: 'sys_oper_type', status: '0', remark: '系统操作类型' },
    { dictId: 4, dictName: '登录状态', dictType: 'sys_login_status', status: '0', remark: '登录成功/失败' },
    { dictId: 5, dictName: '指标频次', dictType: 'sm_indicator_freq', status: '0', remark: '日报/月报/季报/年报' },
    { dictId: 6, dictName: '预警级别', dictType: 'sm_alert_level', status: '0', remark: '预警级别定义' },
    { dictId: 7, dictName: '数据源类型', dictType: 'sm_datasource_type', status: '0', remark: 'HIS/PACS/LIS等' },
    { dictId: 8, dictName: '报表类型', dictType: 'sm_report_type', status: '0', remark: '月报/季报/年报/专项' },
])

// 字典数据（Mock，按类型对应）
const allDictData: Record<string, any[]> = {
    'sys_user_sex': [
        { dictSort: 1, dictLabel: '男', dictValue: '0', listClass: 'primary', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '女', dictValue: '1', listClass: 'danger', cssClass: '', status: '0', remark: '' },
        { dictSort: 3, dictLabel: '未知', dictValue: '2', listClass: 'info', cssClass: '', status: '0', remark: '' },
    ],
    'sys_normal_disable': [
        { dictSort: 1, dictLabel: '正常', dictValue: '0', listClass: 'success', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '停用', dictValue: '1', listClass: 'danger', cssClass: '', status: '0', remark: '' },
    ],
    'sys_oper_type': [
        { dictSort: 1, dictLabel: '新增', dictValue: '1', listClass: 'success', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '修改', dictValue: '2', listClass: 'primary', cssClass: '', status: '0', remark: '' },
        { dictSort: 3, dictLabel: '删除', dictValue: '3', listClass: 'danger', cssClass: '', status: '0', remark: '' },
        { dictSort: 4, dictLabel: '授权', dictValue: '4', listClass: 'warning', cssClass: '', status: '0', remark: '' },
        { dictSort: 5, dictLabel: '导出', dictValue: '5', listClass: 'info', cssClass: '', status: '0', remark: '' },
    ],
    'sys_login_status': [
        { dictSort: 1, dictLabel: '成功', dictValue: '0', listClass: 'success', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '失败', dictValue: '1', listClass: 'danger', cssClass: '', status: '0', remark: '' },
    ],
    'sm_indicator_freq': [
        { dictSort: 1, dictLabel: '日报', dictValue: 'daily', listClass: 'primary', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '月报', dictValue: 'monthly', listClass: 'success', cssClass: '', status: '0', remark: '' },
        { dictSort: 3, dictLabel: '季报', dictValue: 'quarterly', listClass: 'warning', cssClass: '', status: '0', remark: '' },
        { dictSort: 4, dictLabel: '年报', dictValue: 'yearly', listClass: 'info', cssClass: '', status: '0', remark: '' },
    ],
    'sm_alert_level': [
        { dictSort: 1, dictLabel: '提示', dictValue: 'info', listClass: 'info', cssClass: '', status: '0', remark: '一般信息提示' },
        { dictSort: 2, dictLabel: '警告', dictValue: 'warning', listClass: 'warning', cssClass: '', status: '0', remark: '需要关注' },
        { dictSort: 3, dictLabel: '严重', dictValue: 'critical', listClass: 'danger', cssClass: '', status: '0', remark: '需要立即处理' },
    ],
    'sm_datasource_type': [
        { dictSort: 1, dictLabel: 'HIS系统', dictValue: 'his', listClass: 'primary', cssClass: '', status: '0', remark: '医院信息系统' },
        { dictSort: 2, dictLabel: 'PACS系统', dictValue: 'pacs', listClass: '', cssClass: '', status: '0', remark: '影像归档系统' },
        { dictSort: 3, dictLabel: 'LIS系统', dictValue: 'lis', listClass: '', cssClass: '', status: '0', remark: '检验信息系统' },
        { dictSort: 4, dictLabel: 'EMR系统', dictValue: 'emr', listClass: 'success', cssClass: '', status: '0', remark: '电子病历系统' },
    ],
    'sm_report_type': [
        { dictSort: 1, dictLabel: '月报', dictValue: 'monthly', listClass: 'primary', cssClass: '', status: '0', remark: '' },
        { dictSort: 2, dictLabel: '季报', dictValue: 'quarterly', listClass: 'warning', cssClass: '', status: '0', remark: '' },
        { dictSort: 3, dictLabel: '年报', dictValue: 'yearly', listClass: 'success', cssClass: '', status: '0', remark: '' },
        { dictSort: 4, dictLabel: '专项', dictValue: 'special', listClass: 'info', cssClass: '', status: '0', remark: '' },
    ],
}

// 筛选字典类型
const filteredTypes = computed(() => {
    if (!typeSearch.value) return dictTypes.value
    return dictTypes.value.filter(t => t.dictName.includes(typeSearch.value) || t.dictType.includes(typeSearch.value))
})

// 当前选中的字典数据
const currentDictData = computed(() => {
    if (!currentType.value) return []
    return allDictData[currentType.value.dictType] || []
})

const selectType = (item: any) => {
    currentType.value = item
}

const getTagType = (cls: string) => {
    if (['primary', 'success', 'warning', 'danger', 'info'].includes(cls)) return cls
    return '' as any
}

// 类型表单
const typeForm = ref({ dictId: 0, dictName: '', dictType: '', status: '0', remark: '' })
const handleAddType = () => {
    typeDialogTitle.value = '新增字典类型'
    typeForm.value = { dictId: 0, dictName: '', dictType: '', status: '0', remark: '' }
    typeDialogVisible.value = true
}
const saveType = () => {
    typeDialogVisible.value = false
    ElMessage.success('保存成功')
}

// 数据表单
const dataForm = ref({ dictLabel: '', dictValue: '', dictSort: 0, listClass: '', cssClass: '', status: '0', remark: '' })
const handleAddData = () => {
    dataDialogTitle.value = '新增字典数据'
    dataForm.value = { dictLabel: '', dictValue: '', dictSort: 0, listClass: '', cssClass: '', status: '0', remark: '' }
    dataDialogVisible.value = true
}
const handleEditData = (row: any) => {
    dataDialogTitle.value = '编辑字典数据'
    dataForm.value = { ...row }
    dataDialogVisible.value = true
}
const handleDeleteData = (row: any) => {
    ElMessageBox.confirm(`确认删除「${row.dictLabel}」吗？`, '提示', { type: 'warning' })
        .then(() => ElMessage.success('删除成功'))
        .catch(() => { })
}
const saveData = () => {
    dataDialogVisible.value = false
    ElMessage.success('保存成功')
}
</script>

<style scoped lang="scss">
.dict-container {
    height: 100%;
    padding-bottom: 20px;
}

.content-layout {
    display: flex;
    gap: 20px;
    height: calc(100vh - 160px);
}

.glass-panel {
    background: #fff;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
        border-color: #cbd5e1;
    }
}

/* 左侧面板 */
.left-panel {
    width: 320px;
    padding: 20px;

    .search-box {
        margin-bottom: 12px;

        :deep(.el-input__wrapper) {
            background: #f8fafc;
            box-shadow: none;
            border: 1px solid transparent;
            border-radius: 10px;

            &:hover,
            &.is-focus {
                background: #fff;
                box-shadow: 0 0 0 1px #0dbda8 inset;
            }
        }
    }

    .type-list {
        flex: 1;
        overflow-y: auto;

        .type-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 14px 12px;
            margin-bottom: 4px;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.2s;

            &:hover {
                background: #f8fafc;
            }

            &.active {
                background: #f0fdfa;
                border-left: 3px solid #0dbda8;

                .type-name {
                    color: #0dbda8;
                    font-weight: 700;
                }
            }

            .type-info {
                display: flex;
                flex-direction: column;
                gap: 4px;

                .type-name {
                    font-size: 14px;
                    font-weight: 600;
                    color: #1e293b;
                }

                .type-code {
                    font-size: 11px;
                    color: #94a3b8;
                    font-family: 'JetBrains Mono', monospace;
                }
            }
        }

        .empty-tip {
            text-align: center;
            color: #94a3b8;
            padding: 40px 0;
            font-size: 14px;
        }
    }
}

/* 右侧面板 */
.right-panel {
    flex: 1;
    padding: 24px;
    min-width: 0;
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
            width: 36px;
            height: 36px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            background: linear-gradient(135deg, #0dbda8, #14d4bc);
            box-shadow: 0 4px 10px rgba(13, 189, 168, 0.3);
        }

        .title {
            font-size: 16px;
            font-weight: 700;
            color: #1e293b;
        }
    }
}

/* 表格 */
.table-wrapper {
    flex: 1;
    min-height: 0;
    min-width: 0;
    overflow-x: visible;
    overflow-y: hidden;
}

.premium-table {
    :deep(th.el-table__cell) {
        background: #f8fafc;
        color: #64748b;
        font-weight: 600;
        font-size: 12px;
        border-bottom: 1px solid #e2e8f0;
    }

    :deep(td.el-table__cell) {
        border-bottom: 1px solid #f1f5f9;
    }

    :deep(tr:hover > td.el-table__cell) {
        background: #f8fafc !important;
    }

    .font-mono {
        font-family: 'JetBrains Mono', monospace;
        color: #6366f1;
        font-weight: 600;
    }

    .css-class {
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
        color: #64748b;
    }

    .text-placeholder {
        color: #cbd5e1;
    }
}

/* 状态标识 */
.status-dot {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;

    .dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
    }

    &.success {
        color: #10b981;

        .dot {
            background: #10b981;
        }
    }

    &.fail {
        color: #ef4444;

        .dot {
            background: #ef4444;
        }
    }
}

/* 空状态 */
.empty-state {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    color: #94a3b8;

    p {
        font-size: 14px;
    }
}

/* 自定义滚动条 */
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

/* 动画 */
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

@media (max-width: 768px) {
    .dict-container .content-layout {
        flex-direction: column;
        height: auto;
        min-height: calc(100vh - 120px);
    }

    .dict-container .left-panel {
        width: 100%;
        max-height: 280px;
        flex-shrink: 0;
    }

    .dict-container .right-panel {
        flex: 1;
        min-height: 320px;
    }

    /* 窄屏关 fixed 后，与 table-responsive 一致 */
    .table-wrapper {
        overflow-x: auto;
        -webkit-overflow-scrolling: touch;
    }
}
</style>
