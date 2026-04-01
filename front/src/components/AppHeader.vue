<template>
  <header class="app-header">
    <div class="header-container">
      <!-- 左侧：品牌标识 -->
      <div class="header-left">
        <div class="brand-logo">
          <div class="logo-icon">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="32" height="32" rx="6" fill="url(#logo-gradient)"/>
              <path d="M16 8L22 14L16 20L10 14L16 8Z" fill="white" opacity="0.9"/>
              <path d="M16 12L20 16L16 20L12 16L16 12Z" fill="white"/>
              <defs>
                <linearGradient id="logo-gradient" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#667eea"/>
                  <stop offset="1" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <span class="brand-name">联盟链版权管理</span>
        </div>

        <!-- 主导航 -->
        <nav class="main-nav">
          <a
            v-for="item in navItems"
            :key="item.path"
            :href="item.path"
            class="nav-item"
            :class="{ active: isActive(item.path) }"
            @click.prevent="navigateTo(item.path)"
          >
            <span>{{ item.title }}</span>
          </a>
        </nav>
      </div>

      <!-- 中间：搜索功能 -->
      <div class="header-center">
        <div class="search-box">
          <svg class="search-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 1C4.13401 1 1 4.13401 1 8C1 11.866 4.13401 15 8 15C9.68473 15 11.2293 14.3956 12.4318 13.3856L12.5 13.5L16.5 17.5L17.5 16.5L13.5 12.5L13.3856 12.4318C14.3956 11.2293 15 9.68473 15 8C15 4.13401 11.866 1 8 1ZM8 3C10.7614 3 13 5.23858 13 8C13 10.7614 10.7614 13 8 13C5.23858 13 3 10.7614 3 8C3 5.23858 5.23858 3 8 3Z" fill="currentColor"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索版权、作品..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button v-if="searchQuery" class="search-clear" @click="clearSearch">×</button>
        </div>
      </div>

      <!-- 右侧：用户中心 -->
      <div class="header-right">
        <template v-if="isAuthenticated">
          <!-- 主题切换器 -->
          <ThemeSwitcher />

          <!-- 通知图标 -->
          <button class="header-icon-btn" @click="showNotifications">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 2C6.68629 2 4 4.68629 4 8V11.5C4 11.7761 3.77614 12 3.5 12H3V14H3.5C4.32843 14 5 14.6716 5 15.5V16C5 16.2761 5.22386 16.5 5.5 16.5H14.5C14.7761 16.5 15 16.2761 15 16V15.5C15 14.6716 15.6716 14 16.5 14H17V12H16.5C16.2239 12 16 11.7761 16 11.5V8C16 4.68629 13.3137 2 10 2Z" fill="currentColor"/>
              <circle cx="10" cy="18" r="1.5" fill="currentColor"/>
            </svg>
            <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
          </button>

          <!-- 用户下拉菜单 -->
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-dropdown">
              <div class="user-avatar">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 3C11.66 3 13 4.34 13 6C13 7.66 11.66 9 10 9C8.34 9 7 7.66 7 6C7 4.34 8.34 3 10 3ZM10 17.2C7.5 17.2 5.29 15.92 4 15C4.03 13.54 8 12.8 10 12.8C11.99 12.8 15.97 13.54 16 15C14.71 15.92 12.5 17.2 10 17.2Z" fill="currentColor"/>
                </svg>
              </div>
              <div class="user-info">
                <div class="user-name">{{ formattedAddress }}</div>
                <div class="user-role">{{ roleName }}</div>
              </div>
              <svg class="dropdown-arrow" width="12" height="12" viewBox="0 0 12 12" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M2.5 4.5L6 8L9.5 4.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 4C9.1 4 10 4.9 10 6C10 7.1 9.1 8 8 8C6.9 8 6 7.1 6 6C6 4.9 6.9 4 8 4ZM8 14C6.67 14 5.5 13.49 4.63 12.66C4.78 11.54 7.2 10.8 8 10.8C8.8 10.8 11.22 11.54 11.38 12.66C10.5 13.49 9.33 14 8 14Z" fill="currentColor"/>
                  </svg>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8 2C6.34 2 5 3.34 5 5C5 6.66 6.34 8 8 8C9.66 8 11 6.66 11 5C11 3.34 9.66 2 8 2ZM13.5 10H13.42L13.15 8.95C13.07 8.63 12.79 8.41 12.46 8.41H11.54C11.21 8.41 10.93 8.63 10.85 8.95L10.58 10H5.42L5.15 8.95C5.07 8.63 4.79 8.41 4.46 8.41H3.54C3.21 8.41 2.93 8.63 2.85 8.95L2.58 10H2.5C1.12 10 0 11.12 0 12.5V13.5C0 14.88 1.12 16 2.5 16H13.5C14.88 16 16 14.88 16 13.5V12.5C16 11.12 14.88 10 13.5 10Z" fill="currentColor"/>
                  </svg>
                  <span>账户设置</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8 2C4.69 2 2 4.69 2 8C2 11.31 4.69 14 8 14C11.31 14 14 11.31 14 8C14 4.69 11.31 2 8 2ZM8 12C6.9 12 6 11.1 6 10C6 8.9 6.9 8 8 8C9.1 8 10 8.9 10 10C10 11.1 9.1 12 8 12ZM11 5H9V7H7V5H5V3H7V1H9V3H11V5Z" fill="currentColor"/>
                  </svg>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <button class="btn btn-secondary" @click="goToLogin">登录</button>
          <button class="btn btn-primary" @click="goToRegister">注册</button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, ROLES } from '@/stores/user'
import { ElMessage } from 'element-plus'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchQuery = ref('')
const unreadCount = ref(3)

const navItems = [
  { path: '/home', title: '首页' },
  { path: '/copyrights', title: '版权管理' },
  { path: '/licenses', title: '授权管理' },
  { path: '/help', title: '帮助中心' }
]

const isAuthenticated = computed(() => !!userStore.token)

const formattedAddress = computed(() => {
  const addr = userStore.address
  if (!addr) return '未登录'
  return `${addr.substring(0, 6)}...${addr.substring(addr.length - 4)}`
})

const roleName = computed(() => {
  const roleNames = {
    [ROLES.CONTENT_OWNER]: '内容拥有者',
    [ROLES.LICENSE_APPROVER]: '授权审批员',
    [ROLES.LICENSE_MANAGER]: '授权管理员',
    [ROLES.AUDITOR]: '审计员',
    [ROLES.SYSTEM_ADMIN]: '系统管理员'
  }
  return roleNames[userStore.role] || '未知角色'
})

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const navigateTo = (path) => {
  if (path === '/home') {
    router.push('/')
  } else {
    router.push(path)
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/copyrights', query: { search: searchQuery.value } })
  }
}

const clearSearch = () => {
  searchQuery.value = ''
}

const showNotifications = () => {
  ElMessage.info('暂无新通知')
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/settings')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/registration')
}
</script>

<style lang="scss" scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--border-primary);
  box-shadow: var(--shadow-sm);
  z-index: 1000;
  transition: background-color var(--transition-slow);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  gap: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 48px;
  flex: 0 0 auto;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-1px);
  }

  .logo-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .brand-name {
    font-size: 18px;
    font-weight: 700;
    background: var(--gradient-primary);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
  position: relative;

  &:hover {
    color: var(--color-primary-600);
    background: var(--color-primary-600);
    background: rgba(102, 126, 234, 0.08);
  }

  &.active {
    color: var(--color-primary-600);
    background: var(--color-primary-600);
    background: rgba(102, 126, 234, 0.1);

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 50%;
      transform: translateX(-50%);
      width: 24px;
      height: 2px;
      background: var(--gradient-primary);
      border-radius: 1px;
    }
  }
}

.header-center {
  flex: 1;
  max-width: 500px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: 0 var(--spacing-md);
  transition: all var(--transition-base);

  &:hover {
    border-color: var(--color-primary-600);
    background: var(--bg-card);
    box-shadow: var(--shadow-glow);
  }

  &:focus-within {
    border-color: var(--color-primary-600);
    background: var(--bg-card);
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  .search-icon {
    color: var(--text-tertiary);
    flex-shrink: 0;
  }

  .search-input {
    flex: 1;
    border: none;
    background: transparent;
    padding: 11px var(--spacing-md);
    font-size: var(--font-size-base);
    color: var(--text-primary);
    outline: none;

    &::placeholder {
      color: var(--text-tertiary);
    }
  }

  .search-clear {
    border: none;
    background: none;
    color: var(--text-tertiary);
    font-size: 20px;
    cursor: pointer;
    padding: 0 4px;
    line-height: 1;

    &:hover {
      color: var(--text-secondary);
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 0 0 auto;
}

.header-icon-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: var(--bg-secondary);
  border-radius: 10px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;

  &:hover {
    background: rgba(102, 126, 234, 0.1);
    color: var(--color-primary-600);
  }

  .notification-badge {
    position: absolute;
    top: 6px;
    right: 6px;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    background: var(--color-danger);
    color: white;
    font-size: 11px;
    font-weight: 600;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(102, 126, 234, 0.08);
    border-color: rgba(102, 126, 234, 0.3);
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--gradient-primary);
    border-radius: 8px;
    color: white;
  }

  .user-info {
    text-align: left;

    .user-name {
      font-size: 13px;
      font-weight: 600;
      color: var(--text-primary);
      line-height: 1.2;
    }

    .user-role {
      font-size: 11px;
      color: var(--text-tertiary);
      margin-top: 2px;
      line-height: 1.2;
    }
  }

  .dropdown-arrow {
    color: var(--text-tertiary);
    transition: transform 0.2s;
  }

  &:hover .dropdown-arrow {
    transform: rotate(180deg);
  }
}

.btn {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;

  &.btn-primary {
    background: var(--gradient-primary);
    color: white;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }

  &.btn-secondary {
    background: var(--bg-secondary);
    color: var(--text-primary);
    border: 1px solid var(--border-primary);

    &:hover {
      background: var(--bg-tertiary);
      border-color: var(--color-primary-600);
      color: var(--color-primary-600);
    }
  }
}

@media (max-width: 1024px) {
  .header-container {
    padding: 0 20px;
    gap: 16px;
  }

  .main-nav {
    display: none;
  }

  .search-box {
    display: none;
  }
}

@media (max-width: 768px) {
  .app-header {
    height: 60px;
  }

  .header-container {
    padding: 0 16px;
  }

  .brand-name {
    display: none;
  }

  .user-info {
    display: none;
  }
}
</style>
