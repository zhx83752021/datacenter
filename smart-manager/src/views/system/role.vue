<template>
    <div class="role-manage-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header">
                <div class="title-section">
                    <div class="icon-box">
                        <el-icon>
                            <Stamp />
                        </el-icon>
                    </div>
                    <span class="title">角色与权限</span>
                    <span class="subtitle">Role & Permission Control</span>
                </div>
                <div class="actions">
                    <el-button type="primary" icon="Plus" round class="glow-btn" @click="handleCreate">新建角色</el-button>
                </div>
            </div>

            <el-table :data="tableData" class="premium-table" height="100%">
                <el-table-column prop="name" label="角色名称" width="200">
                    <template #default="{ row }">
                        <span class="role-name">{{ row.name }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="key" label="权限字符" width="180">
                    <template #default="{ row }">
                        <el-tag type="info" class="mono-font">{{ row.key }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="desc" label="描述" min-width="200" show-overflow-tooltip />
                <el-table-column prop="userCount" label="绑定用户" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag round effect="plain">{{ row.userCount }} 人</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-switch v-model="row.status" size="small" active-value="1" inactive-value="0"
                            style="--el-switch-on-color: #0dbda8" />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" align="right">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button link type="primary" size="small" @click="handlePerms(row)">权限设置</el-button>
                        <el-button link type="danger" size="small">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- Edit/Create Dialog -->
        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新建角色' : '编辑角色'" width="500px"
            class="glass-dialog">
            <el-form label-position="top" :model="form">
                <el-form-item label="角色名称" required>
                    <el-input v-model="form.name" placeholder="如：医疗总监" class="glass-input" />
                </el-form-item>
                <el-form-item label="权限字符" required>
                    <el-input v-model="form.key" placeholder="如：medical_director" class="glass-input" />
                </el-form-item>
                <el-form-item label="角色描述">
                    <el-input v-model="form.desc" type="textarea" :rows="2" class="glass-input" />
                </el-form-item>
                <el-form-item label="显示顺序">
                    <el-input-number v-model="form.order" :min="1" class="glass-input" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="dialogVisible = false">保存</el-button>
            </template>
        </el-dialog>

        <!-- Permission Drawer -->
        <el-drawer v-model="permDrawerVisible" title="权限配置" size="400px" direction="rtl">
            <div class="drawer-content">
                <h4 class="section-title">菜单权限</h4>
                <div class="tree-box custom-scrollbar">
                    <el-tree ref="menuTreeRef" :data="menuData" show-checkbox node-key="id" default-expand-all
                        :props="{ label: 'label' }" />
                </div>

                <h4 class="section-title mt-4">数据权限</h4>
                <el-select v-model="dataScope" class="glass-select w-100 mb-4">
                    <el-option label="全部数据权限" value="1" />
                    <el-option label="自定数据权限" value="2" />
                    <el-option label="本部门及以下数据权限" value="3" />
                    <el-option label="本部门数据权限" value="4" />
                    <el-option label="仅本人数据权限" value="5" />
                </el-select>

                <h4 class="section-title mt-4">附加权限</h4>
                <div class="glass-panel p-3 mb-4">
                    <el-checkbox v-model="form.indicatorSensitive">敏感指标查看权限</el-checkbox>
                    <div class="tip-text small mt-1">开启后可查看标记为“敏感”的财务及核心绩效指标</div>
                </div>

                <div class="drawer-footer">
                    <el-button @click="permDrawerVisible = false">取消</el-button>
                    <el-button type="primary" @click="savePerms">保存配置</el-button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Stamp, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const dialogType = ref('add')
const permDrawerVisible = ref(false)
const dataScope = ref('1')

const form = reactive({
    name: '',
    key: '',
    desc: '',
    order: 1,
    indicatorSensitive: false
})

const tableData = ref([
    { id: 1, name: '超级管理员', key: 'admin', desc: '拥有系统最高权限', userCount: 2, status: '1' },
    { id: 2, name: '科室主任', key: 'director', desc: '负责科室业务管理与数据查看', userCount: 45, status: '1' },
    { id: 3, name: '主治医师', key: 'doctor', desc: '负责临床诊疗业务', userCount: 128, status: '1' },
    { id: 4, name: '护士长', key: 'nurse_head', desc: '负责护理管理', userCount: 32, status: '1' },
    { id: 5, name: '数据分析师', key: 'analyst', desc: '负责运营数据报表分析', userCount: 5, status: '1' },
])

const menuData = [
    { id: 1, label: '首页门户', children: [{ id: 11, label: '工作台' }, { id: 12, label: '分析看板' }] },
    { id: 2, label: '医疗业务', children: [{ id: 21, label: '患者360' }, { id: 22, label: '诊疗辅助' }] },
    { id: 3, label: '运营管理', children: [{ id: 31, label: '全院概况' }, { id: 32, label: '科室运营' }] },
    { id: 4, label: '系统管理', children: [{ id: 41, label: '用户管理' }, { id: 42, label: '角色管理' }] },
]

const handleCreate = () => {
    dialogType.value = 'add'
    form.name = ''
    form.key = ''
    dialogVisible.value = true
}

const handleEdit = (row: any) => {
    dialogType.value = 'edit'
    form.name = row.name
    form.key = row.key
    form.desc = row.desc
    dialogVisible.value = true
}

const handlePerms = (row: any) => {
    permDrawerVisible.value = true
}

const savePerms = () => {
    ElMessage.success('权限配置已保存')
    permDrawerVisible.value = false
}
</script>

<style scoped lang="scss">
.role-manage-container {
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
                background: #eef2ff;
                color: #6366f1;
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

            .subtitle {
                font-size: 12px;
                color: #94a3b8;
                font-family: 'JetBrains Mono';
                margin-top: 4px;
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

        .role-name {
            font-weight: 600;
            color: #1e293b;
        }

        .mono-font {
            font-family: 'JetBrains Mono', monospace;
            font-size: 12px;
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
                border-color: #6366f1;
            }
        }
    }

    .glow-btn {
        background: #6366f1;
        border: none;
        box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);

        &:hover {
            background: #4f46e5;
            transform: translateY(-1px);
        }
    }

    .drawer-content {
        padding: 0 20px;
        height: 100%;
        display: flex;
        flex-direction: column;
    }

    .section-title {
        font-size: 14px;
        font-weight: 700;
        color: #334155;
        margin-bottom: 12px;
    }

    .tree-box {
        flex: 1;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        padding: 12px;
    }

    .drawer-footer {
        margin-top: auto;
        padding: 20px 0;
        display: flex;
        justify-content: flex-end;
        gap: 12px;
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

    .mt-4 {
        margin-top: 24px;
    }

    .mb-4 {
        margin-bottom: 16px;
    }

    .w-100 {
        width: 100%;
    }
    .tip-text {
        font-size: 12px;
        color: #94a3b8;

        &.small {
            font-size: 11px;
        }
    }

    .p-3 {
        padding: 12px;
    }
}
</style>
