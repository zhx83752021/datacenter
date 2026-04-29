<template>
    <div class="dashboard-list-container animate-enter">
        <!-- 页面头部 -->
        <div class="header-section">
            <div class="title-group">
                <div class="icon-box"><el-icon>
                        <Monitor />
                    </el-icon></div>
                <span class="title">看板管理</span>
            </div>
            <el-button type="primary" icon="Plus" round class="glow-btn" @click="openDialog()">新建看板</el-button>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-row">
            <div class="stat-card" v-for="stat in typeStats" :key="stat.type"
                :class="{ active: groupFilter === stat.type }" @click="groupFilter = stat.type">
                <div class="stat-icon" :style="{ background: stat.color }">
                    <el-icon :size="18" color="#fff">
                        <component :is="stat.icon" />
                    </el-icon>
                </div>
                <div class="stat-info">
                    <div class="stat-count">{{ stat.count }}</div>
                    <div class="stat-label">{{ stat.label }}</div>
                </div>
            </div>
        </div>

        <!-- 筛选栏 -->
        <div class="filter-bar glass-panel">
            <div class="left">
                <span class="label">类型：</span>
                <el-radio-group v-model="groupFilter" size="small" class="glass-radio">
                    <el-radio-button label="all">全部</el-radio-button>
                    <el-radio-button label="cockpit">决策驾驶舱</el-radio-button>
                    <el-radio-button label="theme">运营主题分析</el-radio-button>
                    <el-radio-button label="monitor">监测仪表盘</el-radio-button>
                    <el-radio-button label="report">数据分析报表</el-radio-button>
                </el-radio-group>
            </div>
            <div class="right">
                <el-input v-model="searchKey" placeholder="搜索看板名称..." :prefix-icon="Search" class="glass-input" />
            </div>
        </div>

        <!-- 看板卡片网格（随内容增高，整页滚动，避免区域内嵌套滚动条） -->
        <div class="dashboard-grid">            <!-- 添加卡片 -->
            <div class="dash-card add-card" @click="openDialog()">
                <div class="icon-wrap"><el-icon>
                        <Plus />
                    </el-icon></div>
                <span>新建看板</span>
            </div>

            <!-- 看板列表卡片 -->
            <div class="dash-card" v-for="item in screenList" :key="item.id">
                <div class="thumb">
                    <div class="mock-ui" :class="getThemeClass(item.category)">
                        <div class="m-header"></div>
                        <div class="m-body">
                            <div class="m-chart"></div>
                            <div class="m-chart"></div>
                            <div class="m-chart full"></div>
                        </div>
                    </div>
                    <div class="overlay">
                        <el-button type="primary" round size="small" @click="handlePreview(item)">预览</el-button>
                        <el-button round size="small" @click="openDialog(item)">编辑</el-button>
                    </div>
                    <!-- 状态标签 -->
                    <div class="status-tag" :class="item.status">
                        {{ item.status === 'online' ? '已上线' : (item.status === 'offline' ? '已下线' : '草稿') }}
                    </div>
                    <!-- 类型标签 -->
                    <div class="type-tag">{{ getCategoryLabel(item.category) }}</div>
                </div>
                <div class="info">
                    <div class="name" @click="handlePreview(item)">{{ item.name }}</div>
                    <div class="meta">
                        <span class="meta-item">
                            <el-icon>
                                <User />
                            </el-icon> {{ item.publishBy || item.createBy || '系统' }}
                        </span>
                        <span class="meta-item">
                            <el-icon>
                                <Clock />
                            </el-icon> {{ formatDate(item.updateTime) }}
                        </span>
                    </div>
                    <div class="actions">
                        <!-- 上线/下线按钮 -->
                        <el-tooltip :content="item.status === 'online' ? '下线' : '上线'">
                            <el-switch v-model="item.status" active-value="online" inactive-value="offline"
                                active-color="#10b981" inactive-color="#94a3b8" size="small"
                                @change="handleToggleStatus(item)" />
                        </el-tooltip>
                        <el-tooltip content="编辑">
                            <el-icon @click="openDialog(item)">
                                <Edit />
                            </el-icon>
                        </el-tooltip>
                        <el-tooltip content="删除">
                            <el-icon class="del" @click="handleDelete(item.id)">
                                <Delete />
                            </el-icon>
                        </el-tooltip>
                    </div>
                </div>
            </div>
        </div>

        <!-- 新建/编辑看板弹窗 -->
        <el-dialog v-model="dialogVisible" :title="editingItem ? '编辑看板' : '新建看板'" width="620px" destroy-on-close>
            <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" label-position="top">
                <el-form-item label="看板名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入看板名称（25字以内）" maxlength="25" show-word-limit />
                </el-form-item>
                <el-row :gutter="16">
                    <el-col :span="12" :xs="24">
                        <el-form-item label="看板类型" prop="category">
                            <el-select v-model="formData.category" placeholder="请选择看板类型" style="width: 100%">
                                <el-option label="决策驾驶舱" value="cockpit" />
                                <el-option label="运营主题分析" value="theme" />
                                <el-option label="监测仪表盘" value="monitor" />
                                <el-option label="数据分析报表" value="report" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <el-form-item label="指标主题">
                            <el-tree-select v-model="formData.themeId" :data="themeOptions" placeholder="请选择指标主题"
                                check-strictly style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="发布对象">
                    <el-row :gutter="8" style="width:100%">
                        <el-col :span="8" :xs="24">
                            <el-select v-model="formData.publishType" placeholder="对象类型" style="width: 100%">
                                <el-option label="系统角色" value="role" />
                                <el-option label="科室" value="dept" />
                                <el-option label="指定用户" value="user" />
                            </el-select>
                        </el-col>
                        <el-col :span="16" :xs="24">
                            <el-input v-model="formData.publishTarget" placeholder="角色/科室/用户名，多个用逗号分隔" />
                        </el-col>
                    </el-row>
                </el-form-item>
                <el-form-item label="看板地址" prop="url">
                    <div class="form-url-row">
                        <el-input v-model="formData.url" placeholder="输入看板页面链接（如：/cockpit 或外部URL）" class="form-url-input" />
                        <el-button type="primary" plain @click="handlePreviewUrl"
                            :disabled="!formData.url">预览</el-button>
                    </div>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="简要描述看板用途" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
                <el-button type="success" @click="handleSaveAndPublish" v-if="!editingItem">保存并发布</el-button>
            </template>
        </el-dialog>

        <!-- 预览弹窗 -->
        <el-dialog v-model="previewVisible" title="看板预览" width="95%" top="2vh" destroy-on-close class="preview-dialog">
            <div class="preview-frame-wrapper">
                <iframe :src="previewUrl" class="preview-iframe" frameborder="0" allowfullscreen></iframe>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    Monitor, Plus, Search, User, Clock, Edit, Delete, View,
    DataAnalysis, TrendCharts, Odometer, Document
} from '@element-plus/icons-vue'
import { getDashboardList, saveDashboard, publishDashboard, deleteDashboard } from '../../api/dashboard'

// 筛选
const groupFilter = ref('all')
const searchKey = ref('')
const loading = ref(false)
const screenList = ref<any[]>([])

// 弹窗
const dialogVisible = ref(false)
const editingItem = ref<any>(null)
const formRef = ref<any>(null)
const previewVisible = ref(false)
const previewUrl = ref('')

// 表单数据
const formData = ref({
    name: '',
    category: '',
    themeId: null as number | null,
    publishType: 'role',
    publishTarget: '',
    url: '',
    description: ''
})

// 表单校验
const formRules = {
    name: [{ required: true, message: '请输入看板名称', trigger: 'blur' }],
    category: [{ required: true, message: '请选择看板类型', trigger: 'change' }],
    url: [{ required: true, message: '请输入看板地址', trigger: 'blur' }]
}

// 指标主题选项（从分类树获取）
const themeOptions = ref([
    {
        value: 1, label: '医疗质量', children: [
            { value: 11, label: '住院指标' },
            { value: 12, label: '手术指标' },
            { value: 13, label: '院感指标' }
        ]
    },
    {
        value: 2, label: '运营效率', children: [
            { value: 21, label: '门诊效率' },
            { value: 22, label: '床位周转' },
            { value: 23, label: '资源利用' }
        ]
    },
    {
        value: 3, label: '财务收益', children: [
            { value: 31, label: '收入结构' },
            { value: 32, label: '成本控制' }
        ]
    },
    { value: 4, label: '满意度', children: [] }
])

// 类型映射
const categoryMap: Record<string, string> = {
    cockpit: '决策驾驶舱',
    theme: '运营主题分析',
    monitor: '监测仪表盘',
    report: '数据分析报表'
}

const getCategoryLabel = (category: string) => categoryMap[category] || category || '未分类'

// 类型配色映射
const getThemeClass = (category: string) => {
    const map: Record<string, string> = {
        cockpit: 'dark-blue',
        theme: 'purple',
        monitor: 'teal',
        report: 'light'
    }
    return map[category] || 'light'
}

// 全量数据缓存用于统计
const allScreens = ref<any[]>([])

// 统计数据
const typeStats = computed(() => {
    const all = allScreens.value
    return [
        { type: 'all', label: '全部看板', count: all.length, color: '#3b82f6', icon: Monitor },
        { type: 'cockpit', label: '决策驾驶舱', count: all.filter((i: any) => i.category === 'cockpit').length, color: '#0dbda8', icon: DataAnalysis },
        { type: 'theme', label: '运营主题', count: all.filter((i: any) => i.category === 'theme').length, color: '#8b5cf6', icon: TrendCharts },
        { type: 'monitor', label: '监测仪表盘', count: all.filter((i: any) => i.category === 'monitor').length, color: '#f59e0b', icon: Odometer },
        { type: 'report', label: '分析报表', count: all.filter((i: any) => i.category === 'report').length, color: '#ef4444', icon: Document },
    ]
})

// 格式化日期
const formatDate = (date: string | Date) => {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}


// 获取看板列表
const fetchList = async () => {
    loading.value = true
    try {
        const res: any = await getDashboardList({
            name: '', // 获取全部以支持前端搜索和统计回显
            pageSize: 1000
        })

        let rawData = []
        if (res && res.records) {
            rawData = res.records
        } else if (res && Array.isArray(res)) {
            rawData = res
        } else {
            // 无数据时展示 mock 数据
            rawData = [
                { id: 1, name: '院长驾驶舱', category: 'cockpit', status: 'online', url: '/cockpit', publishBy: 'admin', updateTime: '2026-03-01', createBy: 'admin' },
                { id: 2, name: '全景运营监控', category: 'monitor', status: 'online', url: '/monitor', publishBy: 'admin', updateTime: '2026-03-01', createBy: 'admin' },
                { id: 3, name: '运营效率分析', category: 'theme', status: 'online', url: '/analysis/theme', publishBy: 'admin', updateTime: '2026-02-28', createBy: 'admin' },
                { id: 4, name: '月度运营报表', category: 'report', status: 'draft', url: '/reports', publishBy: '', updateTime: '2026-02-25', createBy: 'admin' },
                { id: 5, name: '科室驾驶舱', category: 'cockpit', status: 'online', url: '/cockpit/dept', publishBy: 'admin', updateTime: '2026-02-20', createBy: 'admin' },
                { id: 6, name: '重点效率监控', category: 'monitor', status: 'offline', url: '/analysis/theme', publishBy: 'admin', updateTime: '2026-02-15', createBy: 'admin' },
            ]
        }

        allScreens.value = rawData

        // 前端过滤逻辑
        applyFilter()
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

// 执行过滤
const applyFilter = () => {
    screenList.value = allScreens.value.filter(item => {
        const matchName = !searchKey.value || item.name.toLowerCase().includes(searchKey.value.toLowerCase())
        const matchCategory = groupFilter.value === 'all' || item.category === groupFilter.value
        return matchName && matchCategory
    })
}

onMounted(() => { fetchList() })
watch([groupFilter, searchKey], () => { applyFilter() })

// 打开新建/编辑弹窗
const openDialog = (item?: any) => {
    if (item) {
        editingItem.value = item
        formData.value = {
            name: item.name,
            category: item.category,
            themeId: item.themeId || null,
            publishType: item.publishType || 'role',
            publishTarget: item.publishTarget || '',
            url: item.url || '',
            description: item.description || ''
        }
    } else {
        editingItem.value = null
        formData.value = { name: '', category: '', themeId: null, publishType: 'role', publishTarget: '', url: '', description: '' }
    }
    dialogVisible.value = true
}

// 保存看板
const handleSave = async () => {
    const form = formRef.value
    if (!form) return
    await form.validate()
    try {
        const payload: any = { ...formData.value, status: 'draft' }
        if (editingItem.value) {
            payload.id = editingItem.value.id
            payload.status = editingItem.value.status
        }
        await saveDashboard(payload)
        ElMessage.success(editingItem.value ? '看板已更新' : '看板已保存')
        dialogVisible.value = false
        fetchList()
    } catch (e) {
        ElMessage.error('保存失败')
    }
}

// 保存并发布
const handleSaveAndPublish = async () => {
    const form = formRef.value
    if (!form) return
    await form.validate()
    try {
        const payload: any = { ...formData.value, status: 'online', publishBy: 'admin', publishTime: new Date() }
        await saveDashboard(payload)
        ElMessage.success('看板已保存并发布上线')
        dialogVisible.value = false
        fetchList()
    } catch (e) {
        ElMessage.error('发布失败')
    }
}

// 上下线切换
const handleToggleStatus = async (item: any) => {
    try {
        await publishDashboard({ id: item.id, status: item.status })
        ElMessage.success(item.status === 'online' ? '看板已上线' : '看板已下线')
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

// 预览看板（通过已有URL）
const handlePreview = (item: any) => {
    if (item.url) {
        // 如果是站内路径，拼接嵌入模式参数
        const isInternal = !item.url.startsWith('http')
        const url = isInternal
            ? `${window.location.origin}${item.url}${item.url.includes('?') ? '&' : '?'}embedded=true`
            : item.url
        previewUrl.value = url
        previewVisible.value = true
    } else {
        ElMessage.warning('该看板未配置访问地址')
    }
}

// 预览表单中的URL
const handlePreviewUrl = () => {
    if (formData.value.url) {
        const isInternal = !formData.value.url.startsWith('http')
        const url = isInternal
            ? `${window.location.origin}${formData.value.url}${formData.value.url.includes('?') ? '&' : '?'}embedded=true`
            : formData.value.url
        previewUrl.value = url
        previewVisible.value = true
    }
}

// 删除看板
const handleDelete = async (id: number) => {
    await ElMessageBox.confirm('确认删除该看板？删除后不可恢复。', '提示', { type: 'warning' })
    try {
        await deleteDashboard(id)
        ElMessage.success('已删除')
        fetchList()
    } catch (e) {
        ElMessage.error('删除失败')
    }
}
</script>

<style scoped lang="scss">
/* 容器：整页纵向滚动，避免网格区域单独出现滚动条 */
.dashboard-list-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;
    overflow-x: hidden;
    overflow-y: auto;
    min-height: 0;
}

/* 头部 */
.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .title-group {
        display: flex;
        align-items: center;
        gap: 12px;

        .icon-box {
            width: 44px;
            height: 44px;
            background: linear-gradient(135deg, #3b82f6, #6366f1);
            color: #fff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }

        .title {
            font-size: clamp(17px, 2vw + 0.75rem, 20px);
            font-weight: 700;
            color: #1e293b;
        }
    }
}

/* 统计行 */
.stats-row {
    display: flex;
    gap: 16px;
    margin-bottom: 20px;

    .stat-card {
        flex: 1;
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 20px;
        background: #fff;
        border: 1px solid #e2e8f0;
        border-radius: 14px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover,
        &.active {
            border-color: #3b82f6;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
            transform: translateY(-2px);
        }

        &.active {
            background: #eff6ff;
        }

        .stat-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .stat-info {
            .stat-count {
                font-size: 22px;
                font-weight: 800;
                color: #1e293b;
                line-height: 1;
            }

            .stat-label {
                font-size: 12px;
                color: #64748b;
                margin-top: 4px;
            }
        }
    }
}

/* 筛选栏 */
.filter-bar {
    padding: 16px 24px;
    border-radius: 16px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 14px;
        color: #64748b;
    }

    .glass-input {
        width: 240px;
    }
}

/* 卡片网格：高度随内容伸展，不占用 flex:1 内滚动 */
.dashboard-grid {
    flex: 0 0 auto;
    overflow: visible;
    padding: 4px;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;

    .dash-card {
        background: #fff;
        border-radius: 16px;
        border: 1px solid #e2e8f0;
        overflow: hidden;
        transition: all 0.3s;
        position: relative;
        display: flex;
        flex-direction: column;
        height: 282px;
        box-sizing: border-box;

        &:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 24px -4px rgba(0, 0, 0, 0.1);
            border-color: #3b82f6;

            .thumb .overlay {
                opacity: 1;
            }
        }

        /* 新建卡片 */
        &.add-card {
            border: 2px dashed #cbd5e1;
            background: #f8fafc;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            height: 282px;
            color: #94a3b8;

            &:hover {
                border-color: #3b82f6;
                color: #3b82f6;
                background: #eff6ff;
            }

            .icon-wrap {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                background: #e2e8f0;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 12px;
            }
        }

        /* 缩略图区域 */
        .thumb {
            height: 160px;
            background: #f1f5f9;
            position: relative;
            overflow: hidden;

            .mock-ui {
                width: 100%;
                height: 100%;
                padding: 12px;
                display: flex;
                flex-direction: column;
                gap: 8px;

                &.dark-blue {
                    background: #0f172a;

                    .m-header,
                    .m-chart {
                        background: rgba(255, 255, 255, 0.1);
                    }
                }

                &.teal {
                    background: #115e59;

                    .m-header,
                    .m-chart {
                        background: rgba(255, 255, 255, 0.1);
                    }
                }

                &.purple {
                    background: #581c87;

                    .m-header,
                    .m-chart {
                        background: rgba(255, 255, 255, 0.1);
                    }
                }

                &.light {
                    background: #f8fafc;

                    .m-header,
                    .m-chart {
                        background: #cbd5e1;
                    }
                }

                .m-header {
                    height: 12px;
                    width: 40%;
                    border-radius: 4px;
                }

                .m-body {
                    flex: 1;
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 4px;
                }

                .m-chart {
                    border-radius: 4px;

                    &.full {
                        grid-column: span 2;
                    }
                }
            }

            .overlay {
                position: absolute;
                inset: 0;
                background: rgba(0, 0, 0, 0.4);
                backdrop-filter: blur(2px);
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 12px;
                opacity: 0;
                transition: all 0.3s;
            }

            .status-tag {
                position: absolute;
                top: 10px;
                right: 10px;
                padding: 2px 10px;
                border-radius: 6px;
                font-size: 11px;
                font-weight: 600;
                color: #fff;

                &.online {
                    background: #10b981;
                }

                &.offline {
                    background: #94a3b8;
                }

                &.draft {
                    background: #f59e0b;
                }
            }

            .type-tag {
                position: absolute;
                top: 10px;
                left: 10px;
                padding: 2px 8px;
                border-radius: 6px;
                font-size: 10px;
                font-weight: 600;
                color: #fff;
                background: rgba(0, 0, 0, 0.5);
                backdrop-filter: blur(4px);
            }
        }

        /* 信息区域 */
        .info {
            padding: 16px;
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .name {
                font-weight: 600;
                font-size: 15px;
                color: #1e293b;
                margin-bottom: 8px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                cursor: pointer;
                transition: color 0.2s;

                &:hover {
                    color: #3b82f6;
                }
            }

            .meta {
                display: flex;
                justify-content: space-between;
                font-size: 12px;
                color: #94a3b8;
                margin-bottom: 12px;

                .meta-item {
                    display: flex;
                    align-items: center;
                    gap: 4px;
                }
            }

            .actions {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                gap: 14px;
                font-size: 16px;
                color: #64748b;

                .el-icon {
                    cursor: pointer;
                    transition: color 0.2s;

                    &:hover {
                        color: #3b82f6;
                    }

                    &.del:hover {
                        color: #ef4444;
                    }
                }
            }
        }
    }
}

/* 面板 */
.glass-panel {
    background: #fff;
    border: 1px solid #e2e8f0;
}

.glass-input :deep(.el-input__wrapper) {
    box-shadow: none;
    border: 1px solid #e2e8f0;
    background: #f8fafc;

    &:hover,
    &.is-focus {
        background: #fff;
        border-color: #3b82f6;
    }
}

/* 预览弹窗 */
:deep(.preview-dialog) {
    .el-dialog__body {
        padding: 0 !important;
        background: transparent;
    }
}

.preview-frame-wrapper {
    width: 100%;
    height: 85vh;
    overflow: hidden;
    background: #fff;

    .preview-iframe {
        width: 100%;
        height: 100%;
        border: none;
        display: block;
    }
}

/* 动画 */
.animate-enter {
    animation: fadeIn 0.5s ease-out;
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

.glow-btn {
    background: #3b82f6;
    border: none;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);

    &:hover {
        background: #2563eb;
        transform: translateY(-1px);
    }
}

.form-url-row {
    display: flex;
    gap: 8px;
    width: 100%;
    align-items: flex-start;
    flex-wrap: wrap;

    .form-url-input {
        flex: 1;
        min-width: min(100%, 200px);
    }
}

@media (max-width: 768px) {
    .dashboard-list-container {
        padding-bottom: 16px;
        min-height: 0;
    }

    .header-section {
        flex-direction: column;
        align-items: stretch;
        gap: 14px;
        margin-bottom: 16px;

        .title-group .title {
            font-size: clamp(17px, 4.5vw, 20px);
        }

        .glow-btn {
            width: 100%;
        }
    }

    .stats-row {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 10px;
        margin-bottom: 16px;

        .stat-card {
            padding: 12px 14px;
            min-width: 0;

            .stat-info {
                min-width: 0;

                .stat-count {
                    font-size: clamp(18px, 5vw, 22px);
                }

                .stat-label {
                    font-size: 11px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }
            }
        }
    }

    .filter-bar {
        flex-direction: column;
        align-items: stretch;
        gap: 14px;
        padding: 14px 16px;
        margin-bottom: 16px;

        .left {
            flex-direction: column;
            align-items: stretch;
            gap: 10px;
            width: 100%;

            .glass-radio {
                width: 100%;

                :deep(.el-radio-group) {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 8px;
                }
            }
        }

        .glass-input {
            width: 100% !important;
        }
    }

    .dashboard-grid {
        grid-template-columns: minmax(0, 1fr);
        gap: 16px;
    }

    .form-url-row {
        flex-direction: column;
        align-items: stretch;

        .form-url-input {
            width: 100%;
            min-width: 0;
        }
    }

    :deep(.el-dialog:not(.preview-dialog)) {
        width: min(100vw - 32px, 620px) !important;
        margin: 2vh auto !important;
    }

    .preview-frame-wrapper {
        height: min(85vh, 80dvh);
    }
}
</style>
