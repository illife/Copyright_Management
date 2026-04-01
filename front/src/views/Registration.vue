<template>
  <div class="registration-container">
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

        <!-- 注册说明 -->
        <div class="info-card">
          <div class="info-header">
            <div class="info-icon">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <h3 class="info-title">注册流程说明</h3>
          </div>
          <div class="info-steps">
            <div class="step-item">
              <div class="step-number">1</div>
              <div class="step-content">
                <h4>提交申请</h4>
                <p>填写完整的注册信息并提交</p>
              </div>
            </div>
            <div class="step-item">
              <div class="step-number">2</div>
              <div class="step-content">
                <h4>管理员审核</h4>
                <p>管理员将在1-3个工作日内审核</p>
              </div>
            </div>
            <div class="step-item">
              <div class="step-number">3</div>
              <div class="step-content">
                <h4>区块链注册</h4>
                <p>审核通过后自动完成区块链用户注册</p>
              </div>
            </div>
            <div class="step-item">
              <div class="step-number">4</div>
              <div class="step-content">
                <h4>开始使用</h4>
                <p>使用您的区块链地址登录系统</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部说明 -->
        <div class="brand-footer">
          <p>政府级安全标准 · 企业级服务保障</p>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单区 -->
    <div class="registration-section">
      <div class="registration-card">
        <div class="registration-header">
          <h2 class="registration-title">用户注册申请</h2>
          <p class="registration-subtitle">请填写您的注册信息</p>
        </div>

        <el-form
          ref="registrationFormRef"
          :model="registrationForm"
          :rules="registrationRules"
          class="registration-form"
          label-position="top"
        >
          <el-form-item label="区块链地址" prop="userAddress">
            <el-input
              v-model="registrationForm.userAddress"
              size="large"
              placeholder="请输入您的区块链地址（0x...）"
              clearable
              class="registration-input"
            >
              <template #prefix>
                <el-icon><Wallet /></el-icon>
              </template>
            </el-input>
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>如果没有地址，可以使用工具生成一个以太坊格式的地址</span>
            </div>
          </el-form-item>

          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registrationForm.username"
              size="large"
              placeholder="请输入用户名"
              clearable
              class="registration-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="registrationForm.realName"
              size="large"
              placeholder="请输入真实姓名"
              clearable
              class="registration-input"
            >
              <template #prefix>
                <el-icon><Avatar /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="邮箱地址" prop="email">
            <el-input
              v-model="registrationForm.email"
              size="large"
              placeholder="请输入邮箱地址"
              clearable
              class="registration-input"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="联系电话" prop="phone">
            <el-input
              v-model="registrationForm.phone"
              size="large"
              placeholder="请输入联系电话"
              clearable
              class="registration-input"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="申请说明" prop="description">
            <el-input
              v-model="registrationForm.description"
              type="textarea"
              :rows="4"
              placeholder="请简单说明您的注册用途和身份（如：内容创作者、版权代理人等）"
              maxlength="500"
              show-word-limit
              class="registration-textarea"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="registration-button"
              @click="handleSubmit"
            >
              <span v-if="!loading">提交注册申请</span>
              <span v-else>提交中...</span>
            </el-button>
          </el-form-item>

          <el-form-item>
            <el-button
              size="large"
              class="back-button"
              @click="handleBack"
            >
              返回登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 注册成功对话框 -->
    <el-dialog
      v-model="showSuccessDialog"
      title="注册申请提交成功"
      width="480px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div class="success-dialog-content">
        <el-icon class="success-icon"><SuccessFilled /></el-icon>
        <h3 class="success-title">申请已提交</h3>
        <p class="success-desc">您的注册申请已成功提交，我们将在1-3个工作日内完成审核。</p>
        <div class="success-info">
          <div class="info-item">
            <span class="info-label">申请编号：</span>
            <span class="info-value">{{ applicationId }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">区块链地址：</span>
            <span class="info-value">{{ registrationForm.userAddress }}</span>
          </div>
        </div>
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          show-icon
        >
          审核通过后，您可以直接使用该区块链地址登录系统。
          <a href="#" @click.prevent="handleGoToCheckStatus" style="color: var(--color-primary-600); text-decoration: underline;">查询申请状态</a>
        </el-alert>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleDialogConfirm">
          我知道了
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Wallet,
  User,
  Avatar,
  Message,
  Phone,
  InfoFilled,
  SuccessFilled
} from '@element-plus/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()
const registrationFormRef = ref(null)
const loading = ref(false)
const showSuccessDialog = ref(false)
const applicationId = ref('')

const registrationForm = reactive({
  userAddress: '',
  username: '',
  realName: '',
  email: '',
  phone: '',
  description: ''
})

const registrationRules = {
  userAddress: [
    { required: true, message: '请输入区块链地址', trigger: 'blur' },
    {
      pattern: /^0x[a-fA-F0-9]{40}$/,
      message: '请输入有效的区块链地址格式（0x开头，42位字符）',
      trigger: 'blur'
    }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '真实姓名长度在2-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入有效的邮箱地址',
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入有效的手机号码',
      trigger: 'blur'
    }
  ],
  description: [
    { required: true, message: '请填写申请说明', trigger: 'blur' },
    { min: 10, max: 500, message: '申请说明长度在10-500个字符之间', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!registrationFormRef.value) return

  await registrationFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const data = await register(registrationForm)

      // 显示成功对话框
      applicationId.value = data.id || Date.now()
      showSuccessDialog.value = true

      ElMessage.success({
        message: '注册申请提交成功！',
        duration: 2000
      })
    } catch (error) {
      console.error('注册申请失败:', error)
      ElMessage.error({
        message: error.message || '注册申请提交失败，请稍后重试',
        duration: 3000
      })
    } finally {
      loading.value = false
    }
  })
}

const handleBack = () => {
  router.push('/login')
}

const handleDialogConfirm = () => {
  showSuccessDialog.value = false
  // 跳转到状态查询页面，而不是登录页
  router.push('/my-status')
}

const handleGoToCheckStatus = () => {
  showSuccessDialog.value = false
  router.push('/my-status')
}
</script>

<style lang="scss" scoped>
.registration-container {
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

  /* 注册说明卡片 */
  .info-card {
    padding: 30px;
    background: var(--bg-secondary);
    border: 1px solid var(--border-primary);
    border-radius: 12px;
    margin-bottom: 40px;

    .info-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;

      .info-icon {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--bg-tertiary);
        border: 1px solid var(--color-primary-600);
        border-radius: 8px;
        color: var(--color-primary-600);
        font-size: 20px;
      }

      .info-title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
      }
    }

    .info-steps {
      .step-item {
        display: flex;
        gap: 16px;
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .step-number {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: var(--gradient-primary);
          color: white;
          border-radius: 50%;
          font-size: 14px;
          font-weight: 600;
          flex-shrink: 0;
        }

        .step-content {
          flex: 1;

          h4 {
            font-size: 15px;
            font-weight: 600;
            color: var(--text-primary);
            margin: 0 0 6px 0;
          }

          p {
            font-size: 13px;
            color: var(--text-tertiary);
            margin: 0;
            line-height: 1.5;
          }
        }
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

/* ========== 右侧注册表单区 ========== */
.registration-section {
  flex: 0 0 45%;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 120px 40px 60px;
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

.registration-card {
  width: 100%;
  max-width: 480px;
  padding: 40px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);

  .registration-header {
    text-align: center;
    margin-bottom: 32px;

    .registration-title {
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary);
      margin: 0 0 12px 0;
    }

    .registration-subtitle {
      font-size: 14px;
      color: var(--text-tertiary);
      margin: 0;
    }
  }

  .registration-form {
    :deep(.el-form-item__label) {
      color: var(--text-secondary);
      font-weight: 500;
      margin-bottom: 8px;
    }

    .registration-input {
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
          border: 1px solid var(--color-primary-600) !important;
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
          border-color: var(--color-primary-600) !important;
        }
      }

      :deep(.el-input__prefix) {
        color: var(--text-tertiary);
      }
    }

    .registration-textarea {
      :deep(.el-textarea__inner) {
        background: var(--bg-tertiary) !important;
        border: 1px solid var(--border-primary) !important;
        color: var(--text-primary);

        &:focus {
          border-color: var(--color-primary-600) !important;
        }

        &::placeholder {
          color: var(--text-tertiary);
        }
      }
    }

    .form-tip {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-top: 8px;
      font-size: 12px;
      color: var(--text-tertiary);

      .el-icon {
        color: var(--color-primary-600);
        flex-shrink: 0;
      }
    }

    .registration-button {
      width: 100%;
      height: 48px;
      font-size: 16px;
      font-weight: 600;
      background: var(--gradient-primary);
      border: none;
      margin-top: 8px;

      &:hover {
        background: var(--gradient-primary);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px var(--shadow-color-primary);
      }

      &:active {
        transform: translateY(0);
      }
    }

    .back-button {
      width: 100%;
      height: 48px;
      font-size: 16px;
      font-weight: 600;
      background: transparent;
      border: 1px solid var(--border-primary);
      color: var(--text-secondary);

      &:hover {
        background: var(--bg-tertiary);
        border-color: var(--text-tertiary);
        color: var(--text-primary);
      }
    }
  }
}

/* ========== 成功对话框 ========== */
.success-dialog-content {
  text-align: center;
  padding: 20px 0;

  .success-icon {
    font-size: 64px;
    color: var(--color-success-600);
    margin-bottom: 20px;
  }

  .success-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 12px 0;
  }

  .success-desc {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0 0 24px 0;
    line-height: 1.6;
  }

  .success-info {
    background: var(--bg-tertiary);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    text-align: left;

    .info-item {
      display: flex;
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: 13px;
        color: var(--text-tertiary);
        min-width: 100px;
      }

      .info-value {
        font-size: 13px;
        color: var(--text-primary);
        font-weight: 500;
      }
    }
  }

  :deep(.el-alert) {
    text-align: left;
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

  .registration-section {
    flex: 0 0 50%;
    padding: 80px 40px 60px;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .registration-container {
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

    .info-card {
      padding: 20px;
    }

    .brand-footer {
      display: none;
    }
  }

  .registration-section {
    flex: 1;
    padding: 30px 20px;
    align-items: center;
  }

  .registration-card {
    max-width: 100%;
    padding: 30px 20px;
  }
}
</style>
