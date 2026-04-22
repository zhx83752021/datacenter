import request from '../utils/request'

/**
 * 获取手术效率分析数据
 */
export function getSurgicalEfficiency() {
    return request({
        url: '/sm/analysis/surgical-efficiency',
        method: 'get'
    })
}

/**
 * 获取医疗收入分析数据
 */
export function getIncomeAnalysis() {
    return request({
        url: '/sm/analysis/income-analysis',
        method: 'get'
    })
}

/**
 * 获取医疗质量监控数据
 */
export function getQualityMonitor() {
    return request({
        url: '/sm/analysis/quality-monitor',
        method: 'get'
    })
}
