<template>
    <div class="user-manage-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header">
                <div class="title-section">
                    <div class="icon-box">
                        <el-icon>
                            <UserFilled />
                        </el-icon>
                    </div>
                    <span class="title">用户管理</span>
                    <span class="count-badge">248 Users</span>
                </div>
                <div class="action-section">
                    <el-button type="primary" icon="Plus" round class="glow-btn" @click="handleCreate">新增用户</el-button>
                    <el-button icon="Download" circle />
                </div>
            </div>

            <div class="filter-bar">
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

            <el-table :data="tableData" class="premium-table" height="100%">
                <el-table-column type="selection" width="50" />
                <el-table-column label="用户">
                    <template #default="{ row }">
                        <div class="user-info-cell">
                            <el-avatar :src="row.avatar" :size="36">{{ row.name.charAt(0) }}</el-avatar>
                            <div class="info">
                                <div class="name">{{ row.name }}</div>
                                <div class="username">@{{ row.username }}</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="dept" label="所属部门">
                    <template #default="{ row }">
                        <el-tag size="small" effect="plain" round>{{ row.dept }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="role" label="角色">
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
                        <el-button link type="primary" size="small">编辑</el-button>
                        <el-button link type="warning" size="small">重置密码</el-button>
                        <el-button link type="danger" size="small">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-bar">
                <el-pagination background layout="prev, pager, next, total" :total="100" />
            </div>
        </div>

        <!-- Edit Dialog -->
        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增用户' : '编辑用户'" width="500px"
            class="glass-dialog">
            <el-form label-position="top" :model="form">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户名" required>
                            <el-input v-model="form.username" placeholder="登录账号" class="glass-input" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="姓名" required>
                            <el-input v-model="form.name" placeholder="真实姓名" class="glass-input" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="所属部门" required>
                    <el-select v-model="form.dept" class="w-100 glass-select">
                        <el-option label="信息科" value="IT" />
                        <el-option label="医务部" value="Medical" />
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
                <el-button type="primary" @click="dialogVisible = false">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { UserFilled, Search, Plus, Download, Refresh } from '@element-plus/icons-vue'

const searchKey = ref('')
const deptFilter = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')

const form = reactive({ username: '', name: '', dept: '', roles: [], phone: '', password: '' })

const tableData = ref([
    { id: 1, name: 'Admin', username: 'admin', dept: '信息科', roles: ['超级管理员'], phone: '13800138000', status: '1', createTime: '2024-01-01 10:00:00', avatar: '' },
    { id: 2, name: '张主任', username: 'zhangsan', dept: '医务部', roles: ['部门主管', '数据分析师'], phone: '13912345678', status: '1', createTime: '2024-03-12 14:20:00', avatar: '' },
    { id: 3, name: '李医生', username: 'lisi', dept: '呼吸内科', roles: ['普通用户'], phone: '13788889999', status: '1', createTime: '2024-04-05 09:15:00', avatar: '' },
    { id: 4, name: '王护士', username: 'wangwu', dept: '护理部', roles: ['普通用户'], phone: '13666667777', status: '0', createTime: '2024-05-10 11:30:00', avatar: '' },
])

const handleCreate = () => {
    dialogType.value = 'add'
    dialogVisible.value = true
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

        .info {
            .name {
                font-weight: 600;
                font-size: 14px;
                color: #1e293b;
            }

            .username {
                font-size: 12px;
                color: #94a3b8;
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
}
</style>
