import request from '@/utils/request'

export function login(data: any) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}

export function getUserInfo() {
    return request({
        url: '/auth/info',
        method: 'get'
    })
}

export function logout() {
    return request({
        url: '/auth/logout',
        method: 'post'
    })
}

export function updateProfile(data: any) {
    return request({
        url: '/auth/updateProfile',
        method: 'post',
        data
    })
}

export function updatePassword(data: any) {
    return request({
        url: '/auth/updatePassword',
        method: 'post',
        data
    })
}
