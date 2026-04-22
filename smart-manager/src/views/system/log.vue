<template>
    <div class="log-container animate-enter">
        <div class="glass-panel main-panel">
            <!-- 头部 -->
            <div class="panel-header">
                <div class="title-with-icon">
                    <div class="icon-box">
                        <el-icon :size="20">
                            <Tickets />
                        </el-icon>
                    </div>
                    <span class="title">日志管理</span>
                </div>
                <div class="actions">
                    <el-button plain icon="Download" round size="small" @click="handleExport">导出日志</el-button>
                </div>
            </div>

            <!-- 标签页切换 -->
            <div class="tab-bar">
                <span class="tab-item" :class="{ active: activeTab === 'operation' }" @click="activeTab = 'operation'">
                    <el-icon>
                        <Edit />
                    </el-icon> 操作审计
                </span>
                <span class="tab-item" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">
                    <el-icon>
                        <Key />
                    </el-icon> 安全登录
                </span>
                <span class="tab-item" :class="{ active: activeTab === 'error' }" @click="activeTab = 'error'">
                    <el-icon>
                        <WarningFilled />
                    </el-icon> 错误追踪
                </span>
            </div>

            <!-- 筛选栏 -->
            <div class="filter-row">
                <el-input v-model="searchKey" placeholder="搜索操作人/模块/内容..." prefix-icon="Search" class="glass-input"
                    style="width: 260px" clearable />
                <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                    end-placeholder="结束日期" class="glass-input" style="width: 260px" :editable="false" />
                <el-select v-model="statusFilter" placeholder="状态" class="glass-select" style="width: 120px" clearable>
                    <el-option label="成功" value="success" />
                    <el-option label="失败" value="fail" />
                </el-select>
            </div>

            <!-- 操作审计表格 -->
            <div class="table-wrapper" v-if="activeTab === 'operation'">
                <el-table :data="operationLogs" class="premium-table" style="width: 100%" height="calc(100vh - 360px)">
                    <el-table-column prop="operTime" label="操作时间" width="170">
                        <template #default="{ row }">
                            <span class="font-mono text-sm text-secondary">{{ row.operTime }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="operName" label="操作人" width="100">
                        <template #default="{ row }">
                            <div class="user-cell">
                                <el-avatar :size="24" class="avatar-bg">{{ row.operName?.charAt(0) }}</el-avatar>
                                <span>{{ row.operName }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="title" label="操作模块" width="140">
                        <template #default="{ row }">
                            <el-tag effect="plain" round size="small">{{ row.title }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="businessType" label="操作类型" width="100">
                        <template #default="{ row }">
                            <span class="type-label" :class="getBusinessTypeClass(row.businessType)">{{
                                getBusinessTypeLabel(row.businessType) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="operUrl" label="请求地址" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="status" label="状态" width="80" align="center">
                        <template #default="{ row }">
                            <div class="status-dot" :class="row.status === 0 ? 'success' : 'fail'">
                                <span class="dot"></span>
                                {{ row.status === 0 ? '成功' : '失败' }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="costTime" label="耗时" width="80" align="right">
                        <template #default="{ row }">
                            <span class="font-mono text-sm">{{ row.costTime }}ms</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="80" align="center">
                        <template #default="{ row }">
                            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 安全登录表格 -->
            <div class="table-wrapper" v-if="activeTab === 'login'">
                <el-table :data="loginLogs" class="premium-table" style="width: 100%" height="calc(100vh - 360px)">
                    <el-table-column prop="loginTime" label="登录时间" width="170">
                        <template #default="{ row }">
                            <span class="font-mono text-sm text-secondary">{{ row.loginTime }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="用户账号" width="120" />
                    <el-table-column prop="ipaddr" label="登录IP" width="140">
                        <template #default="{ row }">
                            <span class="font-mono">{{ row.ipaddr }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="loginLocation" label="登录地点" width="160" />
                    <el-table-column prop="browser" label="浏览器" width="120" />
                    <el-table-column prop="os" label="操作系统" min-width="140" />
                    <el-table-column prop="status" label="状态" width="80" align="center">
                        <template #default="{ row }">
                            <div class="status-dot" :class="row.status === 0 ? 'success' : 'fail'">
                                <span class="dot"></span>
                                {{ row.status === 0 ? '成功' : '失败' }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="msg" label="描述" min-width="160" show-overflow-tooltip />
                </el-table>
            </div>

            <!-- 错误追踪表格 -->
            <div class="table-wrapper" v-if="activeTab === 'error'">
                <el-table :data="errorLogs" class="premium-table" style="width: 100%" height="calc(100vh - 360px)">
                    <el-table-column prop="errorTime" label="发生时间" width="170">
                        <template #default="{ row }">
                            <span class="font-mono text-sm text-secondary">{{ row.errorTime }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="errorType" label="异常类型" width="180">
                        <template #default="{ row }">
                            <el-tag type="danger" effect="plain" size="small" round>{{ row.errorType }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="errorUrl" label="请求路径" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="errorMsg" label="异常信息" min-width="260" show-overflow-tooltip />
                    <el-table-column prop="operName" label="操作人" width="100" />
                    <el-table-column label="操作" width="80" align="center">
                        <template #default="{ row }">
                            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-footer">
                <el-pagination background layout="total, prev, pager, next" :total="50" :page-size="10" />
            </div>
        </div>

        <!-- 详情抽屉 -->
        <el-drawer v-model="detailVisible" title="日志详情" size="480px">
            <div class="detail-content" v-if="currentDetail">
                <div class="detail-item" v-for="(val, key) in currentDetail" :key="key">
                    <span class="label">{{ key }}</span>
                    <span class="value">{{ val }}</span>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, Edit, Key, WarningFilled, Search, Download } from '@element-plus/icons-vue'

const activeTab = ref('operation')
const searchKey = ref('')
const dateRange = ref('')
const statusFilter = ref('')
const detailVisible = ref(false)
const currentDetail = ref<any>(null)

// 操作审计数据
const operationLogs = ref([
    { operTime: '2024-05-20 14:32:10', operName: 'Admin', title: '用户管理', businessType: 1, operUrl: '/api/system/user/save', status: 0, costTime: 45 },
    { operTime: '2024-05-20 14:28:05', operName: '张主任', title: '角色管理', businessType: 2, operUrl: '/api/system/role/authorize', status: 0, costTime: 32 },
    { operTime: '2024-05-20 13:45:22', operName: 'Admin', title: '指标管理', businessType: 5, operUrl: '/api/sm/indicator/export', status: 0, costTime: 1200 },
    { operTime: '2024-05-20 11:20:08', operName: '李科长', title: '数据反馈', businessType: 1, operUrl: '/api/sm/feedback/submit', status: 0, costTime: 58 },
    { operTime: '2024-05-20 10:15:33', operName: 'Admin', title: '系统配置', businessType: 2, operUrl: '/api/system/config', status: 1, costTime: 15 },
    { operTime: '2024-05-19 16:42:19', operName: '王护士', title: '目标预警', businessType: 2, operUrl: '/api/sm/alert/rule/save', status: 0, costTime: 88 },
])

// 安全登录数据
const loginLogs = ref([
    { loginTime: '2024-05-20 08:30:12', userName: 'admin', ipaddr: '192.168.1.100', loginLocation: '内网', browser: 'Chrome 124', os: 'Windows 11', status: 0, msg: '登录成功' },
    { loginTime: '2024-05-20 08:25:40', userName: 'zhangzr', ipaddr: '192.168.1.105', loginLocation: '内网', browser: 'Edge 124', os: 'Windows 10', status: 0, msg: '登录成功' },
    { loginTime: '2024-05-20 07:55:18', userName: 'test', ipaddr: '10.0.0.55', loginLocation: '外网', browser: 'Safari 17', os: 'macOS 14', status: 1, msg: '密码错误' },
    { loginTime: '2024-05-19 18:10:30', userName: 'admin', ipaddr: '192.168.1.100', loginLocation: '内网', browser: 'Chrome 124', os: 'Windows 11', status: 0, msg: '退出成功' },
])

// 错误追踪数据
const errorLogs = ref([
    { errorTime: '2024-05-20 14:30:05', errorType: 'NullPointerException', errorUrl: '/api/sm/monitor/drill', errorMsg: 'Cannot invoke method on null object reference at DrillService.java:125', operName: 'Admin' },
    { errorTime: '2024-05-19 11:22:45', errorType: 'SQLException', errorUrl: '/api/sm/indicator/value/import', errorMsg: 'Duplicate entry for key PRIMARY at DataImportService.java:88', operName: '李科长' },
    { errorTime: '2024-05-18 09:15:30', errorType: 'TimeoutException', errorUrl: '/api/sm/report/generate', errorMsg: 'Request timeout after 30000ms at ReportGenerator.java:256', operName: 'Admin' },
])

// 操作类型映射
const getBusinessTypeLabel = (type: number) => {
    const map: Record<number, string> = { 0: '其他', 1: '新增', 2: '修改', 3: '删除', 4: '授权', 5: '导出' }
    return map[type] || '其他'
}
const getBusinessTypeClass = (type: number) => {
    const map: Record<number, string> = { 1: 'type-add', 2: 'type-edit', 3: 'type-delete', 4: 'type-auth', 5: 'type-export' }
    return map[type] || ''
}

const showDetail = (row: any) => {
    currentDetail.value = row
    detailVisible.value = true
}

const handleExport = () => {
    ElMessage.success('正在导出日志数据...')
}
</script>

<style scoped lang="scss">
.log-container {
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
            background: linear-gradient(135deg, #3b82f6, #60a5fa);
            box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3);
        }

        .title {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-primary);
        }
    }
}

/* 标签页切换 */
.tab-bar {
    display: flex;
    gap: 4px;
    background: #f1f5f9;
    padding: 4px;
    border-radius: 10px;
    margin-bottom: 16px;
    width: fit-content;

    .tab-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 20px;
        border-radius: 8px;
        font-size: 13px;
        font-weight: 500;
        color: #64748b;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
            color: #1e293b;
        }

        &.active {
            background: #fff;
            color: var(--primary-color);
            font-weight: 600;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
        }
    }
}

/* 筛选栏 */
.filter-row {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    align-items: center;

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

/* 表格 */
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
}

/* 用户头像 */
.user-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .avatar-bg {
        background: var(--primary-color);
        font-size: 11px;
    }
}

/* 状态标识 */
.status-dot {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 500;

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

/* 操作类型标签 */
.type-label {
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: 500;

    &.type-add {
        background: #ecfdf5;
        color: #10b981;
    }

    &.type-edit {
        background: #eff6ff;
        color: #3b82f6;
    }

    &.type-delete {
        background: #fef2f2;
        color: #ef4444;
    }

    &.type-auth {
        background: #fefce8;
        color: #eab308;
    }

    &.type-export {
        background: #f5f3ff;
        color: #8b5cf6;
    }
}

/* 工具类 */
.font-mono {
    font-family: 'JetBrains Mono', monospace;
}

.text-sm {
    font-size: 12px;
}

.text-secondary {
    color: #94a3b8;
}

/* 分页 */
.pagination-footer {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}

/* 详情抽屉 */
.detail-content {
    .detail-item {
        display: flex;
        padding: 12px 0;
        border-bottom: 1px solid #f1f5f9;

        .label {
            width: 100px;
            color: #64748b;
            font-size: 13px;
            flex-shrink: 0;
        }

        .value {
            flex: 1;
            font-size: 13px;
            color: #1e293b;
            word-break: break-all;
        }
    }
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
</style>
