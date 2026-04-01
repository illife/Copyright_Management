import request from '@/utils/request'
import {
  mockCopyrights,
  mockLicenses,
  mockRoyalties,
  mockTransactions,
  mockResponse,
  shouldUseMock
} from '@/utils/mock'

/**
 * 获取仪表板统计数据
 */
export const getDashboardStats = () => {
  if (shouldUseMock()) {
    const stats = {
      // 版权统计
      copyrightCount: mockCopyrights.length,
      copyrightIncrease: 12, // 本月增长

      // 授权统计
      licenseCount: mockLicenses.length,
      licenseActive: mockLicenses.filter(l => l.status === 'ACTIVE').length,
      licensePending: mockLicenses.filter(l => l.status === 'PENDING').length,

      // 版税统计
      totalRoyalty: mockRoyalties.reduce((sum, r) => sum + parseFloat(r.amount), 0),
      royaltyIncrease: 28.5, // 较上月增长百分比
      recentRoyalties: mockRoyalties.slice(0, 5),

      // 交易统计
      transactionCount: mockTransactions.length,
      transactionSuccess: mockTransactions.filter(t => t.status === 'SUCCESS').length,
      transactionPending: mockTransactions.filter(t => t.status === 'PENDING').length,

      // 待处理事项
      pendingApprovals: mockLicenses.filter(l => l.status === 'PENDING').length,

      // 近期活动
      recentActivities: [
        ...mockCopyrights.slice(0, 3).map(c => ({
          type: 'copyright',
          message: `注册了新版权: ${c.title}`,
          user: c.author,
          time: c.registerTime
        })),
        ...mockLicenses.filter(l => l.status === 'PENDING').slice(0, 3).map(l => ({
          type: 'license',
          message: `申请授权: ${l.copyrightTitle}`,
          user: l.licenseeAddress,
          time: l.createTime
        }))
      ].sort((a, b) => b.time - a.time).slice(0, 10)
    }
    return mockResponse(stats)
  }
  return request({
    url: '/stats/dashboard',
    method: 'get'
  })
}

/**
 * 获取版权统计趋势数据（用于图表）
 * @param {string} period - 时间周期（week/month/year）
 */
export const getCopyrightTrend = (period = 'month') => {
  if (shouldUseMock()) {
    // 模拟趋势数据
    const labels = {
      week: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      month: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
      year: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    }

    const data = {
      week: [3, 5, 2, 8, 6, 4, 7],
      month: Array.from({ length: 30 }, () => Math.floor(Math.random() * 10) + 1),
      year: [45, 52, 38, 61, 55, 67, 72, 68, 75, 82, 78, 85]
    }

    return mockResponse({
      labels: labels[period],
      data: data[period],
      total: data[period].reduce((sum, val) => sum + val, 0)
    })
  }
  return request({
    url: '/stats/copyright-trend',
    method: 'get',
    params: { period }
  })
}

/**
 * 获取版税统计趋势数据
 * @param {string} period - 时间周期（week/month/year）
 */
export const getRoyaltyTrend = (period = 'month') => {
  if (shouldUseMock()) {
    const labels = {
      week: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      month: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
      year: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    }

    const data = {
      week: [1200, 1800, 900, 2100, 1500, 1700, 2300],
      month: Array.from({ length: 30 }, () => Math.floor(Math.random() * 3000) + 500),
      year: [25000, 28000, 32000, 35000, 38000, 42000, 45000, 48000, 52000, 55000, 58000, 62000]
    }

    return mockResponse({
      labels: labels[period],
      data: data[period],
      total: data[period].reduce((sum, val) => sum + val, 0)
    })
  }
  return request({
    url: '/stats/royalty-trend',
    method: 'get',
    params: { period }
  })
}

/**
 * 获取授权类型分布
 */
export const getLicenseDistribution = () => {
  if (shouldUseMock()) {
    const distribution = {
      exclusive: mockLicenses.filter(l => l.isExclusive).length,
      nonExclusive: mockLicenses.filter(l => !l.isExclusive).length
    }
    return mockResponse(distribution)
  }
  return request({
    url: '/stats/license-distribution',
    method: 'get'
  })
}

/**
 * 获取系统概览统计
 */
export const getSystemOverview = () => {
  if (shouldUseMock()) {
    const overview = {
      totalUsers: 152,
      totalCopyrights: mockCopyrights.length,
      totalLicenses: mockLicenses.length,
      totalTransactions: mockTransactions.length,
      totalRoyalties: mockRoyalties.reduce((sum, r) => sum + parseFloat(r.amount), 0),
      activeNodes: 4,
      blockHeight: 12345678,
      pendingTransactions: mockTransactions.filter(t => t.status === 'PENDING').length
    }
    return mockResponse(overview)
  }
  return request({
    url: '/stats/system-overview',
    method: 'get'
  })
}
