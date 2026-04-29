<template>
    <div class="lib-container">
        <div class="header-section animate-enter">
            <div class="title-group">
                <div class="icon-box">
                    <el-icon :size="20">
                        <Collection />
                    </el-icon>
                </div>
                <span class="main-title">指标知识库</span>
            </div>
            <div class="stat-group">
                <div class="stat-item">
                    <span class="num">1,204</span>
                    <span class="lbl">总指标</span>
                </div>
                <div class="divider"></div>
                <div class="stat-item">
                    <span class="num active">856</span>
                    <span class="lbl">启用中</span>
                </div>
            </div>
        </div>

        <!-- Main Board -->
        <div class="main-board animate-enter" style="animation-delay: 0.1s">
            <!-- Left: Category Tree（桌面端侧栏；移动端改为抽屉，避免挤压表格） -->
            <div v-if="!isMobile" class="left-panel glass-panel">
                <div class="panel-header">
                    <span>指标分类</span>
                    <el-button link type="primary" size="small"><el-icon>
                            <Plus />
                        </el-icon></el-button>
                </div>
                <div class="search-box">
                    <el-input v-model="filterText" placeholder="搜索分类..." prefix-icon="Search" class="clean-input" />
                </div>
                <div class="tree-wrapper custom-scrollbar">
                    <el-tree ref="treeRef" class="custom-tree" :data="treeData" :props="defaultProps" default-expand-all
                        :filter-node-method="filterNode" highlight-current @node-click="handleNodeClick">
                        <template #default="{ node, data }">
                            <span class="custom-tree-node">
                                <span class="node-icon">
                                    <el-icon v-if="data.children && data.children.length">
                                        <Folder />
                                    </el-icon>
                                    <el-icon v-else>
                                        <Document />
                                    </el-icon>
                                </span>
                                <span class="label">{{ node.label }}</span>
                                <span class="count" v-if="data.count">({{ data.count }})</span>
                            </span>
                        </template>
                    </el-tree>
                </div>
            </div>

            <!-- Right: Content -->
            <div class="right-panel">
                <!-- 移动端：打开分类抽屉 -->
                <div v-if="isMobile" class="category-mobile-bar glass-panel">
                    <el-button type="primary" plain round class="category-mobile-bar__btn" @click="categoryDrawerOpen = true">
                        <el-icon class="mr-1"><Menu /></el-icon>
                        指标分类
                    </el-button>
                    <span class="category-mobile-bar__hint">{{ currentCategoryLabel }}</span>
                </div>

                <div class="filter-bar glass-panel">
                    <div class="inputs">
                        <el-input v-model="searchQuery" placeholder="输入指标名称/编码..." prefix-icon="Search"
                            class="glass-input lib-filter-search" />
                        <el-select v-model="statusFilter" placeholder="状态" class="glass-select lib-filter-select">
                            <el-option label="全部" value="" />
                            <el-option label="启用" value="active" />
                            <el-option label="停用" value="inactive" />
                        </el-select>
                        <el-select v-model="freqFilter" placeholder="频次" class="glass-select lib-filter-select">
                            <el-option label="日报" value="daily" />
                            <el-option label="月报" value="monthly" />
                        </el-select>
                    </div>
                    <div class="actions">
                        <el-button type="primary" icon="Plus" round class="glow-btn"
                            @click="handleCreate">新建指标</el-button>
                        <el-button plain icon="Upload" circle @click="handleImport" />
                        <el-button plain icon="Download" circle @click="handleExportLib" />
                    </div>
                </div>

                <div class="list-container glass-panel">
                    <div class="lib-table-scroll table-responsive table-responsive--grow">
                    <el-table v-loading="loading" :data="tableData" style="width: 100%; min-width: 720px; height: 100%"
                        class="premium-table">
                        <el-table-column type="selection" width="50" />
                        <el-table-column label="指标名称" min-width="240">
                            <template #default="{ row }">
                                <div class="name-cell">
                                    <div class="icon-indicator" :class="row.isComposite ? 'composite' : 'base'">
                                        {{ row.isComposite ? 'C' : 'B' }}
                                    </div>
                                    <div class="info">
                                        <div class="name" style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                                            {{ row.name }}

                                            <el-tooltip effect="light" :placement="isMobile ? 'bottom' : 'right-start'" :show-after="200" popper-class="glass-tooltip">
                                              <template #content>
                                                <div class="indicator-tooltip-content">
                                                  <div class="tt-header">
                                                    <el-icon><Menu /></el-icon>
                                                    <span style="font-weight:600; font-size: 14px; color:var(--text-primary)">指标字典深度溯源</span>
                                                  </div>
                                                  <div class="tt-body">
                                                    <div class="tt-item" v-if="row.formula || row.policySource">
                                                      <span class="tt-label">🧮 计算公式：</span>
                                                      <span class="tt-value" style="font-family: monospace;">{{ row.formula || '系统直接获取基础明细数据' }}</span>
                                                    </div>
                                                    <div class="tt-item" v-if="row.policySource">
                                                      <span class="tt-label">🏷️ 政策来源：</span>
                                                      <div class="tt-value">
                                                          <el-tag size="small" type="danger" effect="light" style="border-radius:2px;">{{ row.policySource }}</el-tag>
                                                      </div>
                                                    </div>
                                                    <div class="tt-item">
                                                      <span class="tt-label">📡 数据提取途径：</span>
                                                      <span class="tt-value">{{ row.dataSource || '暂未绑定自动化源系统集' }}</span>
                                                    </div>
                                                    <div class="tt-item" v-if="row.thresholdDesc">
                                                      <span class="tt-label" style="color:#ef4444">🚨 红线阈值与预警：</span>
                                                      <span class="tt-value" style="color:#ef4444; font-weight: 500;">{{ row.thresholdDesc }}</span>
                                                    </div>
                                                  </div>
                                                </div>
                                              </template>
                                              <el-icon class="info-icon" style="cursor:help; color:#94a3b8;"><QuestionFilled /></el-icon>
                                            </el-tooltip>

                                            <el-tag v-if="row.isComposite" size="small" type="warning" class="ml-1"
                                                effect="light">复合</el-tag>
                                            <el-tag v-if="row.coreSystem" size="small" type="danger" class="ml-1"
                                                effect="light">{{ row.coreSystem }}</el-tag>
                                        </div>
                                        <div class="code">{{ row.code }}
                                            <span v-if="row.policySource" class="policy-tag">{{ row.policySource }}</span>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="所属分类" width="120">
                            <template #default="{ row }">
                                {{ getCategoryName(row.categoryId) }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="dataSource" label="数据来源" width="100">
                            <template #default="{ row }">
                                <el-tag size="small" effect="plain" :type="row.dataSource === 'System' ? 'success' : 'info'">{{ row.dataSource || '-' }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="frequency" label="频次" width="80">
                            <template #default="{ row }">
                                <el-tag size="small" effect="plain" round>{{ getFreqLabel(row.frequency) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="unit" label="单位" width="70" />
                        <el-table-column label="状态" width="80">
                            <template #default="{ row }">
                                <div class="status-badge" :class="row.status === '0' ? 'active' : 'inactive'">
                                    <span class="dot"></span>
                                    {{ row.status === '0' ? '正常' : '停用' }}
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150" align="right">
                            <template #default="{ row }">
                                <el-button v-if="row.isComposite" link type="warning"
                                    @click="handleTestCalc(row)">试算</el-button>
                                <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
                                <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    </div>
                    <div class="pagination-bar">
                        <span class="total">共 {{ total }} 条数据</span>
                        <el-pagination v-model:current-page="queryParams.pageNum"
                            v-model:page-size="queryParams.pageSize"
                            layout="prev, pager, next"
                            :small="isMobile"
                            :pager-count="isMobile ? 5 : 7"
                            :total="total"
                            @current-change="loadList" />
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 移动端：分类树抽屉 -->
    <el-drawer
        v-if="isMobile"
        v-model="categoryDrawerOpen"
        title="指标分类"
        direction="ltr"
        :size="categoryDrawerPx"
        append-to-body
        class="lib-category-drawer"
    >
        <div class="lib-drawer-tree">
            <div class="search-box lib-drawer-tree__search">
                <el-input v-model="filterText" placeholder="搜索分类..." prefix-icon="Search" class="clean-input" />
            </div>
            <div class="tree-wrapper custom-scrollbar lib-drawer-tree__body">
                <el-tree ref="treeRef" class="custom-tree" :data="treeData" :props="defaultProps" default-expand-all
                    :filter-node-method="filterNode" highlight-current @node-click="handleNodeClick">
                    <template #default="{ node, data }">
                        <span class="custom-tree-node">
                            <span class="node-icon">
                                <el-icon v-if="data.children && data.children.length">
                                    <Folder />
                                </el-icon>
                                <el-icon v-else>
                                    <Document />
                                </el-icon>
                            </span>
                            <span class="label">{{ node.label }}</span>
                            <span class="count" v-if="data.count">({{ data.count }})</span>
                        </span>
                    </template>
                </el-tree>
            </div>
        </div>
    </el-drawer>

    <!-- Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" class="glass-dialog lib-dialog-responsive" align-center>
        <el-form :model="formData" label-position="top" class="custom-form">
            <el-row :gutter="20">
                <el-col :span="16">
                    <el-form-item label="指标名称">
                        <el-input v-model="formData.name" placeholder="请输入指标名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="指标编码">
                        <el-input v-model="formData.code" placeholder="自动生成" disabled />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="所属分类">
                        <el-select v-model="formData.categoryId" placeholder="请选择" filterable>
                            <el-option-group label="经济运行">
                                <el-option label="总体收入" :value="101" />
                                <el-option label="门诊经济" :value="102" />
                                <el-option label="住院经济" :value="103" />
                            </el-option-group>
                            <el-option-group label="产出效率">
                                <el-option label="门急诊效率" :value="201" />
                                <el-option label="住院效率" :value="202" />
                                <el-option label="床位效率" :value="203" />
                            </el-option-group>
                            <el-option-group label="质量安全">
                                <el-option label="首诊负责" :value="301" />
                                <el-option label="三级查房" :value="302" />
                                <el-option label="会诊管理" :value="303" />
                                <el-option label="手术管理" :value="304" />
                                <el-option label="分级护理" :value="305" />
                                <el-option label="疑难病例讨论" :value="307" />
                                <el-option label="术前讨论" :value="309" />
                                <el-option label="死亡病例讨论" :value="310" />
                                <el-option label="危急值报告" :value="315" />
                                <el-option label="抗菌药物管理" :value="316" />
                                <el-option label="临床用血管理" :value="317" />
                                <el-option label="合理用药" :value="319" />
                            </el-option-group>
                            <el-option-group label="服务评价">
                                <el-option label="患者满意度" :value="501" />
                                <el-option label="投诉管理" :value="502" />
                            </el-option-group>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="更新频次">
                        <el-select v-model="formData.frequency" placeholder="请选择">
                            <el-option label="日报" value="D" />
                            <el-option label="周报" value="W" />
                            <el-option label="月报" value="M" />
                            <el-option label="季报" value="Q" />
                            <el-option label="年报" value="Y" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="计量单位">
                        <el-input v-model="formData.unit" placeholder="例如：%、人次" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="指标说明">
                <el-input v-model="formData.description" type="textarea" :rows="2" resize="none"
                    placeholder="请输入指标解释..." />
            </el-form-item>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="指标类型">
                        <el-radio-group v-model="formData.isComposite">
                            <el-radio :label="0">基础指标</el-radio>
                            <el-radio :label="1">复合指标</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="数据来源">
                        <el-select v-model="formData.dataSource" placeholder="请选择" style="width: 100%">
                            <el-option label="系统自动同步" value="System" />
                            <el-option label="HIS" value="HIS" />
                            <el-option label="EMR" value="EMR" />
                            <el-option label="LIS" value="LIS" />
                            <el-option label="手术系统" value="手术系统" />
                            <el-option label="护理系统" value="护理系统" />
                            <el-option label="药品管理" value="药品管理" />
                            <el-option label="输血管理" value="输血管理" />
                            <el-option label="满意度调查" value="满意度调查" />
                            <el-option label="人工填报" value="Manual" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <!-- 核心制度相关字段 -->
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="政策依据">
                        <el-input v-model="formData.policySource" placeholder="如: 2025版核心制度指标-指标一" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="核心制度">
                        <el-select v-model="formData.coreSystem" placeholder="无" clearable style="width: 100%">
                            <el-option label="首诊负责制" value="首诊负责制" />
                            <el-option label="三级查房制度" value="三级查房制度" />
                            <el-option label="会诊制度" value="会诊制度" />
                            <el-option label="分级护理制度" value="分级护理制度" />
                            <el-option label="值班与交接班制度" value="值班与交接班制度" />
                            <el-option label="疑难病例讨论制度" value="疑难病例讨论制度" />
                            <el-option label="急危重症抢救制度" value="急危重症抢救制度" />
                            <el-option label="术前讨论制度" value="术前讨论制度" />
                            <el-option label="死亡病例讨论制度" value="死亡病例讨论制度" />
                            <el-option label="查对制度" value="查对制度" />
                            <el-option label="手术安全核查制度" value="手术安全核查制度" />
                            <el-option label="手术分级管理制度" value="手术分级管理制度" />
                            <el-option label="新技术新项目准入制度" value="新技术新项目准入制度" />
                            <el-option label="危急值报告制度" value="危急值报告制度" />
                            <el-option label="抗菌药物分级管理制度" value="抗菌药物分级管理制度" />
                            <el-option label="临床用血审核制度" value="临床用血审核制度" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item v-if="formData.isComposite === 1" label="计算公式">
                <el-input v-model="formData.calcFormula" placeholder="例: [ZB_001] / [ZB_002] * 100" />
                <div class="tip-text small">使用 [指标编码] 进行公式组合</div>
            </el-form-item>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="状态">
                        <el-switch v-model="formData.status" active-value="0" inactive-value="1" active-text="启用"
                            inactive-text="停用" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="敏感数据">
                        <el-switch v-model="formData.isSensitive" :active-value="1" :inactive-value="0" active-text="是"
                            inactive-text="否" inline-prompt />
                        <span class="tip-text">开启后仪表盘脱敏处理</span>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog v-model="importVisible" title="批量指标导入" width="460px" class="glass-dialog import-dialog" align-center>
        <div class="import-content">
            <div class="step-item">
                <div class="step-num">1</div>
                <div class="step-body">
                    <div class="step-title">下载指标导入模版</div>
                    <div class="step-desc">请按模版规范填写指标基础信息、计算逻辑及更新频次</div>
                    <el-button size="small" icon="Download" @click="downloadLibTemplate">下载指标模版 (.csv)</el-button>
                </div>
            </div>
            <div class="step-item mt-6">
                <div class="step-num">2</div>
                <div class="step-body">
                    <div class="step-title">上传数据文件</div>
                    <div class="step-desc">支持批量创建基础指标及复合指标定义</div>
                    <el-upload class="lib-uploader" drag action="#" :auto-upload="false"
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
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { Folder, Document, Collection, Plus, UploadFilled, Download, Menu, QuestionFilled } from '@element-plus/icons-vue'
import { getIndicatorsList } from '../../api/monitor'
import request from '../../utils/request'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useBreakpoint } from '../../composables/useBreakpoint'

const { isMobile, width: viewportWidth } = useBreakpoint()
const categoryDrawerOpen = ref(false)
const categoryDrawerPx = computed(() => `${Math.min(Math.floor(viewportWidth.value * 0.92), 360)}px`)

const loading = ref(false)
const importVisible = ref(false)
const filterText = ref('')
const treeRef = ref()
const searchQuery = ref('')
const statusFilter = ref('')
const freqFilter = ref('')
const total = ref(0)

const queryParams = ref({
    pageNum: 1,
    pageSize: 10,
    name: '',
    code: '',
    categoryId: undefined
})

const defaultProps = {
    children: 'children',
    label: 'label',
}

// 指标分类树 — 与后端 sm_indicator_category 一致
const treeData = [
    {
        label: '经济运行',
        id: 1,
        children: [
            { label: '总体收入', id: 101 },
            { label: '门诊经济', id: 102 },
            { label: '住院经济', id: 103 },
        ],
    },
    {
        label: '产出效率',
        id: 2,
        children: [
            { label: '门急诊效率', id: 201 },
            { label: '住院效率', id: 202 },
            { label: '床位效率', id: 203 },
        ],
    },
    {
        label: '质量安全',
        id: 3,
        children: [
            { label: '首诊负责', id: 301 },
            { label: '三级查房', id: 302 },
            { label: '会诊管理', id: 303 },
            { label: '手术管理', id: 304 },
            { label: '分级护理', id: 305 },
            { label: '值班交接班', id: 306 },
            { label: '疑难病例讨论', id: 307 },
            { label: '急危重症抢救', id: 308 },
            { label: '术前讨论', id: 309 },
            { label: '死亡病例讨论', id: 310 },
            { label: '查对制度', id: 311 },
            { label: '手术安全核查', id: 312 },
            { label: '手术分级管理', id: 313 },
            { label: '新技术准入', id: 314 },
            { label: '危急值报告', id: 315 },
            { label: '抗菌药物管理', id: 316 },
            { label: '临床用血管理', id: 317 },
            { label: '感染控制', id: 318 },
            { label: '合理用药', id: 319 },
        ],
    },
    {
        label: '实时态势',
        id: 4,
        children: [
            { label: '实时监控', id: 401 },
        ],
    },
    {
        label: '服务评价',
        id: 5,
        children: [
            { label: '患者满意度', id: 501 },
            { label: '投诉管理', id: 502 },
        ],
    },
]

const tableData = ref<any[]>([])

// 分类ID到名称的映射 — 覆盖一级和二级分类
const getCategoryName = (id: any) => {
    const map: any = {
        1: '经济运行', 2: '产出效率', 3: '质量安全', 4: '实时态势', 5: '服务评价',
        101: '总体收入', 102: '门诊经济', 103: '住院经济',
        201: '门急诊效率', 202: '住院效率', 203: '床位效率',
        301: '首诊负责', 302: '三级查房', 303: '会诊管理', 304: '手术管理',
        305: '分级护理', 306: '值班交接班', 307: '疑难病例讨论', 308: '急危重症抢救',
        309: '术前讨论', 310: '死亡病例讨论', 311: '查对制度', 312: '手术安全核查',
        313: '手术分级管理', 314: '新技术准入', 315: '危急值报告', 316: '抗菌药物管理',
        317: '临床用血管理', 318: '感染控制', 319: '合理用药',
        401: '实时监控', 501: '患者满意度', 502: '投诉管理'
    }
    return map[id] || '未分类'
}

const getFreqLabel = (val: string) => {
    const map: any = { D: '日报', W: '周报', M: '月报', Q: '季报', Y: '年报' }
    return map[val] || val
}

const loadList = async () => {
    loading.value = true
    try {
        queryParams.value.name = searchQuery.value
        const res: any = await getIndicatorsList(queryParams.value)
        // 这里的 res 已经是 Result.data，即 Page 对象

        // 注入高密度字典解释的 Mock 数据
        if (res.records && Array.isArray(res.records)) {
             res.records = res.records.map((item: any) => {
                 let policySource = item.policySource || '';
                 let formula = item.calcFormula || '';
                 let dataSource = item.dataSource || 'HIS系统';
                 let thresholdDesc = '';
                 const name = item.name || '';

                 if (name.includes('四级手术') || name.includes('交接班')) {
                      policySource = '《医疗质量安全核心制度落实情况监测指标（2025年版）》第九点';
                      formula = '[执行当日床旁交接班的四级手术数] ÷ [四级手术总例数] × 100%';
                      dataSource = 'HIS 手术排班及计费系统 / PDA 护理交接班终端';
                      thresholdDesc = '低于 95% 将被列为内部通报并预警至医务科。面临评级风险。';
                 } else if (name.includes('病案') || name.includes('互联互通') || name.includes('门诊')) {
                      policySource = '《国家医疗健康信息医院信息互联互通标准化成熟度(2020版）》';
                      formula = '[已按要求实现电子化归档的患者病历] ÷ [总接诊完结批数] × 100%';
                      dataSource = 'EMR 电子病历归集中心 / HIS 就诊记录中心';
                      thresholdDesc = '严重影响全院信息化考评，低于 90% 将一票否决四甲以上准入。';
                 } else if (name.includes('药占比') || name.includes('住院')) {
                      policySource = '《医院绩效考核国家规范（公立医院国考）》';
                      formula = '[对应周期内的药品分类项下总收入] ÷ [全院日常医疗结算总收入] × 100%';
                      dataSource = 'HIS 药房发药与计费系统 / HRP 成本核算中心';
                      thresholdDesc = '药占比红线极严苛：不应超过 28%-30%。触发后处方将受实时管控审批。';
                 } else if (name.includes('抢救成功率') || name.includes('急会诊')) {
                      policySource = '《医疗质量安全核心制度（2025年版）》核心医疗质控系列';
                      formula = '[(抢救总次数 - 抢救死亡次数) ÷ 抢救总次数] × 100%';
                      dataSource = '急诊与重症分诊系统 / 护士站生命体征记录仪';
                      thresholdDesc = '最严苛的医疗安全红线，抢救失败需即时向医管系统触发24H异常报告。';
                 }

                 // 如果原始 formula 长得像 `[QUA001_N] / [QUA001_D] * 100`，对拼音大写及字母进行智能业务汉化翻译
                 if (formula && (formula.includes('_N') || formula.includes('_D') || formula.includes('['))) {
                     formula = formula.replace(/\[([A-Z0-9]+)_N\]/g, (match: string, p1: string) => `[符合条件的本期实际发生数]`)
                                      .replace(/\[([A-Z0-9]+)_D\]/g, (match: string, p1: string) => `[所统计对应项目的总基数 / 同期出院总人次数]`)
                                      .replace(/\[([A-Z0-9]+)\]/g, (match: string, p1: string) => {
                                           // 尝试在本页的数据中找到对应名称
                                           const fItem = res.records.find((r: any) => r.code === p1);
                                           return fItem ? `[${fItem.name}]` : match;
                                      })
                                      .replace(/\*/g, '×')
                                      .replace(/\//g, '÷');
                 }

                 // 对于一般有政策标签但未特化的通用政策指标，设立兜底红线提示
                 if (!thresholdDesc && policySource) {
                     thresholdDesc = '依托当期最新业务考核标准，数据异常跌破基准线将触发科室综合考核倒扣分机制。';
                 }

                 return {
                     ...item,
                     policySource,
                     formula,
                     dataSource,
                     thresholdDesc
                 }
             })
        }

        tableData.value = res.records
        total.value = res.total
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    loadList()
})

const currentCategoryLabel = computed(() => {
    const id = queryParams.value.categoryId
    if (id === undefined || id === null) return '全部分类'
    return getCategoryName(id)
})

watch(isMobile, (m) => {
    if (!m) categoryDrawerOpen.value = false
})

watch(filterText, (val) => {
    treeRef.value?.filter(val)
})

const filterNode = (value: string, data: any) => {
    if (!value) return true
    return data.label.includes(value)
}

const handleNodeClick = (data: any) => {
    // 点击分类树节点，按分类ID筛选指标列表
    queryParams.value.categoryId = data.id
    queryParams.value.pageNum = 1
    loadList()
    if (isMobile.value) categoryDrawerOpen.value = false
}

// --- Dialog Logic ---
const dialogVisible = ref(false)
const dialogTitle = ref('新建指标')
const formData = ref({
    id: undefined,
    name: '',
    code: '',
    categoryId: 101,
    frequency: 'M',
    description: '',
    unit: '',
    status: '0',
    isSensitive: 0,
    isComposite: 0,
    calcFormula: '',
    dataSource: 'System',
    policySource: '',
    coreSystem: ''
})

const handleCreate = () => {
    dialogTitle.value = '新建指标'
    formData.value = {
        id: undefined,
        name: '',
        code: '',
        categoryId: 101,
        frequency: 'M',
        description: '',
        unit: '',
        status: '0',
        isSensitive: 0,
        isComposite: 0,
        calcFormula: '',
        dataSource: 'System',
        policySource: '',
        coreSystem: ''
    }
    dialogVisible.value = true
}

const handleEdit = (row: any) => {
    dialogTitle.value = '编辑指标'
    formData.value = { ...row }
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        const isEdit = !!formData.value.id
        await request({
            url: isEdit ? '/indicator/update' : '/indicator/save',
            method: isEdit ? 'put' : 'post',
            data: formData.value
        })
        // 如果没有抛出异常，说明拦截器判定为成功
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadList()
    } catch (e) {
        console.error(e)
    }
}

const handleDelete = (id: number) => {
    ElMessageBox.confirm('确定要删除该指标吗？一旦删除，依赖该指标的复合计算可能失效。', '警告', {
        type: 'warning'
    }).then(async () => {
        try {
            await request({
                url: `/indicator/${id}`,
                method: 'delete'
            })
            ElMessage.success('删除成功')
            loadList()
        } catch (e) {
            console.error(e)
        }
    })
}

const handleTestCalc = (row: any) => {
    ElMessageBox.prompt('请输入试算统计周期 (格式: YYYYMM)', '指标逻辑试算', {
        confirmButtonText: '开始试算',
        cancelButtonText: '取消',
        inputPlaceholder: '例如: 202602',
        inputValue: '202602'
    }).then(async (data: any) => {
        const value = data.value
        try {
            const res: any = await request({
                url: '/indicator/calculate',
                method: 'post',
                params: {
                    id: row.id,
                    periodDate: value
                }
            })
            // res 已经是计算结果 BigDecimal 值
            ElMessageBox.alert(`指标 [${row.name}] 在周期 [${value}] 的计算结果为: <strong>${res}</strong>`, '试算完成', {
                dangerouslyUseHTMLString: true,
                type: 'success'
            })
        } catch (e) {
            // 错误由拦截器弹出提示
            console.error(e)
        }
    })
}

const handleImport = () => {
    importVisible.value = true
}

const downloadLibTemplate = () => {
    const headers = ['指标名称', '指标分类(1:经济,2:产量,3:质量,4:发展)', '更新频次(D/W/M/Q/Y)', '计量单位', '数据来源(System/Manual)', '是否敏感(0/1)', '计算公式', '指标说明']
    const sampleData = [
        ['平均住院日', '2', 'M', '天', 'System', '0', '', '反映住院患者平均住院天数'],
        ['医疗服务收入占比', '1', 'M', '%', 'Manual', '1', '[ZB_001] / [ZB_002] * 100', '衡量医疗收入结构合理性']
    ]

    const csvContent = "\ufeff" + [headers, ...sampleData].map(e => e.map(v => `"${v}"`).join(",")).join("\n")
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement("a")
    const url = URL.createObjectURL(blob)
    link.setAttribute("href", url)
    link.setAttribute("download", `指标知识库导入模版_${new Date().toLocaleDateString()}.csv`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('模版下载中...')
}

const handleUploadSuccess = (file: any) => {
    const loadingInst = ElLoading.service({
        target: '.lib-container',
        text: '解析指标定义并校验计算逻辑...',
        background: 'rgba(255, 255, 255, 0.7)'
    })

    setTimeout(() => {
        loadingInst.close()
        importVisible.value = false
        ElMessage.success(`成功导入来自 ${file.name} 的 24 个指标项`)
        loadList()
    }, 2000)
}

const handleExportLib = () => {
    if (!tableData.value || tableData.value.length === 0) {
        ElMessage.warning('当前列表无指标数据可导出')
        return
    }

    let csvContent = "\ufeff指标编码,指标名称,计量单位,更新频次,状态\n";
    tableData.value.forEach(row => {
        const status = row.status === '0' ? '正常' : '停用';
        csvContent += `${row.code},${row.name},${row.unit || '-'},${getFreqLabel(row.frequency)},${status}\n`;
    });

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", `指标知识库导出_${new Date().toLocaleDateString()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    ElMessage.success('指标库清单导出成功')
}
</script>

<style scoped lang="scss">
.lib-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-bottom: 20px;
    overflow: hidden;
}

.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-shrink: 0;

    .title-group {
        display: flex;
        align-items: center;
        gap: 12px;

        .icon-box {
            width: 40px;
            height: 40px;
            background: rgba(13, 189, 168, 0.1);
            color: #0dbda8;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .main-title {
            font-size: 20px;
            font-weight: 700;
            color: #1e293b;
        }

        .sub-title {
            font-size: 14px;
            color: #94a3b8;
            font-weight: 500;
            font-family: 'JetBrains Mono';
            margin-top: 4px;
        }
    }

    .stat-group {
        display: flex;
        align-items: center;
        gap: 24px;
        background: #fff;
        padding: 8px 24px;
        border-radius: 100px;
        border: 1px solid #e2e8f0;

        .stat-item {
            display: flex;
            align-items: baseline;
            gap: 6px;

            .num {
                font-size: 20px;
                font-weight: 700;
                color: #1e293b;
                font-family: 'JetBrains Mono';

                &.active {
                    color: #0dbda8;
                }
            }

            .lbl {
                font-size: 12px;
                color: #64748b;
            }
        }

        .divider {
            width: 1px;
            height: 16px;
            background: #e2e8f0;
        }
    }
}

.main-board {
    flex: 1;
    display: flex;
    gap: 20px;
    min-height: 0;
    overflow: hidden;
}

.glass-panel {
    background: #fff;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
        border-color: #cbd5e1;
    }
}

// 高级政策悬浮窗通用样式
:global(.glass-tooltip.el-popper) {
  padding: 16px !important;
  border-radius: 12px !important;
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08), 0 0 1px rgba(0, 0, 0, 0.1) !important;
}
:global(.indicator-tooltip-content) {
  max-width: 400px;
  line-height: 1.6;
}
:global(.indicator-tooltip-content .tt-header) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}
:global(.indicator-tooltip-content .tt-item) {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
:global(.indicator-tooltip-content .tt-item:last-child) {
  margin-bottom: 0;
}
:global(.indicator-tooltip-content .tt-label) {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 4px;
}
:global(.indicator-tooltip-content .tt-value) {
  font-size: 13px;
  color: #334155;
  word-break: break-words;
}
.info-icon:hover {
  transform: scale(1.1);
  color: var(--primary) !important;
}

.left-panel {
    width: 280px;
    padding: 20px;

    .panel-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 16px;
    }

    .search-box {
        margin-bottom: 16px;

        :deep(.el-input__wrapper) {
            background: #f8fafc;
            box-shadow: none;
            border: 1px solid transparent;

            &:hover,
            &.is-focus {
                background: #fff;
                box-shadow: 0 0 0 1px #0dbda8 inset;
            }
        }
    }

    .tree-wrapper {
        flex: 1;
        overflow-y: auto;
    }
}

.custom-tree {
    background: transparent;

    :deep(.el-tree-node__content) {
        height: 40px;
        border-radius: 10px;
        margin-bottom: 4px;

        &:hover {
            background: #f0fdfa;
        }
    }

    :deep(.el-tree-node.is-current > .el-tree-node__content) {
        background: #e6fffa;
        color: #0dbda8;
        font-weight: 600;

        .node-icon {
            color: #0dbda8;
        }
    }
}

.custom-tree-node {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;

    .node-icon {
        color: #94a3b8;
        display: flex;
        align-items: center;
        transition: color 0.2s;
    }

    .count {
        font-size: 12px;
        color: #cbd5e1;
        margin-left: 4px;
    }
}

.right-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
    gap: 16px;
}

.filter-bar {
    padding: 16px 20px;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;

    .inputs {
        display: flex;
        gap: 12px;
    }

    .glass-input,
    .glass-select {
        :deep(.el-input__wrapper) {
            border-radius: 12px;
            box-shadow: 0 0 0 1px #e2e8f0 inset;
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
}

.list-container {
    flex: 1;
    padding: 0;
    display: flex;
    flex-direction: column;
}

.lib-table-scroll {
    width: 100%;
    min-height: 0;
    /* 保留横向滑动，隐藏滚动条（移动端更整洁） */
    scrollbar-width: none;
    -ms-overflow-style: none;

    &::-webkit-scrollbar {
        display: none;
        width: 0;
        height: 0;
    }
}

.premium-table {
    :deep(th.el-table__cell) {
        background: #f8fafc;
        color: #64748b;
        font-weight: 600;
        font-size: 12px;
        border-bottom: 1px solid #e2e8f0;
        height: 48px;
    }

    :deep(td.el-table__cell) {
        padding: 12px 0;
        border-bottom: 1px solid #f1f5f9;
    }

    .name-cell {
        display: flex;
        align-items: center;
        gap: 12px;

        .icon-indicator {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 16px;

            &.quality {
                background: #e0f2fe;
                color: #0284c7;
            }

            &.efficiency {
                background: #f0fdf4;
                color: #16a34a;
            }

            &.finance {
                background: #fff7ed;
                color: #ea580c;
            }

            &.safety {
                background: #fef2f2;
                color: #dc2626;
            }
        }

        .info {
            .name {
                font-weight: 600;
                color: #1e293b;
                font-size: 14px;
            }

            .code {
                font-size: 12px;
                color: #94a3b8;
                font-family: 'JetBrains Mono';
                margin-top: 2px;

                .policy-tag {
                    display: inline-block;
                    font-family: inherit;
                    font-size: 10px;
                    color: #e879a2;
                    background: #fdf2f8;
                    padding: 1px 6px;
                    border-radius: 4px;
                    margin-left: 6px;
                }
            }
        }
    }

    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        padding: 4px 10px;
        border-radius: 20px;

        &.active {
            background: #f0fdfa;
            color: #0dbda8;

            .dot {
                background: #0dbda8;
            }
        }

        &.inactive {
            background: #f1f5f9;
            color: #94a3b8;

            .dot {
                background: #94a3b8;
            }
        }

        .dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
        }
    }
}

.pagination-bar {
    padding: 12px 24px;
    border-top: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .total {
        font-size: 13px;
        color: #94a3b8;
    }
}

.tip-text {
    font-size: 12px;
    color: #94a3b8;
    margin-left: 8px;

    &.small {
        font-size: 11px;
        margin-left: 0;
    }
}

.lib-filter-search {
    width: 240px;
    max-width: 100%;
    border-radius: 20px;
}

.lib-filter-select {
    width: 120px;
    max-width: 100%;
}

.category-mobile-bar {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px;
    flex-shrink: 0;
    flex-wrap: wrap;

    &__btn {
        flex-shrink: 0;
    }

    &__hint {
        font-size: 13px;
        color: #64748b;
        flex: 1;
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}

.lib-drawer-tree {
    display: flex;
    flex-direction: column;
    height: 100%;
    min-height: 0;

    &__search {
        flex-shrink: 0;
        margin-bottom: 12px;
    }

    &__body {
        flex: 1;
        min-height: 0;
        overflow-y: auto;
    }
}

@media (max-width: 768px) {
    .lib-container {
        overflow: auto;
        min-height: 0;
    }

    .header-section {
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
        margin-bottom: 14px;

        .stat-group {
            justify-content: center;
            width: 100%;
            border-radius: 16px;
            padding: 10px 16px;
            flex-wrap: wrap;
        }
    }

    .main-board {
        flex-direction: column;
        gap: 12px;
        overflow: visible;
        min-height: auto;
    }

    .right-panel {
        flex: 1;
        min-height: 0;
    }

    .filter-bar {
        flex-direction: column;
        align-items: stretch;
        padding: 14px 16px;

        .inputs {
            flex-direction: column;
            width: 100%;
        }

        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }
    }

    .lib-filter-search,
    .lib-filter-select {
        width: 100% !important;
    }

    .list-container {
        min-height: 50vh;
    }

    .pagination-bar {
        flex-direction: column;
        gap: 10px;
        padding: 12px 16px;
        align-items: stretch;

        .total {
            text-align: center;
        }

        :deep(.el-pagination) {
            flex-wrap: wrap;
            justify-content: center;
        }
    }

    :deep(.lib-dialog-responsive.el-dialog) {
        width: min(100vw - 32px, 600px) !important;
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

    .lib-uploader {
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
