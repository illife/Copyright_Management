<template>
  <div class="apply-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">申请授权</h1>
        <p class="page-subtitle">为您的作品申请区块链授权，安全可信</p>
      </div>
      <button class="btn btn-secondary" @click="goBack">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M15 9H3M3 9L8 14M3 9L8 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回列表
      </button>
    </div>

    <!-- 步骤指示器 -->
    <div class="steps-container">
      <div class="steps-wrapper">
        <div
          v-for="(step, index) in steps"
          :key="index"
          class="step-item"
          :class="{ active: currentStep === index, completed: currentStep > index }"
        >
          <div class="step-number">
            <svg v-if="currentStep > index" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16.6667 5L7.50001 14.1667L3.33334 10" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span v-else>{{ index + 1 }}</span>
          </div>
          <div class="step-info">
            <div class="step-title">{{ step.title }}</div>
            <div class="step-desc">{{ step.description }}</div>
          </div>
          <div v-if="index < steps.length - 1" class="step-line" :class="{ active: currentStep > index }"></div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <!-- 左侧表单 -->
      <div class="form-section">
        <!-- 步骤1: 选择版权 -->
        <div v-show="currentStep === 0" class="step-content">
          <div class="step-header">
            <h2 class="step-heading">选择要授权的版权</h2>
            <p class="step-subheading">从已有版权列表中选择您想要授权的作品</p>
          </div>

          <div class="form-group">
            <label class="field-label">版权作品</label>
            <div class="copyright-select-wrapper">
              <select v-model="form.copyrightId" class="field-select" @change="handleCopyrightChange">
                <option value="">请选择版权作品</option>
                <option v-for="item in copyrightOptions" :key="item.id" :value="String(item.id)">
                  {{ item.title }} - {{ item.author }}
                </option>
              </select>
              <div class="select-icon">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M5 8L10 13L15 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
            </div>
            <div v-if="errors.copyrightId" class="field-error">{{ errors.copyrightId }}</div>
          </div>

          <!-- 版权详情卡片 -->
          <div v-if="selectedCopyright" class="copyright-detail-card">
            <div class="detail-header">
              <h3 class="detail-title">版权信息</h3>
              <div class="detail-badge">已上链</div>
            </div>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">作品标题</span>
                <span class="detail-value">{{ selectedCopyright.title }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">作者</span>
                <span class="detail-value">{{ selectedCopyright.author }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">拥有者地址</span>
                <span class="detail-value">{{ formatAddress(selectedCopyright.ownerAddress) }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">注册时间</span>
                <span class="detail-value">{{ formatTime(selectedCopyright.registerTime) }}</span>
              </div>
              <div class="detail-item detail-full">
                <span class="detail-label">作品描述</span>
                <span class="detail-value">{{ selectedCopyright.description || '暂无描述' }}</span>
              </div>
            </div>
          </div>

          <!-- 搜索帮助 -->
          <div class="search-hint">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V13H11V15ZM11 11H9V5H11V11Z" fill="currentColor"/>
            </svg>
            <div class="hint-text">
              <strong>提示：</strong>如果列表中没有您要的版权，请先前往版权管理页面注册
            </div>
          </div>
        </div>

        <!-- 步骤2: 设置授权参数 -->
        <div v-show="currentStep === 1" class="step-content">
          <div class="step-header">
            <h2 class="step-heading">配置授权参数</h2>
            <p class="step-subheading">设置授权的详细信息和权限范围</p>
          </div>

          <!-- 被授权方地址 -->
          <div class="form-group">
            <label class="field-label">被授权方地址</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 3C11.66 3 13 4.34 13 6C13 7.66 11.66 9 10 9C8.34 9 7 7.66 7 6C7 4.34 8.34 3 10 3ZM10 17.2C7.5 17.2 5.29 16.08 4 14.26C4.03 12.38 8 11.3 10 11.3C11.99 11.3 15.97 12.38 16 14.26C14.71 16.08 12.5 17.2 10 17.2Z" fill="currentColor"/>
              </svg>
              <input
                v-model="form.licenseeAddress"
                type="text"
                class="field-input"
                placeholder="0x..."
                maxlength="42"
              />
            </div>
            <div class="field-hint">被授权方将获得该版权的使用权限</div>
            <div v-if="errors.licenseeAddress" class="field-error">{{ errors.licenseeAddress }}</div>
          </div>

          <!-- 授权时间 -->
          <div class="form-group">
            <label class="field-label">授权结束时间</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.59 18 2 14.41 2 10C2 5.59 5.59 2 10 2C14.41 2 18 5.59 18 10C18 14.41 14.41 18 10 18ZM10.5 5H9V11L14.25 14.15L15 12.92L10.5 10.25V5Z" fill="currentColor"/>
              </svg>
              <input
                v-model="endTimeText"
                type="datetime-local"
                class="field-input"
                :min="minDate"
                :max="maxDate"
              />
            </div>
            <div class="field-hint">授权开始时间默认为当前时间，最长10年</div>
            <div v-if="errors.endTime" class="field-error">{{ errors.endTime }}</div>
          </div>

          <!-- 授权类型 -->
          <div class="form-group">
            <label class="field-label">授权类型</label>
            <div class="license-type-options">
              <div
                class="type-option"
                :class="{ active: !form.isExclusive }"
                @click="form.isExclusive = false"
              >
                <div class="type-radio">
                  <div class="radio-inner" :class="{ checked: !form.isExclusive }"></div>
                </div>
                <div class="type-content">
                  <div class="type-title">普通授权</div>
                  <div class="type-desc">可多人同时使用</div>
                </div>
              </div>
              <div
                class="type-option"
                :class="{ active: form.isExclusive }"
                @click="form.isExclusive = true"
              >
                <div class="type-radio">
                  <div class="radio-inner" :class="{ checked: form.isExclusive }"></div>
                </div>
                <div class="type-content">
                  <div class="type-title">独占授权</div>
                  <div class="type-desc">仅限一人使用</div>
                </div>
              </div>
            </div>
            <div v-if="form.isExclusive" class="exclusive-warning">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V13H11V15ZM11 11H9V5H11V11Z" fill="currentColor"/>
              </svg>
              <span>独占授权期间，版权拥有者不能再向其他人授权同一时间段</span>
            </div>
          </div>

          <!-- 时间预览 -->
          <div v-if="form.endTime" class="time-preview">
            <div class="preview-item">
              <span class="preview-label">开始时间</span>
              <span class="preview-value">{{ formatTimePreview(Date.now()) }}</span>
            </div>
            <div class="preview-divider"></div>
            <div class="preview-item">
              <span class="preview-label">结束时间</span>
              <span class="preview-value">{{ formatTimePreview(form.endTime) }}</span>
            </div>
            <div class="preview-item preview-full">
              <span class="preview-label">授权期限</span>
              <span class="preview-value">{{ calculateDuration() }}</span>
            </div>
          </div>
        </div>

        <!-- 步骤3: 确认提交 -->
        <div v-show="currentStep === 2" class="step-content">
          <div class="step-header">
            <h2 class="step-heading">确认授权申请信息</h2>
            <p class="step-subheading">请仔细核对以下信息，提交后将记录在区块链上</p>
          </div>

          <div class="confirm-card">
            <div class="confirm-header">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 6C13.66 6 15 7.34 15 9C15 10.66 13.66 12 12 12C10.34 12 9 10.66 9 9C9 7.34 10.34 6 12 6ZM12 18.2C9.5 18.2 7.29 17.08 6 15.26C6.03 13.38 10 12.3 12 12.3C13.99 12.3 17.97 13.38 18 15.26C16.71 17.08 14.5 18.2 12 18.2Z" fill="currentColor"/>
              </svg>
              <h3>授权申请详情</h3>
            </div>

            <div class="confirm-list">
              <div class="confirm-item">
                <span class="confirm-label">版权作品</span>
                <span class="confirm-value">{{ selectedCopyright?.title }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">作品作者</span>
                <span class="confirm-value">{{ selectedCopyright?.author }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">被授权方</span>
                <span class="confirm-value">{{ form.licenseeAddress }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">授权类型</span>
                <span class="confirm-value">
                  <span class="confirm-badge" :class="{ exclusive: form.isExclusive }">
                    {{ form.isExclusive ? '独占授权' : '普通授权' }}
                  </span>
                </span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">开始时间</span>
                <span class="confirm-value">{{ formatTimePreview(Date.now()) }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">结束时间</span>
                <span class="confirm-value">{{ formatTimePreview(form.endTime) }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">授权期限</span>
                <span class="confirm-value">{{ calculateDuration() }}</span>
              </div>
            </div>

            <!-- 区块链提示 -->
            <div class="blockchain-notice">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 0L2 4V10C2 15.52 5.84 20.74 10 22C14.16 20.74 18 15.52 18 10V4L10 0ZM10 16H8V11H10V16ZM10 9H8V5H10V9Z" fill="currentColor"/>
              </svg>
              <div class="notice-text">
                <strong>区块链存证：</strong>提交后，授权申请信息将记录在区块链上，不可篡改
              </div>
            </div>
          </div>
        </div>

        <!-- 导航按钮 -->
        <div class="form-actions">
          <button v-if="currentStep > 0" class="btn btn-secondary" @click="prevStep">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M15 9H3M3 9L8 14M3 9L8 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            上一步
          </button>
          <button v-if="currentStep < steps.length - 1" class="btn btn-primary" @click="nextStep">
            下一步
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9H15M15 9L10 4M15 9L10 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button
            v-if="currentStep === steps.length - 1"
            class="btn btn-primary"
            :disabled="submitting"
            @click="handleSubmit"
          >
            <svg v-if="!submitting" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 1V13M9 13L5 9M9 13L13 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M1 13V15C1 16.1 1.9 17 3 17H15C16.1 17 17 16.1 17 15V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </div>

      <!-- 右侧信息栏 -->
      <div class="info-sidebar">
        <!-- 申请说明 -->
        <div class="sidebar-card">
          <div class="sidebar-card-header">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V13H11V15ZM11 11H9V5H11V11Z" fill="currentColor"/>
            </svg>
            <h3>申请说明</h3>
          </div>
          <ul class="info-list">
            <li>申请提交后需要版权拥有者审批</li>
            <li>审批通过后需激活授权才能生效</li>
            <li>授权时间结束后自动失效</li>
            <li>版权拥有者可以撤销授权</li>
            <li>所有操作记录在区块链上，可追溯</li>
          </ul>
        </div>

        <!-- 授权流程 -->
        <div class="sidebar-card">
          <div class="sidebar-card-header">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M2 2H8V8H2V2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 2H18V8H12V2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12H8V18H2V12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 12H18V18H12V12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3>授权流程</h3>
          </div>
          <div class="process-flow">
            <div class="process-step process-done">
              <div class="process-icon">1</div>
              <div class="process-text">
                <div class="process-title">提交申请</div>
                <div class="process-desc">填写授权信息</div>
              </div>
            </div>
            <div class="process-step">
              <div class="process-icon">2</div>
              <div class="process-text">
                <div class="process-title">等待审批</div>
                <div class="process-desc">版权方审核</div>
              </div>
            </div>
            <div class="process-step">
              <div class="process-icon">3</div>
              <div class="process-text">
                <div class="process-title">激活授权</div>
                <div class="process-desc">批准后激活</div>
              </div>
            </div>
            <div class="process-step">
              <div class="process-icon">4</div>
              <div class="process-text">
                <div class="process-title">使用中</div>
                <div class="process-desc">正常使用</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applyLicense } from '@/api/license'
import { getCopyrightList } from '@/api/copyright'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const currentStep = ref(0)
const submitting = ref(false)
const copyrightOptions = ref([])
const selectedCopyright = ref(null)

const steps = [
  { title: '选择版权', description: '选择要授权的版权作品' },
  { title: '设置参数', description: '配置授权详细信息' },
  { title: '确认提交', description: '核对并提交申请' }
]

const form = reactive({
  copyrightId: '',
  licenseeAddress: userStore.address || '',
  endTime: null,
  isExclusive: false
})

const errors = reactive({
  copyrightId: '',
  licenseeAddress: '',
  endTime: ''
})

// 日期计算
const minDate = computed(() => {
  const now = new Date()
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset())
  return now.toISOString().slice(0, 16)
})

const maxDate = computed(() => {
  const now = new Date()
  const tenYearsLater = new Date(now.getTime() + 10 * 365 * 24 * 60 * 60 * 1000)
  tenYearsLater.setMinutes(tenYearsLater.getMinutes() - tenYearsLater.getTimezoneOffset())
  return tenYearsLater.toISOString().slice(0, 16)
})

const endTimeText = computed({
  get: () => {
    if (!form.endTime) return ''
    const date = new Date(form.endTime)
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset())
    return date.toISOString().slice(0, 16)
  },
  set: (value) => {
    if (value) {
      form.endTime = new Date(value).getTime()
    } else {
      form.endTime = null
    }
  }
})

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 8)}...${address.substring(address.length - 6)}`
}

const formatTime = (timestamp) => {
  return dayjs(timestamp * 1000).format('YYYY-MM-DD HH:mm:ss')
}

const formatTimePreview = (timestamp) => {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm')
}

const calculateDuration = () => {
  if (!form.endTime) return '-'
  const now = Date.now()
  const diff = form.endTime - now
  const days = Math.floor(diff / (24 * 60 * 60 * 1000))
  const years = Math.floor(days / 365)
  const remainingDays = days % 365

  if (years > 0) {
    return `${years}年${remainingDays}天`
  }
  return `${days}天`
}

const handleCopyrightChange = () => {
  const copyright = copyrightOptions.value.find(item => String(item.id) === form.copyrightId)
  selectedCopyright.value = copyright || null
  errors.copyrightId = ''
}

const validateStep = (step) => {
  // 清空之前的错误
  Object.keys(errors).forEach(key => errors[key] = '')

  if (step === 0) {
    if (!form.copyrightId) {
      errors.copyrightId = '请选择版权作品'
      return false
    }
  }

  if (step === 1) {
    if (!form.licenseeAddress) {
      errors.licenseeAddress = '请输入被授权方地址'
      return false
    }
    if (!/^0x[a-fA-F0-9]{40}$/.test(form.licenseeAddress)) {
      errors.licenseeAddress = '请输入有效的区块链地址格式'
      return false
    }
    if (!form.endTime) {
      errors.endTime = '请选择授权结束时间'
      return false
    }
    const now = Date.now()
    const maxDuration = 10 * 365 * 24 * 60 * 60 * 1000
    if (form.endTime - now > maxDuration) {
      errors.endTime = '授权期限不能超过10年'
      return false
    }
  }

  return true
}

const nextStep = () => {
  if (validateStep(currentStep.value)) {
    if (currentStep.value < steps.length - 1) {
      currentStep.value++
    }
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleSubmit = async () => {
  if (!validateStep(currentStep.value)) return

  submitting.value = true
  try {
    const data = await applyLicense({
      copyrightId: form.copyrightId,
      licenseeAddress: form.licenseeAddress,
      endTime: form.endTime,
      isExclusive: form.isExclusive
    })

    ElMessage.success(`授权申请成功！授权ID: ${data.contractId}`)

    // 询问是否查看详情
    setTimeout(() => {
      ElMessageBox.confirm(
        '是否跳转到授权详情页面？',
        '申请成功',
        {
          confirmButtonText: '查看详情',
          cancelButtonText: '继续申请',
          type: 'success'
        }
      ).then(() => {
        router.push(`/licenses/detail/${data.id}`)
      }).catch(() => {
        // 重置表单，继续申请
        currentStep.value = 0
        form.copyrightId = ''
        form.endTime = null
        form.isExclusive = false
        selectedCopyright.value = null
      })
    }, 500)
  } catch (error) {
    console.error('申请失败:', error)
    ElMessage.error(error.message || '申请失败，请重试')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/licenses')
}

const loadCopyrights = async () => {
  try {
    const data = await getCopyrightList({ pageNum: 1, pageSize: 100 })
    copyrightOptions.value = data.records || []
  } catch (error) {
    console.error('加载版权列表失败:', error)
  }
}

onMounted(() => {
  loadCopyrights()
})
</script>

<style lang="scss" scoped>
.apply-page {
  max-width: 1400px;
  margin: 0 auto;
}

// 页面头部
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  gap: 24px;
}

.header-content {
  flex: 1;

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

.btn {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  white-space: nowrap;

  &.btn-primary {
    background: var(--gradient-primary);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  &.btn-secondary {
    background: var(--bg-tertiary);
    color: var(--text-primary);

    &:hover {
      background: var(--border-primary);
    }
  }
}

// 步骤指示器
.steps-container {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 32px;
  border: 1px solid var(--border-primary);
}

.steps-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 800px;
  margin: 0 auto;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  position: relative;

  .step-number {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: 600;
    background: var(--bg-tertiary);
    color: var(--text-tertiary);
    flex-shrink: 0;
    transition: all 0.3s;
  }

  .step-info {
    flex: 1;

    .step-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-tertiary);
      margin-bottom: 4px;
    }

    .step-desc {
      font-size: 13px;
      color: var(--text-tertiary);
    }
  }

  .step-line {
    flex: 1;
    height: 2px;
    background: var(--border-primary);
    margin: 0 16px;

    &.active {
      background: linear-gradient(90deg, #667eea, #764ba2);
    }
  }

  &.active {
    .step-number {
      background: var(--gradient-primary);
      color: white;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }

    .step-title {
      color: var(--color-primary-600);
    }
  }

  &.completed {
    .step-number {
      background: var(--gradient-success);
      color: white;
    }

    .step-title {
      color: #22c55e;
    }
  }
}

// 主要内容
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
  align-items: start;
}

.form-section {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 40px;
  border: 1px solid var(--border-primary);
}

.step-content {
  min-height: 400px;
}

.step-header {
  margin-bottom: 32px;
  text-align: center;

  .step-heading {
    font-size: 24px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  .step-subheading {
    font-size: 15px;
    color: var(--text-secondary);
    margin: 0;
  }
}

// 表单元素
.form-group {
  margin-bottom: 28px;
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.copyright-select-wrapper {
  position: relative;

  .field-select {
    width: 100%;
    padding: 14px 48px 14px 16px;
    font-size: 15px;
    border: 1px solid var(--border-primary);
    border-radius: 10px;
    background: var(--bg-card);
    color: var(--text-primary);
    appearance: none;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--color-primary-600);
    }

    &:focus {
      outline: none;
      border-color: var(--color-primary-600);
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
  }

  .select-icon {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    color: var(--text-tertiary);
    pointer-events: none;
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  .input-icon {
    position: absolute;
    left: 16px;
    color: var(--text-tertiary);
    pointer-events: none;
  }

  .field-input {
    width: 100%;
    padding: 14px 16px 14px 48px;
    font-size: 15px;
    border: 1px solid var(--border-primary);
    border-radius: 10px;
    background: var(--bg-card);
    color: var(--text-primary);
    transition: all 0.2s;

    &::placeholder {
      color: var(--text-tertiary);
    }

    &:hover {
      border-color: var(--color-primary-600);
    }

    &:focus {
      outline: none;
      border-color: var(--color-primary-600);
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
  }
}

.field-hint {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 8px;
}

.field-error {
  font-size: 13px;
  color: #ef4444;
  margin-top: 6px;
}

// 版权详情卡片
.copyright-detail-card {
  background: var(--color-primary-light);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;

  .detail-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-primary);

    .detail-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }

    .detail-badge {
      padding: 6px 12px;
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
      font-size: 12px;
      font-weight: 600;
      border-radius: 6px;
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .detail-item {
      display: flex;
      flex-direction: column;
      gap: 6px;

      &.detail-full {
        grid-column: 1 / -1;
      }

      .detail-label {
        font-size: 12px;
        color: var(--text-secondary);
        font-weight: 500;
      }

      .detail-value {
        font-size: 14px;
        color: var(--text-primary);
        font-weight: 500;
      }
    }
  }
}

.search-hint {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fef3c7;
  border-radius: 10px;
  margin-top: 20px;

  svg {
    color: #f59e0b;
    flex-shrink: 0;
  }

  .hint-text {
    font-size: 14px;
    color: #92400e;
    line-height: 1.5;

    strong {
      font-weight: 600;
    }
  }
}

// 授权类型选择
.license-type-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 12px;
}

.type-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border: 2px solid var(--border-primary);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary-600);
  }

  &.active {
    border-color: var(--color-primary-600);
    background: var(--color-primary-light);
  }

  .type-radio {
    width: 20px;
    height: 20px;
    border: 2px solid var(--border-hover);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: all 0.2s;

    .radio-inner {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      transition: all 0.2s;

      &.checked {
        background: var(--gradient-primary);
      }
    }
  }

  .type-content {
    .type-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .type-desc {
      font-size: 13px;
      color: var(--text-secondary);
    }
  }
}

.exclusive-warning {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px;
  background: #fef3c7;
  border-radius: 8px;
  margin-top: 16px;

  svg {
    color: #f59e0b;
    flex-shrink: 0;
    margin-top: 2px;
  }

  span {
    font-size: 13px;
    color: #92400e;
    line-height: 1.5;
  }
}

// 时间预览
.time-preview {
  background: var(--color-primary-light);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 20px;
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .preview-item {
    display: flex;
    flex-direction: column;
    gap: 6px;

    &.preview-full {
      grid-column: 1 / -1;
    }

    .preview-label {
      font-size: 12px;
      color: var(--text-secondary);
      font-weight: 500;
    }

    .preview-value {
      font-size: 15px;
      color: var(--text-primary);
      font-weight: 600;
    }
  }

  .preview-divider {
    grid-column: 1 / -1;
    height: 1px;
    background: var(--border-primary);
  }
}

// 确认卡片
.confirm-card {
  background: var(--color-primary-light);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 32px;
}

.confirm-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-primary);

  svg {
    color: var(--color-primary-600);
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.confirm-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.confirm-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: var(--bg-card);
  border-radius: 10px;
  border: 1px solid var(--border-primary);

  .confirm-label {
    font-size: 14px;
    color: var(--text-secondary);
    font-weight: 500;
  }

  .confirm-value {
    font-size: 15px;
    color: var(--text-primary);
    font-weight: 600;
    text-align: right;

    .confirm-badge {
      padding: 4px 12px;
      background: rgba(102, 126, 234, 0.1);
      color: var(--color-primary-600);
      font-size: 13px;
      font-weight: 600;
      border-radius: 6px;

      &.exclusive {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
      }
    }
  }
}

.blockchain-notice {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: rgba(34, 197, 94, 0.1);
  border-radius: 10px;
  margin-top: 24px;

  svg {
    color: #22c55e;
    flex-shrink: 0;
  }

  .notice-text {
    font-size: 14px;
    color: #166534;
    line-height: 1.6;

    strong {
      font-weight: 600;
    }
  }
}

// 表单操作按钮
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid var(--border-primary);
}

// 信息侧边栏
.info-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: sticky;
  top: 100px;
}

.sidebar-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  padding: 24px;

  .sidebar-card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-secondary);

    svg {
      color: var(--color-primary-600);
    }

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .info-list {
    margin: 0;
    padding-left: 20px;

    li {
      margin: 10px 0;
      font-size: 14px;
      color: var(--text-secondary);
      line-height: 1.6;

      &::marker {
        color: var(--color-primary-600);
      }
    }
  }
}

.process-flow {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.process-step {
  display: flex;
  align-items: center;
  gap: 12px;

  .process-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 600;
    background: var(--bg-tertiary);
    color: var(--text-tertiary);
    flex-shrink: 0;
  }

  .process-text {
    .process-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 2px;
    }

    .process-desc {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }

  &.process-done {
    .process-icon {
      background: var(--gradient-primary);
      color: white;
    }

    .process-title {
      color: var(--color-primary-600);
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .info-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;

    .btn {
      width: 100%;
      justify-content: center;
    }
  }

  .steps-container {
    padding: 20px;
  }

  .steps-wrapper {
    flex-direction: column;
    gap: 20px;
  }

  .step-item {
    flex-direction: column;
    text-align: center;

    .step-line {
      display: none;
    }
  }

  .form-section {
    padding: 24px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .license-type-options {
    grid-template-columns: 1fr;
  }

  .time-preview {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;

    .btn {
      width: 100%;
      justify-content: center;
    }
  }
}
</style>
