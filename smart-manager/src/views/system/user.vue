<template>
    <div class="user-manage-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header sys-page-header">
                <div class="title-section">
                    <div class="icon-box">
                        <el-icon>
                            <UserFilled />
                        </el-icon>
                    </div>
                    <span class="title">用户管理</span>
                    <span class="count-badge">共 248 人</span>
                </div>
                <div class="action-section">
                    <el-button type="primary" icon="Plus" round class="glow-btn" @click="handleCreate">新增用户</el-button>
                    <el-button icon="Download" circle />
                </div>
            </div>

            <div class="filter-bar sys-toolbar">
                <el-input v-model="searchKey" placeholder="搜索用户名/姓名/手机号..." prefix-icon="Search"
                    class="glass-input w-240" />
                <el-select v-model="deptFilter" placeholder="所属科室" class="glass-select w-160">
                    <el-option label="信息科" value="IT" />
                    <el-option label="医务部" value="Medical" />
                    <el-option label="财务科" value="Finance" />
                </el-select>
                <el-select v-model="statusFilter" placeholder="账号状态" class="glass-select w-120">
                    <el-option label="正常" value="1" />
                    <el-option label="停用" value="0" />
                </el-select>
                <div class="spacer"></div>
                <el-button icon="Refresh" circle text />
            </div>

            <!-- 桌面 fixed + sys-table-shell；窄屏关 fixed 后外层 table-responsive 横向滚动 -->
            <div class="user-table-shell" :class="tableShellClass">
            <el-table :data="tableData" class="premium-table" height="100%" style="width: 100%">
                <el-table-column type="selection" width="48" :fixed="fixedLeft" />
                <el-table-column label="用户" min-width="168" show-overflow-tooltip>
                    <template #default="{ row }">
                        <div class="user-info-cell">
                            <el-avatar class="user-info-cell__avatar" :src="row.avatar" :size="36">{{ row.name.charAt(0) }}</el-avatar>
                            <div class="info">
                                <div class="name">{{ row.name }}</div>
                                <div class="username">@{{ row.username }}</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="dept" label="所属部门" min-width="112">
                    <template #default="{ row }">
                        <el-tag size="small" effect="plain" round>{{ row.dept }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="role" label="角色" min-width="140">
                    <template #default="{ row }">
                        <el-tag v-for="role in row.roles" :key="role" size="small" type="info" class="mr-1">{{ role
                            }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="手机号码" width="120" />
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-switch v-model="row.status" size="small" active-value="1" inactive-value="0"
                            style="--el-switch-on-color: #0dbda8" />
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" width="160" prop="createTime" class-name="mono-font" />
                <el-table-column label="操作" width="180" align="right">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button link type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
                        <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            </div>

            <div class="pagination-bar">
                <el-pagination
                    v-model:current-page="pagination.pageNum"
                    v-model:page-size="pagination.pageSize"
                    class="user-pagination"
                    background
                    :total="pagination.total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper"
                    :hide-on-single-page="false"
                    :pager-count="5"
                />
            </div>
        </div>

        <!-- Edit Dialog -->
        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
            class="glass-dialog sys-dialog-responsive"
            width="500px">
            <el-form label-position="top" :model="form">
                <el-row :gutter="20">
                    <el-col :span="12" :xs="24">
                        <el-form-item label="用户名" required>
                            <el-input v-model="form.username" placeholder="登录账号" class="glass-input" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <el-form-item label="姓名" required>
                            <el-input v-model="form.name" placeholder="真实姓名" class="glass-input" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="所属部门" required>
                    <el-select v-model="form.dept" class="w-100 glass-select">
                        <el-option label="信息科" value="信息科" />
                        <el-option label="医务部" value="医务部" />
                        <el-option label="呼吸内科" value="呼吸内科" />
                        <el-option label="护理部" value="护理部" />
                        <el-option label="财务科" value="财务科" />
                    </el-select>
                </el-form-item>
                <el-form-item label="分配角色">
                    <el-select v-model="form.roles" multiple class="w-100 glass-select">
                        <el-option label="系统管理员" value="Admin" />
                        <el-option label="普通用户" value="User" />
                        <el-option label="数据分析师" value="Analyst" />
                    </el-select>
                </el-form-item>
                <el-form-item label="手机号码">
                    <el-input v-model="form.phone" class="glass-input" />
                </el-form-item>
                <el-form-item label="初始密码" v-if="dialogType === 'add'" required>
                    <el-input v-model="form.password" type="password" show-password class="glass-input" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveUser">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Search, Plus, Download, Refresh } from '@element-plus/icons-vue'
import { useTableFixedColumns } from '@/composables/useTableFixedColumns'

const { fixedLeft, tableShellClass } = useTableFixedColumns()

const searchKey = ref('')
const deptFilter = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const editingId = ref<number | null>(null)

const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 100,
})

type UserForm = {
    username: string
    name: string
    dept: string
    roles: string[]
    phone: string
    password: string
}

const emptyForm = (): UserForm => ({
    username: '',
    name: '',
    dept: '',
    roles: [],
    phone: '',
    password: '',
})

const form = reactive<UserForm>(emptyForm())

/** 表格里中文角色标签 → 表单多选 value */
function mapTableRolesToForm(roles: string[]): string[] {
    const m: Record<string, string> = {
        超级管理员: 'Admin',
        部门主管: 'User',
        普通用户: 'User',
        数据分析师: 'Analyst',
    }
    const set = new Set<string>()
    for (const r of roles) {
        if (m[r]) set.add(m[r])
    }
    return Array.from(set)
}

/** 表单角色 value → 表格展示用中文标签 */
function roleKeysToTableRoles(keys: string[]): string[] {
    const label: Record<string, string> = {
        Admin: '超级管理员',
        User: '普通用户',
        Analyst: '数据分析师',
    }
    return [...new Set(keys.map((k) => label[k] || '普通用户'))]
}

const tableData = ref([
    { id: 1, name: 'Admin', username: 'admin', dept: '信息科', roles: ['超级管理员'], phone: '13800138000', status: '1', createTime: '2024-01-01 10:00:00', avatar: '' },
    { id: 2, name: '张主任', username: 'zhangsan', dept: '医务部', roles: ['部门主管', '数据分析师'], phone: '13912345678', status: '1', createTime: '2024-03-12 14:20:00', avatar: '' },
    { id: 3, name: '李医生', username: 'lisi', dept: '呼吸内科', roles: ['普通用户'], phone: '13788889999', status: '1', createTime: '2024-04-05 09:15:00', avatar: '' },
    { id: 4, name: '王护士', username: 'wangwu', dept: '护理部', roles: ['普通用户'], phone: '13666667777', status: '0', createTime: '2024-05-10 11:30:00', avatar: '' },
])

const handleCreate = () => {
    dialogType.value = 'add'
    editingId.value = null
    Object.assign(form, emptyForm())
    dialogVisible.value = true
}

const handleEdit = (row: (typeof tableData.value)[number]) => {
    dialogType.value = 'edit'
    editingId.value = row.id
    Object.assign(form, {
        ...emptyForm(),
        username: row.username,
        name: row.name,
        dept: row.dept,
        roles: mapTableRolesToForm(row.roles),
        phone: row.phone,
    })
    dialogVisible.value = true
}

const handleSaveUser = () => {
    if (!form.username?.trim() || !form.name?.trim() || !form.dept) {
        ElMessage.warning('请填写必填项')
        return
    }
    if (dialogType.value === 'add' && !form.password?.trim()) {
        ElMessage.warning('请设置初始密码')
        return
    }
    if (dialogType.value === 'edit' && editingId.value != null) {
        const row = tableData.value.find((r) => r.id === editingId.value)
        if (row) {
            row.name = form.name
            row.username = form.username
            row.dept = form.dept
            row.roles = roleKeysToTableRoles(form.roles)
            row.phone = form.phone
        }
        ElMessage.success('已保存修改')
    } else {
        ElMessage.success('已新增用户（演示数据）')
    }
    dialogVisible.value = false
}

const handleResetPwd = (row: (typeof tableData.value)[number]) => {
    ElMessageBox.confirm(`确定将用户「${row.name}」的密码重置为默认密码？`, '重置密码', {
        type: 'warning',
    })
        .then(() => {
            ElMessage.success('已发送重置指令（演示）')
        })
        .catch(() => {})
}

const handleDelete = (row: (typeof tableData.value)[number]) => {
    ElMessageBox.confirm(`确定删除用户「${row.name}」？`, '删除用户', { type: 'warning' })
        .then(() => {
            tableData.value = tableData.value.filter((r) => r.id !== row.id)
            ElMessage.success('已删除')
        })
        .catch(() => {})
}
</script>

<style scoped lang="scss">
.user-manage-container {
    height: 100%;

    .main-panel {
        height: 100%;
        display: flex;
        flex-direction: column;
        background: #fff;
        border-radius: 20px;
        padding: 24px;
        border: 1px solid #e2e8f0;
    }

        .panel-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;

            .title-section {
                display: flex;
                align-items: center;
                gap: 12px;

                .icon-box {
                    width: 40px;
                    height: 40px;
                    background: #f0fdfa;
                    color: #0dbda8;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 20px;
                }

                .title {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1e293b;
                }

                .count-badge {
                    background: #f1f5f9;
                    padding: 2px 8px;
                    border-radius: 12px;
                    font-size: 12px;
                    color: #64748b;
                }
            }
        }

        .filter-bar {
            display: flex;
            gap: 12px;
            margin-bottom: 20px;
            align-items: center;
            flex-wrap: wrap;

            .w-240 {
                width: 240px;
            }

            .w-160 {
                width: 160px;
            }

            .w-120 {
                width: 120px;
            }

            .spacer {
                flex: 1;
            }
        }

    .user-info-cell {
        display: flex;
        align-items: center;
        gap: 12px;
        min-width: 0;
        width: 100%;

        &__avatar {
            flex-shrink: 0;
        }

        .info {
            min-width: 0;
            flex: 1;
            overflow: hidden;

            .name {
                font-weight: 600;
                font-size: 14px;
                color: #1e293b;
                line-height: 1.35;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .username {
                font-size: 12px;
                color: #94a3b8;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }
    }

    .premium-table {
        flex: 1;

        :deep(th.el-table__cell) {
            background: #f8fafc;
            color: #64748b;
            font-weight: 600;
            height: 48px;
            border-bottom: 1px solid #e2e8f0;
        }

        :deep(td.el-table__cell) {
            padding: 12px 0;
        }

        .mono-font .cell {
            font-family: 'JetBrains Mono', monospace;
            color: #64748b;
            font-size: 12px;
        }
    }

    .pagination-bar {
        padding-top: 20px;
        display: flex;
        justify-content: flex-end;
        width: 100%;
        overflow-x: auto;
        overflow-y: hidden;
        -webkit-overflow-scrolling: touch;

        :deep(.el-pagination) {
            display: inline-flex;
            flex-wrap: nowrap;
            align-items: center;
            justify-content: flex-end;
            flex-shrink: 0;
            white-space: nowrap;
            max-width: none;

            .el-pagination__total,
            .el-pagination__sizes,
            .btn-prev,
            .el-pager,
            .btn-next,
            .el-pagination__jump {
                flex-shrink: 0;
            }
        }

        @media (max-width: 768px) {
            justify-content: flex-start;
        }
    }

    // Glass Inputs
    .glass-input,
    .glass-select {
        :deep(.el-input__wrapper) {
            box-shadow: none !important;
            border: 1px solid #e2e8f0;
            background: #f8fafc;
            border-radius: 8px;

            &:hover,
            &.is-focus {
                background: #fff;
                border-color: #0dbda8;
            }
        }
    }

    .glow-btn {
        background: #0dbda8;
        border: none;
        box-shadow: 0 4px 12px rgba(13, 189, 168, 0.3);

        &:hover {
            background: #0fb19e;
            transform: translateY(-1px);
        }
    }

    .animate-enter {
        animation: fadeIn 0.4s ease-out;
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

    .mr-1 {
        margin-right: 4px;
    }

    .w-100 {
        width: 100%;
    }

    .user-table-shell {
        flex: 1;
        min-height: 0;
        min-width: 0;
        width: 100%;
        /* 勿在外层加 overflow-x:auto：会与固定列冲突裁切；横向滚动由表格 body 负责 */
        overflow-x: visible;
        overflow-y: hidden;
    }

    @media (max-width: 768px) {
        .main-panel {
            padding: 16px;
        }

        .panel-header {
            flex-direction: column;
            align-items: stretch;
            gap: 16px;

            .action-section {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                justify-content: flex-end;
            }
        }

        .filter-bar {
            flex-direction: column;
            align-items: stretch;

            .w-240,
            .w-160,
            .w-120 {
                width: 100% !important;
            }

            .spacer {
                display: none;
            }
        }

        /* 窄屏已关 fixed 列，允许外层横向滚动（覆盖上方 overflow-x:visible） */
        .user-table-shell {
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }

        .premium-table {
            min-height: 280px;
        }
    }
}
</style>
