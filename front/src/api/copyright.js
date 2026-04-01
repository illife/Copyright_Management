import request from '@/utils/request'
import { mockCopyrights, mockResponse, shouldUseMock } from '@/utils/mock'

// 注册版权
export const registerCopyright = (data) => {
  if (shouldUseMock()) {
    // Mock模式：返回新注册的版权
    const newCopyright = {
      id: mockCopyrights.length + 1,
      contractId: `CP-2024-${String(mockCopyrights.length + 1).padStart(4, '0')}`,
      ...data,
      registerTime: Date.now() / 1000,
      blockNumber: 12345680 + mockCopyrights.length + 1,
      transactionHash: `0x${Math.random().toString(16).substr(2, 64)}`,
      contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
    }
    mockCopyrights.push(newCopyright)
    return mockResponse(newCopyright)
  }
  return request({
    url: '/copyrights/register',
    method: 'post',
    data
  })
}

// 查询版权详情
export const getCopyrightDetail = (id) => {
  return request({
    url: `/copyrights/${id}`,
    method: 'get'
  })
}

// 分页查询版权
export const getCopyrightList = (params) => {
  return request({
    url: '/copyrights/page',
    method: 'get',
    params
  })
}

// 搜索版权
export const searchCopyrights = (keyword) => {
  if (shouldUseMock()) {
    // Mock模式：搜索模拟数据
    const results = mockCopyrights.filter(c =>
      c.title.includes(keyword) ||
      c.author.includes(keyword) ||
      c.description.includes(keyword)
    )
    return mockResponse(results)
  }
  return request({
    url: '/copyrights/search',
    method: 'get',
    params: { keyword }
  })
}

// 根据用户地址查询版权
export const getCopyrightsByOwner = (ownerAddress) => {
  if (shouldUseMock()) {
    // Mock模式：返回地址匹配的版权
    const results = mockCopyrights.filter(c => c.ownerAddress === ownerAddress)
    return mockResponse(results)
  }
  return request({
    url: '/copyrights/owner',
    method: 'get',
    params: { ownerAddress }
  })
}
