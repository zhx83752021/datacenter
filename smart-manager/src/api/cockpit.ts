import request from '../utils/request'

/**
 * 获取科室运营统计卡片
 */
export function getDeptMetrics(deptId: string) {
    return request({
        url: `/cockpit/dept/metrics/${deptId}`,
        method: 'get'
    })
}

/**
 * 获取科室服务量趋势
 */
export function getDeptTrend(deptId: string, type: string) {
    return request({
        url: `/cockpit/dept/trend/${deptId}`,
        method: 'get',
        params: { type }
    })
}

/**
 * 获取科室医师绩效
 */
export function getDoctorPerformance(deptId: string) {
    return request({
        url: `/cockpit/dept/doctors/${deptId}`,
        method: 'get'
    })
}

/**
 * 获取全院科室排名对比
 */
export function getDeptRankings(metric: string) {
    return request({
        url: '/cockpit/dept/rankings',
        method: 'get',
        params: { metric }
    })
}

/**
 * 获取院级KPI指标
 */
export function getPresidentKPIs() {
    return request({
        url: '/cockpit/president/kpis',
        method: 'get'
    })
}

/**
 * 获取全院收支趋势
 */
export function getPresidentTrend(type: string) {
    return request({
        url: '/cockpit/president/trend',
        method: 'get',
        params: { type }
    })
}

/**
 * 获取收入构成数据
 */
export function getIncomeComposition() {
    return request({
        url: '/cockpit/president/income-composition',
        method: 'get'
    })
}

/**
 * 获取门诊人次排名
 */
export function getOutpatientRankings() {
    return request({
        url: '/cockpit/president/rankings',
        method: 'get'
    })
}

/**
 * 获取关键效率指标
 */
export function getEfficiencyStats() {
    return request({
        url: '/cockpit/president/efficiency',
        method: 'get'
    })
}
