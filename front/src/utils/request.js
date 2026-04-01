import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 开发环境打印详细的请求信息
    if (import.meta.env.DEV) {
      console.log('='.repeat(60))
      console.log('🚀 发送请求')
      console.log('完整 URL:', config.baseURL + config.url)
      console.log('方法:', config.method.toUpperCase())
      console.log('参数:', config.params)
      console.log('请求体:', config.data)
      console.log('配置:', config)
      console.log('='.repeat(60))
    }

    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 开发环境日志
    if (import.meta.env.DEV) {
      console.log('响应拦截器 - 原始响应:', response)
      console.log('响应拦截器 - 解析后数据:', res)
    }

    // 处理后端统一返回格式
    if (res.code === 0 || res.code === 200) {
      return res.data  // 直接返回 data 字段
    } else {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  (error) => {
    // 开发环境详细日志
    if (import.meta.env.DEV) {
      console.error('响应拦截器 - 错误详情:', error)
      console.error('响应拦截器 - 错误配置:', error.config)
      if (error.response) {
        console.error('响应拦截器 - 错误响应:', error.response)
      }
    }

    if (error.response) {
      const { status, data } = error.response

      if (status === 401) {
        ElMessage.error('未授权，请重新登录')
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 404) {
        // 404 错误不显示全局消息，让具体 API 处理
        console.warn('请求的资源不存在:', error.config?.url)
      } else if (status === 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error(data?.msg || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      ElMessage.error('请求超时')
    } else if (error.message.includes('Network')) {
      // 网络错误不显示全局消息，让具体 API 的降级逻辑处理
      console.warn('网络错误，将由 API 降级处理:', error.config?.url)
    } else {
      // 其他错误也由具体 API 处理
      console.warn('请求错误:', error.message)
    }

    return Promise.reject(error)
  }
)

export default request
