<template>
  <div class="copyright-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">版权管理</h1>
        <p class="page-subtitle">管理和查看您的区块链版权存证</p>
      </div>
      <el-button
        v-if="canRegisterCopyright"
        type="primary"
        size="large"
        :icon="DocumentAdd"
        @click="goToRegister"
      >
        注册新版权
      </el-button>
    </div>

    <!-- Search Bar Component -->
    <SearchBar
      v-model="searchKeyword"
      placeholder="搜索版权标题、作者..."
      :show-button="true"
      button-text="搜索"
      :loading="loading"
      @search="handleSearch"
      @clear="clearSearch"
    >
      <template #filters>
        <el-select v-model="sortBy" placeholder="排序方式" @change="loadList">
          <el-option label="最新注册" value="newest" />
          <el-option label="最早注册" value="oldest" />
          <el-option label="标题 A-Z" value="title_asc" />
          <el-option label="标题 Z-A" value="title_desc" />
        </el-select>
      </template>
    </SearchBar>

    <!-- Stats Bar -->
    <div class="stats-bar">
      <Transition name="fade" appear>
        <div class="stat-item">
          <div class="stat-icon stat-purple">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ animatedTotal }}</div>
            <div class="stat-label">版权总数</div>
          </div>
        </div>
      </Transition>
      <Transition name="fade" appear>
        <div class="stat-item" style="transition-delay: 100ms">
          <div class="stat-icon stat-blue">
            <el-icon><Grid /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ copyrightList.length }}</div>
            <div class="stat-label">当前显示</div>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Loading Skeleton -->
    <LoadingSkeleton
      v-if="loading"
      variant="list"
      :items="pagination.pageSize"
    />

    <!-- Copyright Grid -->
    <TransitionGroup
      v-else
      name="stagger"
      tag="div"
      class="copyright-grid"
    >
      <div
        v-for="(item, index) in copyrightList"
        :key="item.id"
        :class="`stagger-${(index % 9) + 1}`"
        class="copyright-card"
        @click="viewDetail(item)"
      >
        <div class="card-header">
          <div class="copyright-id">
            <el-icon><InfoFilled /></el-icon>
            ID: {{ item.contractId || '-' }}
          </div>
          <div class="card-badge">已上链</div>
        </div>

        <div class="card-body">
          <h3 class="card-title">{{ item.title || '未命名版权' }}</h3>

          <div class="card-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>作者: {{ item.author || '-' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Wallet /></el-icon>
              <span>{{ formatAddress(item.ownerAddress) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(item.registerTime) }}</span>
            </div>
          </div>

          <div class="card-description">
            {{ item.description || '暂无描述信息' }}
          </div>
        </div>

        <div class="card-footer">
          <el-button
            text
            type="primary"
            @click.stop="viewDetail(item)"
            class="card-action-btn"
          >
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </TransitionGroup>

    <!-- Empty State -->
    <Transition v-if="!loading && copyrightList.length === 0" name="fade">
      <div class="empty-state">
        <EmptyState
          type="no-data"
          title="暂无版权记录"
          :description="canRegisterCopyright ? '开始注册您的第一个区块链版权存证' : '暂无数据'"
          :show-action="canRegisterCopyright"
          action-text="注册第一个版权"
          @action="goToRegister"
        />
      </div>
    </Transition>

    <!-- Pagination -->
    <div v-if="pagination.total > 0" class="pagination-bar">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCopyrightList, searchCopyrights } from '@/api/copyright'
import { useUserStore } from '@/stores/user'
import { useDebounceFn } from '@/utils/performance'
import { DocumentAdd, Document, Grid, InfoFilled, User, Wallet, Clock, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import SearchBar from '@/components/SearchBar.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const searchKeyword = ref('')
const sortBy = ref('newest')
const copyrightList = ref([])
const animatedTotal = ref(0)

// Permissions
const canRegisterCopyright = computed(() => {
  return userStore.isContentOwner || userStore.isLicenseManager || userStore.isSystemAdmin
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// Format utilities
const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return dayjs(timestamp * 1000).format('YYYY-MM-DD HH:mm')
}

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 6)}...${address.substring(address.length - 4)}`
}

// Debounced search
const debouncedSearch = useDebounceFn(async () => {
  await handleSearch()
}, 500)

watch(searchKeyword, () => {
  debouncedSearch()
})

// Load data
const loadList = async () => {
  loading.value = true
  try {
    const data = await getCopyrightList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      sortBy: sortBy.value
    })
    copyrightList.value = data.records || []
    pagination.total = data.total || 0

    // Animate total count
    animateTotal(pagination.total)
  } catch (error) {
    console.error('加载版权列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadList()
    return
  }

  loading.value = true
  try {
    const results = await searchCopyrights(searchKeyword.value)
    copyrightList.value = results || []
    pagination.total = res.data?.length || 0
    animateTotal(pagination.total)
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  loadList()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadList()
  // Scroll to top
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadList()
}

// Animate total counter
const animateTotal = (target) => {
  const start = animatedTotal.value
  const duration = 500
  const startTime = performance.now()

  const animate = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3) // ease-out cubic

    animatedTotal.value = Math.floor(start + (target - start) * eased)

    if (progress < 1) {
      requestAnimationFrame(animate)
    } else {
      animatedTotal.value = target
    }
  }

  requestAnimationFrame(animate)
}

// Navigation
const goToRegister = () => {
  router.push('/copyrights/register')
}

const viewDetail = (item) => {
  router.push(`/copyrights/detail/${item.id}`)
}

// Lifecycle
onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.copyright-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-2xl);
}

// ============================================
// Page Header
// ============================================
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-2xl);
  gap: var(--spacing-xl);

  .header-content {
    flex: 1;

    .page-title {
      font-size: var(--font-size-3xl);
      font-weight: var(--font-weight-bold);
      color: var(--text-primary);
      margin: 0 0 var(--spacing-sm) 0;
    }

    .page-subtitle {
      font-size: var(--font-size-md);
      color: var(--text-secondary);
      margin: 0;
    }
  }
}

// ============================================
// Stats Bar
// ============================================
.stats-bar {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  background: linear-gradient(135deg, rgba(245, 243, 255, 0.95) 0%, rgba(249, 250, 255, 0.95) 100%) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(102, 126, 234, 0.2) !important;
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.08);
  position: relative;

  // 顶部光效边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    opacity: 0.6;
    border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  }

  // 暗色模式适配
  @media (prefers-color-scheme: dark) {
    background: linear-gradient(135deg, rgba(45, 45, 75, 0.95) 0%, rgba(40, 40, 70, 0.95) 100%) !important;
    border-color: rgba(102, 126, 234, 0.3) !important;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);

    .stat-value {
      color: #e5e7eb !important;
    }

    .stat-label {
      color: #9ca3af !important;
    }
  }

  &:hover {
    border-color: var(--color-primary-600);
    box-shadow: var(--shadow-glow);
    transform: translateY(-2px);

    &::before {
      opacity: 1;
    }
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;

    &.stat-purple {
      background: var(--gradient-primary);
      color: var(--text-inverse);
    }

    &.stat-blue {
      background: linear-gradient(135deg, #3b82f6, #06b6d4);
      color: var(--text-inverse);
    }
  }

  .stat-info {
    .stat-value {
      font-size: var(--font-size-2xl);
      font-weight: var(--font-weight-bold);
      color: #1f2937;
      line-height: 1.2;
    }

    .stat-label {
      font-size: var(--font-size-sm);
      color: #9ca3af;
      margin-top: var(--spacing-xs);
    }
  }
}

// ============================================
// Copyright Grid
// ============================================
.copyright-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
  min-height: 200px;
}

.copyright-card {
  background: linear-gradient(135deg, rgba(245, 243, 255, 0.95) 0%, rgba(249, 250, 255, 0.95) 100%) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(102, 126, 234, 0.2) !important;
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-base);
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.08);

  // 暗色模式适配
  @media (prefers-color-scheme: dark) {
    background: linear-gradient(135deg, rgba(45, 45, 75, 0.95) 0%, rgba(40, 40, 70, 0.95) 100%) !important;
    border-color: rgba(102, 126, 234, 0.3) !important;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);

    .card-title {
      color: #e5e7eb !important;
    }

    .meta-item {
      color: #9ca3af !important;
    }

    .card-description {
      color: #9ca3af !important;
    }

    .card-header {
      border-bottom-color: rgba(102, 126, 234, 0.25) !important;
    }

    .card-footer {
      border-top-color: rgba(102, 126, 234, 0.25) !important;
    }
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: var(--gradient-primary);
    opacity: 0.8;
    transition: opacity var(--transition-base);
  }

  &:hover {
    border-color: var(--color-primary-600);
    transform: translateY(-4px);
    box-shadow: var(--shadow-glow);

    &::before {
      opacity: 1;
    }

    .card-action-btn {
      transform: translateX(4px);
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--spacing-lg) var(--spacing-xl);
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);

    .copyright-id {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
      font-size: var(--font-size-xs);
      color: #6b7280;
      font-weight: var(--font-weight-medium);

      @media (prefers-color-scheme: dark) {
        color: #9ca3af !important;
      }

      .el-icon {
        color: var(--color-primary-600);
      }
    }

    .card-badge {
      padding: var(--spacing-xs) var(--spacing-md);
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
      font-size: var(--font-size-xs);
      font-weight: var(--font-weight-semibold);
      border-radius: var(--radius-full);
    }
  }

  .card-body {
    padding: var(--spacing-xl);
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .card-title {
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-semibold);
    color: #1f2937;
    margin: 0;
    line-height: var(--line-height-tight);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .card-meta {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);

    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      font-size: var(--font-size-sm);
      color: #6b7280;

      .el-icon {
        color: var(--color-primary-600);
        flex-shrink: 0;
        font-size: 14px;
      }

      span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .card-description {
    font-size: var(--font-size-sm);
    color: #9ca3af;
    line-height: var(--line-height-relaxed);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    flex: 1;
  }

  .card-footer {
    padding: var(--spacing-lg) var(--spacing-xl);
    border-top: 1px solid rgba(102, 126, 234, 0.1);
  }

  .card-action-btn {
    width: 100%;
    padding: 8px 0;
    color: #667eea !important;
    font-weight: 600;
    background: transparent !important;
    border: none;
    position: relative;
    transition: all var(--transition-base);

    // 添加左侧装饰条
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 0;
      background: var(--gradient-primary);
      transition: height var(--transition-base);
      border-radius: 0 2px 2px 0;
    }

    &:hover {
      color: #764ba2 !important;
      background: rgba(102, 126, 234, 0.05) !important;

      &::before {
        height: 20px;
      }
    }

    // 确保图标和文字清晰可见
    .el-icon {
      color: inherit;
    }
  }
}

// ============================================
// Empty State
// ============================================
.empty-state {
  grid-column: 1 / -1;
  padding: var(--spacing-3xl) var(--spacing-xl);
}

// ============================================
// Pagination
// ============================================
.pagination-bar {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
}

// ============================================
// Animations
// ============================================
.stagger-enter-active {
  animation: scaleIn 0.4s ease-out backwards;
}

.fade-enter-active {
  animation: fadeIn 0.4s ease-out;
}

// ============================================
// Responsive Design
// ============================================
@media (max-width: 1200px) {
  .copyright-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .copyright-page {
    padding: var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-md);

    .header-content {
      .page-title {
        font-size: var(--font-size-2xl);
      }
    }
  }

  .stats-bar {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .copyright-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }

  .stat-item {
    padding: var(--spacing-md);
  }
}

// Reduced motion
@media (prefers-reduced-motion: reduce) {
  .copyright-card:hover {
    transform: none;
  }

  .stat-item:hover {
    transform: none;
  }

  .card-action-btn {
    transition: none;
  }
}
</style>
