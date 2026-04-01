import request from '@/utils/request'
import { mockRoyalties, mockResponse, shouldUseMock } from '@/utils/mock'

// 记录版税支付
export const recordRoyalty = (data) => {
  if (shouldUseMock()) {
    // Mock模式：返回新记录的版税
    const newRoyalty = {
      id: mockRoyalties.length + 1,
      ...data,
      timestamp: Date.now() / 1000
    }
    mockRoyalties.push(newRoyalty)
    return mockResponse(newRoyalty)
  }
  return request({
    url: '/royalties/record',
    method: 'post',
    data
  })
}

// 根据版权ID查询版税记录
export const getRoyaltiesByCopyright = (copyrightId) => {
  if (shouldUseMock()) {
    // Mock模式：返回版权相关的版税记录
    const results = mockRoyalties.filter(r => r.copyrightId === String(copyrightId))
    return mockResponse(results)
  }
  // 真实后端模式：降级机制
  return request({
    url: `/royalties/copyright/${copyrightId}`,
    method: 'get'
  }).catch(error => {
    console.warn('后端API调用失败，降级使用mock数据:', error.message)
    // 降级：使用mock数据
    const results = mockRoyalties.filter(r => r.copyrightId === String(copyrightId))
    return Promise.resolve({
      code: 200,
      msg: 'success',
      data: results
    })
  })
}

// 根据支付方查询版税记录
export const getRoyaltiesByPayer = (payerAddress) => {
  if (shouldUseMock()) {
    // Mock模式：返回支付方的版税记录
    const results = mockRoyalties.filter(r => r.payerAddress === payerAddress)
    return mockResponse(results)
  }
  return request({
    url: `/royalties/payer/${payerAddress}`,
    method: 'get'
  })
}

// 根据收款方查询版税记录
export const getRoyaltiesByPayee = (payeeAddress) => {
  if (shouldUseMock()) {
    // Mock模式：返回收款方的版税记录
    const results = mockRoyalties.filter(r => r.payeeAddress === payeeAddress)
    return mockResponse(results)
  }
  return request({
    url: `/royalties/payee/${payeeAddress}`,
    method: 'get'
  })
}
