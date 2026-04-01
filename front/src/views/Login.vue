<template>
  <div class="login-container">
    <!-- 左侧品牌展示区 -->
    <div class="brand-section">
      <!-- 简洁的背景效果 -->
      <div class="background-gradient"></div>
      <div class="grid-pattern"></div>

      <!-- 品牌信息 -->
      <div class="brand-content">
        <div class="brand-header">
          <h1 class="brand-title">联盟链版权管理系统</h1>
          <p class="brand-subtitle">Blockchain Copyright Management System</p>
        </div>

        <!-- 简洁的特性列表 -->
        <div class="features-list">
          <div class="feature-item">
            <div class="feature-dot"></div>
            <div class="feature-text">
              <span class="feature-title">安全可靠</span>
              <span class="feature-desc">基于FISCO BCOS联盟链技术</span>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-dot"></div>
            <div class="feature-text">
              <span class="feature-title">版权保护</span>
              <span class="feature-desc">区块链存证，永久可追溯</span>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-dot"></div>
            <div class="feature-text">
              <span class="feature-title">全程监管</span>
              <span class="feature-desc">实时监控交易历史和状态</span>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-dot"></div>
            <div class="feature-text">
              <span class="feature-title">数据可视</span>
              <span class="feature-desc">直观展示区块链网络拓扑</span>
            </div>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-info">
          <div class="stat-box">
            <div class="stat-label">版权总数</div>
            <div class="stat-number">1,234</div>
          </div>
          <div class="stat-box">
            <div class="stat-label">授权次数</div>
            <div class="stat-number">5,678</div>
          </div>
          <div class="stat-box">
            <div class="stat-label">系统可用性</div>
            <div class="stat-number">99.9%</div>
          </div>
        </div>

        <!-- 底部说明 -->
        <div class="brand-footer">
          <p>政府级安全标准 · 企业级服务保障</p>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区 -->
    <div class="login-section">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">欢迎登录</h2>
          <p class="login-subtitle">请选择角色并输入您的区块链地址</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="role">
            <el-select
              v-model="loginForm.role"
              size="large"
              placeholder="请选择角色"
              class="login-input"
              style="width: 100%"
            >
              <el-option label="👤 内容拥有者" value="CONTENT_OWNER">
                <div class="role-option">
                  <span class="role-label">👤 内容拥有者</span>
                  <span class="role-desc">可注册版权、申请授权</span>
                </div>
              </el-option>
              <el-option label="✅ 授权审批员" value="LICENSE_APPROVER">
                <div class="role-option">
                  <span class="role-label">✅ 授权审批员</span>
                  <span class="role-desc">可审批授权申请</span>
                </div>
              </el-option>
              <el-option label="⚙️ 授权管理员" value="LICENSE_MANAGER">
                <div class="role-option">
                  <span class="role-label">⚙️ 授权管理员</span>
                  <span class="role-desc">可管理授权、记录版税</span>
                </div>
              </el-option>
              <el-option label="👁️ 审计员" value="AUDITOR">
                <div class="role-option">
                  <span class="role-label">👁️ 审计员</span>
                  <span class="role-desc">仅查看所有数据</span>
                </div>
              </el-option>
              <el-option label="🔐 系统管理员" value="SYSTEM_ADMIN">
                <div class="role-option">
                  <span class="role-label">🔐 系统管理员</span>
                  <span class="role-desc">拥有所有权限</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="address">
            <el-input
              v-model="loginForm.address"
              size="large"
              placeholder="区块链地址（0x...）"
              clearable
              class="login-input"
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
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loading">登录系统</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p class="footer-tip">
            <el-icon><InfoFilled /></el-icon>
            使用区块链地址即可登录，无需密码
          </p>
          <div class="footer-divider"></div>
          <p class="footer-text">还没有地址？<a href="#" @click.prevent="handleGoToRegister">申请注册</a></p>
          <p class="footer-text">已提交申请？<a href="#" @click.prevent="handleGoToCheckStatus">查询状态</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Wallet, InfoFilled } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  role: '',
  address: ''
})

const loginRules = {
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入区块链地址', trigger: 'blur' },
    {
      pattern: /^0x[a-fA-F0-9]{40}$/,
      message: '请输入有效的区块链地址格式（0x开头，42位字符）',
      trigger: 'blur'
    }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const data = await login(loginForm.address)

      // 保存token、地址和角色
      userStore.setToken(data.token)
      userStore.setAddress(loginForm.address)
      userStore.setRole(loginForm.role)
      userStore.setUsername(loginForm.role)

      ElMessage.success({
        message: `登录成功！当前角色：${getRoleName(loginForm.role)}`,
        duration: 2000
      })

      setTimeout(() => {
        router.push('/dashboard')
      }, 500)
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error({
        message: error.response?.data?.message || error.message || '登录失败，请检查地址是否正确',
        duration: 3000
      })
    } finally {
      loading.value = false
    }
  })
}

const getRoleName = (role) => {
  const roleNames = {
    'CONTENT_OWNER': '内容拥有者',
    'LICENSE_APPROVER': '授权审批员',
    'LICENSE_MANAGER': '授权管理员',
    'AUDITOR': '审计员',
    'SYSTEM_ADMIN': '系统管理员'
  }
  return roleNames[role] || role
}

const handleGoToRegister = () => {
  router.push('/registration')
}

const handleGoToCheckStatus = () => {
  router.push('/my-status')
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
  overflow: hidden;
}

/* ========== 左侧品牌展示区 ========== */
.brand-section {
  flex: 0 0 55%;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 80px 60px;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  overflow: hidden;

  /* 背景渐变效果 */
  .background-gradient {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      radial-gradient(circle at 20% 50%, var(--color-primary-600) 0%, transparent 50%),
      radial-gradient(circle at 80% 80%, var(--color-success-600) 0%, transparent 50%);
    opacity: 0.04;
    z-index: 1;
  }

  /* 网格图案 */
  .grid-pattern {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      linear-gradient(var(--color-primary-600) 1px, transparent 1px),
      linear-gradient(90deg, var(--color-primary-600) 1px, transparent 1px);
    background-size: 50px 50px;
    opacity: 0.02;
    z-index: 1;
  }

  .brand-content {
    position: relative;
    z-index: 2;
    max-width: 600px;
    width: 100%;
  }

  .brand-header {
    margin-bottom: 60px;

    .brand-title {
      font-size: 42px;
      font-weight: 700;
      color: var(--text-primary);
      margin: 0 0 16px 0;
      letter-spacing: 1px;
      line-height: 1.3;
    }

    .brand-subtitle {
      font-size: 14px;
      color: var(--text-tertiary);
      margin: 0;
      letter-spacing: 2px;
      text-transform: uppercase;
    }
  }

  /* 特性列表 */
  .features-list {
    margin-bottom: 60px;

    .feature-item {
      display: flex;
      align-items: center;
      padding: 20px 0;
      border-bottom: 1px solid var(--border-primary);

      &:last-child {
        border-bottom: none;
      }

      .feature-dot {
        width: 8px;
        height: 8px;
        background: var(--color-primary-600);
        border-radius: 50%;
        margin-right: 20px;
        flex-shrink: 0;
        box-shadow: 0 0 12px rgba(102, 126, 234, 0.4);
      }

      .feature-text {
        display: flex;
        flex-direction: column;
        gap: 6px;

        .feature-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
        }

        .feature-desc {
          font-size: 14px;
          color: var(--text-tertiary);
        }
      }
    }
  }

  /* 统计信息 */
  .stats-info {
    display: flex;
    gap: 20px;
    margin-bottom: 40px;

    .stat-box {
      flex: 1;
      padding: 20px;
      background: var(--bg-secondary);
      border: 1px solid var(--border-primary);
      border-radius: var(--radius-lg);
      text-align: center;
      transition: all var(--transition-base);

      &:hover {
        background: var(--bg-tertiary);
        border-color: var(--color-primary-600);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px var(--shadow-color-primary);
      }

      .stat-label {
        font-size: var(--font-size-sm);
        color: var(--text-tertiary);
        margin-bottom: var(--spacing-sm);
      }

      .stat-number {
        font-size: var(--font-size-2xl);
        font-weight: var(--font-weight-bold);
        color: var(--color-primary-600);
      }
    }
  }

  .brand-footer {
    padding-top: 20px;
    border-top: 1px solid var(--border-secondary);

    p {
      font-size: 13px;
      color: var(--text-tertiary);
      margin: 0;
      text-align: center;
    }
  }
}

/* ========== 右侧登录表单区 ========== */
.login-section {
  flex: 0 0 45%;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 160px 40px 60px;
  background: var(--bg-secondary);
  position: relative;

  /* 背景装饰 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: var(--gradient-primary);
  }
}

.login-card {
  width: 100%;
  max-width: 380px;
  padding: 40px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    .login-title {
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary);
      margin: 0 0 12px 0;
    }

    .login-subtitle {
      font-size: 14px;
      color: var(--text-tertiary);
      margin: 0;
    }
  }

  .login-form {
    margin-bottom: 24px;

    .login-input {
      :deep(.el-input__wrapper) {
        background: var(--bg-tertiary) !important;
        border: 1px solid var(--border-primary) !important;
        box-shadow: none !important;
        transition: none !important;

        &:hover {
          background: var(--bg-tertiary) !important;
          border: 1px solid var(--border-primary) !important;
          box-shadow: none !important;
        }

        &.is-focus {
          background: var(--bg-tertiary) !important;
          border: 1px solid var(--border-primary) !important;
          box-shadow: none !important;
        }
      }

      :deep(.el-input__inner) {
        color: var(--text-primary);
        background: transparent !important;
        transition: none !important;

        &::placeholder {
          color: var(--text-tertiary);
        }

        &:focus {
          background: transparent !important;
          border-color: var(--border-primary) !important;
        }
      }

      :deep(.el-input__prefix) {
        color: var(--text-tertiary);
      }

      :deep(.el-input__suffix) {
        color: var(--text-tertiary);
      }
    }

    // 也要处理选择器
    :deep(.el-select) {
      .el-select__wrapper {
        background: var(--bg-tertiary) !important;
        border: 1px solid var(--border-primary) !important;
        box-shadow: none !important;
        transition: none !important;

        &:hover {
          background: var(--bg-tertiary) !important;
          border: 1px solid var(--border-primary) !important;
          box-shadow: none !important;
        }

        &.is-focus {
          background: var(--bg-tertiary) !important;
          border: 1px solid var(--border-primary) !important;
          box-shadow: none !important;
        }
      }
    }

    .login-button {
      width: 100%;
      height: 48px;
      font-size: var(--font-size-md);
      font-weight: var(--font-weight-semibold);
      background: var(--gradient-primary);
      border: none;
      margin-top: var(--spacing-md);

      &:hover {
        background: var(--gradient-primary);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }

  .login-footer {
    .footer-tip {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: var(--text-tertiary);
      margin: 0 0 16px 0;
      padding: 12px;
      background: var(--color-primary-600);
      opacity: 0.08;
      border-radius: 6px;

      .el-icon {
        color: var(--color-primary-600);
        flex-shrink: 0;
      }
    }

    .footer-divider {
      height: 1px;
      background: var(--border-primary);
      margin: 20px 0;
    }

    .footer-text {
      font-size: 14px;
      color: var(--text-secondary);
      text-align: center;
      margin: 0;

      a {
        color: var(--color-primary-600);
        text-decoration: none;
        font-weight: var(--font-weight-semibold);
        transition: color var(--transition-fast);

        &:hover {
          color: var(--color-primary-700);
          text-decoration: underline;
        }
      }
    }
  }
}

/* ========== 响应式设计 ========== */
@media (max-width: 1200px) {
  .brand-section {
    flex: 0 0 50%;
    padding: 60px 40px;
    align-items: flex-start;

    .brand-content {
      max-width: 480px;
    }

    .brand-header {
      .brand-title {
        font-size: 36px;
      }
    }
  }

  .login-section {
    flex: 0 0 50%;
    padding: 120px 40px 60px;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .brand-section {
    flex: 1;
    padding: 40px 20px;
    align-items: center;
    min-height: auto;

    .brand-content {
      max-width: 100%;
    }

    .brand-header {
      margin-bottom: 40px;

      .brand-title {
        font-size: 28px;
      }
    }

    .features-list {
      margin-bottom: 40px;
    }

    .stats-info {
      flex-direction: column;
      gap: 12px;
    }

    .brand-footer {
      display: none;
    }
  }

  .login-section {
    flex: 1;
    padding: 30px 20px;
    align-items: center;
  }

  .login-card {
    max-width: 100%;
    padding: 30px 20px;
  }
}

.role-option {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .role-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);
  }

  .role-desc {
    font-size: 12px;
    color: var(--text-tertiary);
  }
}
</style>
