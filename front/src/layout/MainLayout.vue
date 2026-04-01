<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <AppHeader />

    <!-- 侧边栏 -->
    <el-aside
      :width="isCollapse ? '64px' : '220px'"
      class="sidebar"
      :class="{ 'is-collapsed': isCollapse }"
    >
      <div class="sidebar-header">
        <div class="sidebar-toggle" @click="toggleCollapse">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 5H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M3 10H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M3 15H17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <transition name="fade">
          <div v-show="!isCollapse" class="sidebar-title">功能菜单</div>
        </transition>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="true"
        router
        class="sidebar-menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item
            v-if="!route.meta?.hidden"
            :index="route.path"
            class="menu-item"
          >
            <el-icon>
              <component :is="route.meta?.icon || 'Menu'" />
            </el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主体内容 -->
    <div class="main-wrapper" :class="{ 'collapse': isCollapse }">
      <!-- 面包屑导航 -->
      <div class="breadcrumb-bar">
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbs"
              :key="item.path"
              :to="{ path: item.path }"
            >
              {{ item.meta?.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>

      <!-- 底部信息 -->
      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore, ROLES } from '@/stores/user'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

// 角色权限配置：每个角色可以访问的菜单
const roleMenus = {
  [ROLES.CONTENT_OWNER]: ['dashboard', 'explorer', 'copyrights', 'licenses', 'royalties', 'transactions', 'settings'],
  [ROLES.LICENSE_APPROVER]: ['dashboard', 'explorer', 'licenses', 'transactions', 'settings'],
  [ROLES.LICENSE_MANAGER]: ['dashboard', 'explorer', 'licenses', 'royalties', 'transactions', 'settings'],
  [ROLES.AUDITOR]: ['dashboard', 'explorer', 'copyrights', 'licenses', 'royalties', 'blockchain', 'transactions'],
  [ROLES.SYSTEM_ADMIN]: ['dashboard', 'admin', 'explorer', 'copyrights', 'licenses', 'royalties', 'blockchain', 'transactions', 'settings']
}

const menuRoutes = computed(() => {
  const role = userStore.role
  const allowedMenus = roleMenus[role] || []

  return router.getRoutes()
    .filter(r => {
      if (!r.path.startsWith('/') || !r.meta?.title || r.path === '/login') {
        return false
      }

      const pathKey = r.path.split('/')[1] || ''
      if (!allowedMenus.includes(pathKey)) {
        return false
      }

      return true
    })
    .sort((a, b) => {
      const order = {
        'dashboard': 1,
        'admin': 2,
        'explorer': 3,
        'copyrights': 4,
        'licenses': 5,
        'royalties': 6,
        'blockchain': 7,
        'transactions': 8,
        'settings': 9,
        'registration': 10
      }
      const aKey = a.path.split('/')[1] || ''
      const bKey = b.path.split('/')[1] || ''
      return (order[aKey] || 99) - (order[bKey] || 99)
    })
})

const activeMenu = computed(() => {
  return route.matched[route.matched.length - 1]?.path || route.path
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(r => r.meta?.title)
  return matched.length > 0 ? matched : [{ path: '/', meta: { title: '首页' } }]
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<style lang="scss" scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
  padding-top: 70px;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 70px;
  bottom: 0;
  background: var(--bg-card);
  border-right: 1px solid var(--border-primary);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 100;
  width: 220px;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &.is-collapsed {
    width: 64px;

    .sidebar-header {
      padding: 0 12px;
    }
  }

  :deep(.el-aside) {
    width: 100%;
    height: 100%;
    border: none;
  }
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-primary);
  gap: 12px;

  .sidebar-toggle {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    cursor: pointer;
    color: var(--text-secondary);
    transition: all 0.2s;
    flex-shrink: 0;

    &:hover {
      background: var(--bg-tertiary);
      color: var(--color-primary-600);
    }
  }

  .sidebar-title {
    flex: 1;
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    overflow: hidden;
    white-space: nowrap;
    transition: opacity 0.2s ease, transform 0.2s ease;
  }
}

.sidebar-menu {
  flex: 1;
  border: none;
  background: transparent;
  padding: 16px 12px;
  overflow-y: auto;

  // Element Plus 菜单在收缩模式下的默认样式
  &.el-menu--collapse {
    width: 64px;
    padding: 16px 8px;

    :deep(.el-menu-item) {
      padding: 0 !important;
      margin: 0 auto 4px auto;
      width: 48px;
      height: 48px;
      display: flex !important;
      justify-content: center !important;
      align-items: center !important;

      .el-icon {
        margin-right: 0 !important;
        width: 20px;
        height: 20px;
      }

      span {
        display: none !important;
      }
    }
  }

  :deep(.el-menu-item) {
    color: var(--text-secondary);
    margin-bottom: 4px;
    border-radius: 10px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    height: 44px;
    line-height: 44px;
    display: flex;
    align-items: center;

    &:hover {
      background: var(--bg-tertiary);
      color: var(--color-primary-600);
    }

    &.is-active {
      background: var(--color-primary-600);
      color: white;

      .el-icon {
        color: white;
      }
    }

    .el-icon {
      color: var(--text-tertiary);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
  }
}

.main-wrapper {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 50;

  &.collapse {
    margin-left: 64px;
  }
}

.breadcrumb-bar {
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-primary);
  padding: 16px 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

.breadcrumb-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  transition: padding 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-breadcrumb) {
  .el-breadcrumb__item {
    .el-breadcrumb__inner {
      color: var(--text-secondary);
      font-weight: 500;

      &:hover {
        color: var(--color-primary-600);
      }
    }

    &:last-child {
      .el-breadcrumb__inner {
        color: var(--text-primary);
        font-weight: 600;
      }
    }

    .el-breadcrumb__separator {
      color: var(--text-tertiary);
    }
  }
}

.main-content {
  flex: 1;
  padding: 32px;
  background: var(--bg-primary);
  min-height: calc(100vh - 70px - 60px - 200px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
    z-index: 1000;

    &.show-mobile {
      transform: translateX(0);
    }
  }

  .main-wrapper {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .main-layout {
    padding-top: 60px;
  }

  .breadcrumb-container {
    padding: 0 20px;
  }

  .main-content {
    padding: 20px;
  }

  .breadcrumb-bar {
    padding: 12px 0;
  }

  :deep(.el-breadcrumb) {
    font-size: 13px;
  }
}
</style>
