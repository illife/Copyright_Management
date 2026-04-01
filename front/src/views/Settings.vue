<template>
  <div class="settings-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">账户设置</h1>
        <p class="page-subtitle">管理您的个人信息、安全设置和通知偏好</p>
      </div>
    </div>

    <!-- 设置卡片 -->
    <div class="settings-container">
      <!-- 侧边导航 -->
      <div class="settings-sidebar">
        <div class="sidebar-menu">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="menu-item"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            <component :is="tab.icon" class="menu-icon" />
            <span>{{ tab.label }}</span>
            <div v-if="activeTab === tab.key" class="active-indicator"></div>
          </button>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="settings-content">
        <!-- 个人信息 -->
        <div v-show="activeTab === 'profile'" class="content-card">
          <div class="card-header">
            <h3 class="card-title">个人信息</h3>
            <p class="card-subtitle">更新您的个人资料和联系方式</p>
          </div>

          <div class="card-body">
            <!-- 头像上传 -->
            <div class="form-section">
              <div class="section-label">头像</div>
              <div class="avatar-upload">
                <div class="avatar-preview">
                  <img v-if="avatarUrl" :src="avatarUrl" alt="avatar" />
                  <svg v-else width="48" height="48" viewBox="0 0 48 48" fill="none">
                    <circle cx="24" cy="16" r="8" stroke="#9ca3af" stroke-width="2" fill="none"/>
                    <path d="M8 40C8 32 14 28 24 28C34 28 40 32 40 40" stroke="#9ca3af" stroke-width="2" fill="none" stroke-linecap="round"/>
                  </svg>
                </div>
                <button class="btn-upload">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 1V11M8 11L5 8M8 11L11 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M1 11V13C1 14.1 1.9 15 3 15H13C14.1 15 15 14.1 15 13V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  更换头像
                </button>
              </div>
            </div>

            <!-- 用户名 -->
            <div class="form-group">
              <label class="form-label">
                用户名
                <span class="required">*</span>
              </label>
              <input
                v-model="profileForm.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名"
                maxlength="50"
              />
              <span v-if="profileErrors.username" class="form-error">{{ profileErrors.username }}</span>
            </div>

            <!-- 区块链地址 -->
            <div class="form-group">
              <label class="form-label">区块链地址</label>
              <div class="input-with-action">
                <input
                  :value="userStore.address"
                  type="text"
                  class="form-input input-disabled"
                  disabled
                />
                <button class="input-action-btn" @click="copyAddress">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M6 4V12M6 12L4 10M6 12L8 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M4 8H10C11.1 8 12 7.1 12 6V4C12 2.9 11.1 2 10 2H4C2.9 2 2 2.9 2 4V6C2 7.1 2.9 8 4 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M10 8H14C15.1 8 16 8.9 16 10V12C16 13.1 15.1 14 14 14H10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  复制
                </button>
              </div>
            </div>

            <!-- 当前角色 -->
            <div class="form-group">
              <label class="form-label">当前角色</label>
              <div class="role-badge">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C5.24 0 3 2.24 3 5V6H1C0.45 6 0 6.45 0 7V13C0 13.55 0.45 14 1 14H7V13H1V7H7V5C7 3.34 8.34 2 10 2H11V3H10C8.9 3 8 3.9 8 5V6H5V5C5 2.79 6.79 1 9 1H10C12.76 1 15 3.24 15 6V7H13V6C13 3.79 11.21 2 9 2H8ZM10 7C9.45 7 9 7.45 9 8V14C9 14.55 9.45 15 10 15H15C15.55 15 16 14.55 16 14V8C16 7.45 15.55 7 15 7H10ZM11 13V9H14V13H11Z" fill="currentColor"/>
                </svg>
                {{ roleName }}
              </div>
            </div>

            <!-- 邮箱 -->
            <div class="form-group">
              <label class="form-label">邮箱地址</label>
              <div class="input-with-action">
                <input
                  v-model="profileForm.email"
                  type="email"
                  class="form-input"
                  placeholder="请输入邮箱"
                />
                <button
                  v-if="!profileForm.emailVerified"
                  class="input-action-btn action-secondary"
                  @click="sendVerification"
                >
                  发送验证
                </button>
                <div v-else class="verified-badge">
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                    <path d="M12.25 3.5L5.25 10.5L1.75 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
                  </svg>
                  已验证
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <button class="btn btn-primary" :disabled="saving" @click="saveProfile">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M13.5 3.5L6 11L2.5 7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                {{ saving ? '保存中...' : '保存更改' }}
              </button>
              <button class="btn btn-secondary" @click="resetProfile">重置</button>
            </div>
          </div>
        </div>

        <!-- 安全设置 -->
        <div v-show="activeTab === 'security'" class="content-card">
          <div class="card-header">
            <h3 class="card-title">安全设置</h3>
            <p class="card-subtitle">管理您的密码和钱包安全</p>
          </div>

          <div class="card-body">
            <!-- 钱包地址 -->
            <div class="form-section">
              <div class="section-label">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                  <path d="M9 1L3 4V8.5C3 12.54 5.59 16.23 9 17.5C12.41 16.23 15 12.54 15 8.5V4L9 1Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                钱包信息
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">钱包地址</label>
              <div class="input-with-action">
                <div class="input-prefix">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 0C4.69 0 2 2.69 2 6V7H1C0.45 7 0 7.45 0 8V13C0 13.55 0.45 14 1 14H6C6.55 14 7 13.55 7 13V8C7 7.45 6.55 7 6 7H5V6C5 3.24 7.24 1 10 1H11V2H10C7.79 2 6 3.79 6 6V7H4V6C4 2.69 6.69 0 10 0H8ZM10 7C9.45 7 9 7.45 9 8V13C9 13.55 9.45 14 10 14H15C15.55 14 16 13.55 16 13V8C16 7.45 15.55 7 15 7H10ZM11 12V9H14V12H11Z" fill="currentColor"/>
                  </svg>
                </div>
                <input
                  v-model="securityForm.walletAddress"
                  type="text"
                  class="form-input input-disabled"
                  disabled
                />
                <button class="input-action-btn" @click="viewPrivateKey">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 9C10.21 9 12 7.21 12 5C12 2.79 10.21 1 8 1C5.79 1 4 2.79 4 5C4 7.21 5.79 9 8 9ZM8 3C9.1 3 10 3.9 10 5C10 6.1 9.1 7 8 7C6.9 7 6 6.1 6 5C6 3.9 6.9 3 8 3ZM2 13C2 11 5 10 8 10C11 10 14 11 14 13V15H2V13ZM4 13H12C12 12.3 10 12 8 12C6 12 4 12.3 4 13Z" fill="currentColor"/>
                  </svg>
                  查看私钥
                </button>
              </div>
            </div>

            <div class="form-divider"></div>

            <!-- 修改密码 -->
            <div class="form-section">
              <div class="section-label">修改密码</div>
              <p class="section-hint">为了账户安全，建议定期更换密码</p>
            </div>

            <div class="form-group">
              <label class="form-label">
                当前密码
                <span class="required">*</span>
              </label>
              <div class="input-with-icon">
                <input
                  v-model="securityForm.currentPassword"
                  :type="showCurrentPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请输入当前密码"
                />
                <button class="input-icon-btn" @click="showCurrentPassword = !showCurrentPassword">
                  <svg v-if="showCurrentPassword" width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9S14 14 9 14S1 9 1 9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="9" cy="9" r="2" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  <svg v-else width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M1 9S4 14 9 14S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <line x1="3" y1="3" x2="15" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <span v-if="securityErrors.currentPassword" class="form-error">{{ securityErrors.currentPassword }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">
                新密码
                <span class="required">*</span>
              </label>
              <div class="input-with-icon">
                <input
                  v-model="securityForm.newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请输入新密码（8-20位）"
                />
                <button class="input-icon-btn" @click="showNewPassword = !showNewPassword">
                  <svg v-if="showNewPassword" width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9S14 14 9 14S1 9 1 9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="9" cy="9" r="2" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  <svg v-else width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M1 9S4 14 9 14S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <line x1="3" y1="3" x2="15" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <span v-if="securityErrors.newPassword" class="form-error">{{ securityErrors.newPassword }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">
                确认密码
                <span class="required">*</span>
              </label>
              <div class="input-with-icon">
                <input
                  v-model="securityForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请再次输入新密码"
                />
                <button class="input-icon-btn" @click="showConfirmPassword = !showConfirmPassword">
                  <svg v-if="showConfirmPassword" width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9S14 14 9 14S1 9 1 9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="9" cy="9" r="2" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  <svg v-else width="18" height="18" viewBox="0 0 18 18" fill="none">
                    <path d="M1 9S4 4 9 4S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M1 9S4 14 9 14S17 9 17 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <line x1="3" y1="3" x2="15" y2="15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <span v-if="securityErrors.confirmPassword" class="form-error">{{ securityErrors.confirmPassword }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <button class="btn btn-primary" :disabled="saving" @click="changePassword">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M13.5 3.5L6 11L2.5 7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                {{ saving ? '修改中...' : '修改密码' }}
              </button>
              <button class="btn btn-secondary" @click="resetSecurity">重置</button>
            </div>
          </div>
        </div>

        <!-- 通知设置 -->
        <div v-show="activeTab === 'notifications'" class="content-card">
          <div class="card-header">
            <h3 class="card-title">通知设置</h3>
            <p class="card-subtitle">选择您希望接收的通知类型</p>
          </div>

          <div class="card-body">
            <!-- 通知列表 -->
            <div class="notifications-list">
              <div
                v-for="setting in notificationList"
                :key="setting.key"
                class="notification-item"
              >
                <div class="notification-icon" :class="`icon-${setting.color}`">
                  <component :is="setting.icon" />
                </div>
                <div class="notification-info">
                  <div class="notification-title">{{ setting.title }}</div>
                  <div class="notification-desc">{{ setting.description }}</div>
                </div>
                <button
                  class="toggle-switch"
                  :class="{ active: notificationSettings[setting.key] }"
                  @click="toggleNotification(setting.key)"
                >
                  <div class="toggle-slider"></div>
                </button>
              </div>

              <!-- 邮件通知 -->
              <div class="notification-item">
                <div class="notification-icon icon-blue">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M4 2H16C17.1 2 18 2.9 18 4V16C18 17.1 17.1 18 16 18H4C2.9 18 2 17.1 2 16V4C2 2.9 2.9 2 4 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 4L10 11L18 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <div class="notification-info">
                  <div class="notification-title">邮件通知</div>
                  <div class="notification-desc">将通知同时发送到您的邮箱</div>
                </div>
                <button
                  class="toggle-switch"
                  :class="{ active: notificationSettings.emailNotification }"
                  @click="toggleNotification('emailNotification')"
                >
                  <div class="toggle-slider"></div>
                </button>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <button class="btn btn-primary" @click="saveNotifications">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M13.5 3.5L6 11L2.5 7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                保存设置
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore, ROLES } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// Icons as components
const UserIcon = {
  template: `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <path d="M9 9C11.4853 9 13.5 6.98528 13.5 4.5C13.5 2.01472 11.4853 0 9 0C6.51472 0 4.5 2.01472 4.5 4.5C4.5 6.98528 6.51472 9 9 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M9 11C5.68629 11 2 12.3431 2 14V16C2 16.5523 2.44772 17 3 17H15C15.5523 17 16 16.5523 16 16V14C16 12.3431 12.3137 11 9 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const LockIcon = {
  template: `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <path d="M5 8V5C5 2.79086 6.79086 1 9 1C11.2091 1 13 2.79086 13 5V8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <rect x="1" y="8" width="16" height="9" rx="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const BellIcon = {
  template: `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <path d="M10.5 14.5H7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M9 1C6.24 1 4 3.24 4 6V7C4 9.5 3 10 3 10C2.45 10 2 10.45 2 11V13C2 13.55 2.45 14 3 14H15C15.55 14 16 13.55 16 13V11C16 10.45 15.55 10 15 10C15 10 14 9.5 14 7V6C14 3.24 11.76 1 9 1Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const CopyrightIcon = {
  template: `
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
      <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" fill="none"/>
      <path d="M10 6V14M10 14H12.5M10 14H7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
    </svg>
  `
}

const LicenseIcon = {
  template: `
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
      <path d="M16 2H4C2.9 2 2 2.9 2 4V16C2 17.1 2.9 18 4 18H16C17.1 18 18 17.1 18 16V4C18 2.9 17.1 2 16 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M7 9H13M7 13H10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const ApproveIcon = {
  template: `
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
      <path d="M10 1L3 5V10C3 14.42 6.13 18.47 10 19.5C13.87 18.47 17 14.42 17 10V5L10 1Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M7 10L9 12L13 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const RoyaltyIcon = {
  template: `
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
      <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" fill="none"/>
      <path d="M10 5V10L13 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const SystemIcon = {
  template: `
    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
      <path d="M10 1L2 5V10C2 14.42 5.13 18.47 9 19.5C12.87 18.47 16 14.42 16 10V5L9 1H10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `
}

const tabs = [
  { key: 'profile', label: '个人信息', icon: UserIcon },
  { key: 'security', label: '安全设置', icon: LockIcon },
  { key: 'notifications', label: '通知设置', icon: BellIcon }
]

const activeTab = ref('profile')
const saving = ref(false)

const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const avatarUrl = ref('')

// 角色名称映射
const roleNames = {
  [ROLES.CONTENT_OWNER]: '内容拥有者',
  [ROLES.LICENSE_APPROVER]: '授权审批员',
  [ROLES.LICENSE_MANAGER]: '授权管理员',
  [ROLES.AUDITOR]: '审计员',
  [ROLES.SYSTEM_ADMIN]: '系统管理员'
}

const roleName = computed(() => {
  return roleNames[userStore.role] || '未知角色'
})

// 个人信息表单
const profileForm = reactive({
  username: userStore.username || '',
  email: '',
  emailVerified: false
})

const profileErrors = reactive({
  username: '',
  email: ''
})

// 安全设置表单
const securityForm = reactive({
  walletAddress: userStore.address || '',
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const securityErrors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 通知设置
const notificationSettings = reactive({
  copyrightRegister: true,
  licenseApply: true,
  licenseApprove: true,
  royaltyPayment: true,
  systemUpdate: false,
  emailNotification: false
})

const notificationList = [
  {
    key: 'copyrightRegister',
    title: '版权注册通知',
    description: '当新的版权成功注册到区块链时接收通知',
    icon: CopyrightIcon,
    color: 'purple'
  },
  {
    key: 'licenseApply',
    title: '授权申请通知',
    description: '当有新的授权申请时接收通知',
    icon: LicenseIcon,
    color: 'blue'
  },
  {
    key: 'licenseApprove',
    title: '授权审批通知',
    description: '当授权申请被批准或拒绝时接收通知',
    icon: ApproveIcon,
    color: 'green'
  },
  {
    key: 'royaltyPayment',
    title: '版税到账通知',
    description: '当收到版税支付时接收通知',
    icon: RoyaltyIcon,
    color: 'orange'
  },
  {
    key: 'systemUpdate',
    title: '系统更新通知',
    description: '接收系统功能更新和重要公告',
    icon: SystemIcon,
    color: 'gray'
  }
]

const validateProfile = () => {
  let valid = true
  profileErrors.username = ''
  profileErrors.email = ''

  if (!profileForm.username.trim()) {
    profileErrors.username = '请输入用户名'
    valid = false
  } else if (profileForm.username.length < 2 || profileForm.username.length > 50) {
    profileErrors.username = '用户名长度在2-50个字符之间'
    valid = false
  }

  if (profileForm.email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(profileForm.email)) {
      profileErrors.email = '请输入有效的邮箱地址'
      valid = false
    }
  }

  return valid
}

const validateSecurity = () => {
  let valid = true
  securityErrors.currentPassword = ''
  securityErrors.newPassword = ''
  securityErrors.confirmPassword = ''

  if (!securityForm.currentPassword) {
    securityErrors.currentPassword = '请输入当前密码'
    valid = false
  }

  if (!securityForm.newPassword) {
    securityErrors.newPassword = '请输入新密码'
    valid = false
  } else if (securityForm.newPassword.length < 8 || securityForm.newPassword.length > 20) {
    securityErrors.newPassword = '密码长度在8-20个字符之间'
    valid = false
  }

  if (!securityForm.confirmPassword) {
    securityErrors.confirmPassword = '请再次输入新密码'
    valid = false
  } else if (securityForm.confirmPassword !== securityForm.newPassword) {
    securityErrors.confirmPassword = '两次输入的密码不一致'
    valid = false
  }

  return valid
}

const copyAddress = () => {
  navigator.clipboard.writeText(userStore.address || '').then(() => {
    ElMessage.success('地址已复制到剪贴板')
  })
}

const sendVerification = () => {
  ElMessage.info('验证邮件发送功能开发中...')
}

const saveProfile = async () => {
  if (!validateProfile()) return

  saving.value = true
  try {
    // TODO: 调用API保存用户信息
    userStore.setUsername(profileForm.username)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const resetProfile = () => {
  profileForm.username = userStore.username || ''
  profileForm.email = ''
  profileErrors.username = ''
  profileErrors.email = ''
}

const changePassword = async () => {
  if (!validateSecurity()) return

  saving.value = true
  try {
    // TODO: 调用API修改密码
    ElMessage.success('密码修改成功')
    resetSecurity()
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  } finally {
    saving.value = false
  }
}

const resetSecurity = () => {
  securityForm.currentPassword = ''
  securityForm.newPassword = ''
  securityForm.confirmPassword = ''
  securityErrors.currentPassword = ''
  securityErrors.newPassword = ''
  securityErrors.confirmPassword = ''
  showCurrentPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
}

const viewPrivateKey = () => {
  ElMessageBox.alert(
    '请在您的钱包应用中查看私钥，平台不存储用户私钥。',
    '安全提示',
    {
      confirmButtonText: '我知道了',
      type: 'warning'
    }
  )
}

const toggleNotification = (key) => {
  notificationSettings[key] = !notificationSettings[key]
}

const saveNotifications = () => {
  ElMessage.success('通知设置已保存')
}

onMounted(() => {
  // 加载用户设置
})
</script>

<style lang="scss" scoped>
.settings-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
}

// 页面头部
.page-header {
  margin-bottom: 32px;
}

.header-content {
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

// 设置容器
.settings-container {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: start;
}

// 侧边导航
.settings-sidebar {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 16px;
  position: sticky;
  top: 24px;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  text-align: left;

  .menu-icon {
    color: var(--text-tertiary);
    flex-shrink: 0;
  }

  &:hover {
    background: var(--bg-secondary);
    color: var(--text-primary);

    .menu-icon {
      color: var(--color-primary-600);
    }
  }

  &.active {
    background: rgba(102, 126, 234, 0.1);
    color: var(--color-primary-600);

    .menu-icon {
      color: var(--color-primary-600);
    }
  }

  .active-indicator {
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 24px;
    background: var(--gradient-primary);
    border-radius: 2px;
  }
}

// 内容区域
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.content-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  padding: 24px 28px;
  border-bottom: 1px solid var(--border-tertiary);

  .card-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 4px 0;
  }

  .card-subtitle {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
  }
}

.card-body {
  padding: 28px;
}

// 表单样式
.form-section {
  margin-bottom: 24px;

  .section-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;

    svg {
      color: #667eea;
    }
  }

  .section-hint {
    font-size: 13px;
    color: #9ca3af;
    margin: 4px 0 0 0;
  }
}

.form-group {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;

  .required {
    color: #ef4444;
    margin-left: 2px;
  }
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  font-size: 14px;
  color: var(--text-primary);
  background: var(--bg-card);
  transition: all 0.2s;
  outline: none;

  &:hover {
    border-color: var(--color-primary-600);
  }

  &:focus {
    border-color: var(--color-primary-600);
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  &.input-disabled {
    background: var(--bg-secondary);
    color: var(--text-secondary);
    cursor: not-allowed;

    &:hover {
      border-color: var(--border-primary);
    }
  }

  &::placeholder {
    color: var(--text-tertiary);
  }
}

.input-with-action {
  display: flex;
  gap: 8px;

  .form-input {
    flex: 1;
  }

  .input-action-btn {
    padding: 0 16px;
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 10px;
    color: var(--color-primary-600);
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
      border-color: var(--color-primary-600);
      background: rgba(102, 126, 234, 0.05);
    }

    &.action-secondary {
      color: var(--text-secondary);

      &:hover {
        color: var(--color-primary-600);
      }
    }
  }

  .verified-badge {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 0 16px;
    background: rgba(34, 197, 94, 0.1);
    border: 1px solid #22c55e;
    border-radius: 10px;
    color: #22c55e;
    font-size: 13px;
    font-weight: 500;
    white-space: nowrap;

    svg {
      color: #22c55e;
    }
  }
}

.input-with-icon {
  position: relative;
  display: flex;
  align-items: center;

  .form-input {
    padding-right: 48px;
  }

  .input-icon-btn {
    position: absolute;
    right: 4px;
    top: 50%;
    transform: translateY(-50%);
    width: 36px;
    height: 36px;
    border: none;
    background: transparent;
    border-radius: 8px;
    color: #9ca3af;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      color: #667eea;
      background: rgba(102, 126, 234, 0.05);
    }
  }
}

.form-error {
  display: block;
  font-size: 12px;
  color: #ef4444;
  margin-top: 6px;
}

.form-divider {
  height: 1px;
  background: var(--border-primary);
  margin: 28px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.btn {
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;

  &.btn-primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }
  }

  &.btn-secondary {
    background: var(--bg-card);
    color: #6b7280;
    border: 1px solid #e5e7eb;

    &:hover {
      border-color: #667eea;
      color: #667eea;
    }
  }
}

// 头像上传
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  background: var(--bg-secondary);
  border: 2px dashed var(--border-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.btn-upload {
  padding: 10px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 10px;
  color: var(--color-primary-600);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary-600);
    background: rgba(102, 126, 234, 0.05);
  }
}

// 角色标签
.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid #667eea;
  border-radius: 10px;
  color: #667eea;
  font-size: 13px;
  font-weight: 500;

  svg {
    color: #667eea;
  }
}

// 通知设置
.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 28px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary-600);
    background: var(--bg-card);
  }
}

.notification-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.icon-purple {
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
  }

  &.icon-blue {
    background: rgba(59, 130, 246, 0.1);
    color: #3b82f6;
  }

  &.icon-green {
    background: rgba(34, 197, 94, 0.1);
    color: #22c55e;
  }

  &.icon-orange {
    background: rgba(249, 115, 22, 0.1);
    color: #f97316;
  }

  &.icon-gray {
    background: rgba(107, 114, 128, 0.1);
    color: #6b7280;
  }
}

.notification-info {
  flex: 1;

  .notification-title {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-primary);
    margin-bottom: 2px;
  }

  .notification-desc {
    font-size: 13px;
    color: var(--text-tertiary);
  }
}

.toggle-switch {
  position: relative;
  width: 52px;
  height: 28px;
  border: none;
  border-radius: 14px;
  background: var(--border-primary);
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;

  .toggle-slider {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 24px;
    height: 24px;
    background: var(--bg-card);
    border-radius: 50%;
    transition: all 0.3s;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  &.active {
    background: var(--gradient-primary);

    .toggle-slider {
      transform: translateX(24px);
    }
  }

  &:hover {
    &:not(.active) {
      background: #d1d5db;
    }
  }
}

// 响应式
@media (max-width: 1024px) {
  .settings-container {
    grid-template-columns: 1fr;
  }

  .settings-sidebar {
    position: static;
  }

  .sidebar-menu {
    flex-direction: row;
    overflow-x: auto;
    padding-bottom: 4px;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-track {
      background: #f3f4f6;
      border-radius: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background: #d1d5db;
      border-radius: 2px;
    }
  }

  .menu-item {
    justify-content: center;
    flex-direction: column;
    gap: 6px;
    padding: 12px;
    font-size: 13px;
    white-space: nowrap;

    .menu-icon {
      width: 20px;
      height: 20px;
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    .page-title {
      font-size: 24px;
    }
  }

  .card-header {
    padding: 20px;
  }

  .card-body {
    padding: 20px;
  }

  .form-actions {
    flex-direction: column;

    .btn {
      width: 100%;
      justify-content: center;
    }
  }

  .notification-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .input-with-action {
    flex-direction: column;

    .input-action-btn,
    .verified-badge {
      width: 100%;
      justify-content: center;
    }
  }
}
</style>
