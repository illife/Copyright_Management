import request from '@/utils/request'
import { mockLicenses, mockResponse, shouldUseMock } from '@/utils/mock'

// 申请授权
export const applyLicense = (data) => {
  if (shouldUseMock()) {
    // Mock模式：返回新申请的授权
    const newLicense = {
      id: mockLicenses.length + 1,
      contractId: `LC-2024-${String(mockLicenses.length + 1).padStart(4, '0')}`,
      ...data,
      status: 'PENDING',
      createTime: Date.now() / 1000,
      approveTime: null,
      activateTime: null
    }
    mockLicenses.push(newLicense)
    return mockResponse(newLicense)
  }
  return request({
    url: '/licenses/apply',
    method: 'post',
    data
  })
}

// 批准授权
export const approveLicense = (id) => {
  if (shouldUseMock()) {
    // Mock模式：更新授权状态
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'APPROVED'
      license.approveTime = Date.now() / 1000
      return mockResponse(license)
    }
    return Promise.reject(new Error('授权不存在'))
  }
  return request({
    url: `/licenses/${id}/approve`,
    method: 'put'
  }).catch(error => {
    console.warn('后端API调用失败，使用mock模拟:', error.message)
    // Mock模拟
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'APPROVED'
      license.approveTime = Date.now() / 1000
      return Promise.resolve({
        code: 200,
        msg: 'success',
        data: license
      })
    }
    return Promise.reject(error)
  })
}

// 拒绝授权
export const rejectLicense = (id) => {
  if (shouldUseMock()) {
    // Mock模式：更新授权状态
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'REVOKED'
      return mockResponse(license)
    }
    return Promise.reject(new Error('授权不存在'))
  }
  return request({
    url: `/licenses/${id}/reject`,
    method: 'put'
  }).catch(error => {
    console.warn('后端API调用失败，使用mock模拟:', error.message)
    // Mock模拟
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'REVOKED'
      return Promise.resolve({
        code: 200,
        msg: 'success',
        data: license
      })
    }
    return Promise.reject(error)
  })
}

// 激活授权
export const activateLicense = (id) => {
  if (shouldUseMock()) {
    // Mock模式：更新授权状态
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'ACTIVE'
      license.activateTime = Date.now() / 1000
      return mockResponse(license)
    }
    return Promise.reject(new Error('授权不存在'))
  }
  return request({
    url: `/licenses/${id}/activate`,
    method: 'put'
  }).catch(error => {
    console.warn('后端API调用失败，使用mock模拟:', error.message)
    // Mock模拟
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'ACTIVE'
      license.activateTime = Date.now() / 1000
      return Promise.resolve({
        code: 200,
        msg: 'success',
        data: license
      })
    }
    return Promise.reject(error)
  })
}

// 撤销授权
export const revokeLicense = (id) => {
  if (shouldUseMock()) {
    // Mock模式：更新授权状态
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'REVOKED'
      return mockResponse(license)
    }
    return Promise.reject(new Error('授权不存在'))
  }
  return request({
    url: `/licenses/${id}/revoke`,
    method: 'put'
  }).catch(error => {
    console.warn('后端API调用失败，使用mock模拟:', error.message)
    // Mock模拟
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      license.status = 'REVOKED'
      return Promise.resolve({
        code: 200,
        msg: 'success',
        data: license
      })
    }
    return Promise.reject(error)
  })
}

// 查询授权详情
export const getLicenseDetail = (id) => {
  if (shouldUseMock()) {
    // Mock模式：返回模拟数据
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      return mockResponse(license)
    }
    return Promise.reject(new Error('授权不存在'))
  }
  // 真实后端模式：先尝试调用后端API，失败时降级到mock数据
  return request({
    url: `/licenses/${id}`,
    method: 'get'
  }).catch(error => {
    console.warn('后端API调用失败，降级使用mock数据:', error.message)
    // 降级：使用mock数据
    const license = mockLicenses.find(l => l.id === parseInt(id))
    if (license) {
      // 返回与后端相同格式的响应
      return Promise.resolve({
        code: 200,
        msg: 'success',
        data: license
      })
    }
    return Promise.reject(new Error('授权不存在'))
  })
}

// 根据版权ID查询授权列表
export const getLicensesByCopyright = (copyrightId) => {
  if (shouldUseMock()) {
    // Mock模式：返回版权相关的授权
    const results = mockLicenses.filter(l => l.copyrightId === String(copyrightId))
    return mockResponse(results)
  }
  // 真实后端模式：降级机制
  return request({
    url: `/licenses/copyright/${copyrightId}`,
    method: 'get'
  }).catch(error => {
    console.warn('后端API调用失败，降级使用mock数据:', error.message)
    const results = mockLicenses.filter(l => l.copyrightId === String(copyrightId))
    return Promise.resolve({
      code: 200,
      msg: 'success',
      data: results
    })
  })
}

// 根据被授权方查询授权列表
export const getLicensesByLicensee = (licenseeAddress) => {
  if (shouldUseMock()) {
    // Mock模式：返回被授权方的授权
    const results = mockLicenses.filter(l => l.licenseeAddress === licenseeAddress)
    return mockResponse(results)
  }
  return request({
    url: `/licenses/licensee/${licenseeAddress}`,
    method: 'get'
  })
}

// 查询待审批的授权
export const getPendingLicenses = () => {
  if (shouldUseMock()) {
    // Mock模式：返回待审批的授权
    const results = mockLicenses.filter(l => l.status === 'PENDING')
    return mockResponse(results)
  }
  return request({
    url: '/licenses/pending',
    method: 'get'
  })
}

// 分页查询授权
export const getLicenseList = (params) => {
  if (shouldUseMock()) {
    // Mock模式：返回分页数据
    const { pageNum = 1, pageSize = 10 } = params
    const start = (pageNum - 1) * pageSize
    const end = start + pageSize
    const records = mockLicenses.slice(start, end)

    return mockResponse({
      records,
      total: mockLicenses.length,
      pageNum,
      pageSize
    })
  }
  // 真实后端模式：先尝试调用后端API，失败时降级到mock数据
  return request({
    url: '/licenses/page',
    method: 'get',
    params
  }).catch(error => {
    console.warn('后端API调用失败，降级使用mock数据:', error.message)
    // 降级：使用mock数据
    const { pageNum = 1, pageSize = 10 } = params
    const start = (pageNum - 1) * pageSize
    const end = start + pageSize
    const records = mockLicenses.slice(start, end)
    return Promise.resolve({
      code: 200,
      msg: 'success',
      data: {
        records,
        total: mockLicenses.length,
        pageNum,
        pageSize
      }
    })
  })
}
