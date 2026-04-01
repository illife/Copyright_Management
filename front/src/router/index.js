import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/registration',
    name: 'Registration',
    component: () => import('@/views/Registration.vue'),
    meta: { title: '用户注册', requiresAuth: false }
  },
  {
    path: '/my-status',
    name: 'MyStatus',
    component: () => import('@/views/registration/MyStatus.vue'),
    meta: { title: '我的申请状态', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览', icon: 'DataAnalysis' }
      },
      {
        path: 'copyrights',
        name: 'Copyrights',
        component: () => import('@/views/copyright/Index.vue'),
        meta: { title: '版权管理', icon: 'Document' }
      },
      {
        path: 'copyrights/register',
        name: 'CopyrightRegister',
        component: () => import('@/views/copyright/Register.vue'),
        meta: { title: '注册版权', icon: 'Plus' }
      },
      {
        path: 'copyrights/detail/:id',
        name: 'CopyrightDetail',
        component: () => import('@/views/copyright/Detail.vue'),
        meta: { title: '版权详情', icon: 'DocumentChecked', hidden: true }
      },
      {
        path: 'licenses',
        name: 'Licenses',
        component: () => import('@/views/license/Index.vue'),
        meta: { title: '授权管理', icon: 'Key' }
      },
      {
        path: 'licenses/apply',
        name: 'LicenseApply',
        component: () => import('@/views/license/Apply.vue'),
        meta: { title: '申请授权', icon: 'Plus' }
      },
      {
        path: 'licenses/detail/:id',
        name: 'LicenseDetail',
        component: () => import('@/views/license/Detail.vue'),
        meta: { title: '授权详情', icon: 'DocumentChecked', hidden: true }
      },
      {
        path: 'royalties',
        name: 'Royalties',
        component: () => import('@/views/royalty/Index.vue'),
        meta: { title: '版税管理', icon: 'Coin' }
      },
      {
        path: 'blockchain',
        name: 'Blockchain',
        component: () => import('@/views/Blockchain.vue'),
        meta: { title: '系统信息', icon: 'Connection' }
      },
      {
        path: 'explorer',
        name: 'BlockchainExplorer',
        component: () => import('@/views/BlockchainExplorer.vue'),
        meta: { title: '区块链浏览器', icon: 'Box' }
      },
      {
        path: 'transactions',
        name: 'Transactions',
        component: () => import('@/views/Transactions.vue'),
        meta: { title: '交易记录', icon: 'List' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/Settings.vue'),
        meta: { title: '账户设置', icon: 'Setting' }
      },
      {
        path: 'admin/registrations',
        name: 'AdminRegistrations',
        component: () => import('@/views/registration/AdminList.vue'),
        meta: { title: '注册申请管理', icon: 'UserFilled', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else if (to.meta.requiresAdmin && !userStore.isSystemAdmin) {
    // 需要管理员权限但当前用户不是管理员
    ElMessage.error('您没有访问该页面的权限')
    next('/dashboard')
  } else {
    next()
  }
})

export default router
