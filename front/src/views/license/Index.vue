<template>
  <div class="license-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">授权管理</h1>
        <p class="page-subtitle">管理和查看版权授权记录</p>
      </div>
      <button class="btn btn-primary" @click="goToApply">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 1V13M9 13L5 9M9 13L13 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M1 13V15C1 16.1 1.9 17 3 17H15C16.1 17 17 16.1 17 15V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        申请授权
      </button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label class="filter-label">状态筛选</label>
        <select v-model="filterForm.status" class="filter-select" @change="handleFilter">
          <option value="">全部状态</option>
          <option value="PENDING">待审批</option>
          <option value="APPROVED">已批准</option>
          <option value="ACTIVE">已激活</option>
          <option value="REVOKED">已撤销</option>
          <option value="EXPIRED">已过期</option>
        </select>
      </div>

      <div class="filter-group">
        <label class="filter-label">被授权方</label>
        <div class="search-input-wrapper">
          <svg class="search-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 1C4.13401 1 1 4.13401 1 8C1 11.866 4.13401 15 8 15C9.68473 15 11.2293 14.3956 12.4318 13.3856L12.5 13.5L16.5 17.5L17.5 16.5L13.5 12.5L13.3856 12.4318C14.3956 11.2293 15 9.68473 15 8C15 4.13401 11.866 1 8 1ZM8 3C10.7614 3 13 5.23858 13 8C13 10.7614 10.7614 13 8 13C5.23858 13 3 10.7614 3 8C3 5.23858 5.23858 3 8 3Z" fill="currentColor"/>
          </svg>
          <input
            v-model="filterForm.licenseeAddress"
            type="text"
            placeholder="输入地址搜索"
            class="search-input"
          />
        </div>
      </div>

      <button class="btn btn-search" @click="handleFilter">搜索</button>
      <button class="btn btn-reset" @click="handleReset" v-if="filterForm.status || filterForm.licenseeAddress">重置</button>
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
          <div class="stat-value">{{ pagination.total }}</div>
          <div class="stat-label">授权总数</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-orange">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 1C5.58 1 1 5.58 1 10C1 14.42 5.58 17 10 17C14.42 17 17 14.42 17 10C17 5.58 14.42 1 10 1ZM10 15C6.69 15 3 12.31 3 9C3 5.69 6.69 3 10 3C12.31 3 15 5.69 15 9C15 12.31 12.31 15 10 15ZM10 5C8.34 5 7 6.34 7 8C7 9.66 8.34 11 10 11C11.66 11 13 9.66 13 8C13 6.34 11.66 5 10 5Z" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ pendingCount }}</div>
          <div class="stat-label">待审批</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon stat-green">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 1C5.58 1 1 5.58 1 10C1 14.42 5.58 17 10 17C14.42 17 17 14.42 17 10C17 5.58 14.42 1 10 1ZM7 9L5 11L8 14L15 7" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ activeCount }}</div>
          <div class="stat-label">已激活</div>
        </div>
      </div>
    </div>

    <!-- 授权卡片网格 -->
    <div v-loading="loading" class="license-grid">
      <div
        v-for="item in licenseList"
        :key="item.id"
        class="license-card"
        @click="viewDetail(item)"
      >
        <div class="card-header">
          <div class="license-id">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 4C8.83 4 9.5 4.67 9.5 5.5C9.5 6.33 8.83 7 8 7C7.17 7 6.5 6.33 6.5 5.5C6.5 4.67 7.17 4 8 4ZM10 12H6V11.2C6 9.72 7.24 9.2 8 9.2C8.76 9.2 10 9.72 10 11.2V12Z" fill="currentColor"/>
            </svg>
            ID: {{ item.contractId || '-' }}
          </div>
          <div class="card-badge" :class="`badge-${item.status.toLowerCase()}`">
            {{ getStatusText(item.status) }}
          </div>
        </div>

        <div class="card-body">
          <div class="card-title-row">
            <h3 class="card-title">{{ item.copyrightTitle || '未命名版权' }}</h3>
            <div class="card-type" :class="{ 'exclusive': item.isExclusive }">
              {{ item.isExclusive ? '独占' : '普通' }}
            </div>
          </div>

          <div class="card-meta">
            <div class="meta-item">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 4C8.83 4 9.5 4.67 9.5 5.5C9.5 6.33 8.83 7 8 7C7.17 7 6.5 6.33 6.5 5.5C6.5 4.67 7.17 4 8 4ZM10 12H6V11.2C6 9.72 7.24 9.2 8 9.2C8.76 9.2 10 9.72 10 11.2V12Z" fill="currentColor"/>
              </svg>
              <span>被授权方: {{ formatAddress(item.licenseeAddress) }}</span>
            </div>
            <div class="meta-item">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 2C6.34 2 5 3.34 5 5.5V7H3V8H5V9H3V12C3 13.1 3.9 14 5 14H11C12.1 14 13 13.1 13 12V8H11V7H9V5.5C9 3.34 7.66 2 6 2H8ZM12 13H4V12H12V13ZM11 11H5V8H11V11Z" fill="currentColor"/>
              </svg>
              <span>有效期: {{ formatDateRange(item.startTime, item.endTime) }}</span>
            </div>
          </div>

          <div class="card-actions">
            <!-- 待审批状态 -->
            <template v-if="item.status === 'PENDING' && canApproveReject">
              <button class="btn-card-action btn-success" @click.stop="handleApprove(item)">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM6 7L5 6L8 9L12 4L13 5L8 7Z" fill="currentColor"/>
                </svg>
                批准
              </button>
              <button class="btn-card-action btn-danger" @click.stop="handleReject(item)">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM9 1H7V7H1V9H7V15H9V9H15V7H9V1Z" fill="currentColor"/>
                </svg>
                拒绝
              </button>
            </template>

            <!-- 已批准状态 -->
            <template v-else-if="item.status === 'APPROVED' && canActivate">
              <button class="btn-card-action btn-primary" @click.stop="handleActivate(item)">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M6 4L10 8L14 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M6 12H14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                激活
              </button>
            </template>

            <!-- 已激活状态 -->
            <template v-else-if="item.status === 'ACTIVE' && canRevoke">
              <button class="btn-card-action btn-warning" @click.stop="handleRevoke(item)">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 4H12V12H4V4ZM5 5V11H11V5H5Z" fill="currentColor"/>
                </svg>
                撤销
              </button>
            </template>

            <button class="btn-card-view" @click.stop="viewDetail(item)">
              查看详情
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && licenseList.length === 0" class="empty-state">
        <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M40 8L12 20V40C12 55.2 23.6 68.8 40 74C56.4 68.8 68 55.2 68 40V20L40 8Z" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M30 40L36 46L50 32" stroke="#e5e7eb" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h3>暂无授权记录</h3>
        <p>开始申请版权授权，管理您的版权使用</p>
        <button class="btn btn-primary" @click="goToApply">申请第一个授权</button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getLicenseList,
  approveLicense,
  rejectLicense,
  activateLicense,
  revokeLicense
} from '@/api/license'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const licenseList = ref([])

// 权限控制
const canApproveReject = computed(() => {
  return userStore.isLicenseApprover || userStore.isSystemAdmin
})

const canRevoke = computed(() => {
  return userStore.isLicenseManager || userStore.isSystemAdmin
})

const canActivate = computed(() => {
  return userStore.hasPermission('activate')
})

const filterForm = reactive({
  status: '',
  licenseeAddress: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

const pendingCount = computed(() => {
  return licenseList.value.filter(l => l.status === 'PENDING').length
})

const activeCount = computed(() => {
  return licenseList.value.filter(l => l.status === 'ACTIVE').length
})

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 8)}...${address.substring(address.length - 6)}`
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return dayjs(timestamp * 1000).format('MM-DD HH:mm')
}

const formatDateRange = (start, end) => {
  if (!start || !end) return '-'
  const startDate = dayjs(start * 1000).format('MM-DD')
  const endDate = dayjs(end * 1000).format('MM-DD')
  return `${startDate} ~ ${endDate}`
}

const getStatusText = (status) => {
  const map = {
    PENDING: '待审批',
    APPROVED: '已批准',
    ACTIVE: '已激活',
    REVOKED: '已撤销',
    EXPIRED: '已过期'
  }
  return map[status] || status
}

const loadList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const result = await getLicenseList(params)
    // mockResponse 返回 { code, msg, data }，需要手动提取 data
    const data = result.data !== undefined ? result.data : result

    let records = data.records || []

    // 前端筛选
    if (filterForm.status) {
      records = records.filter(item => item.status === filterForm.status)
    }
    if (filterForm.licenseeAddress) {
      records = records.filter(item =>
        item.licenseeAddress?.toLowerCase().includes(filterForm.licenseeAddress.toLowerCase())
      )
    }

    licenseList.value = records
    pagination.total = data.total || records.length || 0
  } catch (error) {
    console.error('加载授权列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.pageNum = 1
  loadList()
}

const handleReset = () => {
  filterForm.status = ''
  filterForm.licenseeAddress = ''
  handleFilter()
}

const handleApprove = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认批准授权申请 #${item.contractId}？`,
      '批准授权',
      {
        confirmButtonText: '确认批准',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await approveLicense(item.id)
    ElMessage.success('授权已批准')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批准失败:', error)
      ElMessage.error(error.message || '批准失败')
    }
  }
}

const handleReject = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认拒绝授权申请 #${item.contractId}？`,
      '拒绝授权',
      {
        confirmButtonText: '确认拒绝',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await rejectLicense(item.id)
    ElMessage.success('授权已拒绝')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝失败:', error)
      ElMessage.error(error.message || '拒绝失败')
    }
  }
}

const handleActivate = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认激活授权 #${item.contractId}？激活后授权开始生效。`,
      '激活授权',
      {
        confirmButtonText: '确认激活',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    await activateLicense(item.id)
    ElMessage.success('授权已激活')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('激活失败:', error)
      ElMessage.error(error.message || '激活失败')
    }
  }
}

const handleRevoke = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确认撤销授权 #${item.contractId}？撤销后将无法恢复。`,
      '撤销授权',
      {
        confirmButtonText: '确认撤销',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await revokeLicense(item.id)
    ElMessage.success('授权已撤销')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销失败:', error)
      ElMessage.error(error.message || '撤销失败')
    }
  }
}

const viewDetail = (item) => {
  router.push(`/licenses/detail/${item.id}`)
}

const goToApply = () => {
  router.push('/licenses/apply')
}

onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.license-page {
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
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
}

.filter-select {
  padding: 10px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  font-size: 14px;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
    background: var(--bg-card);
  }
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  padding: 0 16px;
  transition: all 0.2s;

  &:hover, &:focus-within {
    border-color: #667eea;
    background: var(--bg-card);
  }

  .search-icon {
    color: var(--text-tertiary);
    flex-shrink: 0;
  }

  .search-input {
    flex: 1;
    border: none;
    background: transparent;
    padding: 10px 8px;
    font-size: 14px;
    color: var(--text-primary);
    outline: none;

    &::placeholder {
      color: var(--text-tertiary);
    }
  }
}

.btn-search, .btn-reset {
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &.btn-search {
    background: var(--gradient-primary);
    color: white;
    border: none;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }
  }

  &.btn-reset {
    background: var(--bg-card);
    color: var(--text-secondary);
    border: 1px solid var(--border-primary);

    &:hover {
      background: var(--bg-secondary);
      color: #667eea;
      border-color: #667eea;
    }
  }
}

// 统计栏
.stats-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.stat-item {
  flex: 1;
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

    &.stat-orange {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(239, 68, 68, 0.15));
      color: #f97316;
    }

    &.stat-green {
      background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(16, 185, 129, 0.15));
      color: #22c55e;
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

// 授权卡片网格
.license-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
  min-height: 200px;
}

.license-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
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
    justify-content: space-between;
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-tertiary);

    .license-id {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: var(--text-secondary);
      font-weight: 500;

      svg {
        color: #667eea;
      }
    }

    .card-badge {
      padding: 4px 10px;
      border-radius: 6px;
      font-size: 11px;
      font-weight: 600;

      &.badge-pending {
        background: rgba(249, 115, 22, 0.1);
        color: #f97316;
      }

      &.badge-approved {
        background: rgba(6, 182, 212, 0.1);
        color: #06b6d4;
      }

      &.badge-active {
        background: rgba(34, 197, 94, 0.1);
        color: #22c55e;
      }

      &.badge-revoked {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
      }

      &.badge-expired {
        background: rgba(107, 114, 128, 0.1);
        color: var(--text-secondary);
      }
    }
  }

  .card-body {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .card-title-row {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
  }

  .card-title {
    flex: 1;
    font-size: 17px;
    font-weight: 600;
    color: var(--text-primary);
    line-height: 1.4;
    margin: 0;
  }

  .card-type {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 11px;
    font-weight: 600;
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;

    &.exclusive {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;
    }
  }

  .card-meta {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: var(--text-secondary);

      svg {
        color: #667eea;
        flex-shrink: 0;
      }

      span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .card-actions {
    display: flex;
    gap: 8px;
    margin-top: auto;
  }

  .btn-card-action {
    flex: 1;
    padding: 8px 12px;
    border: none;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    transition: all 0.2s;

    &.btn-success {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;

      &:hover {
        background: rgba(34, 197, 94, 0.2);
      }
    }

    &.btn-danger {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;

      &:hover {
        background: rgba(239, 68, 68, 0.2);
      }
    }

    &.btn-primary {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;

      &:hover {
        background: rgba(102, 126, 234, 0.2);
      }
    }

    &.btn-warning {
      background: rgba(249, 115, 22, 0.1);
      color: #f97316;

      &:hover {
        background: rgba(249, 115, 22, 0.2);
      }
    }
  }

  .btn-card-view {
    padding: 8px 12px;
    background: rgba(102, 126, 234, 0.08);
    border: none;
    border-radius: 8px;
    color: #667eea;
    font-size: 13px;
    font-weight:600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s;

    &:hover {
      background: rgba(102, 126, 234, 0.15);
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

@media (max-width: 1200px) {
  .license-grid {
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

    .filter-group {
      width: 100%;

      .filter-select,
      .search-input-wrapper {
        width: 100%;
      }
    }

    .btn-search,
    .btn-reset {
      width: 100%;
    }
  }

  .stats-bar {
    flex-direction: column;
  }

  .license-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .empty-state {
    padding: 40px 20px;
  }
}
</style>
