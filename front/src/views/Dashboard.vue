<template>
  <div class="dashboard-page">
    <!-- Hero Section with Gradient Background -->
    <section class="hero-section" :class="{ 'hero-section--loading': loading }">
      <div class="hero-background">
        <div class="hero-gradient"></div>
        <div class="hero-pattern"></div>
      </div>

      <div class="hero-content">
        <!-- Skeleton Loading -->
        <template v-if="loading">
          <div class="hero-skeleton">
            <div class="hero-skeleton__title"></div>
            <div class="hero-skeleton__subtitle"></div>
            <div class="hero-skeleton__actions"></div>
          </div>
        </template>

        <!-- Actual Content -->
        <template v-else>
          <Transition name="slide-up" appear>
            <div key="hero-content">
              <h1 class="hero-title">
                <span class="hero-greeting">欢迎回来，</span>
                <span class="hero-name">{{ formattedAddress }}</span>
              </h1>
              <p class="hero-subtitle">
                您的版权管理控制中心 - 安全、透明、高效
              </p>
              <div class="hero-actions">
                <el-button
                  type="primary"
                  size="large"
                  :icon="DocumentAdd"
                  @click="goToRegister"
                  class="hero-button"
                >
                  注册新版权
                </el-button>
                <el-button
                  size="large"
                  :icon="Key"
                  @click="goToApply"
                  class="hero-button hero-button--secondary"
                >
                  申请授权
                </el-button>
              </div>
            </div>
          </Transition>
        </template>
      </div>

      <!-- Stats Cards -->
      <div class="hero-stats">
        <TransitionGroup name="stagger" appear>
          <StatCard
            v-for="(stat, index) in statsCards"
            :key="stat.key"
            :title="stat.title"
            :value="stat.value"
            :icon="stat.icon"
            :color="stat.color"
            :trend="stat.trend"
            :loading="loading"
            :class="`stagger-${index + 1}`"
            class="hero-stat-card"
            @click="stat.onClick"
          />
        </TransitionGroup>
      </div>
    </section>

    <!-- Quick Access Section -->
    <section class="quick-access-section">
      <h2 class="section-title">
        <span>快捷功能</span>
        <div class="section-title__line"></div>
      </h2>

      <Transition name="fade" appear>
        <div class="feature-grid">
          <div
            v-for="feature in visibleFeatures"
            :key="feature.key"
            class="feature-card"
            @click="feature.onClick"
          >
            <div class="feature-icon" :class="`feature-icon--${feature.color}`">
              <component :is="feature.icon" :size="32" />
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
            <div class="feature-action">
              <span>立即使用</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </Transition>
    </section>

    <!-- Recent Activity Section -->
    <section class="recent-activity-section">
      <div class="activity-grid">
        <!-- Recent Copyrights -->
        <Transition name="slide-in" appear>
          <div class="activity-card" key="copyrights">
            <div class="activity-header">
              <h3 class="activity-title">
                <el-icon><Document /></el-icon>
                最近版权
              </h3>
              <el-button
                text
                size="small"
                @click="goToCopyrights"
                class="view-all-btn"
              >
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>

            <!-- Loading Skeleton -->
            <LoadingSkeleton
              v-if="loading"
              variant="list"
              :items="3"
            />

            <!-- Copyright List -->
            <div v-else class="copyright-list">
              <TransitionGroup name="list" tag="div">
                <div
                  v-for="item in recentCopyrights"
                  :key="item.id"
                  class="copyright-item"
                  @click="viewCopyrightDetail(item)"
                >
                  <div class="copyright-icon">
                    <el-icon><DocumentChecked /></el-icon>
                  </div>
                  <div class="copyright-info">
                    <div class="copyright-name">{{ item.title }}</div>
                    <div class="copyright-meta">{{ item.author }} · {{ formatTime(item.registerTime) }}</div>
                  </div>
                </div>
              </TransitionGroup>

              <!-- Empty State -->
              <EmptyState
                v-if="recentCopyrights.length === 0"
                type="no-data"
                title="暂无版权记录"
                description="注册您的第一个版权，开始保护您的作品"
                :show-action="true"
                action-text="注册版权"
                @action="goToRegister"
              />
            </div>
          </div>
        </Transition>

        <!-- Activity Timeline -->
        <Transition name="slide-in" appear>
          <div class="activity-card" key="activities">
            <div class="activity-header">
              <h3 class="activity-title">
                <el-icon><Clock /></el-icon>
                最近活动
              </h3>
            </div>

            <div class="activity-timeline">
              <TransitionGroup name="timeline" tag="div">
                <div
                  v-for="activity in recentActivities"
                  :key="activity.id"
                  class="timeline-item"
                  :class="`timeline-${activity.type}`"
                >
                  <div class="timeline-dot">
                    <el-icon>
                      <component :is="activity.icon" />
                    </el-icon>
                  </div>
                  <div class="timeline-content">
                    <div class="timeline-title">{{ activity.title }}</div>
                    <div class="timeline-desc">{{ activity.description }}</div>
                    <div class="timeline-time">{{ formatTime(activity.timestamp) }}</div>
                  </div>
                </div>
              </TransitionGroup>
            </div>
          </div>
        </Transition>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, ROLES } from '@/stores/user'
import { useCountUp } from '@/composables/useCountUp'
import dayjs from 'dayjs'
import * as api from '@/api'
import {
  DocumentAdd,
  Key,
  Document,
  DocumentChecked,
  Clock,
  ArrowRight,
  CircleCheck,
  Warning,
  Coin
} from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'

const router = useRouter()
const userStore = useUserStore()

// Loading state
const loading = ref(true)

// Permissions
const visibleFeatures = computed(() => {
  const features = [
    {
      key: 'register',
      title: '版权注册',
      description: '快速将您的作品上链存证，获取不可篡改的版权证书',
      icon: 'Document',
      color: 'primary',
      onClick: goToRegister,
      show: userStore.isContentOwner || userStore.isLicenseManager || userStore.isSystemAdmin
    },
    {
      key: 'license',
      title: '申请授权',
      description: '为您的版权申请使用授权，开启商业化收益之路',
      icon: 'Key',
      color: 'success',
      onClick: goToApply,
      show: userStore.isContentOwner || userStore.isSystemAdmin
    },
    {
      key: 'royalty',
      title: '记录版税',
      description: '记录和管理版税收入，透明可追溯的收益流水',
      icon: 'Coin',
      color: 'warning',
      onClick: goToRoyalties,
      show: !userStore.isAuditor
    },
    {
      key: 'blockchain',
      title: '区块链查询',
      description: '查看区块链上的交易记录，确保数据真实可信',
      icon: 'Connection',
      color: 'info',
      onClick: goToBlockchain,
      show: true
    }
  ]

  return features.filter(f => f.show)
})

// Stats with animated values
const statsData = ref({
  copyrightCount: 0,
  licenseCount: 0,
  pendingCount: 0,
  totalRoyalty: 0
})

const statsCards = computed(() => [
  {
    key: 'copyrights',
    title: '我的版权',
    value: statsData.value.copyrightCount,
    icon: 'Document',
    color: 'primary',
    trend: 12,
    onClick: goToCopyrights
  },
  {
    key: 'licenses',
    title: '授权总数',
    value: statsData.value.licenseCount,
    icon: 'Key',
    color: 'success',
    trend: 8,
    onClick: () => router.push('/licenses')
  },
  {
    key: 'pending',
    title: '待审批',
    value: statsData.value.pendingCount,
    icon: 'Clock',
    color: 'warning',
    trend: -3,
    onClick: () => router.push('/licenses')
  },
  {
    key: 'royalty',
    title: '累计收益',
    value: `¥${statsData.value.totalRoyalty.toLocaleString()}`,
    icon: 'Coin',
    color: 'info',
    trend: 24,
    onClick: goToRoyalties
  }
])

// Recent data
const recentCopyrights = ref([])
const recentActivities = ref([
  {
    id: 1,
    title: '注册了新版权',
    description: '《数字艺术作品》已成功上链',
    timestamp: Date.now() / 1000 - 300,
    type: 'primary',
    icon: 'CircleCheck'
  },
  {
    id: 2,
    title: '授权申请已批准',
    description: '批准了用户 0x123...abc 的授权申请',
    timestamp: Date.now() / 1000 - 3600,
    type: 'success',
    icon: 'CircleCheck'
  },
  {
    id: 3,
    title: '收到版税支付',
    description: '收到 ¥500.00 版税收入',
    timestamp: Date.now() / 1000 - 7200,
    type: 'warning',
    icon: 'Coin'
  }
])

// Computed
const formattedAddress = computed(() => {
  const addr = userStore.address
  if (!addr) return '用户'
  return `${addr.substring(0, 6)}...${addr.substring(addr.length - 4)}`
})

// Methods
const formatTime = (timestamp) => {
  return dayjs(timestamp * 1000).format('MM-DD HH:mm')
}

const goToRegister = () => router.push('/copyrights/register')
const goToApply = () => router.push('/licenses/apply')
const goToRoyalties = () => router.push('/royalties')
const goToBlockchain = () => router.push('/explorer')
const goToCopyrights = () => router.push('/copyrights')
const viewCopyrightDetail = (item) => router.push(`/copyrights/detail/${item.id}`)

const loadDashboardData = async () => {
  loading.value = true

  try {
    const [copyrights, licenses] = await Promise.all([
      api.copyright.getCopyrightList({ pageNum: 1, pageSize: 100 }).catch(() => ({ total: 0, records: [] })),
      api.license.getLicenseList({ pageNum: 1, pageSize: 100 }).catch(() => ({ total: 0, records: [] }))
    ])

    // Simulate delay for animation
    await new Promise(resolve => setTimeout(resolve, 500))

    statsData.value = {
      copyrightCount: copyrights.total || 0,
      licenseCount: licenses.total || 0,
      pendingCount: licenses.records?.filter(l => l.status === 'PENDING').length || 0,
      totalRoyalty: 12500
    }

    recentCopyrights.value = copyrights.records?.slice(0, 5) || []
  } catch (error) {
    console.error('加载Dashboard数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
  position: relative;
}

// ============================================
// Hero Section
// ============================================
.hero-section {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-2xl);
  border-radius: var(--radius-2xl);
  overflow: hidden;

  &--loading {
    min-height: 400px;
  }
}

.hero-background {
  position: absolute;
  inset: 0;
  z-index: 0;

  .hero-gradient {
    position: absolute;
    inset: 0;
    background: var(--gradient-primary);
    opacity: 0.05;
  }

  .hero-pattern {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(circle at 1px 1px, rgba(102, 126, 234, 0.15) 1px, transparent 0);
    background-size: 40px 40px;
  }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  margin-bottom: var(--spacing-xl);

  .hero-greeting {
    display: block;
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-medium);
    color: var(--text-secondary);
    margin-bottom: var(--spacing-sm);
  }

  .hero-name {
    display: block;
    font-size: 48px;
    font-weight: var(--font-weight-bold);
    background: var(--gradient-primary);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1.2;
  }
}

.hero-subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
  line-height: var(--line-height-relaxed);
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.hero-button {
  height: 48px;
  padding: 0 var(--spacing-xl);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-xl);

  &--secondary {
    background: var(--bg-card);
    border: 2px solid var(--color-primary-600);
    color: var(--color-primary-600);

    &:hover {
      background: var(--color-primary-600);
      color: var(--text-inverse);
    }
  }
}

.hero-stats {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
  max-width: 100%;
  overflow: hidden;
  width: 100%;

  * {
    max-width: 100%;
    box-sizing: border-box;
  }
}

.hero-stat-card {
  animation: fadeIn var(--transition-slow) ease-out backwards;
  max-width: 100%;
  width: 100%;
  overflow: hidden;
}

// ============================================
// Sections
// ============================================
.section-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: relative;

  &__line {
    flex: 1;
    height: 2px;
    background: var(--gradient-primary);
    border-radius: var(--radius-full);
    opacity: 0.3;
  }
}

.quick-access-section {
  margin-bottom: var(--spacing-3xl);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
}

.feature-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  cursor: pointer;
  transition: all var(--transition-base);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);

  &:hover {
    border-color: var(--color-primary-600);
    transform: translateY(-4px);
    box-shadow: var(--shadow-glow);

    .feature-action {
      color: var(--color-primary-600);
      transform: translateX(4px);
    }
  }
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);

  &--primary {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
    color: var(--color-primary-600);
  }

  &--success {
    background: var(--color-success-light);
    color: var(--color-success);
  }

  &--warning {
    background: var(--color-warning-light);
    color: var(--color-warning);
  }

  &--info {
    background: var(--color-info-light);
    color: var(--color-info);
  }
}

.feature-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.feature-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: var(--line-height-relaxed);
  flex: 1;
}

.feature-action {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-primary-600);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-base);
}

// ============================================
// Activity Section
// ============================================
.recent-activity-section {
  margin-bottom: var(--spacing-3xl);
}

.activity-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
}

.activity-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  transition: all var(--transition-base);

  &:hover {
    box-shadow: var(--shadow-md);
  }
}

.activity-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-secondary);

  .activity-title {
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-semibold);
    color: var(--text-primary);
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    margin: 0;
  }

  .view-all-btn {
    color: var(--text-secondary);
    font-size: var(--font-size-sm);
    transition: all var(--transition-fast);

    &:hover {
      color: var(--color-primary-600);
    }
  }
}

.copyright-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.copyright-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    background: var(--bg-hover);
    transform: translateX(4px);
  }

  .copyright-icon {
    width: 40px;
    height: 40px;
    border-radius: var(--radius-lg);
    background: var(--color-primary-600);
    color: var(--text-inverse);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .copyright-info {
    flex: 1;
    min-width: 0;
  }

  .copyright-name {
    font-size: var(--font-size-md);
    font-weight: var(--font-weight-medium);
    color: var(--text-primary);
    margin-bottom: var(--spacing-xs);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .copyright-meta {
    font-size: var(--font-size-sm);
    color: var(--text-tertiary);
  }
}

.activity-timeline {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.timeline-item {
  display: flex;
  gap: var(--spacing-md);
  position: relative;

  &:not(:last-child)::before {
    content: '';
    position: absolute;
    left: 12px;
    top: 32px;
    bottom: -28px;
    width: 2px;
    background: var(--border-secondary);
  }

  .timeline-dot {
    width: 28px;
    height: 28px;
    border-radius: var(--radius-full);
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    z-index: 1;
    font-size: 14px;
  }

  &.timeline-primary .timeline-dot {
    background: var(--color-primary-600);
    color: var(--text-inverse);
    box-shadow: 0 0 0 4px var(--color-primary-600);
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
  }

  &.timeline-success .timeline-dot {
    background: var(--color-success);
    color: var(--text-inverse);
    box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.15);
  }

  &.timeline-warning .timeline-dot {
    background: var(--color-warning);
    color: var(--text-inverse);
    box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.15);
  }

  .timeline-content {
    flex: 1;
  }

  .timeline-title {
    font-size: var(--font-size-md);
    font-weight: var(--font-weight-semibold);
    color: var(--text-primary);
    margin-bottom: var(--spacing-xs);
  }

  .timeline-desc {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
    margin-bottom: var(--spacing-xs);
  }

  .timeline-time {
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
  }
}

// ============================================
// Hero Skeleton
// ============================================
.hero-skeleton {
  &__title {
    height: 48px;
    width: 60%;
    background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    border-radius: var(--radius-md);
    margin-bottom: var(--spacing-lg);
  }

  &__subtitle {
    height: 20px;
    width: 40%;
    background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
    border-radius: var(--radius-md);
    margin-bottom: var(--spacing-xl);
  }

  &__actions {
    display: flex;
    gap: var(--spacing-md);

    &::before,
    &::after {
      content: '';
      width: 140px;
      height: 48px;
      background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
      border-radius: var(--radius-xl);
    }
  }
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

// ============================================
// Animations
// ============================================
.slide-up-enter-active {
  animation: slideUp 0.5s ease-out;
}

.fade-enter-active {
  animation: fadeIn 0.4s ease-out;
}

.slide-in-enter-active {
  animation: slideIn 0.5s ease-out;
}

.stagger-enter-active {
  animation: scaleIn 0.4s ease-out;
}

.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.timeline-enter-active {
  animation: slideIn 0.4s ease-out;
}

// ============================================
// Responsive
// ============================================
@media (max-width: 1200px) {
  .hero-section {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
  }

  .hero-stats {
    grid-template-columns: repeat(4, 1fr);
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 0;
  }

  .hero-section {
    padding: var(--spacing-lg);
    border-radius: var(--radius-xl);
  }

  .hero-name {
    font-size: 32px;
  }

  .hero-stats {
    grid-template-columns: 1fr 1fr;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .activity-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    flex-direction: column;

    .hero-button {
      width: 100%;
    }
  }
}

// Reduced motion
@media (prefers-reduced-motion: reduce) {
  .feature-card,
  .copyright-item,
  .hero-button {
    transition: none;

    &:hover {
      transform: none;
    }
  }

  .hero-skeleton * {
    animation: none;
  }
}
</style>
