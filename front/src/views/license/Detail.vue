<template>
  <div class="license-detail" v-loading="loading">
    <!-- 面包屑导航 -->
    <el-breadcrumb class="breadcrumb-nav" separator="/">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/licenses' }">授权管理</el-breadcrumb-item>
      <el-breadcrumb-item>授权详情</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">授权详情</h1>
        <el-tag v-if="license.contractId" type="info" size="large">{{ license.contractId }}</el-tag>
      </div>
      <div class="header-right">
        <!-- 根据授权状态显示不同的操作按钮 -->
        <template v-if="license.status === 'PENDING' && canApproveReject">
          <el-button type="success" @click="handleApprove">
            <el-icon><Select /></el-icon>
            批准
          </el-button>
          <el-button type="danger" @click="handleReject">
            <el-icon><CloseBold /></el-icon>
            拒绝
          </el-button>
        </template>

        <template v-else-if="license.status === 'APPROVED' && canActivate">
          <el-button type="primary" @click="handleActivate">
            <el-icon><VideoPlay /></el-icon>
            激活
          </el-button>
        </template>

        <template v-else-if="license.status === 'ACTIVE' && canRevoke">
          <el-button type="warning" @click="handleRevoke">
            <el-icon><RemoveFilled /></el-icon>
            撤销
          </el-button>
        </template>
      </div>
    </div>

    <!-- 授权基本信息 -->
    <div class="detail-section card-container">
      <h2 class="section-title">
        <el-icon><Document /></el-icon>
        授权信息
      </h2>
      <el-descriptions :column="2" border class="detail-descriptions">
        <el-descriptions-item label="授权ID" label-class-name="desc-label" content-class-name="content-dark">
          <span class="dark-text">{{ license.contractId || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="数据库ID" content-class-name="content-dark">
          <span class="dark-text">{{ license.id || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="版权标题" :span="2">
          <el-link @click="viewCopyrightDetail" type="primary">
            {{ license.copyrightTitle || '-' }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="版权拥有者">
          <div class="address-cell">
            <el-icon><User /></el-icon>
            {{ formatAddress(license.ownerAddress) }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="被授权方">
          <div class="address-cell">
            <el-icon><UserFilled /></el-icon>
            {{ formatAddress(license.licenseeAddress) }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="授权类型">
          <el-tag :type="license.isExclusive ? 'danger' : 'primary'">
            {{ license.isExclusive ? '独占授权' : '普通授权' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="授权状态">
          <el-tag :type="getStatusType(license.status)">
            {{ getStatusText(license.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="授权开始时间" content-class-name="content-dark">
          <span class="dark-text time-text">{{ formatFullTime(license.startTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="授权结束时间" content-class-name="content-dark">
          <span class="dark-text time-text">{{ formatFullTime(license.endTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="授权期限" :span="2">
          <div class="duration-info">
            <span>{{ calculateDuration() }}</span>
            <el-tag v-if="isExpired" type="danger" size="small">已过期</el-tag>
            <el-tag v-else-if="license.status === 'ACTIVE'" type="success" size="small">生效中</el-tag>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 审批流程时间线 -->
    <div class="detail-section card-container">
      <h2 class="section-title">
        <el-icon><Clock /></el-icon>
        审批流程
      </h2>
      <el-timeline class="approval-timeline">
        <el-timeline-item
          :timestamp="formatFullTime(license.createTime)"
          placement="top"
          :type="getTimelineType('created')"
          :icon="getTimelineIcon('created')"
        >
          <el-card>
            <h4>提交申请</h4>
            <p>被授权方提交了授权申请</p>
            <p class="timeline-address">申请人：{{ formatAddress(license.licenseeAddress) }}</p>
          </el-card>
        </el-timeline-item>

        <el-timeline-item
          v-if="license.status !== 'PENDING'"
          :timestamp="formatFullTime(license.approveTime)"
          placement="top"
          :type="getTimelineType('approved')"
          :icon="getTimelineIcon('approved')"
        >
          <el-card>
            <h4>{{ license.status === 'REVOKED' ? '已拒绝' : '已批准' }}</h4>
            <p v-if="license.status === 'REVOKED'">
              版权拥有者拒绝了该授权申请
            </p>
            <p v-else>
              版权拥有者批准了该授权申请
            </p>
            <p class="timeline-address">操作人：{{ formatAddress(license.ownerAddress) }}</p>
          </el-card>
        </el-timeline-item>

        <el-timeline-item
          v-if="license.status === 'ACTIVE' || license.status === 'REVOKED' || license.status === 'EXPIRED'"
          :timestamp="formatFullTime(license.activateTime)"
          placement="top"
          :type="getTimelineType('activated')"
          :icon="getTimelineIcon('activated')"
        >
          <el-card>
            <h4>{{ license.status === 'REVOKED' ? '已撤销' : '已激活' }}</h4>
            <p v-if="license.status === 'REVOKED'">
              版权拥有者撤销了该授权
            </p>
            <p v-else-if="license.status === 'EXPIRED'">
              授权已过期
            </p>
            <p v-else>
              授权已激活，开始生效
            </p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 版税记录 -->
    <div class="detail-section card-container">
      <h2 class="section-title">
        <el-icon><Coin /></el-icon>
        版税记录
      </h2>
      <el-table
        :data="royaltyRecords"
        stripe
        style="width: 100%; table-layout: auto;"
        v-loading="royaltiesLoading"
      >
        <el-table-column label="支付方" width="180">
          <template #default="{ row }">
            <div class="address-cell">
              <el-icon><User /></el-icon>
              {{ formatAddress(row.payerAddress) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收款方" width="180">
          <template #default="{ row }">
            <div class="address-cell">
              <el-icon><UserFilled /></el-icon>
              {{ formatAddress(row.payeeAddress) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="150">
          <template #default="{ row }">
            <div class="amount-cell">
              <span class="amount-value">{{ row.amount }}</span>
              <el-tag size="small" type="info">{{ row.currency }}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="timestamp" label="记录时间">
          <template #default="{ row }">
            {{ formatFullTime(row.timestamp) }}
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!royaltiesLoading && royaltyRecords.length === 0" description="暂无版税记录" />
    </div>

    <!-- 操作历史 -->
    <div class="detail-section card-container">
      <h2 class="section-title">
        <el-icon><List /></el-icon>
        操作历史
      </h2>
      <el-table
        :data="operationHistory"
        stripe
        style="width: 100%; table-layout: auto;"
      >
        <el-table-column prop="operation" label="操作" width="120">
          <template #default="{ row }">
            <el-tag :type="row.tagType" size="small">{{ row.operation }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="180">
          <template #default="{ row }">
            <div class="address-cell">
              <el-icon><User /></el-icon>
              {{ formatAddress(row.operator) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="timestamp" label="时间" width="180">
          <template #default="{ row }">
            {{ formatFullTime(row.timestamp) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Document,
  Clock,
  Coin,
  List,
  User,
  UserFilled,
  Select,
  CloseBold,
  VideoPlay,
  RemoveFilled
} from '@element-plus/icons-vue'
import {
  getLicenseDetail,
  approveLicense,
  rejectLicense,
  activateLicense,
  revokeLicense
} from '@/api/license'
import { getCopyrightDetail } from '@/api/copyright'
import { getRoyaltiesByCopyright } from '@/api/royalty'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const royaltiesLoading = ref(false)

const license = ref({})
const royaltyRecords = ref([])
const operationHistory = ref([])
const copyrightDetail = ref({})

// 权限控制：判断当前用户是否是版权拥有者
const isCopyrightOwner = computed(() => {
  return userStore.address?.toLowerCase() === copyrightDetail.value.ownerAddress?.toLowerCase()
})

// 判断当前用户是否是被授权方
const isLicensee = computed(() => {
  return userStore.address?.toLowerCase() === license.value.licenseeAddress?.toLowerCase()
})

// 权限控制：只有授权审批员和系统管理员可以审批
const canApproveReject = computed(() => {
  return userStore.isLicenseApprover || userStore.isSystemAdmin
})

// 权限控制：有激活权限的角色可以激活
const canActivate = computed(() => {
  return userStore.hasPermission('activate')
})

// 权限控制：只有授权管理员和系统管理员可以撤销
const canRevoke = computed(() => {
  return userStore.isLicenseManager || userStore.isSystemAdmin
})

// 判断是否已过期
const isExpired = computed(() => {
  if (!license.value.endTime) return false
  return Date.now() > license.value.endTime * 1000
})

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

const getStatusType = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'info',
    ACTIVE: 'success',
    REVOKED: 'danger',
    EXPIRED: 'info'
  }
  return map[status] || 'info'
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

const getTimelineType = (step) => {
  if (license.value.status === 'REVOKED') return 'danger'
  if (license.value.status === 'EXPIRED') return 'info'
  return 'primary'
}

const getTimelineIcon = (step) => {
  return null
}

const calculateDuration = () => {
  if (!license.value.startTime || !license.value.endTime) return '-'
  const start = dayjs(license.value.startTime * 1000)
  const end = dayjs(license.value.endTime * 1000)
  const days = end.diff(start, 'day')
  return `${days} 天`
}

const loadLicense = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('缺少授权ID')
    router.push('/licenses')
    return
  }

  loading.value = true
  try {
    console.log('正在加载授权详情，ID:', id, '类型:', typeof id)

    // 确保ID是数字类型
    const licenseId = parseInt(id)
    console.log('转换后的ID:', licenseId, '类型:', typeof licenseId)

    const result = await getLicenseDetail(licenseId)
    console.log('API返回的原始数据:', result)

    // mockResponse 返回 { code, msg, data }，需要手动提取 data
    const data = result.data !== undefined ? result.data : result
    console.log('提取后的数据:', data)

    if (!data || Object.keys(data).length === 0) {
      throw new Error('授权记录不存在')
    }

    license.value = data
    console.log('授权详情加载成功:', license.value)

    // 加载版权详情以获取拥有者信息
    if (license.value.copyrightId) {
      try {
        console.log('正在加载版权详情，ID:', license.value.copyrightId)
        const copyrightResult = await getCopyrightDetail(license.value.copyrightId)
        const copyrightData = copyrightResult.data !== undefined ? copyrightResult.data : copyrightResult
        copyrightDetail.value = copyrightData || {}
        console.log('版权详情加载成功:', copyrightDetail.value)
      } catch (error) {
        console.error('加载版权详情失败:', error)
        // 不阻塞主流程，版权详情加载失败不影响页面显示
        copyrightDetail.value = {
          ownerAddress: license.value.ownerAddress // 从授权记录中获取
        }
      }
    } else {
      // 如果没有 copyrightId，尝试从授权记录中获取 ownerAddress
      copyrightDetail.value = {
        ownerAddress: license.value.ownerAddress
      }
    }

    // 生成操作历史
    generateOperationHistory()
  } catch (error) {
    console.error('加载授权详情失败:', error)
    ElMessage.error(error.message || '加载失败')
    // 延迟跳转，让用户看到错误信息
    setTimeout(() => {
      router.push('/licenses')
    }, 1500)
  } finally {
    loading.value = false
  }
}

const loadRoyaltyRecords = async () => {
  // 如果copyrightId不存在，不加载版税记录
  if (!license.value.copyrightId) {
    console.log('copyrightId不存在，跳过版税记录加载')
    return
  }

  royaltiesLoading.value = true
  try {
    const result = await getRoyaltiesByCopyright(license.value.copyrightId)
    const data = result.data !== undefined ? result.data : result
    royaltyRecords.value = (data || []).filter(
      item => item.copyrightId === String(license.value.copyrightId)
    )
  } catch (error) {
    console.error('加载版税记录失败:', error)
  } finally {
    royaltiesLoading.value = false
  }
}

const generateOperationHistory = () => {
  const history = []

  // 提交申请
  history.push({
    operation: '提交申请',
    operator: license.value.licenseeAddress || '-',
    remark: '提交授权申请',
    timestamp: license.value.createTime,
    tagType: 'info'
  })

  // 批准/拒绝
  if (license.value.status !== 'PENDING' && license.value.approveTime) {
    const ownerAddress = copyrightDetail.value?.ownerAddress || license.value.ownerAddress || '-'
    history.push({
      operation: license.value.status === 'REVOKED' ? '拒绝申请' : '批准申请',
      operator: ownerAddress,
      remark: license.value.status === 'REVOKED' ? '版权拥有者拒绝申请' : '版权拥有者批准申请',
      timestamp: license.value.approveTime,
      tagType: license.value.status === 'REVOKED' ? 'danger' : 'success'
    })
  }

  // 激活/撤销
  if (license.value.activateTime) {
    history.push({
      operation: license.value.status === 'REVOKED' ? '撤销授权' : '激活授权',
      operator: license.value.licenseeAddress || '-',
      remark: license.value.status === 'REVOKED' ? '版权拥有者撤销授权' : '被授权方激活授权',
      timestamp: license.value.activateTime,
      tagType: license.value.status === 'REVOKED' ? 'warning' : 'success'
    })
  }

  operationHistory.value = history.sort((a, b) => b.timestamp - a.timestamp)
}

const handleApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确认批准授权申请 #${license.value.contractId}？`,
      '批准授权',
      { confirmButtonText: '确认批准', cancelButtonText: '取消', type: 'success' }
    )

    await approveLicense(license.value.id)
    ElMessage.success('授权已批准')
    await loadLicense()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批准失败:', error)
      ElMessage.error(error.message || '批准失败')
    }
  }
}

const handleReject = async () => {
  try {
    await ElMessageBox.confirm(
      `确认拒绝授权申请 #${license.value.contractId}？`,
      '拒绝授权',
      { confirmButtonText: '确认拒绝', cancelButtonText: '取消', type: 'warning' }
    )

    await rejectLicense(license.value.id)
    ElMessage.success('授权已拒绝')
    await loadLicense()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝失败:', error)
      ElMessage.error(error.message || '拒绝失败')
    }
  }
}

const handleActivate = async () => {
  try {
    await ElMessageBox.confirm(
      `确认激活授权 #${license.value.contractId}？激活后授权开始生效。`,
      '激活授权',
      { confirmButtonText: '确认激活', cancelButtonText: '取消', type: 'success' }
    )

    await activateLicense(license.value.id)
    ElMessage.success('授权已激活')
    await loadLicense()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('激活失败:', error)
      ElMessage.error(error.message || '激活失败')
    }
  }
}

const handleRevoke = async () => {
  try {
    await ElMessageBox.confirm(
      `确认撤销授权 #${license.value.contractId}？撤销后将无法恢复。`,
      '撤销授权',
      { confirmButtonText: '确认撤销', cancelButtonText: '取消', type: 'warning' }
    )

    await revokeLicense(license.value.id)
    ElMessage.success('授权已撤销')
    await loadLicense()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销失败:', error)
      ElMessage.error(error.message || '撤销失败')
    }
  }
}

const viewCopyrightDetail = () => {
  router.push(`/copyrights/detail/${license.value.copyrightId}`)
}

const goBack = () => {
  router.push('/licenses')
}

onMounted(async () => {
  await loadLicense()
  // 等待授权信息加载完成后再加载版税记录
  if (license.value.copyrightId) {
    await loadRoyaltyRecords()
  }
})
</script>

<style lang="scss" scoped>
.license-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  overflow-x: hidden;

  // 强制使用浅色模式，防止系统暗色模式影响文本可读性
  color-scheme: light !important;

  // 全局字体优化和颜色强制
  * {
    -webkit-font-smoothing: antialiased !important;
    -moz-osx-font-smoothing: grayscale !important;
    text-rendering: optimizeLegibility !important;
  }

  // 优化所有文本显示
  :deep(.el-descriptions),
  :deep(.el-timeline),
  :deep(.el-table) {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif !important;
    color-scheme: light !important;
  }

  .breadcrumb-nav {
    margin-bottom: var(--spacing-lg);
    padding: var(--spacing-sm) var(--spacing-md);
    background: rgba(245, 243, 255, 0.8);
    border-radius: var(--radius-md);
    border: 1px solid rgba(102, 126, 234, 0.2);

    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: #374151 !important;
        font-weight: 600;
        transition: color 0.3s ease;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;

        &:hover {
          color: #4f46e5 !important;
        }

        &.is-link {
          color: #4f46e5 !important;

          &:hover {
            color: #4338ca !important;
          }
        }
      }

      &:last-child {
        .el-breadcrumb__inner {
          color: #111827 !important;
          font-weight: 700;
          cursor: default;
        }
      }
    }

    :deep(.el-breadcrumb__separator) {
      color: #6b7280 !important;
      margin: 0 8px;
    }
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--spacing-xl);
    padding-bottom: var(--spacing-lg);
    border-bottom: 2px solid var(--border-primary);

    .header-left {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);

      .page-title {
        font-size: 28px;
        font-weight: 700;
        color: #111827 !important;
        background: var(--gradient-purple);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin: 0;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;

        // 降级方案：如果背景渐变不生效
        @supports not (background-clip: text) {
          color: #667eea !important;
          -webkit-text-fill-color: #667eea;
        }
      }

      .el-tag {
        font-size: 14px;
        padding: 8px 16px;
        font-weight: 700;
        border: 1px solid var(--accent-cyan);
        color: #111827 !important;
        background: #ffffff !important;
      }
    }

    .header-right {
      display: flex;
      gap: var(--spacing-sm);
    }
  }

  .detail-section {
    margin-bottom: var(--spacing-xl);
    overflow: hidden;

    .section-title {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      font-size: 18px;
      font-weight: 700;
      color: #111827 !important;
      margin-bottom: var(--spacing-lg);
      padding-bottom: var(--spacing-sm);
      border-bottom: 2px solid rgba(102, 126, 234, 0.2);
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;

      .el-icon {
        color: var(--accent-primary);
        font-size: 20px;
      }
    }

    .detail-descriptions {
      // 优化整体可读性 - 针对性覆盖 Element Plus 默认样式
      :deep(.el-descriptions) {
        .el-descriptions__label {
          &.desc-label {
            background: linear-gradient(135deg, #ede9fe 0%, #e0e7ff 100%) !important;
            color: #111827 !important;
            font-weight: 700 !important;
            font-size: 14px !important;
            padding: 14px 18px !important;
            border-right: 2px solid #c7d2fe !important;
            border-bottom: 1px solid #c7d2fe !important;
            -webkit-font-smoothing: antialiased !important;
            -moz-osx-font-smoothing: grayscale !important;
            text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;
          }

          &:not(.desc-label) {
            background: #f3f4f6 !important;
            color: #111827 !important;
            font-weight: 600 !important;
            font-size: 14px !important;
            padding: 14px 18px !important;
            -webkit-font-smoothing: antialiased !important;
            -moz-osx-font-smoothing: grayscale !important;
            text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;
          }
        }

        .el-descriptions__content {
          color: #111827 !important;
          font-size: 14px !important;
          font-weight: 600 !important;
          padding: 14px 18px !important;
          background: #ffffff !important;
          border-bottom: 1px solid #e5e7eb !important;
          -webkit-font-smoothing: antialiased !important;
          -moz-osx-font-smoothing: grayscale !important;
          text-shadow: 0 0 1px rgba(0,0,0,0.05) !important;

          // 确保所有子元素也继承正确的颜色
          * {
            color: #111827 !important;
          }

          .el-tag,
          .el-link {
            color: inherit !important;
          }
        }

        .el-descriptions__body {
          .el-descriptions__table {
            background: #ffffff !important;
            border: 1px solid #e5e7eb !important;

            .el-descriptions__row {
              background: #ffffff !important;
              border-bottom: 1px solid #e5e7eb !important;

              &:last-child {
                border-bottom: none !important;
              }

              .el-descriptions__cell {
                background: #ffffff !important;
                border-color: #e5e7eb !important;
                color: #111827 !important;
                word-break: break-word !important;
                line-height: 1.8 !important;
                padding: 14px 18px !important;
                font-weight: 500 !important;
                -webkit-font-smoothing: antialiased !important;
                -moz-osx-font-smoothing: grayscale !important;

                // 确保单元格内所有文本都清晰可见
                span,
                div,
                p {
                  color: #111827 !important;
                }
              }
            }
          }
        }
      }

      .address-cell {
        display: flex !important;
        align-items: center !important;
        gap: 8px !important;
        font-family: 'Consolas', 'Monaco', 'Courier New', monospace !important;
        font-size: 14px !important;
        font-weight: 700 !important;
        color: #0d0d0d !important;
        word-break: break-all !important;
        max-width: 100% !important;
        letter-spacing: 0.3px !important;
        line-height: 1.8 !important;
        -webkit-font-smoothing: antialiased !important;
        -moz-osx-font-smoothing: grayscale !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.15) !important;

        // 确保地址内的所有元素都可见
        span,
        .el-icon {
          color: #0d0d0d !important;
        }
      }

      // 链接样式
      :deep(.el-link) {
        color: #4f46e5 !important;
        font-weight: 700 !important;
        font-size: 14px !important;
        text-decoration: none !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.05) !important;

        &:hover {
          color: #4338ca !important;
          text-decoration: underline !important;
        }

        // 确保链接内的文本也清晰可见
        span {
          color: inherit !important;
        }
      }

      // 标签样式
      :deep(.el-tag) {
        font-weight: 700 !important;
        font-size: 13px !important;
        padding: 6px 14px !important;
        border-width: 2px !important;

        // 确保标签文本清晰可见
        span {
          color: inherit !important;
        }
      }

      // 深色文本类 - 用于ID和时间
      .dark-text {
        color: #000000 !important;
        font-weight: 700 !important;
        font-size: 15px !important;
        -webkit-font-smoothing: antialiased !important;
        -moz-osx-font-smoothing: grayscale !important;
        text-shadow: 0 0 2px rgba(0,0,0,0.3) !important;
        letter-spacing: 0.5px !important;
        display: inline-block !important;
      }

      // 时间文本特殊样式
      .time-text {
        font-family: 'Consolas', 'Monaco', 'Courier New', monospace !important;
        color: #000000 !important;
        font-size: 14px !important;
        font-weight: 700 !important;
        background: #ffffff !important;
        padding: 4px 8px !important;
        border-radius: 4px !important;
        border: 1px solid #d1d5db !important;
        -webkit-font-smoothing: antialiased !important;
        -moz-osx-font-smoothing: grayscale !important;
        text-shadow: 0 0 2px rgba(0,0,0,0.2) !important;
      }

      // content-dark 类样式
      :deep(.content-dark) {
        background: #ffffff !important;

        .el-descriptions__content__label {
          color: #000000 !important;
        }
      }

      .duration-info {
        display: flex;
        align-items: center;
        gap: var(--spacing-sm);
        font-weight: 700 !important;
        color: #111827 !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;

        span {
          color: #111827 !important;
        }
      }
    }

    .approval-timeline {
      padding: var(--spacing-lg) 0;

      :deep(.el-timeline-item__timestamp) {
        color: #000000 !important;
        font-size: 15px !important;
        font-weight: 700 !important;
        -webkit-font-smoothing: antialiased !important;
        -moz-osx-font-smoothing: grayscale !important;
        text-shadow: 0 0 2px rgba(0,0,0,0.3) !important;
        font-family: 'Consolas', 'Monaco', 'Courier New', monospace !important;
        background: #ffffff !important;
        padding: 4px 8px !important;
        border-radius: 4px !important;
        border: 1px solid #d1d5db !important;
        letter-spacing: 0.3px !important;
      }

      :deep(.el-card) {
        background: #ffffff !important;
        border: 1px solid #e5e7eb !important;
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1) !important;

        h4 {
          margin: 0 0 12px 0 !important;
          font-size: 16px !important;
          font-weight: 700 !important;
          color: #111827 !important;
          -webkit-font-smoothing: antialiased !important;
          -moz-osx-font-smoothing: grayscale !important;
          text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;
        }

        p {
          margin: 6px 0 !important;
          font-size: 14px !important;
          font-weight: 500 !important;
          color: #111827 !important;
          line-height: 1.8 !important;
          -webkit-font-smoothing: antialiased !important;
          -moz-osx-font-smoothing: grayscale !important;
          text-shadow: 0 0 1px rgba(0,0,0,0.05) !important;

          &.timeline-address {
            font-family: 'Consolas', 'Monaco', 'Courier New', monospace !important;
            font-size: 13px !important;
            font-weight: 700 !important;
            color: #000000 !important;
            word-break: break-all !important;
            letter-spacing: 0.3px !important;
            background: #f9fafb !important;
            padding: 8px 12px !important;
            border-radius: 4px !important;
            border: 1px solid #e5e7eb !important;
            text-shadow: 0 0 2px rgba(0,0,0,0.2) !important;
          }
        }

        // 确保卡片内所有文本都清晰可见
        span,
        div {
          color: #111827 !important;
        }
      }

      // 确保时间轴图标清晰可见
      :deep(.el-timeline-item__marker) {
        color: #667eea !important;
      }
    }

    .address-cell {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 14px;
      font-weight: 700;
      color: #0d0d0d !important;
      word-break: break-all;
      letter-spacing: 0.3px;
      line-height: 1.8;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      max-width: 100%;
      text-shadow: 0 0 1px rgba(0,0,0,0.15) !important;

      // 确保地址内的所有元素都可见
      span,
      .el-icon {
        color: #0d0d0d !important;
      }
    }

    :deep(.el-table) {
      // 表格背景
      .el-table__body-wrapper {
        overflow-x: auto;
        background: #ffffff !important;
      }

      // 表格行
      .el-table__row {
        background: #ffffff !important;

        &:hover {
          background: #f9fafb !important;
        }
      }

      .el-table__cell {
        word-break: break-word;
        font-size: 14px;
        font-weight: 600;
        color: #111827 !important;
        line-height: 1.8;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        background: #ffffff !important;
        border-color: #e5e7eb !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.05) !important;

        // 确保单元格内所有文本都清晰可见
        span,
        div,
        p {
          color: #111827 !important;
        }
      }

      .el-table__header th {
        font-weight: 700;
        color: #111827 !important;
        font-size: 14px;
        background: #f9fafb !important;
        border-color: #e5e7eb !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;

        .cell {
          color: #111827 !important;
        }
      }
    }

    .amount-cell {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);

      .amount-value {
        font-size: 16px;
        font-weight: 700;
        color: #059669 !important;
        text-shadow: 0 0 1px rgba(0,0,0,0.1) !important;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
      }
    }
  }
}

@media (max-width: 768px) {
  .license-detail {
    padding: 16px;

    .page-title {
      font-size: 22px;
    }

    .detail-descriptions {
      :deep(.el-descriptions__label) {
        &.desc-label {
          font-size: 13px !important;
          padding: 10px 12px !important;
          color: #111827 !important;
        }

        &:not(.desc-label) {
          color: #111827 !important;
        }
      }

      :deep(.el-descriptions__content) {
        font-size: 13px !important;
        padding: 10px 12px !important;
        color: #111827 !important;
      }

      .address-cell {
        font-size: 12px !important;
        color: #0d0d0d !important;
      }
    }

    .approval-timeline {
      :deep(.el-timeline-item__timestamp) {
        font-size: 13px !important;
        color: #111827 !important;
      }

      :deep(.el-card) {
        h4 {
          font-size: 15px !important;
          color: #111827 !important;
        }

        p {
          font-size: 13px !important;
          color: #111827 !important;

          &.timeline-address {
            font-size: 11px !important;
            color: #0d0d0d !important;
          }
        }
      }
    }

    :deep(.el-table) {
      .el-table__cell {
        font-size: 13px !important;
        color: #111827 !important;
      }

      .el-table__header th {
        color: #111827 !important;
      }
    }

    .page-header {
      flex-direction: column;
      gap: var(--spacing-md);
      align-items: stretch;

      .header-left {
        flex-direction: column;
        align-items: flex-start;
      }

      .header-right {
        justify-content: stretch;

        .el-button {
          flex: 1;
        }
      }
    }

    .detail-section {
      .detail-descriptions {
        :deep(.el-descriptions__label) {
          width: 120px;
        }
      }
    }
  }
}
</style>
