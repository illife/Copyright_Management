import request from '@/utils/request'
import { mockResponse, shouldUseMock } from '@/utils/mock'

// Mock用户数据
const mockUsers = [
  {
    address: '0x1234567890abcdef1234567890abcdef12345678',
    username: '内容创作者',
    role: 'CONTENT_OWNER',
    email: 'creator@example.com'
  },
  {
    address: '0x2345678901bcdefa2345678901bcdefa23456789',
    username: '授权审批人',
    role: 'LICENSE_APPROVER',
    email: 'approver@example.com'
  },
  {
    address: '0x3456789012cdefab3456789012cdefab34567890',
    username: '授权管理员',
    role: 'LICENSE_MANAGER',
    email: 'manager@example.com'
  },
  {
    address: '0x4567890123defabc4567890123defabc45678901',
    username: '审计员',
    role: 'AUDITOR',
    email: 'auditor@example.com'
  },
  {
    address: '0x5678901234abcdef5678901234abcdef56789012',
    username: '系统管理员',
    role: 'SYSTEM_ADMIN',
    email: 'admin@example.com'
  }
]

// Mock注册申请数据
const mockRegistrations = []

// 用户登录
export const login = (address) => {
  if (shouldUseMock()) {
    // Mock模式：根据地址返回用户信息
    const user = mockUsers.find(u => u.address.toLowerCase() === address.toLowerCase())
    if (user) {
      return mockResponse({
        ...user,
        token: `mock_token_${Date.now()}`
      })
    }
    // 如果找不到匹配的用户，返回一个默认的内容创作者角色
    return mockResponse({
      address,
      username: '测试用户',
      role: 'CONTENT_OWNER',
      email: 'test@example.com',
      token: `mock_token_${Date.now()}`
    })
  }
  return request({
    url: '/users/login',
    method: 'post',
    params: { address }
  })
}

// 用户注册申请
export const register = (data) => {
  if (shouldUseMock()) {
    // Mock模式：返回新申请
    const newRegistration = {
      id: mockRegistrations.length + 1,
      ...data,
      status: 'PENDING',
      createTime: Date.now() / 1000
    }
    mockRegistrations.push(newRegistration)
    return mockResponse(newRegistration)
  }
  return request({
    url: '/registration/submit',
    method: 'post',
    data
  })
}

// 查询注册申请状态
export const getRegistrationStatus = (userAddress) => {
  if (shouldUseMock()) {
    // Mock模式：返回申请状态
    const registration = mockRegistrations.find(r => r.userAddress === userAddress)
    if (registration) {
      return mockResponse(registration)
    }
    return Promise.reject(new Error('未找到注册申请'))
  }
  return request({
    url: '/registration/my-status',
    method: 'get',
    params: { userAddress }
  })
}

// 审批注册申请
export const reviewRegistration = (data) => {
  if (shouldUseMock()) {
    // Mock模式：更新申请状态
    const registration = mockRegistrations.find(r => r.id === data.id)
    if (registration) {
      registration.status = data.approved ? 'APPROVED' : 'REJECTED'
      registration.reviewTime = Date.now() / 1000
      registration.reviewerAddress = data.reviewerAddress
      return mockResponse(registration)
    }
    return Promise.reject(new Error('注册申请不存在'))
  }
  return request({
    url: '/registration/review',
    method: 'post',
    data
  })
}
