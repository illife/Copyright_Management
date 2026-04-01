<template>
  <div class="blockchain-explorer">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">🔗 区块链浏览器</h1>
        <p class="page-subtitle">探索 FISCO BCOS 联盟链的区块、交易和节点信息</p>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchQuery"
          placeholder="搜索区块高度 / 交易哈希 / 地址"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span style="font-size: 16px;">🔍</span>
          </template>
        </el-input>
        <el-button @click="fetchData">🔄 刷新</el-button>
      </div>
    </div>

    <!-- 网络统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
          <div class="stat-label">当前区块高度</div>
          <div class="stat-value">{{ formatNumber(networkStats.blockHeight) }}</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">📄</div>
        <div class="stat-content">
          <div class="stat-label">总交易数</div>
          <div class="stat-value">{{ formatNumber(networkStats.totalTransactions) }}</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">🖥️</div>
        <div class="stat-content">
          <div class="stat-label">活跃节点</div>
          <div class="stat-value">{{ networkStats.activeNodes }} / {{ networkStats.totalNodes }}</div>
        </div>
      </div>
      <div class="stat-card info">
        <div class="stat-icon">⏱️</div>
        <div class="stat-content">
          <div class="stat-label">平均出块时间</div>
          <div class="stat-value">{{ networkStats.avgBlockTime }}s</div>
        </div>
      </div>
    </div>

    <!-- 节点状态监控 -->
    <div class="nodes-section">
      <div class="section-header">
        <h3 class="section-title">🖥️ 节点状态监控</h3>
        <el-button type="primary" size="small" @click="fetchNodes">🔄 刷新节点</el-button>
      </div>
      <div class="nodes-grid">
        <div
          v-for="node in nodes"
          :key="node.id"
          class="node-card"
          :class="{ 'node-inactive': node.status !== 'ACTIVE' }"
        >
          <div class="node-header">
            <div class="node-status" :class="`status-${node.status?.toLowerCase() || 'unknown'}`"></div>
            <div class="node-name">{{ node.name || '未命名节点' }}</div>
          </div>
          <div class="node-body">
            <div class="node-info">
              <span class="node-label">地址:</span>
              <span class="node-value font-mono">{{ node.address }}</span>
            </div>
            <div class="node-info">
              <span class="node-label">区块高度:</span>
              <span class="node-value">#{{ node.blockNumber }}</span>
            </div>
            <div class="node-info">
              <span class="node-label">连接数:</span>
              <span class="node-value">{{ node.peerCount }}</span>
            </div>
            <div class="node-info">
              <span class="node-label">群组数:</span>
              <span class="node-value">{{ node.groupCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 区块列表 -->
    <div class="blocks-section">
      <div class="section-header">
        <h3 class="section-title">📦 最新区块</h3>
        <el-button type="primary" size="small" @click="fetchBlocks">加载更多</el-button>
      </div>
      <div class="blocks-table">
        <el-table
          v-loading="loading.blocks"
          :data="blocks"
          stripe
          @row-click="handleBlockClick"
          style="width: 100%; max-width: 100%; cursor: pointer;"
          :flexible="true"
        >
          <el-table-column prop="number" label="区块高度" width="120">
            <template #default="{ row }">
              <span class="block-number">#{{ row.number }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="hash" label="区块哈希" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="hash-text font-mono">{{ formatHash(row.hash) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="transactionsCount" label="交易数" width="90" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.transactionsCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="gasUsed" label="Gas使用" width="110">
            <template #default="{ row }">
              <span v-if="row.gasUsageRate !== undefined" class="gas-rate">
                {{ row.gasUsageRate }}
              </span>
              <span v-else class="gas-text">
                {{ formatGas(row.gasUsed) }} / {{ formatGas(row.gasLimit) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="miner" label="矿工" min-width="140" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="address-text font-mono">{{ formatAddress(row.miner) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="timestamp" label="时间" width="150">
            <template #default="{ row }">
              {{ formatTimestamp(row.timestamp) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" link @click.stop="handleBlockClick(row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>

    <!-- 区块详情对话框 -->
    <el-dialog
      v-model="blockDetailVisible"
      :title="`区块详情 #${currentBlock?.number || ''}`"
      width="900px"
      :close-on-click-modal="false"
      class="block-detail-dialog"
    >
      <div v-if="currentBlock" class="block-detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4 class="section-title">📋 基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">区块高度:</span>
              <span class="detail-value">{{ currentBlock.number }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">时间戳:</span>
              <span class="detail-value">{{ currentBlock.timestamp ? formatTimestamp(currentBlock.timestamp) : '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">交易数量:</span>
              <span class="detail-value">{{ currentBlock.transactionsCount }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">区块大小:</span>
              <span class="detail-value">{{ formatBytes(currentBlock.size) }}</span>
            </div>
          </div>
        </div>

        <!-- Gas 信息 -->
        <div class="detail-section">
          <h4 class="section-title">⛽ Gas 信息</h4>
          <div class="gas-info">
            <div class="gas-bar">
              <div class="gas-label">Gas 使用量:</div>
              <div class="gas-value">
                <span class="gas-number">{{ formatGas(currentBlock.gasUsed) }}</span>
                <span class="gas-total"> / {{ formatGas(currentBlock.gasLimit) }}</span>
              </div>
              <div class="gas-percent">
                {{ ((currentBlock.gasUsed / currentBlock.gasLimit) * 100).toFixed(2) }}%
              </div>
            </div>
            <el-progress
              :percentage="(currentBlock.gasUsed / currentBlock.gasLimit) * 100"
              :stroke-width="12"
              :color="getGasColor(currentBlock.gasUsed / currentBlock.gasLimit)"
            />
          </div>
        </div>

        <!-- 哈希信息 -->
        <div class="detail-section">
          <h4 class="section-title">🔗 哈希信息</h4>
          <div class="hash-grid">
            <div class="hash-item">
              <span class="hash-label">区块哈希:</span>
              <span class="hash-value font-mono">{{ currentBlock.hash }}</span>
            </div>
            <div class="hash-item">
              <span class="hash-label">父区块哈希:</span>
              <span class="hash-value font-mono">{{ currentBlock.parentHash }}</span>
            </div>
            <div class="hash-item">
              <span class="hash-label">矿工地址:</span>
              <span class="hash-value font-mono">{{ currentBlock.miner }}</span>
            </div>
          </div>
        </div>

        <!-- 区块交易列表 -->
        <div class="detail-section">
          <h4 class="section-title">📝 区块交易 ({{ currentBlock.transactionsCount }})</h4>
          <div class="transactions-list">
            <el-table
              :data="currentBlock.transactions || []"
              size="small"
              max-height="300"
            >
              <el-table-column prop="hash" label="交易哈希" min-width="150">
                <template #default="{ row }">
                  <span class="hash-text-small font-mono">{{ formatHash(row.hash) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="from" label="发送方" width="120">
                <template #default="{ row }">
                  <span class="font-mono">{{ formatAddress(row.from) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="to" label="接收方" width="120">
                <template #default="{ row }">
                  <span class="font-mono">{{ formatAddress(row.to) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="120">
                <template #default="{ row }">
                  <el-tag size="small">{{ getTypeText(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="gasUsed" label="Gas" width="80" align="right">
                <template #default="{ row }">
                  {{ formatGas(row.gasUsed) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getBlockList,
  getBlockDetail,
  getNetworkStats,
  getNodeList
} from '@/api/blockchain'

const router = useRouter()
const route = useRoute()
const searchQuery = ref('')
const loading = reactive({
  blocks: false,
  nodes: false
})

const networkStats = ref({
  blockHeight: 0,
  totalTransactions: 0,
  totalNodes: 0,
  activeNodes: 0,
  avgBlockTime: 0,
  avgGasPrice: 0
})

const blocks = ref([])
const nodes = ref([])
const blockDetailVisible = ref(false)
const currentBlock = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取网络统计
const fetchStats = async () => {
  try {
    const result = await getNetworkStats()
    // 处理可能的数据格式
    const data = result.data !== undefined ? result.data : result
    networkStats.value = data
    console.log('网络统计加载成功:', data)
  } catch (error) {
    console.error('获取网络统计失败:', error)
    ElMessage.error('获取网络统计失败')
  }
}

// 获取节点列表
const fetchNodes = async () => {
  loading.nodes = true
  try {
    const result = await getNodeList()
    // mockResponse 直接返回 { code, msg, data }，需要手动提取 data
    const data = result.data || result
    nodes.value = data
    console.log('节点列表原始响应:', result)
    console.log('节点列表 data:', data)
    console.log('nodes.value:', nodes.value)
    console.log('节点列表长度:', nodes.value?.length)
    console.log('是否为数组:', Array.isArray(nodes.value))
  } catch (error) {
    console.error('获取节点列表失败:', error)
    ElMessage.error('获取节点列表失败')
  } finally {
    loading.nodes = false
  }
}

// 获取区块列表
const fetchBlocks = async () => {
  loading.blocks = true
  try {
    console.log('正在获取区块列表，参数:', { page: pagination.page, size: pagination.size })
    const data = await getBlockList({
      page: pagination.page,
      size: pagination.size
    })
    blocks.value = data.list || []
    pagination.total = data.total || 0
    console.log('区块列表加载成功，共', blocks.value.length, '条记录')
  } catch (error) {
    console.error('获取区块列表失败:', error)
    ElMessage.error('获取区块列表失败')
  } finally {
    loading.blocks = false
  }
}

// 获取所有数据
const fetchData = async () => {
  await Promise.all([
    fetchStats(),
    fetchNodes(),
    fetchBlocks()
  ])
}

// 处理搜索
const handleSearch = () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }

  const query = searchQuery.value.trim()

  // 判断是区块高度还是交易哈希
  const blockNumber = parseInt(query)
  if (!isNaN(blockNumber) && blockNumber > 0) {
    // 是区块高度
    viewBlockDetail(blockNumber)
  } else if (query.startsWith('0x') && query.length === 66) {
    // 是交易哈希
    ElMessage.info('交易详情功能开发中，请通过交易记录查看')
  } else {
    ElMessage.warning('请输入有效的区块高度或交易哈希')
  }
}

// 查看区块详情
const viewBlockDetail = async (blockNumber) => {
  try {
    console.log('查看区块详情:', blockNumber)
    const data = await getBlockDetail(blockNumber)
    currentBlock.value = data
    blockDetailVisible.value = true

    // 清除 URL 中的查询参数
    router.replace({ query: {} })
  } catch (error) {
    console.error('获取区块详情失败:', error)
    ElMessage.error('获取区块详情失败')
  }
}

// 处理区块点击
const handleBlockClick = async (row) => {
  viewBlockDetail(row.number)
}

// 分页处理
const handlePageChange = (page) => {
  pagination.page = page
  fetchBlocks()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchBlocks()
}

// 格式化函数
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

const formatHash = (hash) => {
  if (!hash) return '-'
  return `${hash.slice(0, 10)}...${hash.slice(-8)}`
}

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.slice(0, 8)}...${address.slice(-6)}`
}

const formatTimestamp = (timestamp) => {
  if (!timestamp) return '-'
  const date = new Date(timestamp * 1000)
  return date.toLocaleString('zh-CN')
}

const formatGas = (gas) => {
  if (!gas && gas !== 0) return '0'
  // 如果是0，显示"0"而不是"0.00M"
  if (gas === 0) return '0'
  // 大于1M才显示M单位
  if (gas >= 1000000) {
    return (parseInt(gas) / 1000000).toFixed(2) + 'M'
  }
  // 小于1M显示原始数字
  return parseInt(gas).toLocaleString()
}

const formatBytes = (bytes) => {
  if (!bytes && bytes !== 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

const getGasColor = (percent) => {
  if (percent < 0.5) return '#67c23a'
  if (percent < 0.8) return '#e6a23c'
  return '#f56c6c'
}

const getTypeText = (type) => {
  const types = {
    'COPYRIGHT_REGISTER': '版权注册',
    'LICENSE_APPLY': '授权申请',
    'LICENSE_APPROVE': '授权审批',
    'LICENSE_ACTIVATE': '授权激活',
    'LICENSE_REVOKE': '授权撤销',
    'ROYALTY_PAYMENT': '版税支付'
  }
  return types[type] || type
}

// 组件挂载
onMounted(async () => {
  console.log('区块链浏览器组件已挂载')

  // 先加载基础数据
  await fetchData()

  // 检查 URL 查询参数
  const { block, tx } = route.query

  if (block) {
    // 如果有 block 参数，显示区块详情
    const blockNumber = parseInt(block)
    if (!isNaN(blockNumber) && blockNumber > 0) {
      console.log('从 URL 参数加载区块:', blockNumber)
      viewBlockDetail(blockNumber)
    }
  } else if (tx) {
    // 如果有 tx 参数，提示用户
    ElMessage.info({
      message: '交易详情功能正在开发中，请通过交易记录页面查看',
      duration: 3000
    })
  }
})
</script>

<style lang="scss" scoped>
.blockchain-explorer {
  padding: 20px;
  max-width: 100%;
  overflow-x: hidden;
  overflow-y: visible;
  width: 100%;

  // 确保所有子元素不会超出
  * {
    max-width: 100%;
    box-sizing: border-box;
  }

  // 强制Element Plus组件不超出
  :deep(.el-table) {
    max-width: 100%;
    overflow: hidden;
  }

  :deep(.el-dialog__wrapper) {
    .el-dialog {
      max-width: 95vw;
      overflow: hidden;
    }
  }

  :deep(.el-dialog__body) {
    overflow-x: hidden;
    max-width: 100%;
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;

  .header-content {
    flex: 1;
    min-width: 0;

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary);
      margin: 0 0 8px 0;
      word-break: break-word;
    }

    .page-subtitle {
      font-size: 14px;
      color: var(--text-tertiary);
      margin: 0;
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;

    .search-input {
      width: 360px;
      max-width: 100%;
    }
  }
}

// 统计卡片
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
  max-width: 100%;
  overflow: hidden;

  .stat-card {
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 12px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    min-width: 0;
    overflow: hidden;

    &:hover {
      transform: translateY(-2px);
      box-shadow: var(--shadow-md);
    }

    &.primary {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      border-color: rgba(102, 126, 234, 0.2);
      .stat-icon { font-size: 32px; }
    }

    &.success {
      background: linear-gradient(135deg, rgba(103, 194, 58, 0.1), rgba(16, 185, 129, 0.1));
      border-color: rgba(103, 194, 58, 0.2);
      .stat-icon { font-size: 32px; }
    }

    &.warning {
      background: linear-gradient(135deg, rgba(230, 162, 60, 0.1), rgba(249, 115, 22, 0.1));
      border-color: rgba(230, 162, 60, 0.2);
      .stat-icon { font-size: 32px; }
    }

    &.info {
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(6, 182, 212, 0.1));
      border-color: rgba(59, 130, 246, 0.2);
      .stat-icon { font-size: 32px; }
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      flex-shrink: 0;
    }

    .stat-content {
      flex: 1;
      min-width: 0;
      overflow: hidden;

      .stat-label {
        font-size: 13px;
        color: var(--text-tertiary);
        margin-bottom: 4px;
        word-break: break-word;
      }

      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--text-primary);
        word-break: break-word;
      }
    }
  }
}

// 节点监控
.nodes-section {
  margin-bottom: 32px;
  max-width: 100%;
  overflow: hidden;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 12px;

    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .nodes-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 16px;
    max-width: 100%;
    overflow: hidden;
  }

  .node-card {
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 12px;
    padding: 20px;
    transition: all 0.3s ease;
    overflow: hidden;
    max-width: 100%;

    &:hover {
      border-color: #67c23a;
      box-shadow: var(--shadow-md);
    }

    &.node-inactive {
      opacity: 0.6;
    }

    .node-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;

      .node-status {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        flex-shrink: 0;

        &.status-active {
          background: #67c23a;
          box-shadow: 0 0 8px rgba(103, 194, 58, 0.4);
        }

        &.status-inactive {
          background: #f56c6c;
        }
      }

      .node-name {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .node-body {
      .node-info {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        font-size: 13px;
        gap: 8px;

        &:last-child {
          margin-bottom: 0;
        }

        .node-label {
          color: var(--text-tertiary);
          flex-shrink: 0;
        }

        .node-value {
          color: var(--text-primary);
          font-weight: 500;
          word-break: break-all;
          overflow-wrap: break-word;
          text-align: right;
          flex: 1;
          min-width: 0;
        }
      }
    }
  }
}

// 区块列表
.blocks-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .blocks-table {
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 12px;
    padding: 20px;
    overflow: hidden;
    max-width: 100%;
    width: 100%;

    // 确保表格不超出容器
    :deep(.el-table) {
      width: 100% !important;
      max-width: 100% !important;
      overflow: hidden;
      table-layout: fixed;

      .el-table__body-wrapper {
        overflow-x: auto;
      }

      .el-table__header,
      .el-table__body {
        width: 100% !important;
      }

      // 确保单元格内容正确换行
      .cell {
        overflow: hidden;
        text-overflow: ellipsis;
        word-break: break-word;
      }
    }

    .block-number {
      font-family: 'Courier New', monospace;
      font-size: 14px;
      font-weight: 600;
      color: var(--accent-primary);
    }

    .hash-text {
      font-family: 'Courier New', monospace;
      font-size: 12px;
      color: var(--text-secondary);
      word-break: break-all;
      overflow-wrap: break-word;
      display: block;
      line-height: 1.4;
    }

    .address-text {
      font-family: 'Courier New', monospace;
      font-size: 12px;
      color: var(--text-secondary);
      word-break: break-all;
      overflow-wrap: break-word;
      display: block;
    }

    .gas-text {
      font-size: 12px;
      word-break: break-all;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

// 区块详情对话框
.block-detail-content {
  overflow: hidden;
  max-width: 100%;

  .detail-section {
    margin-bottom: 32px;
    overflow: hidden;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 20px 0;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--border-primary);
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      min-width: 0;

      .detail-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        min-width: 0;

        .detail-label {
          font-size: 13px;
          color: var(--text-tertiary);
        }

        .detail-value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
          word-break: break-word;
          overflow-wrap: break-word;
        }
      }
    }

    .gas-info {
      .gas-bar {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        font-size: 14px;

        .gas-label {
          color: var(--text-tertiary);
        }

        .gas-value {
          flex: 1;
          display: flex;
          justify-content: flex-end;

          .gas-number {
            font-weight: 600;
            color: var(--text-primary);
          }

          .gas-total {
            color: var(--text-tertiary);
          }
        }

        .gas-percent {
          font-weight: 600;
          color: var(--accent-primary);
        }
      }
    }

    .hash-grid {
      display: flex;
      flex-direction: column;
      gap: 16px;
      overflow: hidden;

      .hash-item {
        display: flex;
        flex-direction: column;
        gap: 6px;
        min-width: 0;

        .hash-label {
          font-size: 13px;
          color: var(--text-tertiary);
        }

        .hash-value {
          font-size: 11px;
          color: var(--text-primary);
          background: var(--bg-tertiary);
          padding: 10px;
          border-radius: 6px;
          word-break: break-all;
          overflow-wrap: break-word;
          word-break: break-word;
          line-height: 1.4;
          font-family: 'Courier New', monospace;
          max-width: 100%;
          overflow: hidden;
        }
      }
    }

    .transactions-list {
      .hash-text-small {
        font-family: 'Courier New', monospace;
        font-size: 11px;
        color: var(--accent-primary);
      }
    }
  }
}

// 工具类
.font-mono {
  font-family: 'Courier New', monospace;
  word-break: break-all;
  overflow-wrap: break-word;
}

.gas-rate {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

// 响应式
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;

    .header-actions {
      .search-input {
        width: 100%;
      }
    }
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .nodes-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr !important;
  }

  :deep(.el-dialog) {
    max-width: 95vw !important;
    margin: 0 auto !important;
  }

  :deep(.el-dialog__body) {
    overflow-x: hidden !important;
    max-width: 100% !important;
  }
}
</style>
