import router from './router'
import { useUserStore } from './stores/user'
import { ElMessage } from 'element-plus'

const whiteList = ['/login']

/**
 * 根据用户角色返回默认首页路径
 * admin/president/director → /monitor (全院指标监控)
 * user → /reports (数据分析报表)
 */
function getDefaultRoute(roles: string[]): string {
    const normalizedRoles = roles.map(r => r.replace('ROLE_', '').toLowerCase())
    if (normalizedRoles.some(r => ['admin', 'president', 'director'].includes(r))) {
        return '/monitor'
    }
    return '/reports'
}

/**
 * 检查用户是否有访问目标路由的权限
 */
function hasRoutePermission(roles: string[], requiredRoles: string[]): boolean {
    return roles.some((r: string) => {
        return requiredRoles.includes(r) || requiredRoles.includes(r.replace('ROLE_', '').toLowerCase())
    })
}

router.beforeEach(async (to, from, next) => {
    // 判断用户是否已登录
    const hasToken = localStorage.getItem('token')

    if (hasToken) {
        if (to.path === '/login') {
            // 已登录状态下访问登录页，重定向到首页
            next({ path: '/' })
        } else {
            const userStore = useUserStore()
            const hasRoles = userStore.roles && userStore.roles.length > 0

            if (hasRoles) {
                // 处理根路径重定向：根据角色跳转到不同的默认页
                if (to.path === '/') {
                    next({ path: getDefaultRoute(userStore.roles), replace: true })
                    return
                }

                // 检查目标路由是否需要特定角色权限
                if (to.meta && to.meta.roles) {
                    const requiredRoles = to.meta.roles as string[]

                    if (hasRoutePermission(userStore.roles, requiredRoles)) {
                        next()
                    } else {
                        ElMessage.error('权限不足，无法访问该页面')
                        // 重定向到该角色的默认首页，避免死循环
                        next({ path: getDefaultRoute(userStore.roles), replace: true })
                    }
                } else {
                    next() // 无角色要求的路由，所有人可访问
                }
            } else {
                try {
                    // 首次加载，获取用户信息
                    await userStore.getInfo()
                    next({ ...to, replace: true })
                } catch (error) {
                    // token 失效，跳回登录页
                    userStore.resetToken()
                    ElMessage.error(error instanceof Error ? error.message : 'Has Error')
                    next(`/login?redirect=${to.path}`)
                }
            }
        }
    } else {
        /* 未登录状态 */
        if (whiteList.indexOf(to.path) !== -1) {
            // 白名单页面直接放行
            next()
        } else {
            // 重定向到登录页
            next(`/login?redirect=${to.path}`)
        }
    }
})

router.afterEach(() => {
    // typically finish progress bar here
})
