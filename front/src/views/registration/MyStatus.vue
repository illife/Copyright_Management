<template>
  <div class="my-registration-status">
    <div class="page-header">
      <h2 class="page-title">我的注册申请</h2>
      <p class="page-subtitle">查询您的注册申请状态</p>
    </div>

    <!-- 查询表单 -->
    <div class="query-card">
      <el-form
        ref="queryFormRef"
        :model="queryForm"
        :rules="queryRules"
        label-width="100px"
        class="query-form"
      >
        <el-form-item label="区块链地址" prop="userAddress">
          <el-input
            v-model="queryForm.userAddress"
            placeholder="请输入您的区块链地址（0x...）"
            clearable
            class="query-input"
          >
            <template #prefix>
              <el-icon><Wallet /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleQuery"
          >
            查询申请状态
          </el-button>
          <el-button size="large" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 查询结果 -->
    <div v-if="registrationData" class="result-card">
      <div class="result-header">
        <div class="status-badge" :class="`status-${registrationData.status.toLowerCase()}`">
          <el-icon class="status-icon">
            <Clock v-if="registrationData.status === 'PENDING'" />
            <Select v-else-if="registrationData.status === 'APPROVED'" />
            <CloseBold v-else />
          </el-icon>
          <span class="status-text">{{ getStatusText(registrationData.status) }}</span>
        </div>
      </div>

      <div class="result-content">
        <div class="info-section">
          <h4 class="section-title">申请信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">申请编号</span>
              <span class="info-value">{{ registrationData.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ registrationData.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">真实姓名</span>
              <span class="info-value">{{ registrationData.realName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ registrationData.email }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">电话</span>
              <span class="info-value">{{ registrationData.phone }}</span>
            </div>
            <div class="info-item full-width">
              <span class="info-label">区块链地址</span>
              <span class="info-value address">{{ registrationData.userAddress }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h4 class="section-title">申请说明</h4>
          <div class="description-box">
            {{ registrationData.description }}
          </div>
        </div>

        <div class="info-section">
          <h4 class="section-title">时间信息</h4>
          <div class="time-info">
            <div class="time-item">
              <span class="time-label">申请时间</span>
              <span class="time-value">{{ formatTimestamp(registrationData.createTime) }}</span>
            </div>
            <div v-if="registrationData.reviewTime" class="time-item">
              <span class="time-label">审核时间</span>
              <span class="time-value">{{ formatTimestamp(registrationData.reviewTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 审核信息 -->
        <div v-if="registrationData.status !== 'PENDING'" class="info-section">
          <h4 class="section-title">审核信息</h4>
          <div class="review-result">
            <div v-if="registrationData.reviewerAddress" class="review-item">
              <span class="review-label">审核人地址</span>
              <span class="review-value">{{ registrationData.reviewerAddress }}</span>
            </div>
            <div v-if="registrationData.reviewerRemark" class="review-item">
              <span class="review-label">审核备注</span>
              <span class="review-value">{{ registrationData.reviewerRemark }}</span>
            </div>
          </div>
        </div>

        <!-- 状态提示 -->
        <div class="status-alert">
          <el-alert
            v-if="registrationData.status === 'PENDING'"
            title="您的申请正在审核中"
            type="warning"
            :closable="false"
            show-icon
          >
            我们将在1-3个工作日内完成审核，请耐心等待。您可以随时回来查询最新状态。
          </el-alert>
          <el-alert
            v-else-if="registrationData.status === 'APPROVED'"
            title="恭喜！您的申请已通过"
            type="success"
            :closable="false"
            show-icon
          >
            您现在可以使用区块链地址登录系统了。
            <el-button
              type="primary"
              size="small"
              link
              @click="handleGoToLogin"
            >
              前往登录
            </el-button>
          </el-alert>
          <el-alert
            v-else-if="registrationData.status === 'REJECTED'"
            title="很遗憾，您的申请被拒绝"
            type="error"
            :closable="false"
            show-icon
          >
            请根据审核备注修改信息后重新提交申请。
            <el-button
              type="primary"
              size="small"
              link
              @click="handleGoToRegister"
            >
              重新申请
            </el-button>
          </el-alert>
        </div>
      </div>
    </div>

    <!-- 未找到申请 -->
    <div v-else-if="hasQueried && !registrationData" class="no-result">
      <el-empty description="未找到注册申请">
        <template #image>
          <el-icon :size="80" color="#909399">
            <DocumentDelete />
          </el-icon>
        </template>
        <p class="no-result-tip">该地址尚未提交注册申请，或者申请已被删除</p>
        <el-button type="primary" @click="handleGoToRegister">
          提交注册申请
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Wallet,
  Clock,
  Select,
  CloseBold,
  DocumentDelete
} from '@element-plus/icons-vue'
import { getMyRegistrationStatus } from '@/api/registration'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const queryFormRef = ref(null)
const loading = ref(false)
const registrationData = ref(null)
const hasQueried = ref(false)

const queryForm = reactive({
  userAddress: userStore.address || ''
})

const queryRules = {
  userAddress: [
    { required: true, message: '请输入区块链地址', trigger: 'blur' },
    {
      pattern: /^0x[a-fA-F0-9]{40}$/,
      message: '请输入有效的区块链地址格式（0x开头，42位字符）',
      trigger: 'blur'
    }
  ]
}

const handleQuery = async () => {
  if (!queryFormRef.value) return

  await queryFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    hasQueried.value = true
    try {
      const res = await getMyRegistrationStatus(queryForm.userAddress)
      registrationData.value = res.data
      ElMessage.success('查询成功')
    } catch (error) {
      console.error('查询失败:', error)
      registrationData.value = null
      ElMessage.error(error.response?.data?.message || '未找到注册申请')
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  queryFormRef.value?.resetFields()
  registrationData.value = null
  hasQueried.value = false
}

const handleGoToLogin = () => {
  router.push('/login')
}

const handleGoToRegister = () => {
  router.push('/registration')
}

const formatTimestamp = (timestamp) => {
  if (!timestamp) return '-'
  const date = new Date(timestamp * 1000)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '审核中',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}
</script>

<style lang="scss" scoped>
.my-registration-status {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;

  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 12px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: var(--text-tertiary);
    margin: 0;
  }
}

/* 查询卡片 */
.query-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .query-input {
    :deep(.el-input__wrapper) {
      height: 48px;
    }
  }
}

/* 结果卡片 */
.result-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .result-header {
    padding: 32px 40px;
    background: var(--bg-tertiary);
    border-bottom: 1px solid var(--border-primary);
    display: flex;
    justify-content: center;

    .status-badge {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px 32px;
      border-radius: 50px;
      font-size: 18px;
      font-weight: 600;

      &.status-pending {
        background: rgba(230, 162, 60, 0.15);
        color: #e6a23c;
      }

      &.status-approved {
        background: rgba(103, 194, 58, 0.15);
        color: #67c23a;
      }

      &.status-rejected {
        background: rgba(245, 108, 108, 0.15);
        color: #f56c6c;
      }

      .status-icon {
        font-size: 24px;
      }
    }
  }

  .result-content {
    padding: 40px;
  }

  .info-section {
    margin-bottom: 32px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0 0 20px 0;
      padding-bottom: 12px;
      border-bottom: 2px solid var(--border-primary);
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      .info-item {
        display: flex;
        flex-direction: column;
        gap: 8px;

        &.full-width {
          grid-column: 1 / -1;
        }

        .info-label {
          font-size: 13px;
          color: var(--text-tertiary);
          font-weight: 500;
        }

        .info-value {
          font-size: 15px;
          color: var(--text-primary);
          font-weight: 600;

          &.address {
            font-family: 'Courier New', monospace;
            word-break: break-all;
          }
        }
      }
    }

    .description-box {
      padding: 20px;
      background: var(--bg-tertiary);
      border-radius: 8px;
      font-size: 15px;
      color: var(--text-primary);
      line-height: 1.8;
    }

    .time-info {
      .time-item {
        display: flex;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-primary);

        &:last-child {
          border-bottom: none;
        }

        .time-label {
          font-size: 14px;
          color: var(--text-tertiary);
        }

        .time-value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }

    .review-result {
      .review-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .review-label {
          font-size: 13px;
          color: var(--text-tertiary);
          font-weight: 500;
        }

        .review-value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
          line-height: 1.6;
        }
      }
    }
  }

  .status-alert {
    margin-top: 32px;
    padding-top: 32px;
    border-top: 1px solid var(--border-primary);
  }
}

/* 无结果 */
.no-result {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;

  .no-result-tip {
    font-size: 14px;
    color: var(--text-tertiary);
    margin: 20px 0 24px 0;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .my-registration-status {
    padding: 20px 16px;
  }

  .page-header {
    .page-title {
      font-size: 24px;
    }

    .page-subtitle {
      font-size: 14px;
    }
  }

  .query-card,
  .result-card,
  .no-result {
    padding: 24px 16px;
  }

  .info-grid {
    grid-template-columns: 1fr !important;
  }

  .result-header .status-badge {
    padding: 12px 24px;
    font-size: 16px;
  }
}
</style>
