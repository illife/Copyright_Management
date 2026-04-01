import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 角色定义
export const ROLES = {
  CONTENT_OWNER: 'CONTENT_OWNER',      // 内容拥有者
  LICENSE_APPROVER: 'LICENSE_APPROVER', // 授权审批员
  LICENSE_MANAGER: 'LICENSE_MANAGER',   // 授权管理员
  AUDITOR: 'AUDITOR',                   // 审计员
  SYSTEM_ADMIN: 'SYSTEM_ADMIN'          // 系统管理员
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const address = ref(localStorage.getItem('address') || '')
  const role = ref(localStorage.getItem('role') || '')
  const username = ref(localStorage.getItem('username') || '')

  // 计算属性：判断当前用户的角色
  const isContentOwner = computed(() => role.value === ROLES.CONTENT_OWNER)
  const isLicenseApprover = computed(() => role.value === ROLES.LICENSE_APPROVER)
  const isLicenseManager = computed(() => role.value === ROLES.LICENSE_MANAGER)
  const isAuditor = computed(() => role.value === ROLES.AUDITOR)
  const isSystemAdmin = computed(() => role.value === ROLES.SYSTEM_ADMIN)

  // 判断是否有某个权限
  const hasPermission = (permission) => {
    if (isSystemAdmin.value) return true // 系统管理员拥有所有权限
    if (isAuditor.value) return permission === 'view' // 审计员只能查看

    // 根据角色判断权限
    const rolePermissions = {
      [ROLES.CONTENT_OWNER]: ['view', 'register', 'apply', 'activate', 'revoke_own'],
      [ROLES.LICENSE_APPROVER]: ['view', 'approve', 'reject'],
      [ROLES.LICENSE_MANAGER]: ['view', 'activate', 'revoke', 'record_royalty'],
      [ROLES.SYSTEM_ADMIN]: ['*'], // 所有权限
      [ROLES.AUDITOR]: ['view']
    }

    const permissions = rolePermissions[role.value] || []
    return permissions.includes('*') || permissions.includes(permission)
  }

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setAddress = (newAddress) => {
    address.value = newAddress
    localStorage.setItem('address', newAddress)
  }

  const setRole = (newRole) => {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  const setUsername = (newUsername) => {
    username.value = newUsername
    localStorage.setItem('username', newUsername)
  }

  const logout = () => {
    token.value = ''
    address.value = ''
    role.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('address')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
  }

  return {
    token,
    address,
    role,
    username,
    isContentOwner,
    isLicenseApprover,
    isLicenseManager,
    isAuditor,
    isSystemAdmin,
    hasPermission,
    setToken,
    setAddress,
    setRole,
    setUsername,
    logout
  }
})
