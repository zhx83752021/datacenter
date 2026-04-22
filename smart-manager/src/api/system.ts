import request from '@/utils/request'

// --- 用户管理 ---

export function listUser(query: any) {
    return request({
        url: '/system/user/list',
        method: 'get',
        params: query
    })
}

export function getUser(id: number | string) {
    return request({
        url: `/system/user/${id}`,
        method: 'get'
    })
}

export function addUser(data: any) {
    return request({
        url: '/system/user',
        method: 'post',
        data
    })
}

export function updateUser(data: any) {
    return request({
        url: '/system/user',
        method: 'put',
        data
    })
}

export function delUser(id: number | string) {
    return request({
        url: `/system/user/${id}`,
        method: 'delete'
    })
}

export function resetUserPwd(id: number | string, password: string) {
    const data = { id, password }
    return request({
        url: '/system/user/resetPwd',
        method: 'put',
        data
    })
}

// --- 菜单管理 ---

export function listMenu(query?: any) {
    return request({
        url: '/system/menu/list',
        method: 'get',
        params: query
    })
}

export function getMenuTree() {
    return request({
        url: '/system/menu/treelist',
        method: 'get'
    })
}

export function getMenu(id: number | string) {
    return request({
        url: `/system/menu/${id}`,
        method: 'get'
    })
}

export function addMenu(data: any) {
    return request({
        url: '/system/menu',
        method: 'post',
        data
    })
}

export function updateMenu(data: any) {
    return request({
        url: '/system/menu',
        method: 'put',
        data
    })
}

export function delMenu(id: number | string) {
    return request({
        url: `/system/menu/${id}`,
        method: 'delete'
    })
}
