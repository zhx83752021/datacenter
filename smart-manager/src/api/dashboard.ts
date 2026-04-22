import request from '../utils/request'

/**
 * 获取看板列表
 */
export function getDashboardList(params: any) {
    return request({
        url: '/sm/dashboard/list',
        method: 'get',
        params
    })
}

/**
 * 获取看板配置详情
 */
export function getDashboardConfig(id: number | string) {
    return request({
        url: `/sm/dashboard/config/${id}`,
        method: 'get'
    })
}

/**
 * 保存看板配置
 */
export function saveDashboard(data: any) {
    return request({
        url: '/sm/dashboard/save',
        method: 'post',
        data
    })
}

/**
 * 发布/隐藏看板
 */
export function publishDashboard(data: { id: string | number; status: string }) {
    return request({
        url: '/sm/dashboard/publish',
        method: 'put',
        data
    })
}

/**
 * 删除看板
 */
export function deleteDashboard(id: number | string) {
    return request({
        url: `/sm/dashboard/${id}`,
        method: 'delete'
    })
}
