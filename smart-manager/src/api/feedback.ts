import request from '../utils/request'

/**
 * 获取反馈工单列表（分页）
 */
export function getFeedbackList(params: {
    pageNum?: number,
    pageSize?: number,
    indicatorId?: number,
    status?: number
}) {
    return request({
        url: '/sm/feedback/list',
        method: 'get',
        params
    })
}

/**
 * 提交新的反馈工单
 */
export function createFeedback(data: {
    indicatorId?: number,
    content: string,
    createBy?: string
}) {
    return request({
        url: '/sm/feedback/create',
        method: 'post',
        data
    })
}

/**
 * 处理反馈工单（更新状态、处理人、处理意见）
 */
export function processFeedback(data: {
    id: number,
    status?: number,
    processor?: string,
    resultMsg?: string
}) {
    return request({
        url: '/sm/feedback/process',
        method: 'post',
        data
    })
}
