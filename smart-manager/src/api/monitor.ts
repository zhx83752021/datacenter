import request from '../utils/request'

/**
 * 获取指标图谱数据
 */
export function getIndicatorGraph() {
    return request({
        url: '/indicator/graph',
        method: 'get'
    })
}

/**
 * 获取指标列表（带分页和搜索）
 */
export function getIndicatorsList(params: any) {
    return request({
        url: '/indicator/list',
        method: 'get',
        params
    })
}
/**
 * 获取看板指标概览数据
 */
export function getDashboardData(params: any) {
    return request({
        url: '/monitor/dashboard',
        method: 'get',
        params
    })
}

/**
 * 获取态势趋势图数据
 */
export function getMonitorTrend(params: any) {
    return request({
        url: '/monitor/trend',
        method: 'get',
        params
    })
}
/**
 * 获取指标深度钻取数据
 */
export function getIndicatorDrillData(id: any) {
    return request({
        url: `/monitor/indicator/${id}/drill`,
        method: 'get'
    })
}

/**
 * 获取指标历史趋势
 */
export function getIndicatorHistoryTrend(params: any) {
    return request({
        url: '/monitor/indicator/history',
        method: 'get',
        params
    })
}

/**
 * 获取指标科室排名
 */
export function getIndicatorDeptRanking(params: any) {
    return request({
        url: '/monitor/indicator/ranking',
        method: 'get',
        params
    })
}

/**
 * 获取指标构成溯源拆解树
 */
export function getIndicatorComposition(data: {
    indicatorCode: string,
    periodDate: string,
    deptCode?: string
}) {
    return request({
        url: '/monitor/composition',
        method: 'post',
        data
    })
}

/**
 * 获取高级指标同环比序列分析
 */
export function getIndicatorYoyAnalysis(data: {
    indicatorCode: string,
    periodDate: string,
    deptCode?: string,
    limitMonths?: number
}) {
    return request({
        url: '/monitor/yoy',
        method: 'post',
        data
    })
}
/**
 * 导出指标知识库 Excel
 */
export function exportIndicatorLib() {
    return request({
        url: '/indicator/export',
        method: 'get',
        responseType: 'blob'
    })
}
