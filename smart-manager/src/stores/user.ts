import { defineStore } from 'pinia'
import { login, getUserInfo, logout } from '@/api/user'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        name: '',
        realName: '',
        username: '',
        avatar: '',
        mobile: '',
        email: '',
        empNo: '',
        roles: [] as string[],
        deptId: 0,
        deptName: '',
        dataScope: '' // 数据权限范围：1全部 2自定义 3本部门 4部门及以下 5仅本人
    }),
    actions: {
        // 登录
        async login(userInfo: any) {
            try {
                const res: any = await login(userInfo)
                if (res.token) {
                    this.token = res.token
                    localStorage.setItem('token', res.token)
                    return Promise.resolve()
                } else {
                    return Promise.reject('Token not found')
                }
            } catch (error) {
                return Promise.reject(error)
            }
        },

        // 获取用户信息
        async getInfo() {
            try {
                const res: any = await getUserInfo()
                if (!res) {
                    return Promise.reject('Verification failed, please Login again.')
                }

                const { roles, name, realName, username, avatar, mobile, email, empNo, deptId, deptName, dataScope } = res

                if (!roles || roles.length <= 0) {
                    return Promise.reject('getInfo: roles must be a non-null array!')
                }

                this.roles = roles
                this.name = name
                this.realName = realName || name
                this.username = username
                this.avatar = avatar
                this.mobile = mobile
                this.email = email
                this.empNo = empNo
                this.deptId = deptId || 0
                this.deptName = deptName || ''
                this.dataScope = dataScope || ''
                return Promise.resolve(res)
            } catch (error) {
                return Promise.reject(error)
            }
        },

        // 退出
        async logout() {
            try {
                await logout()
            } catch (e) {
                console.error(e)
            } finally {
                this.token = ''
                this.roles = []
                localStorage.removeItem('token')
            }
        },

        // 前端直接重置 Token
        resetToken() {
            this.token = ''
            this.roles = []
            localStorage.removeItem('token')
        }
    }
})
