// Mock数据用于开发模式演示

// 开发模式标识 - 设置为false以使用真实后端API
const USE_MOCK = false

// Mock版权数据
export const mockCopyrights = [
  {
    id: 1,
    contractId: 'CP-2024-0001',
    title: '数字艺术作品系列1',
    author: '张三',
    ownerAddress: '0x1234567890abcdef1234567890abcdef12345678',
    fileHash: 'QmXxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQr',
    description: '这是一组原创数字艺术作品，包含10幅独特的抽象画作。',
    registerTime: Date.now() / 1000 - 86400 * 7,
    blockNumber: 12345678,
    transactionHash: '0x8f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6',
    contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
  },
  {
    id: 2,
    contractId: 'CP-2024-0002',
    title: '商业摄影作品集',
    author: '李四',
    ownerAddress: '0x2345678901bcdefa2345678901bcdefa23456789',
    fileHash: 'QmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZ',
    description: '包含50张商业级摄影作品，涵盖风景、人像、产品等多个主题。',
    registerTime: Date.now() / 1000 - 86400 * 3,
    blockNumber: 12345650,
    transactionHash: '0xa1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2',
    contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
  },
  {
    id: 3,
    contractId: 'CP-2024-0003',
    title: '原创音乐专辑《时光》',
    author: '王五',
    ownerAddress: '0x3456789012cdefab3456789012cdefab34567890',
    fileHash: 'QmZnYmXwVuTsRqPoNmLkJhGfIdEaBcZyXwVuTsRqPo',
    description: '一张包含12首原创歌曲的音乐专辑，风格以流行和民谣为主。',
    registerTime: Date.now() / 1000 - 86400,
    blockNumber: 12345600,
    transactionHash: '0xb2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e',
    contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
  },
  {
    id: 4,
    contractId: 'CP-2024-0004',
    title: '网络小说《星辰大海》',
    author: '赵六',
    ownerAddress: '0x4567890123defabc4567890123defabc45678901',
    fileHash: 'QmAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlM',
    description: '一部200万字的科幻网络小说，已完结。',
    registerTime: Date.now() / 1000 - 86400 * 15,
    blockNumber: 12345500,
    transactionHash: '0xc3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f',
    contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
  },
  {
    id: 5,
    contractId: 'CP-2024-0005',
    title: '视频教程系列',
    author: '孙七',
    ownerAddress: '0x5678901234abcdef5678901234abcdef56789012',
    fileHash: 'QmDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoP',
    description: '一套完整的编程视频教程，包含100个视频文件。',
    registerTime: Date.now() / 1000 - 86400 * 20,
    blockNumber: 12345400,
    transactionHash: '0xd4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a',
    contractAddress: '0x9876543210fedcba9876543210fedcba98765432'
  }
]

// Mock授权数据
export const mockLicenses = [
  {
    id: 1,
    contractId: 'LC-2024-0001',
    copyrightId: '1',
    copyrightTitle: '数字艺术作品系列1',
    ownerAddress: '0x1234567890abcdef1234567890abcdef12345678',
    licenseeAddress: '0x2345678901bcdefa2345678901bcdefa23456789',
    isExclusive: false,
    startTime: Date.now() / 1000 - 86400 * 30,
    endTime: Date.now() / 1000 + 86400 * 365,
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 31,
    approveTime: Date.now() / 1000 - 86400 * 30,
    activateTime: Date.now() / 1000 - 86400 * 29
  },
  {
    id: 2,
    contractId: 'LC-2024-0002',
    copyrightId: '1',
    copyrightTitle: '数字艺术作品系列1',
    ownerAddress: '0x1234567890abcdef1234567890abcdef12345678',
    licenseeAddress: '0x3456789012cdefab3456789012cdefab34567890',
    isExclusive: true,
    startTime: Date.now() / 1000 - 86400 * 15,
    endTime: Date.now() / 1000 + 86400 * 180,
    status: 'APPROVED',
    createTime: Date.now() / 1000 - 86400 * 16,
    approveTime: Date.now() / 1000 - 86400 * 15,
    activateTime: null
  },
  {
    id: 3,
    contractId: 'LC-2024-0003',
    copyrightId: '2',
    copyrightTitle: '商业摄影作品集',
    ownerAddress: '0x2345678901bcdefa2345678901bcdefa23456789',
    licenseeAddress: '0x4567890123defabc4567890123defabc45678901',
    isExclusive: false,
    startTime: null,
    endTime: null,
    status: 'PENDING',
    createTime: Date.now() / 1000 - 3600,
    approveTime: null,
    activateTime: null
  },
  {
    id: 4,
    contractId: 'LC-2024-0004',
    copyrightId: '3',
    copyrightTitle: '原创音乐专辑《时光》',
    ownerAddress: '0x3456789012cdefab3456789012cdefab34567890',
    licenseeAddress: '0x5678901234abcdef5678901234abcdef56789012',
    isExclusive: false,
    startTime: Date.now() / 1000 - 86400 * 10,
    endTime: Date.now() / 1000 + 86400 * 90,
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 11,
    approveTime: Date.now() / 1000 - 86400 * 10,
    activateTime: Date.now() / 1000 - 86400 * 9
  }
]

// Mock版税数据
export const mockRoyalties = [
  {
    id: 1,
    copyrightId: '1',
    copyrightTitle: '数字艺术作品系列1',
    payerAddress: '0x2345678901bcdefa2345678901bcdefa23456789',
    payeeAddress: '0x1234567890abcdef1234567890abcdef12345678',
    amount: '500.00',
    currency: 'CNY',
    timestamp: Date.now() / 1000 - 86400 * 2
  },
  {
    id: 2,
    copyrightId: '1',
    copyrightTitle: '数字艺术作品系列1',
    payerAddress: '0x3456789012cdefab3456789012cdefab34567890',
    payeeAddress: '0x1234567890abcdef1234567890abcdef12345678',
    amount: '300.00',
    currency: 'CNY',
    timestamp: Date.now() / 1000 - 86400 * 5
  },
  {
    id: 3,
    copyrightId: '2',
    copyrightTitle: '商业摄影作品集',
    payerAddress: '0x5678901234abcdef5678901234abcdef56789012',
    payeeAddress: '0x2345678901bcdefa2345678901bcdefa23456789',
    amount: '800.00',
    currency: 'CNY',
    timestamp: Date.now() / 1000 - 86400 * 10
  },
  {
    id: 4,
    copyrightId: '3',
    copyrightTitle: '原创音乐专辑《时光》',
    payerAddress: '0x6789012345bcdefa6789012345bcdefa67890123',
    payeeAddress: '0x3456789012cdefab3456789012cdefab34567890',
    amount: '1200.00',
    currency: 'CNY',
    timestamp: Date.now() / 1000 - 86400 * 7
  }
]

// Mock交易数据
export const mockTransactions = [
  {
    id: 1,
    txHash: '0x8f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6',
    blockNumber: 12345678,
    from: '0x1234567890abcdef1234567890abcdef12345678',
    to: '0x9876543210fedcba9876543210fedcba98765432',
    type: 'COPYRIGHT_REGISTER',
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 86400 * 7,
    description: '注册版权: 数字艺术作品系列1'
  },
  {
    id: 2,
    txHash: '0xa1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2',
    blockNumber: 12345677,
    from: '0x2345678901bcdefa2345678901bcdefa23456789',
    to: '0x1234567890abcdef1234567890abcdef12345678',
    type: 'LICENSE_APPROVE',
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 86400 * 30,
    description: '审批授权申请: LC-2024-0001'
  },
  {
    id: 3,
    txHash: '0xb2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f',
    blockNumber: 12345676,
    from: '0x2345678901bcdefa2345678901bcdefa23456789',
    to: '0x1234567890abcdef1234567890abcdef12345678',
    type: 'ROYALTY_PAYMENT',
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 86400 * 2,
    description: '支付版税: 500.00 CNY'
  },
  {
    id: 4,
    txHash: '0xc3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a',
    blockNumber: 12345675,
    from: '0x3456789012cdefab3456789012cdefab34567890',
    to: '0x1234567890abcdef1234567890abcdef12345678',
    type: 'LICENSE_ACTIVATE',
    status: 'SUCCESS',
    timestamp: Date.now() / 1000 - 86400 * 29,
    description: '激活授权: LC-2024-0001'
  },
  {
    id: 5,
    txHash: '0xd4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b',
    blockNumber: 12345674,
    from: '0x4567890123defabc4567890123defabc45678901',
    to: '0x2345678901bcdefa2345678901bcdefa23456789',
    type: 'COPYRIGHT_REGISTER',
    status: 'PENDING',
    timestamp: Date.now() / 1000 - 3600,
    description: '注册版权: 商业摄影作品集'
  }
]

// Mock用户数据
export const mockUsers = [
  {
    id: 1,
    address: '0x1234567890abcdef1234567890abcdef12345678',
    username: 'zhang_san',
    realName: '张三',
    email: 'zhangsan@example.com',
    role: 'CONTENT_OWNER',
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 100
  },
  {
    id: 2,
    address: '0x2345678901bcdefa2345678901bcdefa23456789',
    username: 'li_si',
    realName: '李四',
    email: 'lisi@example.com',
    role: 'CONTENT_OWNER',
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 80
  },
  {
    id: 3,
    address: '0x3456789012cdefab3456789012cdefab34567890',
    username: 'wang_wu',
    realName: '王五',
    email: 'wangwu@example.com',
    role: 'LICENSE_MANAGER',
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 60
  },
  {
    id: 4,
    address: '0x4567890123defabc4567890123defabc45678901',
    username: 'zhao_liu',
    realName: '赵六',
    email: 'zhaoliu@example.com',
    role: 'LICENSE_APPROVER',
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 40
  },
  {
    id: 5,
    address: '0xb82ea6626cb8cd46081b2dc6ece702864490f366',
    username: 'admin',
    realName: '系统管理员',
    email: 'admin@example.com',
    role: 'SYSTEM_ADMIN',
    status: 'ACTIVE',
    createTime: Date.now() / 1000 - 86400 * 120
  }
]

// 模拟API响应
export function mockResponse(data, delay = 500) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: 'success',
        data: data
      })
    }, delay)
  })
}

// 检查是否使用Mock数据
export function shouldUseMock() {
  return USE_MOCK && import.meta.env.DEV
}
