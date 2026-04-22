<template>
    <div class="config-container animate-enter">
        <div class="glass-panel main-panel">
            <!-- 头部 -->
            <div class="panel-header">
                <div class="title-with-icon">
                    <div class="icon-box">
                        <el-icon :size="20">
                            <Setting />
                        </el-icon>
                    </div>
                    <span class="title">系统参数配置</span>
                </div>
                <div class="actions">
                    <el-button type="primary" icon="Plus" round size="small" @click="handleAdd">新增参数</el-button>
                    <el-button plain icon="Refresh" circle size="small" @click="handleRefreshCache" />
                </div>
            </div>

            <!-- 搜索筛选栏 -->
            <div class="filter-row">
                <el-input v-model="searchKey" placeholder="搜索参数名称/键名..." prefix-icon="Search" class="glass-input"
                    style="width: 280px" clearable />
                <el-select v-model="typeFilter" placeholder="参数类型" class="glass-select" style="width: 140px" clearable>
                    <el-option label="系统内置" value="Y" />
                    <el-option label="自定义" value="N" />
                </el-select>
            </div>

            <!-- 参数列表 -->
            <div class="table-wrapper">
                <el-table :data="filteredData" class="premium-table" style="width: 100%" height="calc(100vh - 320px)">
                    <el-table-column prop="configId" label="参数编号" width="80" align="center" />
                    <el-table-column prop="configName" label="参数名称" min-width="200">
                        <template #default="{ row }">
                            <div class="name-cell">
                                <span class="name-text">{{ row.configName }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="configKey" label="参数键名" min-width="260">
                        <template #default="{ row }">
                            <el-tag effect="plain" size="small" class="key-tag">{{ row.configKey }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="configValue" label="参数键值" width="160">
                        <template #default="{ row }">
                            <span class="font-mono value-text">{{ row.configValue }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="configType" label="类型" width="100" align="center">
                        <template #default="{ row }">
                            <el-tag :type="row.configType === 'Y' ? 'success' : 'info'" effect="light" round
                                size="small">
                                {{ row.configType === 'Y' ? '内置' : '自定义' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
                    <el-table-column label="操作" width="140" align="center" fixed="right">
                        <template #default="{ row }">
                            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                            <el-button link type="danger" size="small" @click="handleDelete(row)"
                                :disabled="row.configType === 'Y'">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-footer">
                <span class="total-text">共 {{ filteredData.length }} 条参数</span>
                <el-pagination background layout="prev, pager, next" :total="filteredData.length" :page-size="20" />
            </div>
        </div>

        <!-- 编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" class="glass-dialog" align-center>
            <el-form :model="formData" label-position="top" class="custom-form">
                <el-form-item label="参数名称" required>
                    <el-input v-model="formData.configName" placeholder="请输入参数名称" />
                </el-form-item>
                <el-form-item label="参数键名" required>
                    <el-input v-model="formData.configKey" placeholder="请输入参数键名，如 sys.xxx.xxx" />
                </el-form-item>
                <el-form-item label="参数键值" required>
                    <el-input v-model="formData.configValue" placeholder="请输入参数键值" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="参数类型">
                            <el-radio-group v-model="formData.configType">
                                <el-radio label="Y" border>系统内置</el-radio>
                                <el-radio label="N" border>自定义</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="备注说明">
                    <el-input v-model="formData.remark" type="textarea" :rows="2" resize="none"
                        placeholder="请输入备注说明..." />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">保存</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Plus, Refresh, Search } from '@element-plus/icons-vue'

const searchKey = ref('')
const typeFilter = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增参数')

const formData = ref({
    configId: 0,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'N',
    remark: ''
})

// 参数列表数据
const tableData = ref([
    { configId: 1, configName: '系统名称', configKey: 'sys.app.name', configValue: '智慧管理平台', configType: 'Y', remark: '平台显示名称' },
    { configId: 2, configName: '用户初始密码', configKey: 'sys.user.initPassword', configValue: '123456', configType: 'Y', remark: '新用户默认密码' },
    { configId: 3, configName: '账号自助注册', configKey: 'sys.account.registerUser', configValue: 'false', configType: 'Y', remark: '是否开放用户注册（true/false）' },
    { configId: 4, configName: '验证码开关', configKey: 'sys.account.captchaEnabled', configValue: 'true', configType: 'Y', remark: '登录是否开启验证码' },
    { configId: 5, configName: '预警检测间隔', configKey: 'sm.alert.checkInterval', configValue: '30', configType: 'N', remark: '预警检测间隔时间（分钟）' },
    { configId: 6, configName: '数据保留天数', configKey: 'sm.data.retentionDays', configValue: '730', configType: 'N', remark: '指标数据保留天数' },
    { configId: 7, configName: '报告生成超时', configKey: 'sm.report.timeout', configValue: '60000', configType: 'N', remark: '报告生成超时毫秒数' },
    { configId: 8, configName: '导出行数限制', configKey: 'sm.export.maxRows', configValue: '50000', configType: 'N', remark: 'Excel导出最大行数限制' },
])

// 筛选
const filteredData = computed(() => {
    return tableData.value.filter(item => {
        const matchKey = !searchKey.value || item.configName.includes(searchKey.value) || item.configKey.includes(searchKey.value)
        const matchType = !typeFilter.value || item.configType === typeFilter.value
        return matchKey && matchType
    })
})

const handleAdd = () => {
    dialogTitle.value = '新增参数'
    formData.value = { configId: 0, configName: '', configKey: '', configValue: '', configType: 'N', remark: '' }
    dialogVisible.value = true
}

const handleEdit = (row: any) => {
    dialogTitle.value = '编辑参数'
    formData.value = { ...row }
    dialogVisible.value = true
}

const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确认删除参数「${row.configName}」吗？`, '提示', { type: 'warning' })
        .then(() => {
            tableData.value = tableData.value.filter(item => item.configId !== row.configId)
            ElMessage.success('删除成功')
        })
        .catch(() => { })
}

const handleSave = () => {
    dialogVisible.value = false
    ElMessage.success('保存成功')
}

const handleRefreshCache = () => {
    ElMessage.success('缓存刷新成功')
}
</script>

<style scoped lang="scss">
.config-container {
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
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
        }

        .title {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-primary);
        }
    }

    .actions {
        display: flex;
        gap: 8px;
    }
}

.filter-row {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;

    .glass-input,
    .glass-select {
        :deep(.el-input__wrapper) {
            box-shadow: none !important;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 10px;

            &:hover,
            &.is-focus {
                border-color: var(--primary-color);
                background: #fff;
            }
        }
    }
}

.table-wrapper {
    flex: 1;
    overflow: hidden;
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

    .name-cell {
        .name-text {
            font-weight: 600;
            color: #1e293b;
        }
    }

    .key-tag {
        font-family: 'JetBrains Mono', monospace;
        font-size: 12px;
    }

    .value-text {
        color: #6366f1;
        font-weight: 600;
    }
}

.font-mono {
    font-family: 'JetBrains Mono', monospace;
}

.pagination-footer {
    margin-top: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .total-text {
        font-size: 13px;
        color: #94a3b8;
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
