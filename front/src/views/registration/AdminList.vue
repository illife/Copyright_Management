<template>
  <div class="admin-registration-list">
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">注册申请管理</h2>
        <p class="page-subtitle">管理和审批用户注册申请</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="fetchData">刷新</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待审核</div>
          <div class="stat-value">{{ stats.pending }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon approved">
          <el-icon><Select /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已通过</div>
          <div class="stat-value">{{ stats.approved }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rejected">
          <el-icon><CloseBold /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">已拒绝</div>
          <div class="stat-value">{{ stats.rejected }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">总申请</div>
          <div class="stat-value">{{ stats.total }}</div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="handleFilterChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待审核</el-radio-button>
        <el-radio-button label="APPROVED">已通过</el-radio-button>
        <el-radio-button label="REJECTED">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 申请列表 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="userAddress" label="区块链地址" min-width="200">
          <template #default="{ row }">
            <div class="address-cell">
              <el-tooltip :content="row.userAddress" placement="top">
                <span>{{ formatAddress(row.userAddress) }}</span>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatTimestamp(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                link
                @click="handleViewDetail(row)"
              >
                查看详情
              </el-button>
              <template v-if="row.status === 'PENDING'">
                <el-divider direction="vertical" />
                <el-button
                  type="success"
                  size="small"
                  link
                  @click="handleReview(row, true)"
                >
                  通过
                </el-button>
                <el-divider direction="vertical" />
                <el-button
                  type="danger"
                  size="small"
                  link
                  @click="handleReview(row, false)"
                >
                  拒绝
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="注册申请详情"
      width="700px"
    >
      <div v-if="currentDetail" class="detail-content">
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">申请ID：</span>
              <span class="detail-value">{{ currentDetail.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用户名：</span>
              <span class="detail-value">{{ currentDetail.username }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">真实姓名：</span>
              <span class="detail-value">{{ currentDetail.realName }}</span>
            </div>
            <div class="detail-item full-width">
              <span class="detail-label">区块链地址：</span>
              <span class="detail-value address-value">{{ currentDetail.userAddress }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ currentDetail.email }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">电话：</span>
              <span class="detail-value">{{ currentDetail.phone }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">申请说明</h4>
          <div class="description-box">
            {{ currentDetail.description }}
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">申请状态</h4>
          <div class="status-info">
            <el-tag :type="getStatusType(currentDetail.status)" size="large">
              {{ getStatusText(currentDetail.status) }}
            </el-tag>
            <div class="time-info">
              <div>申请时间：{{ formatTimestamp(currentDetail.createTime) }}</div>
              <div v-if="currentDetail.reviewTime">
                审核时间：{{ formatTimestamp(currentDetail.reviewTime) }}
              </div>
            </div>
          </div>
        </div>

        <div v-if="currentDetail.status !== 'PENDING'" class="detail-section">
          <h4 class="section-title">审核信息</h4>
          <div class="review-info">
            <div class="review-item">
              <span class="review-label">审核人地址：</span>
              <span class="review-value">{{ currentDetail.reviewerAddress }}</span>
            </div>
            <div v-if="currentDetail.reviewerRemark" class="review-item">
              <span class="review-label">审核备注：</span>
              <span class="review-value">{{ currentDetail.reviewerRemark }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer v-if="currentDetail && currentDetail.status === 'PENDING'">
        <el-button @click="detailDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReviewFromDialog(false)">拒绝</el-button>
        <el-button type="primary" @click="handleReviewFromDialog(true)">通过</el-button>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewForm.approved ? '通过申请' : '拒绝申请'"
      width="500px"
    >
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="申请ID">
          <el-input v-model="reviewForm.id" disabled />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="reviewForm.username" disabled />
        </el-form-item>
        <el-form-item label="审核备注" prop="remark">
          <el-input
            v-model="reviewForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="reviewForm.approved ? '请输入通过理由（可选）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button
          :type="reviewForm.approved ? 'primary' : 'danger'"
          @click="confirmReview"
        >
          确认{{ reviewForm.approved ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Clock,
  Select,
  CloseBold,
  Document
} from '@element-plus/icons-vue'
import { getRegistrationList, getPendingCount, reviewRegistration, getRegistrationDetail } from '@/api/registration'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const filterStatus = ref('')
const detailDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const currentDetail = ref(null)

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const reviewForm = reactive({
  id: null,
  username: '',
  approved: true,
  remark: '',
  reviewerAddress: userStore.address
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const data = await getPendingCount()
    stats.pending = data.count
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }

    const data = await getRegistrationList(params)
    tableData.value = data.records || data.list || []
    pagination.total = data.total || 0

    // 更新统计数据
    stats.total = data.total
    stats.approved = tableData.value.filter(r => r.status === 'APPROVED').length
    stats.rejected = tableData.value.filter(r => r.status === 'REJECTED').length
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error(error.message || '获取列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选变更
const handleFilterChange = () => {
  pagination.page = 1
  fetchData()
}

// 分页变更
const handlePageChange = (page) => {
  pagination.page = page
  fetchData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchData()
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    const res = await getRegistrationDetail(row.id)
    currentDetail.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 审批操作
const handleReview = (row, approved) => {
  reviewForm.id = row.id
  reviewForm.username = row.username
  reviewForm.approved = approved
  reviewForm.remark = ''
  reviewDialogVisible.value = true
}

// 从详情对话框审批
const handleReviewFromDialog = (approved) => {
  detailDialogVisible.value = false
  handleReview(currentDetail.value, approved)
}

// 确认审批
const confirmReview = async () => {
  try {
    await reviewRegistration({
      id: reviewForm.id,
      approved: reviewForm.approved,
      reviewerAddress: reviewForm.reviewerAddress,
      remark: reviewForm.remark
    })

    ElMessage.success(reviewForm.approved ? '已通过申请' : '已拒绝申请')
    reviewDialogVisible.value = false
    fetchData()
    fetchStats()
  } catch (error) {
    console.error('审批失败:', error)
    ElMessage.error(error.response?.data?.message || '审批失败')
  }
}

// 格式化地址
const formatAddress = (address) => {
  if (!address) return ''
  return `${address.slice(0, 8)}...${address.slice(-6)}`
}

// 格式化时间戳
const formatTimestamp = (timestamp) => {
  if (!timestamp) return '-'

  let date
  // 如果是字符串（ISO格式），直接解析
  if (typeof timestamp === 'string') {
    date = new Date(timestamp)
  } else {
    // 如果是数字，假设是Unix时间戳（秒）
    date = new Date(timestamp * 1000)
  }

  // 检查日期是否有效
  if (isNaN(date.getTime())) return '-'

  // 格式化为：YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}

onMounted(() => {
  fetchData()
  fetchStats()
})
</script>

<style lang="scss" scoped>
.admin-registration-list {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  overflow-x: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .header-content {
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 8px 0;
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
  }
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;

  .stat-card {
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #5b9bd5;
      box-shadow: 0 2px 8px rgba(91, 155, 213, 0.2);
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;

      &.pending {
        background: rgba(230, 162, 60, 0.1);
        color: #e6a23c;
      }

      &.approved {
        background: rgba(103, 194, 58, 0.1);
        color: #67c23a;
      }

      &.rejected {
        background: rgba(245, 108, 108, 0.1);
        color: #f56c6c;
      }

      &.total {
        background: rgba(91, 155, 213, 0.1);
        color: #5b9bd5;
      }
    }

    .stat-content {
      .stat-label {
        font-size: 14px;
        color: var(--text-tertiary);
        margin-bottom: 4px;
      }

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }
  }
}

/* 筛选栏 */
.filter-bar {
  margin-bottom: 20px;
}

/* 表格容器 */
.table-container {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 8px;
  padding: 20px;
  overflow-x: auto;

  :deep(.el-table) {
    table-layout: fixed;
  }

  .address-cell {
    font-family: 'Courier New', monospace;
    font-size: 13px;
    word-break: break-all;
  }

  .action-buttons {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    flex-wrap: nowrap;

    .el-divider--vertical {
      height: 16px;
      margin: 0 4px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

/* 详情对话框 */
.detail-content {
  .detail-section {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 16px 0;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-primary);
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .detail-item {
        display: flex;
        gap: 8px;

        &.full-width {
          grid-column: 1 / -1;
        }

        .detail-label {
          font-size: 14px;
          color: var(--text-tertiary);
          min-width: 80px;
        }

        .detail-value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;

          &.address-value {
            font-family: 'Courier New', monospace;
            word-break: break-all;
          }
        }
      }
    }

    .description-box {
      padding: 16px;
      background: var(--bg-tertiary);
      border-radius: 6px;
      font-size: 14px;
      color: var(--text-primary);
      line-height: 1.6;
    }

    .status-info {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .time-info {
        font-size: 13px;
        color: var(--text-tertiary);

        div {
          margin-bottom: 4px;

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }

    .review-info {
      .review-item {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .review-label {
          font-size: 14px;
          color: var(--text-tertiary);
          min-width: 80px;
        }

        .review-value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }
  }
}
</style>
