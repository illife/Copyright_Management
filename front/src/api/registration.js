import request from '@/utils/request'
import { mockResponse, shouldUseMock } from '@/utils/mock'

// Mock注册申请数据
const mockRegistrations = [
  {
    id: 1,
    userAddress: '0x1234567890123456789012345678901234567890',
    username: 'alice_wang',
    realName: '王芳',
    email: 'alice@example.com',
    phone: '13800138001',
    description: '我是独立音乐人，想要注册版权保护我的原创作品',
    status: 'PENDING',
    createTime: 1704067200,
    reviewTime: null,
    reviewerAddress: null,
    reviewerRemark: null
  },
  {
    id: 2,
    userAddress: '0x2345678901234567890123456789012345678901',
    username: 'bob_chen',
    realName: '陈明',
    email: 'bob@example.com',
    phone: '13800138002',
    description: '我是摄影师，需要保护我的摄影作品版权',
    status: 'APPROVED',
    createTime: 1703980800,
    reviewTime: 1704037200,
    reviewerAddress: '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    reviewerRemark: '审核通过，信息完整'
  },
  {
    id: 3,
    userAddress: '0x3456789012345678901234567890123456789012',
    username: 'charlie_zhang',
    realName: '张伟',
    email: 'charlie@example.com',
    phone: '13800138003',
    description: '我是作家，需要版权保护',
    status: 'REJECTED',
    createTime: 1703894400,
    reviewTime: 1703950800,
    reviewerAddress: '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    reviewerRemark: '信息不完整，请重新提交'
  }
]

/**
 * 提交注册申请
 * @param {Object} data - 注册申请数据
 * @param {string} data.userAddress - 区块链地址
 * @param {string} data.username - 用户名
 * @param {string} data.realName - 真实姓名
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @param {string} data.description - 申请说明
 */
export const submitRegistration = (data) => {
  if (shouldUseMock()) {
    const newRegistration = {
      id: mockRegistrations.length + 1,
      ...data,
      status: 'PENDING',
      createTime: Math.floor(Date.now() / 1000),
      reviewTime: null,
      reviewerAddress: null,
      reviewerRemark: null
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

/**
 * 查询我的注册申请状态
 * @param {string} userAddress - 区块链地址
 */
export const getMyRegistrationStatus = (userAddress) => {
  if (shouldUseMock()) {
    const registration = mockRegistrations.find(
      r => r.userAddress.toLowerCase() === userAddress.toLowerCase()
    )
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

/**
 * 获取注册申请列表（管理员）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @param {string} params.status - 状态筛选（可选）
 */
export const getRegistrationList = (params) => {
  if (shouldUseMock()) {
    let filteredList = [...mockRegistrations]

    // 状态筛选
    if (params.status) {
      filteredList = filteredList.filter(r => r.status === params.status)
    }

    // 分页
    const page = params.page || 1
    const size = params.size || 10
    const start = (page - 1) * size
    const end = start + size
    const paginatedList = filteredList.slice(start, end)

    return mockResponse({
      list: paginatedList,
      total: filteredList.length,
      page: page,
      size: size
    })
  }
  return request({
    url: '/registration/list',
    method: 'get',
    params
  })
}

/**
 * 获取注册申请详情
 * @param {number} id - 申请ID
 */
export const getRegistrationDetail = (id) => {
  if (shouldUseMock()) {
    const registration = mockRegistrations.find(r => r.id === id)
    if (registration) {
      return mockResponse(registration)
    }
    return Promise.reject(new Error('注册申请不存在'))
  }
  return request({
    url: `/registration/${id}`,
    method: 'get'
  })
}

/**
 * 审批注册申请（管理员）
 * @param {Object} data - 审批数据
 * @param {number} data.id - 申请ID
 * @param {boolean} data.approved - 是否通过
 * @param {string} data.reviewerAddress - 审批人地址
 * @param {string} data.remark - 审批备注（可选）
 */
export const reviewRegistration = (data) => {
  if (shouldUseMock()) {
    const registration = mockRegistrations.find(r => r.id === data.id)
    if (registration) {
      registration.status = data.approved ? 'APPROVED' : 'REJECTED'
      registration.reviewTime = Math.floor(Date.now() / 1000)
      registration.reviewerAddress = data.reviewerAddress
      registration.reviewerRemark = data.remark || ''
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

/**
 * 获取待审核申请数量（管理员）
 */
export const getPendingCount = () => {
  if (shouldUseMock()) {
    const pendingCount = mockRegistrations.filter(r => r.status === 'PENDING').length
    return mockResponse({ count: pendingCount })
  }
  return request({
    url: '/registration/pending-count',
    method: 'get'
  })
}
