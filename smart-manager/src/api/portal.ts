import request from '../utils/request'

/**
 * 获取门户快速统计数据
 */
export function getQuickStats() {
    return request({
        url: '/portal/stats',
        method: 'get'
    })
}

/**
 * 获取我的看板列表
 */
export function getMyDashboards() {
    return request({
        url: '/portal/dashboards',
        method: 'get'
    })
}

/**
 * 获取预警消息
 */
export function getAlerts() {
    return request({
        url: '/portal/alerts',
        method: 'get'
    })
}
