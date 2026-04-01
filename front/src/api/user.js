import request from '@/utils/request'
import { mockUsers, mockResponse, shouldUseMock } from '@/utils/mock'

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @param {string} params.role - 角色筛选（可选）
 * @param {string} params.status - 状态筛选（可选）
 * @param {string} params.keyword - 搜索关键词（可选）
 */
export const getUserList = (params) => {
  if (shouldUseMock()) {
    let filteredList = [...mockUsers]

    // 角色筛选
    if (params.role) {
      filteredList = filteredList.filter(u => u.role === params.role)
    }

    // 状态筛选
    if (params.status) {
      filteredList = filteredList.filter(u => u.status === params.status)
    }

    // 关键词搜索
    if (params.keyword) {
      const keyword = params.keyword.toLowerCase()
      filteredList = filteredList.filter(u =>
        u.username.toLowerCase().includes(keyword) ||
        u.realName.toLowerCase().includes(keyword) ||
        u.email.toLowerCase().includes(keyword) ||
        u.address.toLowerCase().includes(keyword)
      )
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
    url: '/users/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 */
export const getUserDetail = (id) => {
  if (shouldUseMock()) {
    const user = mockUsers.find(u => u.id === parseInt(id))
    if (user) {
      return mockResponse(user)
    }
    return Promise.reject(new Error('用户不存在'))
  }
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

/**
 * 根据地址获取用户信息
 * @param {string} address - 区块链地址
 */
export const getUserByAddress = (address) => {
  if (shouldUseMock()) {
    const user = mockUsers.find(u =>
      u.address.toLowerCase() === address.toLowerCase()
    )
    if (user) {
      return mockResponse(user)
    }
    return Promise.reject(new Error('用户不存在'))
  }
  return request({
    url: '/users/address',
    method: 'get',
    params: { address }
  })
}

/**
 * 更新用户信息
 * @param {number} id - 用户ID
 * @param {Object} data - 更新数据
 */
export const updateUser = (id, data) => {
  if (shouldUseMock()) {
    const user = mockUsers.find(u => u.id === parseInt(id))
    if (user) {
      Object.assign(user, data)
      return mockResponse(user)
    }
    return Promise.reject(new Error('用户不存在'))
  }
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 更新用户状态
 * @param {number} id - 用户ID
 * @param {string} status - 状态（ACTIVE/INACTIVE）
 */
export const updateUserStatus = (id, status) => {
  if (shouldUseMock()) {
    const user = mockUsers.find(u => u.id === parseInt(id))
    if (user) {
      user.status = status
      return mockResponse(user)
    }
    return Promise.reject(new Error('用户不存在'))
  }
  return request({
    url: `/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export const deleteUser = (id) => {
  if (shouldUseMock()) {
    const index = mockUsers.findIndex(u => u.id === parseInt(id))
    if (index !== -1) {
      mockUsers.splice(index, 1)
      return mockResponse({ success: true })
    }
    return Promise.reject(new Error('用户不存在'))
  }
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

/**
 * 获取用户统计
 */
export const getUserStats = () => {
  if (shouldUseMock()) {
    const stats = {
      total: mockUsers.length,
      contentOwners: mockUsers.filter(u => u.role === 'CONTENT_OWNER').length,
      licenseManagers: mockUsers.filter(u => u.role === 'LICENSE_MANAGER').length,
      licenseApprovers: mockUsers.filter(u => u.role === 'LICENSE_APPROVER').length,
      auditors: mockUsers.filter(u => u.role === 'AUDITOR').length,
      systemAdmins: mockUsers.filter(u => u.role === 'SYSTEM_ADMIN').length,
      active: mockUsers.filter(u => u.status === 'ACTIVE').length,
      inactive: mockUsers.filter(u => u.status === 'INACTIVE').length
    }
    return mockResponse(stats)
  }
  return request({
    url: '/users/stats',
    method: 'get'
  })
}

// 角色映射
export const USER_ROLES = {
  CONTENT_OWNER: '内容拥有者',
  LICENSE_APPROVER: '授权审批员',
  LICENSE_MANAGER: '授权管理员',
  AUDITOR: '审计员',
  SYSTEM_ADMIN: '系统管理员'
}

// 用户状态映射
export const USER_STATUS = {
  ACTIVE: '正常',
  INACTIVE: '禁用'
}
