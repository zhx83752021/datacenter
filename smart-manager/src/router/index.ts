import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/index.vue'),
        meta: { title: 'Login', hidden: true }
    },
    {
        path: '/',
        component: () => import('../layout/index.vue'),
        // redirect 由 permission.ts 根据角色动态处理，不在此硬编码
        children: [
            // ========== 全院指标监控模块 (admin / president / director) ==========
            {
                path: 'monitor',
                name: 'Monitor',
                component: () => import('../views/monitor/index.vue'),
                meta: { title: '全院指标监控', icon: 'TrendCharts', roles: ['admin', 'president', 'director'] }
            },
            {
                path: 'monitor/analysis/:id',
                name: 'MonitorAnalysis',
                component: () => import('../views/monitor/analysis.vue'),
                meta: { title: '指标深度分析', icon: 'TrendCharts', hidden: true, roles: ['admin', 'president', 'director'] }
            },
            {
                path: 'monitor/lib',
                name: 'IndicatorLib',
                component: () => import('../views/monitor/lib.vue'),
                meta: { title: '指标知识库', icon: 'Collection', roles: ['admin', 'president', 'director'] }
            },
            {
                path: 'monitor/feedback',
                name: 'MonitorFeedback',
                component: () => import('../views/monitor/feedback.vue'),
                meta: { title: '数据反馈管理', icon: 'ChatLineSquare', roles: ['admin', 'president', 'director'] }
            },
            {
                path: 'monitor/target',
                name: 'MonitorTarget',
                component: () => import('../views/monitor/target.vue'),
                meta: { title: '目标与预警', icon: 'Aim', roles: ['admin', 'president', 'director'] }
            },
            {
                path: 'monitor/report',
                name: 'MonitorReport',
                component: () => import('../views/monitor/report.vue'),
                meta: { title: '智能报告分发', icon: 'Share', roles: ['admin', 'president', 'director'] }
            },
            {
                path: '/monitor/graph',
                name: 'MonitorGraph',
                component: () => import('../views/monitor/graph.vue'),
                meta: { title: '指标图谱', icon: 'Share', roles: ['admin', 'president', 'director'] }
            },

            // ========== 运营决策门户 (admin / president / director) ==========
            {
                path: 'portal',
                name: 'Portal',
                component: () => import('../views/portal/index.vue'),
                meta: { title: '运营决策门户', icon: 'DataBoard', roles: ['admin', 'president', 'director'] }
            },

            // ========== 决策驾驶舱 (admin / president) ==========
            {
                path: 'cockpit',
                name: 'Cockpit',
                component: () => import('../views/cockpit/index.vue'),
                meta: { title: '决策驾驶舱', icon: 'Aim', roles: ['admin', 'president'] }
            },

            // ========== 科室驾驶舱 (admin / president / director) ==========
            {
                path: 'cockpit/dept',
                name: 'DeptCockpit',
                component: () => import('../views/cockpit/department.vue'),
                meta: { title: '科室驾驶舱', icon: 'Aim', hidden: true, roles: ['admin', 'president', 'director'] }
            },

            // ========== 数据分析报表 (所有角色可访问) ==========
            {
                path: 'reports',
                name: 'Reports',
                component: () => import('../views/reports/index.vue'),
                meta: { title: '数据分析报表', icon: 'Document' }
            },
            {
                path: '/analysis/theme',
                name: 'AnalysisTheme',
                component: () => import('../views/analysis/theme.vue'),
                meta: { title: '运营主题分析', icon: 'DataAnalysis' }
            },
            {
                path: '/report/advanced',
                name: 'ReportAdvanced',
                component: () => import('../views/reports/advanced.vue'),
                meta: { title: '自助BI设计', icon: 'DataBoard' }
            },

            // ========== 医疗业务 (所有角色可访问) ==========
            {
                path: '/medical/patient360',
                name: 'Patient360',
                component: () => import('../views/medical/patient360.vue'),
                meta: { title: '患者360视图' }
            },
            {
                path: '/medical/ai-diagnosis',
                name: 'AiDiagnosis',
                component: () => import('../views/medical/ai-diagnosis.vue'),
                meta: { title: '智能辅助诊疗' }
            },

            // ========== 看板管理 (admin / president) ==========
            {
                path: '/dashboard/list',
                name: 'DashboardList',
                component: () => import('../views/dashboard/list.vue'),
                meta: { title: '看板管理', icon: 'Monitor', roles: ['admin', 'president'] }
            },

            // ========== 系统管理 (仅 admin) ==========
            {
                path: '/system/user',
                name: 'SystemUser',
                component: () => import('../views/system/user.vue'),
                meta: { title: '用户管理', icon: 'User', roles: ['admin'] }
            },
            {
                path: '/system/role',
                name: 'SystemRole',
                component: () => import('../views/system/role.vue'),
                meta: { title: '角色管理', icon: 'Stamp', roles: ['admin'] }
            },
            {
                path: '/system/menu',
                name: 'SystemMenu',
                component: () => import('../views/system/menu.vue'),
                meta: { title: '菜单管理', icon: 'Menu', roles: ['admin'] }
            },
            {
                path: '/system/log',
                name: 'SystemLog',
                component: () => import('../views/system/log.vue'),
                meta: { title: '日志管理', icon: 'Tickets', roles: ['admin'] }
            },
            {
                path: '/system/config',
                name: 'SystemConfig',
                component: () => import('../views/system/config.vue'),
                meta: { title: '参数配置', icon: 'Setting', roles: ['admin'] }
            },
            {
                path: '/system/dict',
                name: 'SystemDict',
                component: () => import('../views/system/dict.vue'),
                meta: { title: '字典管理', icon: 'Notebook', roles: ['admin'] }
            },

            // ========== 个人中心 (所有角色可访问) ==========
            {
                path: '/system/profile',
                name: 'SystemProfile',
                component: () => import('../views/system/profile.vue'),
                meta: { title: '个人中心', icon: 'User' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
