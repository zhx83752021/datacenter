<template>
    <div class="menu-manage-container animate-enter">
        <div class="glass-panel main-panel">
            <div class="panel-header sys-page-header">
                <div class="title-section">
                    <div class="icon-box">
                        <el-icon>
                            <Menu />
                        </el-icon>
                    </div>
                    <span class="title">菜单管理</span>
                </div>
                <div class="actions">
                    <el-button type="primary" icon="Plus" round class="glow-btn" @click="handleCreate">新增菜单</el-button>
                </div>
            </div>

            <div class="menu-table-wrap table-responsive table-responsive--grow">
            <el-table :data="tableData" class="premium-table" height="100%" row-key="id" default-expand-all>
                <el-table-column prop="menuName" label="菜单名称" min-width="250">
                    <template #default="{ row }">
                        <div class="menu-label" :class="{ 'is-root': row.parentId === 0 }">
                            <el-icon v-if="row.icon" class="menu-icon">
                                <component :is="row.icon" />
                            </el-icon>
                            <span class="menu-name-text">{{ row.menuName }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="icon" label="图标" width="80" align="center">
                    <template #default="{ row }">
                        <el-icon v-if="row.icon">
                            <component :is="row.icon" />
                        </el-icon>
                    </template>
                </el-table-column>
                <el-table-column prop="orderNum" label="排序" width="80" align="center" />
                <el-table-column prop="path" label="路由地址" width="200" class-name="mono-font" />
                <el-table-column prop="perms" label="权限字符" width="180" class-name="mono-font" />
                <el-table-column prop="menuType" label="类型" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag v-if="row.menuType === 'M'" type="primary">目录</el-tag>
                        <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
                        <el-tag v-else type="info">按钮</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                        <el-switch v-model="row.status" size="small" active-value="1" inactive-value="0"
                            style="--el-switch-on-color: #0dbda8" />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="right">
                    <template #default="{ row }">
                        <el-button link type="primary" size="small" icon="Edit" @click="handleEdit(row)">修改</el-button>
                        <el-button link type="primary" size="small" icon="Plus" @click="handleAddChild(row)">新增</el-button>
                        <el-button link type="danger" size="small" icon="Delete" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            </div>
        </div>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" append-to-body
            class="menu-dialog-responsive sys-dialog-responsive"
            :close-on-click-modal="false">
            <el-form :model="formData" :label-position="formLabelPosition" :label-width="formLabelWidth"
                class="menu-form">
                <el-form-item label="上级菜单">
                    <el-tree-select v-model="formData.parentId" :data="menuTreeOptions"
                        :props="{ label: 'menuName', value: 'id', children: 'children' }" value-key="id"
                        placeholder="选择上级菜单" check-strictly class="menu-form-fullwidth" />
                </el-form-item>
                <el-form-item label="菜单类型">
                    <el-radio-group v-model="formData.menuType" class="menu-form-radio-wrap">
                        <el-radio label="M">目录</el-radio>
                        <el-radio label="C">菜单</el-radio>
                        <el-radio label="F">按钮</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="菜单名称" required>
                    <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12" :xs="24">
                        <el-form-item label="显示顺序">
                            <el-input-number v-model="formData.orderNum" :min="0" class="menu-input-number" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <el-form-item label="菜单图标">
                            <el-input v-model="formData.icon" placeholder="图标名称" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item v-if="formData.menuType !== 'F'" label="路由地址">
                    <el-input v-model="formData.path" placeholder="请输入路由地址" />
                </el-form-item>
                <el-form-item v-if="formData.menuType === 'C'" label="组件路径">
                    <el-input v-model="formData.component" placeholder="请输入组件路径" />
                </el-form-item>
                <el-form-item label="权限字符">
                    <el-input v-model="formData.perms" placeholder="请输入权限字符" />
                </el-form-item>
                <el-form-item label="菜单状态">
                    <el-radio-group v-model="formData.status" size="small" class="menu-form-radio-wrap">
                        <el-radio label="0">正常</el-radio>
                        <el-radio label="1">停用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="menu-dialog-footer sys-dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">保存</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Menu } from '@element-plus/icons-vue'
import { addMenu, updateMenu, delMenu, getMenuTree } from '@/api/system'
import { useBreakpoint } from '@/composables/useBreakpoint'

const { isMobile } = useBreakpoint()

const formLabelPosition = computed(() => (isMobile.value ? 'top' : 'right'))
const formLabelWidth = computed((): string | undefined => (isMobile.value ? undefined : '100px'))

const tableData = ref<any[]>([])
const menuTreeOptions = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({
    id: undefined,
    parentId: 0,
    menuName: '',
    menuType: 'C',
    orderNum: 1,
    path: '',
    component: '',
    perms: '',
    icon: '',
    status: '0'
})

const getList = async () => {
    try {
        // 需要构建树形结构，后端 treelist 直接返回树
        const treeRes: any = await getMenuTree()
        tableData.value = treeRes

        // 用于下拉选择，增加一个“顶级菜单”选项
        menuTreeOptions.value = [{ id: 0, menuName: '顶级菜单', children: treeRes }]
    } catch (e) {
        console.error(e)
    }
}

const handleCreate = () => {
    formData.value = {
        id: undefined,
        parentId: 0,
        menuName: '',
        menuType: 'C',
        orderNum: 1,
        path: '',
        component: '',
        perms: '',
        icon: '',
        status: '0'
    }
    dialogTitle.value = '新增菜单'
    dialogVisible.value = true
}

const handleAddChild = (row: any) => {
    handleCreate()
    formData.value.parentId = row.id
}

const handleEdit = (row: any) => {
    formData.value = { ...row }
    dialogTitle.value = '修改菜单'
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        if (formData.value.id) {
            await updateMenu(formData.value)
            ElMessage.success('修改成功')
        } else {
            await addMenu(formData.value)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList()
    } catch (e) {
        console.error(e)
    }
}

const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除菜单「${row.menuName}」吗？`, '警告', {
        type: 'warning'
    }).then(async () => {
        await delMenu(row.id)
        ElMessage.success('删除成功')
        getList()
    }).catch(() => { })
}

onMounted(() => {
    getList()
})
</script>

<style scoped lang="scss">
.menu-manage-container {
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
                background: #f8fafc;
                color: #475569;
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
        }
    }

    .menu-table-wrap {
        width: 100%;
        min-height: 0;
    }

    .premium-table {
        :deep(th.el-table__cell) {
            background: #f8fafc;
            color: #64748b;
            font-weight: 600;
            height: 48px;
            border-bottom: 1px solid #e2e8f0;
        }

        :deep(td.el-table__cell) {
            padding: 8px 0;
        }

        .menu-label {
            display: flex;
            align-items: center;
            font-weight: 500;
            color: #1e293b;

            .menu-icon {
                margin-right: 8px;
                font-size: 16px;
                color: #64748b;
            }

            &.is-root {
                font-weight: 600;
                font-size: 15px;
                color: #0f172a;
            }
        }

        /* 调整树形箭头位置：放到文字后面 */
        :deep(.el-table__expand-icon) {
            order: 2;
            margin-right: 0;
            margin-left: 8px;
            color: #94a3b8;
            transition: all 0.3s;

            &:hover {
                color: #0dbda8;
            }
        }

        /* 解决基础缩进问题 */
        :deep(.el-table__indent) {
            padding-left: 0 !important;
        }

        :deep(.el-table__placeholder) {
            width: 0;
        }

        /* 自定义二级菜单缩进 */
        :deep(.el-table__row--level-1) {
            .menu-label {
                padding-left: 40px;
            }
        }

        /* 增加更强的缩进感，并对一级菜单进行视觉增强 */
        .is-root {
            .menu-name-text {
                font-weight: 700 !important;
                font-size: 15px;
                color: #0f172a;
            }
        }

        /* 对于三级或更多级增加更多缩进 */
        :deep(.el-table__row--level-2) {
            .menu-label {
                padding-left: 48px;
            }
        }

        :deep(.el-table__cell .cell) {
            display: flex;
            align-items: center;
        }

        .mono-font {
            font-family: 'JetBrains Mono', monospace;
            font-size: 12px;
            color: #64748b;
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

    .mr-2 {
        margin-right: 8px;
    }
}

.menu-dialog-footer {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-end;
    gap: 10px;
    width: 100%;
}

.menu-form {
    :deep(.menu-form-fullwidth) {
        width: 100%;
    }

    :deep(.menu-form-radio-wrap) {
        display: flex;
        flex-wrap: wrap;
        gap: 8px 16px;
        row-gap: 8px;
    }

    :deep(.menu-input-number) {
        width: 100%;

        .el-input__wrapper {
            width: 100%;
        }
    }

    :deep(.el-input),
    :deep(.el-input-number) {
        width: 100%;
        max-width: 100%;
    }

    :deep(.el-tree-select) {
        width: 100%;
    }
}
</style>
