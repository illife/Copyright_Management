import request from '@/utils/request'
import { mockResponse, shouldUseMock } from '@/utils/mock'

// Mock区块数据
const mockBlocks = [
  {
    number: 12345678,
    hash: '0x8f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2',
    parentHash: '0xa1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1',
    timestamp: Date.now() / 1000 - 3600,
    transactionsCount: 156,
    gasUsed: 8500000,
    gasLimit: 10000000,
    miner: '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    size: 52341
  },
  {
    number: 12345677,
    hash: '0x7b3c4d5e6f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c',
    parentHash: '0xb2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2',
    timestamp: Date.now() / 1000 - 7200,
    transactionsCount: 203,
    gasUsed: 9200000,
    gasLimit: 10000000,
    miner: '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    size: 48956
  },
  {
    number: 12345676,
    hash: '0x6c4d5e6f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d',
    parentHash: '0xc3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3',
    timestamp: Date.now() / 1000 - 10800,
    transactionsCount: 178,
    gasUsed: 7800000,
    gasLimit: 10000000,
    miner: '0x1234567890abcdef1234567890abcdef12345678',
    size: 51234
  }
]

// Mock节点数据
const mockNodes = [
  {
    id: 'node1',
    name: '节点-北京-01',
    address: '127.0.0.1:20200',
    status: 'ACTIVE',
    blockNumber: 12345682,
    peerCount: 3,
    groupCount: 1
  },
  {
    id: 'node2',
    name: '节点-上海-01',
    address: '127.0.0.1:20201',
    status: 'ACTIVE',
    blockNumber: 12345681,
    peerCount: 3,
    groupCount: 1
  },
  {
    id: 'node3',
    name: '节点-深圳-01',
    address: '127.0.0.1:20202',
    status: 'ACTIVE',
    blockNumber: 12345679,
    peerCount: 3,
    groupCount: 1
  },
  {
    id: 'node4',
    name: '节点-广州-01',
    address: '127.0.0.1:20203',
    status: 'ACTIVE',
    blockNumber: 12345680,
    peerCount: 3,
    groupCount: 1
  }
]

/**
 * 获取区块列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
export const getBlockList = (params) => {
  if (shouldUseMock()) {
    console.log('使用 mock 模式获取区块列表')
    const page = params.page || 1
    const size = params.size || 10
    const start = (page - 1) * size
    const end = start + size

    // 生成更多Mock数据
    const blocks = Array.from({ length: Math.min(size, mockBlocks.length) }, (_, i) => ({
      ...mockBlocks[i % mockBlocks.length],
      number: 12345678 - i
    }))

    return mockResponse({
      list: blocks,
      total: 12345678,
      page: page,
      size: size
    })
  }
  console.log('使用真实后端获取区块列表，请求路径: /api/blockchain/blocks/latest，参数:', params)
  // 使用后端的 /blocks/latest 接口，并适配分页格式
  return request({
    url: '/blockchain/blocks/latest',
    method: 'get',
    params: { limit: params.size || 10 }
  }).then(response => {
    console.log('后端返回的区块数据原始数据:', response)

    // 适配后端返回格式为前端期望的格式
    // 响应拦截器已经返回 res.data，所以 response 直接是数组
    const blocks = (response || []).map(block => ({
      // 后端字段 → 前端字段映射
      number: block.blockNumber,
      hash: block.blockHash,
      parentHash: block.parentHash,
      transactionsCount: block.transactionCount || 0,
      gasUsed: 0, // 后端没有返回，使用默认值
      gasLimit: 10000000, // 后端没有返回，使用默认值
      miner: '0x0000000000000000000000000000000000000000', // 后端没有返回，使用默认值
      size: block.blockSize || 0,
      timestamp: Date.now() / 1000, // 后端没有返回，使用当前时间
      // 保留原始后端字段
      gasUsageRate: block.gasUsageRate,
      blockSizeFormatted: block.blockSizeFormatted,
      status: block.status
    }))

    // 直接返回前端期望的格式（响应拦截器已处理 code/msg）
    const result = {
      list: blocks,
      total: blocks.length || 10,
      page: params.page || 1,
      size: params.size || 10
    }
    console.log('适配后的区块列表数据:', result)
    return result
  }).catch(error => {
    console.warn('获取区块列表失败，降级使用mock数据:', error.message)
    // 降级到 mock 数据
    const size = params.size || 10
    const blocks = mockBlocks.slice(0, size).map((block, i) => ({
      ...block,
      number: 12345678 - i
    }))
    // 直接返回格式化后的数据
    const mockResult = {
      list: blocks,
      total: blocks.length,
      page: params.page || 1,
      size: size
    }
    console.log('降级返回的 mock 区块数据:', mockResult)
    return Promise.resolve(mockResult)
  })
}

/**
 * 获取区块详情
 * @param {number} blockNumber - 区块高度
 */
export const getBlockDetail = (blockNumber) => {
  if (shouldUseMock()) {
    const block = mockBlocks.find(b => b.number === parseInt(blockNumber))
    if (block) {
      return mockResponse({
        ...block,
        transactions: mockTransactions.slice(0, 5).map(tx => ({
          ...tx,
          blockNumber: block.number,
          blockHash: block.hash
        }))
      })
    }
    return Promise.reject(new Error('区块不存在'))
  }
  return request({
    url: `/blockchain/block/${blockNumber}`,
    method: 'get'
  }).then(response => {
    console.log('后端返回的区块详情:', response)

    // 响应拦截器已返回 res.data
    const block = response
    if (!block) {
      throw new Error('区块不存在')
    }

    // 适配后端返回格式为前端期望的格式
    return {
      // 后端字段 → 前端字段映射
      number: block.blockNumber,
      hash: block.blockHash,
      parentHash: block.parentHash,
      transactionsCount: block.transactionCount || 0,
      gasUsed: 0, // 后端没有返回
      gasLimit: 10000000, // 后端没有返回
      miner: block.miner || '0x0000000000000000000000000000000000000000',
      size: block.blockSize || 0,
      timestamp: block.timestamp || Date.now() / 1000,
      // 交易列表
      transactions: block.transactions || [],
      // 保留原始字段
      gasUsageRate: block.gasUsageRate,
      blockSizeFormatted: block.blockSizeFormatted,
      status: block.status
    }
  }).catch(error => {
    console.warn('获取区块详情失败，尝试使用 mock 数据:', error.message)
    // 降级到 mock 数据
    const block = mockBlocks.find(b => b.number === parseInt(blockNumber))
    if (block) {
      return Promise.resolve({
        ...block,
        transactions: mockTransactions.slice(0, 5).map(tx => ({
          ...tx,
          blockNumber: block.number,
          blockHash: block.hash
        }))
      })
    }
    return Promise.reject(error)
  })
}

/**
 * 获取最新区块信息
 */
export const getLatestBlock = () => {
  if (shouldUseMock()) {
    return mockResponse(mockBlocks[0])
  }
  return request({
    url: '/blockchain/blocks/latest',
    method: 'get',
    params: { limit: 1 }
  }).then(response => {
    // 响应拦截器已返回 res.data（数组）
    const blocks = response || []
    return blocks.length > 0 ? blocks[0] : null
  })
}

/**
 * 获取节点列表
 */
export const getNodeList = () => {
  if (shouldUseMock()) {
    console.log('使用 mock 模式获取节点列表')
    return mockResponse(mockNodes)
  }
  // 后端没有节点列表接口，返回mock数据
  console.log('后端没有节点接口，使用 mock 数据')
  return mockResponse(mockNodes)
}

/**
 * 获取节点详情
 * @param {string} nodeId - 节点ID
 */
export const getNodeDetail = (nodeId) => {
  if (shouldUseMock()) {
    const node = mockNodes.find(n => n.id === nodeId)
    if (node) {
      return mockResponse(node)
    }
    return Promise.reject(new Error('节点不存在'))
  }
  return request({
    url: `/blockchain/nodes/${nodeId}`,
    method: 'get'
  })
}

/**
 * 获取网络统计信息
 */
export const getNetworkStats = () => {
  if (shouldUseMock()) {
    console.log('使用 mock 模式获取网络统计')
    return mockResponse({
      blockHeight: 12345678,
      totalTransactions: 567890,
      totalNodes: 4,
      activeNodes: 4,
      avgBlockTime: 1.5,
      avgGasPrice: 1000000000,
      marketCap: 0,
      hashRate: 150000000000
    })
  }
  console.log('使用真实后端获取网络统计，请求路径: /api/blockchain/stats')
  return request({
    url: '/blockchain/stats',
    method: 'get'
  }).then(response => {
    console.log('后端返回的网络统计原始数据:', response)

    // 适配后端返回格式为前端期望的格式
    // 响应拦截器已返回 res.data
    const stats = response || {}
    const adaptedData = {
      blockHeight: stats.latestBlockNumber || stats.blockHeight || 0,
      totalTransactions: stats.totalTransactions || 0,
      totalNodes: stats.totalNodes || mockNodes.length,
      activeNodes: stats.activeNodes || mockNodes.filter(n => n.status === 'ACTIVE').length,
      avgBlockTime: stats.avgBlockTime || 1.5,
      avgGasPrice: stats.avgGasPrice || 1000000000,
      marketCap: stats.marketCap || 0,
      hashRate: stats.hashRate || 0
    }
    console.log('适配后的网络统计数据:', adaptedData)

    // 直接返回数据（响应拦截器已处理 code/msg）
    return adaptedData
  }).catch(error => {
    console.warn('获取网络统计失败，降级使用mock数据:', error.message)
    // 降级到 mock 数据
    const mockData = {
      blockHeight: 170,
      totalTransactions: 170,
      totalNodes: 4,
      activeNodes: 4,
      avgBlockTime: 1.5,
      avgGasPrice: 1000000000,
      marketCap: 0,
      hashRate: 0
    }
    console.log('降级返回的 mock 数据:', mockData)
    return Promise.resolve(mockData)
  })
}

/**
 * 获取区块内的交易列表
 * @param {number} blockNumber - 区块高度
 */
export const getBlockTransactions = (blockNumber) => {
  if (shouldUseMock()) {
    return mockResponse({
      blockNumber: blockNumber,
      transactions: mockTransactions.slice(0, 5)
    })
  }
  return request({
    url: `/blockchain/blocks/${blockNumber}/transactions`,
    method: 'get'
  })
}

// Mock交易数据
const mockTransactions = [
  {
    hash: '0x8f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2',
    from: '0x1234567890abcdef1234567890abcdef12345678',
    to: '0x2345678901bcdefa2345678901bcdefa23456789',
    value: '0',
    gasUsed: 45000,
    gasPrice: 1000000000,
    blockNumber: 12345678,
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 300,
    type: 'COPYRIGHT_REGISTER',
    description: '注册版权: 数字艺术作品系列1'
  },
  {
    hash: '0xa1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0',
    from: '0x2345678901bcdefa2345678901bcdefa23456789',
    to: '0x3456789012cdefab3456789012cdefab34567890',
    value: '500000000000000000',
    gasUsed: 35000,
    gasPrice: 1000000000,
    blockNumber: 12345678,
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 600,
    type: 'ROYALTY_PAYMENT',
    description: '版税支付: 500.00 CNY'
  }
]
