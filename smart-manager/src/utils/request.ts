import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
    timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 可以在这里添加 token 等逻辑
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error: any) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data
        // 如果是文件流(Blob/ArrayBuffer)，直接返回数据
        if (res instanceof Blob || res instanceof ArrayBuffer) {
            return res;
        }
        // 兼容处理：支持多种成功码标识 (0 或 200)
        const isSuccess = res.code === 200 || res.code === 0 || res.success === true;

        if (!isSuccess) {
            ElMessage({
                message: res.message || res.msg || '执行失败',
                type: 'error',
                duration: 5 * 1000
            })
            return Promise.reject(new Error(res.message || res.msg || 'Error'))
        }
        return res.data
    },
    (error: any) => {
        console.error('API Error:', error)
        let message = '网络连接异常，请检查后端服务';

        if (error.response) {
            const { status, data } = error.response;
            switch (status) {
                case 401:
                    message = '登录已过期，请重新登录';
                    // 这里可以触发退出登录逻辑
                    break;
                case 403:
                    message = '权限不足，无法访问该资源';
                    break;
                case 404:
                    message = '请求资源不存在';
                    break;
                case 500:
                    message = data.message || '服务器内部错误';
                    break;
                default:
                    message = data.message || message;
            }
        } else if (error.message.includes('timeout')) {
            message = '请求超时，请稍后重试';
        }

        ElMessage({
            message: message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
