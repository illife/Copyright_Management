<template>
  <div class="royalty-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">版税管理</h1>
        <p class="page-subtitle">管理和查看版权的版税支付记录</p>
      </div>
      <button
        v-if="canRecordRoyalty"
        class="btn btn-primary"
        @click="showRecordDialog = true"
      >
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 1V13M9 13L5 9M9 13L13 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M1 13V15C1 16.1 1.9 17 3 17H15C16.1 17 17 16.1 17 15V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        记录版税
      </button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <svg class="filter-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M2 4H16M2 9H16M2 14H16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <select v-model="filterForm.copyrightId" class="filter-select">
          <option value="">版权ID筛选</option>
        </select>
      </div>
      <div class="filter-group">
        <svg class="filter-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 0C4.58 0 0 4.58 0 10C0 15.42 4.58 20 9 20C13.42 20 18 15.42 18 10C18 4.58 13.42 0 9 0ZM7 15L4 12L5.41 10.59L7 12.17L11.59 7.58L13 9L7 15Z" fill="currentColor"/>
        </svg>
        <input
          v-model="filterForm.payerAddress"
          type="text"
          class="filter-input"
          placeholder="支付方地址"
        />
      </div>
      <div class="filter-group">
        <svg class="filter-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 1L6 7M6 7L12 13M6 7H18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <input
          v-model="filterForm.payeeAddress"
          type="text"
          class="filter-input"
          placeholder="收款方地址"
        />
      </div>
      <div class="filter-actions">
        <button class="btn btn-search" @click="handleFilter">搜索</button>
        <button class="btn btn-reset" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon stat-purple">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14 2H6C4.9 2 4 2.9 4 4V16C4 17.1 4.9 18 6 18H14C15.1 18 16 17.1 16 16V4C16 2.9 15.1 2 14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalCount }}</div>
          <div class="stat-label">总记录数</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-green">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 0L4 5V10C4 14.42 6.69 18 10 19C13.31 18 16 14.42 16 10V5L10 0Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ stats.totalAmount }}</div>
          <div class="stat-label">总金额</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-orange">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 1L3 5V10C3 14.42 5.84 19.27 10 20C14.16 19.27 17 14.42 17 10V5L10 1ZM10 11.5H7V10H9V6H11V10H13V11.5H10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.payCount }}</div>
          <div class="stat-label">支付次数</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-blue">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V14H11V15ZM13 9L12 10L11 9V11H9V9L8 10L7 9L8 8L9 9V7H11V9L12 8L13 9Z" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.currencyCount }}</div>
          <div class="stat-label">货币种类</div>
        </div>
      </div>
    </div>

    <!-- 版税记录卡片网格 -->
    <div v-loading="loading" class="royalty-grid">
      <div
        v-for="item in royaltyList"
        :key="item.id"
        class="royalty-card"
      >
        <div class="card-header">
          <div class="card-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V14H11V15ZM12.5 10H11V12H9V10H7.5L10 7.5L12.5 10Z" fill="currentColor"/>
            </svg>
          </div>
          <div class="card-title">
            <span class="record-id">记录 #{{ item.id }}</span>
          </div>
        </div>

        <div class="card-body">
          <div class="card-row">
            <span class="row-label">版权标题</span>
            <span class="row-value">{{ item.copyrightTitle || '-' }}</span>
          </div>
          <div class="card-row">
            <span class="row-label">版权ID</span>
            <span class="row-value">{{ item.copyrightId || '-' }}</span>
          </div>
          <div class="card-divider"></div>
          <div class="card-row">
            <span class="row-label">支付方</span>
            <span class="row-value">{{ formatAddress(item.payerAddress) }}</span>
          </div>
          <div class="card-row">
            <span class="row-label">收款方</span>
            <span class="row-value">{{ formatAddress(item.payeeAddress) }}</span>
          </div>
          <div class="card-divider"></div>
          <div class="amount-section">
            <div class="amount-label">支付金额</div>
            <div class="amount-value">
              <span class="amount-number">{{ item.amount }}</span>
              <span class="amount-currency">{{ item.currency }}</span>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <div class="footer-time">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 0C3.13 0 0 3.13 0 7C0 10.87 3.13 14 7 14C10.87 14 14 10.87 14 7C14 3.13 10.87 0 7 0ZM7.7 10.5H6.3V6.3H7.7V10.5ZM7.7 4.9H6.3V3.5H7.7V4.9Z" fill="currentColor"/>
            </svg>
            {{ formatFullTime(item.timestamp) }}
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && royaltyList.length === 0" class="empty-state">
        <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M40 8L12 20V40C12 55.2 23.6 68.8 40 74C56.4 68.8 68 55.2 68 40V20L40 8Z" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M30 40L36 46L50 32" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h3>暂无版税记录</h3>
        <p v-if="canRecordRoyalty">开始记录您的第一笔版税支付</p>
        <button v-if="canRecordRoyalty" class="btn btn-primary" @click="showRecordDialog = true">
          记录版税
        </button>
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

    <!-- 记录版税对话框 -->
    <div v-if="showRecordDialog" class="dialog-overlay" @click.self="showRecordDialog = false">
      <div class="dialog-container">
        <div class="dialog-header">
          <h2 class="dialog-title">记录版税支付</h2>
          <button class="dialog-close" @click="showRecordDialog = false">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M15 5L5 15M5 5L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <div class="dialog-body">
          <div class="form-group">
            <label class="field-label">版权ID *</label>
            <input
              v-model="recordForm.copyrightId"
              type="text"
              class="field-input"
              placeholder="请输入版权ID（数据库ID）"
            />
            <div v-if="errors.copyrightId" class="field-error">{{ errors.copyrightId }}</div>
          </div>

          <div class="form-group">
            <label class="field-label">支付方地址 *</label>
            <input
              v-model="recordForm.payer"
              type="text"
              class="field-input"
              placeholder="0x..."
              maxlength="42"
            />
            <div v-if="errors.payer" class="field-error">{{ errors.payer }}</div>
          </div>

          <div class="form-group">
            <label class="field-label">收款方地址 *</label>
            <input
              v-model="recordForm.payee"
              type="text"
              class="field-input"
              placeholder="0x..."
              maxlength="42"
            />
            <div v-if="errors.payee" class="field-error">{{ errors.payee }}</div>
          </div>

          <div class="form-row">
            <div class="form-group form-half">
              <label class="field-label">金额 *</label>
              <input
                v-model.number="recordForm.amount"
                type="number"
                class="field-input"
                placeholder="0.00"
                step="0.01"
                min="0.00000001"
              />
              <div v-if="errors.amount" class="field-error">{{ errors.amount }}</div>
            </div>
            <div class="form-group form-half">
              <label class="field-label">货币类型 *</label>
              <select v-model="recordForm.currency" class="field-select">
                <option value="CNY">人民币 (CNY)</option>
                <option value="USD">美元 (USD)</option>
                <option value="EUR">欧元 (EUR)</option>
                <option value="HKD">港币 (HKD)</option>
                <option value="JPY">日元 (JPY)</option>
                <option value="GBP">英镑 (GBP)</option>
              </select>
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <button class="btn btn-secondary" @click="showRecordDialog = false">取消</button>
          <button class="btn btn-primary" :disabled="submitting" @click="handleRecord">
            {{ submitting ? '记录中...' : '确认记录' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { recordRoyalty, getRoyaltiesByCopyright, getRoyaltiesByPayer, getRoyaltiesByPayee } from '@/api/royalty'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const loading = ref(false)
const showRecordDialog = ref(false)
const submitting = ref(false)
const royaltyList = ref([])

const canRecordRoyalty = computed(() => {
  return !userStore.isAuditor
})

const stats = ref({
  totalCount: 0,
  totalAmount: '0.00',
  payCount: 0,
  currencyCount: 0
})

const filterForm = reactive({
  copyrightId: '',
  payerAddress: '',
  payeeAddress: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

const recordForm = reactive({
  copyrightId: '',
  payer: '',
  payee: '',
  amount: 0,
  currency: 'CNY'
})

const errors = reactive({
  copyrightId: '',
  payer: '',
  payee: '',
  amount: ''
})

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 8)}...${address.substring(address.length - 6)}`
}

const formatFullTime = (timestamp) => {
  if (!timestamp) return '-'
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm')
}

const loadList = async () => {
  loading.value = true
  try {
    let res
    let records = []

    if (filterForm.copyrightId) {
      res = await getRoyaltiesByCopyright(filterForm.copyrightId)
      records = res.data || []
    } else if (filterForm.payerAddress) {
      res = await getRoyaltiesByPayer(filterForm.payerAddress)
      records = res.data || []
    } else if (filterForm.payeeAddress) {
      res = await getRoyaltiesByPayee(filterForm.payeeAddress)
      records = res.data || []
    } else {
      records = []
    }

    stats.value = {
      totalCount: records.length,
      totalAmount: records.reduce((sum, r) => sum + parseFloat(r.amount || 0), 0).toFixed(2),
      payCount: records.length,
      currencyCount: new Set(records.map(r => r.currency)).size
    }

    const start = (pagination.pageNum - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    royaltyList.value = records.slice(start, end)
    pagination.total = records.length
  } catch (error) {
    console.error('加载版税记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.pageNum = 1
  loadList()
}

const handleReset = () => {
  filterForm.copyrightId = ''
  filterForm.payerAddress = ''
  filterForm.payeeAddress = ''
  handleFilter()
}

const validateRecordForm = () => {
  Object.keys(errors).forEach(key => errors[key] = '')

  if (!recordForm.copyrightId) {
    errors.copyrightId = '请输入版权ID'
    return false
  }

  if (!recordForm.payer) {
    errors.payer = '请输入支付方地址'
    return false
  }
  if (!/^0x[a-fA-F0-9]{40}$/.test(recordForm.payer)) {
    errors.payer = '请输入有效的区块链地址格式'
    return false
  }

  if (!recordForm.payee) {
    errors.payee = '请输入收款方地址'
    return false
  }
  if (!/^0x[a-fA-F0-9]{40}$/.test(recordForm.payee)) {
    errors.payee = '请输入有效的区块链地址格式'
    return false
  }

  if (!recordForm.amount || recordForm.amount <= 0) {
    errors.amount = '请输入有效的金额'
    return false
  }

  return true
}

const handleRecord = async () => {
  if (!validateRecordForm()) return

  submitting.value = true
  try {
    await recordRoyalty(recordForm)
    ElMessage.success('版税记录成功')
    showRecordDialog.value = false

    Object.assign(recordForm, {
      copyrightId: '',
      payer: '',
      payee: '',
      amount: 0,
      currency: 'CNY'
    })

    await loadList()
  } catch (error) {
    console.error('记录失败:', error)
    ElMessage.error(error.message || '记录失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.royalty-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
}

// 页面头部
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
  white-space: nowrap;

  &.btn-primary {
    background: var(--gradient-primary);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  &.btn-secondary {
    background: var(--bg-tertiary);
    color: var(--text-primary);

    &:hover {
      background: #e5e7eb;
    }
  }

  &.btn-search {
    padding: 12px 28px;
    background: var(--gradient-primary);
    color: white;
    border: none;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }
  }

  &.btn-reset {
    padding: 12px 24px;
    background: var(--bg-tertiary);
    color: var(--text-primary);
    border: 1px solid var(--border-primary);

    &:hover {
      background: #e5e7eb;
    }
  }
}

// 筛选栏
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

.filter-group {
  flex: 1;
  min-width: 200px;
  position: relative;
  display: flex;
  align-items: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  padding: 0 16px;
  transition: all 0.2s;

  &:hover {
    border-color: #667eea;
    background: var(--bg-card);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
  }

  .filter-icon {
    color: var(--text-tertiary);
    flex-shrink: 0;
    margin-right: 10px;
  }

  .filter-input,
  .filter-select {
    flex: 1;
    border: none;
    background: transparent;
    padding: 12px 0;
    font-size: 14px;
    color: var(--text-primary);
    outline: none;

    &::placeholder {
      color: var(--text-tertiary);
    }
  }

  .filter-select {
    cursor: pointer;
  }
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

// 统计栏
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
    display: flex;
    align-items: center;
    justify-content: center;

    &.stat-purple {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
      color: #667eea;
    }

    &.stat-green {
      background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(16, 185, 129, 0.15));
      color: #22c55e;
    }

    &.stat-orange {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(249, 168, 37, 0.15));
      color: #f97316;
    }

    &.stat-blue {
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(6, 182, 212, 0.15));
      color: #3b82f6;
    }
  }

  .stat-info {
    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: var(--text-primary);
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: var(--text-tertiary);
      margin-top: 4px;
    }
  }
}

// 版税卡片网格
.royalty-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
  min-height: 200px;
}

.royalty-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;

  &:hover {
    border-color: #667eea;
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    border-bottom: 1px solid #f3f4f6;

    .card-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
      color: #667eea;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .card-title {
      flex: 1;

      .record-id {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }
  }

  .card-body {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .card-row {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .row-label {
      font-size: 13px;
      color: var(--text-secondary);
      font-weight: 500;
    }

    .row-value {
      font-size: 14px;
      color: var(--text-primary);
      font-weight: 600;
      text-align: right;
      max-width: 60%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .card-divider {
    height: 1px;
    background: var(--bg-tertiary);
    margin: 4px 0;
  }

  .amount-section {
    padding: 16px;
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.08), rgba(16, 185, 129, 0.08));
    border-radius: 10px;
    border: 1px solid rgba(34, 197, 94, 0.2);

    .amount-label {
      font-size: 12px;
      color: #16a34a;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .amount-value {
      display: flex;
      align-items: baseline;
      justify-content: center;
      gap: 6px;

      .amount-number {
        font-size: 28px;
        font-weight: 700;
        color: #16a34a;
      }

      .amount-currency {
        font-size: 16px;
        font-weight: 600;
        color: #16a34a;
      }
    }
  }

  .card-footer {
    padding: 12px 20px;
    border-top: 1px solid #f3f4f6;
  }

  .footer-time {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-size: 13px;
    color: var(--text-tertiary);

    svg {
      color: var(--text-tertiary);
    }
  }
}

// 空状态
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;

  svg {
    margin-bottom: 24px;
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  p {
    font-size: 15px;
    color: var(--text-secondary);
    margin: 0 0 24px 0;
  }

  .btn {
    margin: 0 auto;
  }
}

// 分页
.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 24px 0;

  :deep(.el-pagination) {
    .el-pager li,
    button {
      background: var(--bg-card);
      border: 1px solid var(--border-primary);
      border-radius: 8px;
      color: var(--text-secondary);
      font-weight: 500;
      margin: 0 4px;

      &:hover {
        color: #667eea;
        border-color: #667eea;
      }

      &.active {
        background: var(--gradient-primary);
        color: white;
        border-color: transparent;
      }
    }

    .el-pagination__total {
      color: var(--text-secondary);
      font-weight: 500;
    }
  }
}

// 对话框
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.dialog-container {
  background: var(--bg-card);
  border-radius: 16px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: auto;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;

  .dialog-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }

  .dialog-close {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    border: none;
    background: var(--bg-tertiary);
    color: var(--text-secondary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #e5e7eb;
      color: var(--text-primary);
    }
  }
}

.dialog-body {
  padding: 24px;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
}

.form-group {
  margin-bottom: 20px;

  &.form-half {
    flex: 1;
  }
}

.form-row {
  display: flex;
  gap: 16px;

  .form-group {
    margin-bottom: 0;
  }
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.field-input,
.field-select {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  background: var(--bg-card);
  color: var(--text-primary);
  transition: all 0.2s;

  &::placeholder {
    color: var(--text-tertiary);
  }

  &:hover {
    border-color: #667eea;
  }

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }
}

.field-error {
  font-size: 13px;
  color: #ef4444;
  margin-top: 6px;
}

// 响应式
@media (max-width: 1200px) {
  .royalty-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;

    .btn {
      width: 100%;
      justify-content: center;
    }
  }

  .filter-bar {
    flex-direction: column;

    .filter-group,
    .filter-actions {
      width: 100%;
    }

    .filter-actions {
      flex-direction: column;

      .btn {
        width: 100%;
      }
    }
  }

  .stats-bar {
    flex-direction: column;
  }

  .royalty-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .empty-state {
    padding: 40px 20px;
  }

  .form-row {
    flex-direction: column;
  }
}
</style>
