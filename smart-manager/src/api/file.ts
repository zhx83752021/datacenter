import request from '../utils/request'

/**
 * 上传文件
 * @param file 文件对象
 * @param module 所属模块名称
 */
export function uploadFile(file: File, module?: string) {
    const formData = new FormData()
    formData.append('file', file)
    if (module) {
        formData.append('module', module)
    }

    return request({
        url: '/file/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 获取文件详情
 */
export function getFileInfo(id: number) {
    return request({
        url: `/file/${id}`,
        method: 'get'
    })
}
