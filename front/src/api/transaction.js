import request from '@/utils/request'

/**
 * 获取交易统计信息
 * @returns {Promise} 返回统计数据（total, success, pending, failed）
 */
export const getTransactionStats = () => {
  return request({
    url: '/transactions/stats',
    method: 'get'
  })
}

/**
 * 获取交易记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {Number} params.pageNum - 页码，默认1
 * @param {Number} params.pageSize - 每页大小，默认12
 * @param {String} params.type - 交易类型（可选）
 * @param {String} params.status - 交易状态（可选）
 * @param {String} params.fromAddress - 发送方地址（可选）
 * @param {String} params.toAddress - 接收方地址（可选）
 * @param {Number} params.blockNumber - 区块号（可选）
 * @param {String} params.transactionHash - 交易哈希（可选）
 * @returns {Promise} 返回分页交易列表
 */
export const getTransactionList = (params) => {
  return request({
    url: '/transactions/list',
    method: 'get',
    params
  })
}

/**
 * 获取交易详情
 * @param {string} txHash - 交易哈希
 */
export const getTransactionDetail = (txHash) => {
  return request({
    url: `/transactions/${txHash}`,
    method: 'get'
  })
}

/**
 * 根据地址获取交易列表
 * @param {string} address - 地址
 * @param {Object} params - 其他查询参数
 */
export const getTransactionsByAddress = (address, params = {}) => {
  return request({
    url: `/transactions/address/${address}`,
    method: 'get',
    params
  })
}

/**
 * 导出交易记录
 * @param {Object} params - 导出筛选条件
 * @returns {Promise} 返回导出结果
 */
export const exportTransactions = (params) => {
  return request({
    url: '/transactions/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 交易类型映射
export const TRANSACTION_TYPES = {
  COPYRIGHT_REGISTER: '版权注册',
  LICENSE_APPLY: '授权申请',
  LICENSE_APPROVE: '授权批准',
  LICENSE_ACTIVATE: '授权激活',
  LICENSE_REVOKE: '授权撤销',
  ROYALTY_PAYMENT: '版税支付',
  USER_REGISTER: '用户注册'
}

// 交易状态映射
export const TRANSACTION_STATUS = {
  SUCCESS: '成功',
  PENDING: '待确认',
  FAILED: '失败'
}
