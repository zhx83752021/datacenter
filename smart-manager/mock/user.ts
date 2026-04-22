import { MockMethod } from 'vite-plugin-mock'

export default [
    {
        url: '/api/auth/login',
        method: 'post',
        response: ({ body }: any) => {
            const { username, password } = body;

            // 验证密码是否为 123456
            if (password !== '123456') {
                return { code: 401, message: '密码错误', data: null }
            }

            if (username === 'admin') {
                return { code: 200, message: '登录成功', data: { token: 'admin-token' } }
            } else if (username === 'president') {
                return { code: 200, message: '登录成功', data: { token: 'president-token' } }
            } else if (username === 'director_li') {
                return { code: 200, message: '登录成功', data: { token: 'director-token' } }
            } else if (username === 'wangwu') {
                return { code: 200, message: '登录成功', data: { token: 'wangwu-token' } }
            } else {
                return { code: 401, message: '用户不存在', data: null }
            }
        }
    },
    {
        url: '/api/auth/info',
        method: 'get',
        response: (req: any) => {
            const auth = req.headers['authorization'] || '';
            const token = auth.replace('Bearer ', '');

            if (token === 'admin-token') {
                return {
                    code: 200,
                    data: {
                        name: '系统管理员', username: 'admin', realName: '系统管理员',
                        avatar: 'https://img.icons8.com/color/96/administrator-male.png',
                        roles: ['admin'], deptId: 100, deptName: '青岛妇女儿童医院',
                        dataScope: '1' // 全部数据
                    }
                }
            } else if (token === 'president-token') {
                return {
                    code: 200,
                    data: {
                        name: '王院长', username: 'president', realName: '王院长',
                        avatar: 'https://img.icons8.com/color/96/medical-doctor.png',
                        roles: ['president'], deptId: 100, deptName: '青岛妇女儿童医院',
                        dataScope: '1' // 全部数据
                    }
                }
            } else if (token === 'director-token') {
                return {
                    code: 200,
                    data: {
                        name: '李主任（妇科中心）', username: 'director_li', realName: '李主任',
                        avatar: 'https://img.icons8.com/color/96/doctor-female.png',
                        roles: ['director'], deptId: 2304, deptName: '妇科中心',
                        dataScope: '4' // 部门及以下
                    }
                }
            } else if (token === 'wangwu-token') {
                return {
                    code: 200,
                    data: {
                        name: '王五（呼吸内科）', username: 'wangwu', realName: '王五',
                        avatar: 'https://img.icons8.com/color/96/nurse.png',
                        roles: ['user'], deptId: 2205, deptName: '呼吸内科',
                        dataScope: '3' // 本部门
                    }
                }
            } else {
                return {
                    code: 200,
                    data: {
                        name: '普通用户', username: 'guest', realName: '普通用户',
                        avatar: 'https://img.icons8.com/color/96/user.png',
                        roles: ['user'], deptId: 104, deptName: '外科',
                        dataScope: '3'
                    }
                }
            }
        }
    },
    {
        url: '/api/auth/logout',
        method: 'post',
        response: () => {
            return {
                code: 200,
                message: 'success',
                data: 'success'
            }
        }
    }
] as MockMethod[]
