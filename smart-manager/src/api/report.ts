import request from '../utils/request'

/**
 * 获取报表列表
 */
export function getReportList(params: {
    pageNum?: number,
    pageSize?: number,
    name?: string,
    type?: string,
    dept?: string
}) {
    return request({
        url: '/sm/report/list',
        method: 'get',
        params
    })
}

/**
 * 生成新报表
 */
export function generateReport(data: {
    name: string,
    type: string,
    dept?: string,
    status?: string
}) {
    return request({
        url: '/sm/report/generate',
        method: 'post',
        data
    })
}

/**
 * 获取导出路径
 */
export function getExportPath(id: number) {
    return request({
        url: `/sm/report/export/${id}`,
        method: 'get'
    })
}

/**
 * 删除报表
 */
export function deleteReport(id: number) {
    return request({
        url: `/sm/report/${id}`,
        method: 'delete'
    })
}
