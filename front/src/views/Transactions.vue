<template>
  <div class="transactions-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">交易记录</h1>
        <p class="page-subtitle">查看区块链上的所有交易历史</p>
      </div>
      <button class="btn btn-primary" @click="exportTransactions">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 13V5M9 13L5 9M9 13L13 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M1 13V15C1 16.1 1.9 17 3 17H15C16.1 17 17 16.1 17 15V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        导出记录
      </button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon stat-purple">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 4H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M3 9H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M3 14H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总交易数</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-green">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM8 15L3 10L4.41 8.59L8 12.17L15.59 4.58L17 6L8 15Z" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.success }}</div>
          <div class="stat-label">成功交易</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-orange">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V14H11V15ZM11 11H9V5H11V11Z" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待确认</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-red">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V5H11V15Z" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.failed }}</div>
          <div class="stat-label">失败交易</div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <select v-model="filterForm.type" class="filter-select">
        <option value="">全部类型</option>
        <option value="COPYRIGHT_REGISTER">版权注册</option>
        <option value="LICENSE_APPLY">授权申请</option>
        <option value="LICENSE_APPROVE">授权批准</option>
        <option value="LICENSE_ACTIVATE">授权激活</option>
        <option value="LICENSE_REVOKE">授权撤销</option>
        <option value="ROYALTY_PAYMENT">版税支付</option>
      </select>
      <select v-model="filterForm.status" class="filter-select">
        <option value="">全部状态</option>
        <option value="SUCCESS">成功</option>
        <option value="PENDING">待确认</option>
        <option value="FAILED">失败</option>
      </select>
      <button class="btn btn-search" @click="handleFilter">搜索</button>
      <button class="btn btn-reset" @click="handleReset">重置</button>
    </div>

    <!-- 交易卡片网格 -->
    <div v-loading="loading" class="transactions-grid">
      <div
        v-for="tx in transactionList"
        :key="tx.id"
        class="transaction-card"
        @click="viewDetail(tx)"
      >
        <div class="card-header">
          <div class="tx-icon" :class="`tx-${tx.status.toLowerCase()}`">
            <svg v-if="tx.status === 'SUCCESS'" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M8 15L3 10L4.41 8.59L8 12.17L15.59 4.58L17 6L8 15Z" fill="currentColor"/>
            </svg>
            <svg v-else-if="tx.status === 'PENDING'" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V14H11V15ZM11 11H9V5H11V11Z" fill="currentColor"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V5H11V15Z" fill="currentColor"/>
            </svg>
          </div>
          <div class="tx-type-badge" :class="`type-${tx.type}`">
            {{ getTypeText(tx.type) }}
          </div>
        </div>

        <div class="card-body">
          <div class="tx-hash">
            <span class="tx-label">交易哈希</span>
            <code class="hash-text">{{ formatHash(tx.transactionHash) }}</code>
          </div>
          <div class="tx-info-grid">
            <div class="tx-info-item">
              <span class="info-label">区块高度</span>
              <span class="info-value">#{{ tx.blockNumber }}</span>
            </div>
            <div class="tx-info-item">
              <span class="info-label">发送方</span>
              <span class="info-value">{{ formatAddress(tx.fromAddress) }}</span>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <div class="tx-time">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 0C3.13 0 0 3.13 0 7C0 10.87 3.13 14 7 14C10.87 14 14 10.87 14 7C14 3.13 10.87 0 7 0ZM7.7 10.5H6.3V6.3H7.7V10.5ZM7.7 4.9H6.3V3.5H7.7V4.9Z" fill="currentColor"/>
            </svg>
            {{ formatTime(tx.timestamp) }}
          </div>
          <div class="tx-status-badge" :class="`status-${tx.status.toLowerCase()}`">
            {{ getStatusText(tx.status) }}
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && transactionList.length === 0" class="empty-state">
        <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M40 8L12 20V40C12 55.2 23.6 68.8 40 74C56.4 68.8 68 55.2 68 40V20L40 8Z" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M30 40L36 46L50 32" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h3>暂无交易记录</h3>
        <p>您还没有任何区块链交易</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="pagination.total > 0" class="pagination-bar">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 48, 96]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadList"
        @current-change="loadList"
      />
    </div>

    <!-- 交易详情抽屉 -->
    <el-drawer
      v-model="showDetailDrawer"
      :size="520"
      direction="rtl"
      class="transaction-drawer"
    >
      <template #header>
        <div class="drawer-header-content">
          <div class="header-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M13 3L4 14H12L11 21L20 10H12L13 3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="header-text">
            <h3 class="header-title">交易详情</h3>
            <p class="header-subtitle">Transaction Details</p>
          </div>
        </div>
      </template>

      <div v-if="selectedTransaction" class="drawer-content">
        <!-- 交易状态卡片 -->
        <div class="status-card" :class="`status-${selectedTransaction.status.toLowerCase()}`">
          <div class="status-icon">
            <svg v-if="selectedTransaction.status === 'SUCCESS'" width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M11 21L4 14L6.41 11.59L11 16.17L21.59 5.58L24 8L11 21Z" fill="currentColor"/>
            </svg>
            <svg v-else-if="selectedTransaction.status === 'PENDING'" width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="14" cy="14" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M14 9V14H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <svg v-else width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2C7.37 2 2 7.37 2 14C2 20.63 7.37 26 14 26C20.63 26 26 20.63 26 14C26 7.37 20.63 2 14 2ZM15.5 19H13V17H15.5V19ZM15.5 14.5H13V8H15.5V14.5Z" fill="currentColor"/>
            </svg>
          </div>
          <div class="status-info">
            <div class="status-text">{{ getStatusText(selectedTransaction.status) }}</div>
            <div class="status-desc">{{ getStatusDescription(selectedTransaction.status) }}</div>
          </div>
        </div>

        <!-- 交易基本信息 -->
        <div class="info-section">
          <h4 class="section-title">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 0C4.03 0 0 4.03 0 9C0 13.97 4.03 18 9 18C13.97 18 18 13.97 18 9C18 4.03 13.97 0 9 0ZM9 13.5C8.17 13.5 7.5 12.83 7.5 12C7.5 11.17 8.17 10.5 9 10.5C9.83 10.5 10.5 11.17 10.5 12C10.5 12.83 9.83 13.5 9 13.5ZM10 9H8V4.5H10V9Z" fill="currentColor"/>
            </svg>
            交易信息
          </h4>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">交易哈希</span>
              <div class="info-value-group">
                <code class="info-hash">{{ formatHash(selectedTransaction.transactionHash) }}</code>
                <el-button
                  size="small"
                  text
                  @click="copyHash(selectedTransaction.transactionHash)"
                  class="copy-btn"
                >
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 4H4V9H2V4C2 2.9 2.9 2 4 2H9V4ZM12 12H7V10H12V12ZM10 5H5V10H10V5Z" fill="currentColor"/>
                  </svg>
                </el-button>
              </div>
            </div>
            <div class="info-row">
              <span class="info-label">交易类型</span>
              <el-tag :type="getTypeColor(selectedTransaction.type)" size="small">
                {{ getTypeText(selectedTransaction.type) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="info-label">交易时间</span>
              <span class="info-value">{{ formatFullTime(selectedTransaction.timestamp) }}</span>
            </div>
          </div>
        </div>

        <!-- 区块信息 -->
        <div class="info-section">
          <h4 class="section-title">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M2 2H7V7H2V2ZM11 2H16V7H11V2ZM2 11H7V16H2V11ZM16 11C16 13.76 13.76 16 11 16V14C12.66 14 14 12.66 14 11H16ZM11 8C11 9.66 12.34 11 14 11V13C11.24 13 9 10.76 9 8H11Z" fill="currentColor"/>
            </svg>
            区块信息
          </h4>
          <div class="info-list">
            <div class="info-row interactive" @click="goToBlock(selectedTransaction.blockNumber)">
              <span class="info-label">区块高度</span>
              <div class="info-value-group">
                <span class="info-value highlight">#{{ selectedTransaction.blockNumber }}</span>
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 4H5V8H3V2H9V4ZM11 6V10H7V6H11ZM13 8V14H7V12H11V8H13Z" fill="currentColor"/>
                </svg>
              </div>
            </div>
            <div class="info-row">
              <span class="info-label">Gas 使用量</span>
              <span class="info-value">{{ selectedTransaction.gasUsed || '21,000' }} Gas</span>
            </div>
          </div>
        </div>

        <!-- 参与方信息 -->
        <div class="info-section">
          <h4 class="section-title">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 9C11.21 9 13 7.21 13 5C13 2.79 11.21 1 9 1C6.79 1 5 2.79 5 5C5 7.21 6.79 9 9 9ZM9 11C5.13 11 2 12.34 2 14V17H16V14C16 12.34 12.87 11 9 11Z" fill="currentColor"/>
            </svg>
            参与方
          </h4>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">发送方</span>
              <code class="info-address">{{ formatAddress(selectedTransaction.fromAddress) }}</code>
            </div>
            <div v-if="selectedTransaction.toAddress" class="info-row">
              <span class="info-label">接收方</span>
              <code class="info-address">{{ formatAddress(selectedTransaction.toAddress) }}</code>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="action-section">
          <h4 class="section-title">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 3L5 6.99H7V12H9V6.99H11L9 3ZM5 14V16H13V14H5Z" fill="currentColor"/>
            </svg>
            快捷操作
          </h4>
          <div class="action-buttons">
            <el-button
              type="primary"
              :icon="LinkIcon"
              @click="viewOnBlockchainExplorer"
              class="action-btn primary-action"
            >
              在区块浏览器查看
            </el-button>
            <el-button
              :icon="CopyIcon"
              @click="copyHash(selectedTransaction.transactionHash)"
              class="action-btn"
            >
              复制交易哈希
            </el-button>
          </div>
        </div>

        <!-- 相关链接 -->
        <div v-if="getRelatedLinks(selectedTransaction).length > 0" class="related-section">
          <h4 class="section-title">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 2C2.9 2 2 2.9 2 4V14C2 15.1 2.9 16 4 16H12V14H4V4H14V8H16V4C16 2.9 15.1 2 14 2H4ZM10 7V10H14V7H10ZM15 12V8H18V12H15ZM8 10H5V13H8V10Z" fill="currentColor"/>
            </svg>
            相关资源
          </h4>
          <div class="related-links">
            <router-link
              v-for="link in getRelatedLinks(selectedTransaction)"
              :key="link.text"
              :to="link.to"
              class="related-link"
            >
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 6L6 10M6 6L10 10M14 8C14 11.31 11.31 14 8 14C4.69 14 2 11.31 2 8C2 4.69 4.69 2 8 2C11.31 2 14 4.69 14 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ link.text }}
            </router-link>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { Link, CopyDocument } from '@element-plus/icons-vue'
import { getTransactionStats, getTransactionList, exportTransactions as exportTransactionsAPI } from '@/api/transaction'

const router = useRouter()
const loading = ref(false)
const showDetailDrawer = ref(false)
const selectedTransaction = ref(null)
const transactionList = ref([])

// 图标
const LinkIcon = Link
const CopyIcon = CopyDocument

const stats = ref({
  total: 0,
  success: 0,
  pending: 0,
  failed: 0
})

const filterForm = reactive({
  type: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

const formatHash = (hash) => {
  if (!hash) return '-'
  return `${hash.substring(0, 10)}...${hash.substring(hash.length - 8)}`
}

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 8)}...${address.substring(address.length - 6)}`
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return dayjs(timestamp * 1000).format('YYYY-MM-DD HH:mm')
}

const formatFullTime = (timestamp) => {
  if (!timestamp) return '-'
  return dayjs(timestamp * 1000).format('YYYY-MM-DD HH:mm:ss')
}

const getTypeText = (type) => {
  const types = {
    'COPYRIGHT_REGISTER': '版权注册',
    'LICENSE_APPLY': '授权申请',
    'LICENSE_APPROVE': '授权批准',
    'LICENSE_ACTIVATE': '授权激活',
    'LICENSE_REVOKE': '授权撤销',
    'ROYALTY_PAYMENT': '版税支付',
    'COPYRIGHT_TRANSFER': '版权转让'
  }
  return types[type] || type
}

const getStatusText = (status) => {
  const statuses = {
    'SUCCESS': '成功',
    'PENDING': '待确认',
    'FAILED': '失败'
  }
  return statuses[status] || status
}

const handleFilter = () => {
  pagination.pageNum = 1
  loadList()
}

const handleReset = () => {
  filterForm.type = ''
  filterForm.status = ''
  handleFilter()
}

// 加载统计数据
const loadStats = async () => {
  try {
    const data = await getTransactionStats()
    stats.value = data
  } catch (error) {
    console.error('获取交易统计失败:', error)
    // 不显示错误消息，避免打扰用户
  }
}

const loadList = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    // 添加筛选条件
    if (filterForm.type) {
      params.type = filterForm.type
    }
    if (filterForm.status) {
      params.status = filterForm.status
    }

    // 调用真实 API
    const data = await getTransactionList(params)
    transactionList.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载交易列表失败:', error)
    ElMessage.error(error.message || '加载交易列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (tx) => {
  selectedTransaction.value = tx
  showDetailDrawer.value = true
}

const copyHash = (hash) => {
  navigator.clipboard.writeText(hash)
  ElMessage.success('交易哈希已复制到剪贴板')
}

const exportTransactions = async () => {
  try {
    const params = {}
    if (filterForm.type) params.type = filterForm.type
    if (filterForm.status) params.status = filterForm.status

    const res = await exportTransactionsAPI(params)

    // 处理文件下载
    if (res instanceof Blob) {
      const url = window.URL.createObjectURL(res)
      const link = document.createElement('a')
      link.href = url
      link.download = `transactions_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 获取交易类型的颜色
const getTypeColor = (type) => {
  const colors = {
    'COPYRIGHT_REGISTER': 'success',
    'LICENSE_APPLY': 'warning',
    'LICENSE_APPROVE': 'primary',
    'LICENSE_ACTIVATE': 'success',
    'LICENSE_REVOKE': 'danger',
    'ROYALTY_PAYMENT': 'info',
    'COPYRIGHT_TRANSFER': 'warning',
    'USER_REGISTER': 'success'
  }
  return colors[type] || 'info'
}

// 获取状态描述
const getStatusDescription = (status) => {
  const descriptions = {
    'SUCCESS': '交易已成功确认并写入区块链',
    'PENDING': '交易正在等待区块确认',
    'FAILED': '交易执行失败，请检查相关参数'
  }
  return descriptions[status] || ''
}

// 跳转到区块详情
const goToBlock = (blockNumber) => {
  router.push({ name: 'BlockchainExplorer', query: { block: blockNumber } })
  showDetailDrawer.value = false
}

// 在区块浏览器查看
const viewOnBlockchainExplorer = () => {
  if (!selectedTransaction.value) return

  // 跳转到区块链浏览器页面，并传递交易哈希参数
  router.push({
    name: 'BlockchainExplorer',
    query: { tx: selectedTransaction.value.transactionHash }
  })
  showDetailDrawer.value = false
  ElMessage.success('正在跳转到区块浏览器...')
}

// 获取相关链接
const getRelatedLinks = (tx) => {
  const links = []

  // 根据交易类型添加相关链接
  if (tx.copyrightId) {
    links.push({
      text: '查看关联版权',
      to: { name: 'CopyrightDetail', params: { id: tx.copyrightId } }
    })
  }

  if (tx.licenseId) {
    links.push({
      text: '查看关联授权',
      to: { name: 'LicenseDetail', params: { id: tx.licenseId } }
    })
  }

  // 根据交易类型添加默认链接
  switch (tx.type) {
    case 'COPYRIGHT_REGISTER':
      if (tx.copyrightId) {
        links.push({
          text: '查看版权详情',
          to: { name: 'CopyrightDetail', params: { id: tx.copyrightId } }
        })
      }
      break
    case 'LICENSE_APPLY':
    case 'LICENSE_APPROVE':
      if (tx.licenseId) {
        links.push({
          text: '查看授权详情',
          to: { name: 'LicenseDetail', params: { id: tx.licenseId } }
        })
      }
      break
    case 'ROYALTY_PAYMENT':
      if (tx.copyrightId) {
        links.push({
          text: '查看版税历史',
          to: { name: 'Royalties' }
        })
      }
      break
  }

  return links
}

onMounted(() => {
  loadStats()
  loadList()
})
</script>

<style lang="scss" scoped>
.transactions-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  gap: 24px;
}

.header-content {
  flex: 1;

  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  .page-subtitle {
    font-size: 15px;
    color: var(--text-secondary);
    margin: 0;
  }
}

.btn {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;

  &.btn-primary {
    background: var(--gradient-primary);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
  }

  &.btn-secondary {
    background: var(--bg-tertiary);
    color: var(--text-primary);

    &:hover {
      background: var(--border-primary);
    }
  }

  &.btn-search {
    padding: 12px 28px;
    background: var(--gradient-primary);
    color: white;
  }

  &.btn-reset {
    padding: 12px 24px;
    background: var(--bg-tertiary);
    color: var(--text-primary);
    border: 1px solid var(--border-primary);

    &:hover {
      background: var(--border-primary);
    }
  }
}

.stats-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.stat-item {
  flex: 1;
  min-width: 200px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;

    &.stat-purple { background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15)); color: #667eea; }
    &.stat-green { background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(16, 185, 129, 0.15)); color: #22c55e; }
    &.stat-orange { background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(249, 168, 37, 0.15)); color: #f97316; }
    &.stat-red { background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.15)); color: #ef4444; }
  }

  .stat-info {
    .stat-value { font-size: 24px; font-weight: 700; color: var(--text-primary); }
    .stat-label { font-size: 13px; color: var(--text-tertiary); margin-top: 4px; }
  }
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  background: var(--bg-card);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid var(--border-primary);
  flex-wrap: wrap;
}

.filter-select {
  flex: 1;
  min-width: 150px;
  padding: 12px 16px;
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  background: var(--bg-card);
  color: var(--text-primary);
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: var(--color-primary-600);
  }
}

.transactions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
  min-height: 200px;
}

.transaction-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary-600);
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-tertiary);
  }

  .tx-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;

    &.tx-success { background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(16, 185, 129, 0.15)); color: #22c55e; }
    &.tx-pending { background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(249, 168, 37, 0.15)); color: #f97316; }
    &.tx-failed { background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.15)); color: #ef4444; }
  }

  .tx-type-badge {
    padding: 6px 12px;
    background: var(--bg-tertiary);
    color: var(--text-secondary);
    font-size: 12px;
    font-weight: 600;
    border-radius: 6px;
  }

  .card-body {
    padding: 20px;
  }

  .tx-hash {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;

    .tx-label { font-size: 12px; color: var(--text-secondary); }
    .hash-text { font-family: monospace; font-size: 13px; color: var(--color-primary-600); background: rgba(102, 126, 234, 0.08); padding: 8px 12px; border-radius: 6px; word-break: break-all; }
  }

  .tx-info-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .tx-info-item {
    display: flex;
    justify-content: space-between;

    .info-label { font-size: 13px; color: var(--text-secondary); }
    .info-value { font-size: 14px; font-weight: 600; color: var(--text-primary); }
  }

  .card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    border-top: 1px solid var(--border-tertiary);
  }

  .tx-time {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: var(--text-tertiary);

    svg { color: var(--text-tertiary); }
  }

  .tx-status-badge {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 600;

    &.status-success { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
    &.status-pending { background: rgba(249, 115, 22, 0.1); color: #f97316; }
    &.status-failed { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
  }
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;

  svg { margin-bottom: 24px; }
  h3 { font-size: 20px; font-weight: 600; color: var(--text-primary); margin: 0 0 8px 0; }
  p { font-size: 15px; color: var(--text-secondary); margin: 0; }
}

.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

// 交易详情抽屉样式
.transaction-drawer {
  :deep(.el-drawer__header) {
    margin-bottom: 0;
    padding: 20px 24px;
    border-bottom: 1px solid #e5e7eb;
  }

  :deep(.el-drawer__body) {
    padding: 0;
    overflow-y: auto;
  }
}

.drawer-header-content {
  display: flex;
  align-items: center;
  gap: 16px;

  .header-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    color: #667eea;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .header-text {
    flex: 1;

    .header-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 4px 0;
    }

    .header-subtitle {
      font-size: 13px;
      color: #9ca3af;
      margin: 0;
    }
  }
}

.drawer-content {
  padding: 24px;
}

// 状态卡片
.status-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;

  &.status-success {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.08), rgba(16, 185, 129, 0.08));
    border: 1px solid rgba(34, 197, 94, 0.2);

    .status-icon { color: #22c55e; }
    .status-text { color: #22c55e; }
  }

  &.status-pending {
    background: linear-gradient(135deg, rgba(249, 115, 22, 0.08), rgba(249, 168, 37, 0.08));
    border: 1px solid rgba(249, 115, 22, 0.2);

    .status-icon { color: #f97316; }
    .status-text { color: #f97316; }
  }

  &.status-failed {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.08), rgba(220, 38, 38, 0.08));
    border: 1px solid rgba(239, 68, 68, 0.2);

    .status-icon { color: #ef4444; }
    .status-text { color: #ef4444; }
  }

  .status-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: var(--bg-card);
  }

  .status-info {
    flex: 1;

    .status-text {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .status-desc {
      font-size: 13px;
      color: #6b7280;
    }
  }
}

// 信息区块
.info-section {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;

  svg {
    color: #9ca3af;
  }
}

.info-list {
  background: #f9fafb;
  border-radius: 10px;
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;

  &:last-child {
    border-bottom: none;
  }

  &.interactive {
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #f3f4f6;
    }

    .info-value-group {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }

  .info-label {
    font-size: 13px;
    color: #6b7280;
  }

  .info-value {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);

    &.highlight {
      color: #667eea;
      font-weight: 600;
    }
  }

  .info-value-group {
    display: flex;
    align-items: center;
    gap: 8px;

    .copy-btn {
      padding: 4px;
      color: #9ca3af;

      &:hover {
        color: #667eea;
      }
    }
  }

  .info-hash {
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 12px;
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    padding: 4px 8px;
    border-radius: 4px;
  }

  .info-address {
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 12px;
    color: var(--text-secondary);
    background: var(--bg-card);
    padding: 4px 8px;
    border-radius: 4px;
    border: 1px solid var(--border-primary);
  }
}

// 操作区块
.action-section {
  margin-bottom: 24px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .action-btn {
    width: 100%;
    justify-content: center;
    border-radius: 10px;
    padding: 12px 20px;
    font-weight: 500;

    &.primary-action {
      background: linear-gradient(135deg, #667eea, #764ba2);
      border: none;
      color: white;

      &:hover {
        opacity: 0.9;
      }
    }
  }
}

// 相关链接
.related-section {
  margin-bottom: 24px;
}

.related-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.related-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 10px;
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    background: rgba(102, 126, 234, 0.08);
    transform: translateX(4px);
  }

  svg {
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  .page-header { flex-direction: column; align-items: stretch; .btn { width: 100%; justify-content: center; } }
  .filter-bar { flex-direction: column; .filter-select, .btn { width: 100%; } }
  .stats-bar { flex-direction: column; }
  .transactions-grid { grid-template-columns: 1fr; }
  .detail-grid { grid-template-columns: 1fr; }
}
</style>
