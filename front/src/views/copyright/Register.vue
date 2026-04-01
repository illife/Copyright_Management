<template>
  <div class="register-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12.5 5L7.5 10L12.5 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回列表
      </button>
      <div class="header-content">
        <h1 class="page-title">注册版权</h1>
        <p class="page-subtitle">分步骤完成区块链版权存证</p>
      </div>
    </div>

    <!-- 步骤指示器 -->
    <div class="steps-container">
      <div class="steps-wrapper">
        <div
          v-for="(step, index) in steps"
          :key="index"
          class="step-item"
          :class="{
            'active': currentStep === index,
            'completed': currentStep > index
          }"
        >
          <div class="step-number">
            <svg v-if="currentStep > index" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16.67 5L7.5 14.17L3.33 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span v-else>{{ index + 1 }}</span>
          </div>
          <div class="step-info">
            <div class="step-title">{{ step.title }}</div>
            <div class="step-desc">{{ step.description }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 表单内容区 -->
    <div class="form-container">
      <!-- 步骤1: 基本信息 -->
      <div v-show="currentStep === 0" class="step-content">
        <div class="content-header">
          <h2 class="content-title">基本信息</h2>
          <p class="content-desc">填写版权的基本信息，包括标题和作者</p>
        </div>

        <div class="form-fields">
          <div class="field-group">
            <label class="field-label">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 1C4.58 1 1 4.58 1 9C1 13.42 4.58 17 9 17C13.42 17 17 13.42 17 9C17 4.58 13.42 1 9 1ZM9 15C5.69 15 3 12.31 3 9C3 5.69 5.69 3 9 3C12.31 3 15 5.69 15 9C15 12.31 12.31 15 9 15ZM8 5H10V10H13V12H5V10H8V5Z" fill="currentColor"/>
              </svg>
              版权标题 <span class="required">*</span>
            </label>
            <input
              v-model="form.title"
              type="text"
              class="field-input"
              placeholder="请输入版权标题（如：歌曲名称、作品名称等）"
              maxlength="100"
            />
            <div class="field-counter">{{ form.title.length }}/100</div>
            <div v-if="errors.title" class="field-error">{{ errors.title }}</div>
          </div>

          <div class="field-group">
            <label class="field-label">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 1C4.58 1 1 4.58 1 9C1 12.42 3.58 15 6.58 15H11.42C14.42 15 17 12.42 17 9C17 4.58 14.42 1 11.42 1H6.58ZM9 7C10.1 7 11 7.9 11 9C11 10.1 10.1 11 9 11C7.9 11 7 10.1 7 9C7 7.9 7.9 7 9 7ZM12 13H6V12.2C6 10.7 7.5 10 9 10C10.5 10 12 10.7 12 12.2V13Z" fill="currentColor"/>
              </svg>
              作者/创作者 <span class="required">*</span>
            </label>
            <input
              v-model="form.author"
              type="text"
              class="field-input"
              placeholder="请输入作者或创作者名称"
              maxlength="50"
            />
            <div class="field-counter">{{ form.author.length }}/50</div>
            <div v-if="errors.author" class="field-error">{{ errors.author }}</div>
          </div>

          <div class="field-group">
            <label class="field-label">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 1C4.58 1 1 4.58 1 9C1 13.42 4.58 17 9 17C13.42 17 17 13.42 17 9C17 4.58 13.42 1 9 1ZM9 15C5.69 15 3 12.31 3 9C3 5.69 5.69 3 9 3C12.31 3 15 5.69 15 9C15 12.31 12.31 15 9 15ZM9.5 5H8.5V9.25L5.5 11.15L6 12L10.5 9.25V5Z" fill="currentColor"/>
              </svg>
              作品描述 <span class="required">*</span>
            </label>
            <textarea
              v-model="form.description"
              class="field-textarea"
              rows="6"
              placeholder="请输入作品描述、创作背景、使用范围等信息"
              maxlength="500"
            ></textarea>
            <div class="field-counter">{{ form.description.length }}/500</div>
            <div v-if="errors.description" class="field-error">{{ errors.description }}</div>
          </div>
        </div>
      </div>

      <!-- 步骤2: 文件信息 -->
      <div v-show="currentStep === 1" class="step-content">
        <div class="content-header">
          <h2 class="content-title">文件信息</h2>
          <p class="content-desc">上传文件或输入文件哈希值用于验证</p>
        </div>

        <div class="form-fields">
          <!-- 文件上传区域 -->
          <div class="upload-area" :class="{ 'drag-over': isDragOver }" @dragover.prevent @dragleave.prevent @drop.prevent="handleDrop">
            <div class="upload-icon">
              <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M32 8C18.75 8 8 18.75 8 32C8 45.25 18.75 56 32 56C45.25 56 56 45.25 56 32C56 18.75 45.25 8 32 8ZM32 52C21.5 52 13 43.5 13 33C13 32.33 13.05 31.68 13.14 31.05L20 37.91L23.41 34.5L15.83 26.92C17.33 22.34 21.41 19 26.18 19H29V30L40 19L29 8V15H26.18C19.41 15 13.73 19.12 11.34 25.01L17.41 31.08C18.11 23.85 24.27 18.25 31.5 18.25C39.5 18.25 46 24.75 46 32.75C46 40.75 39.5 47.25 31.5 47.25C28.13 47.25 25.13 46.08 22.89 44.14L19.59 47.44C22.74 50.11 26.85 51.75 31.5 51.75C41.61 51.75 50 43.36 50 33.25C50 30.08 49.17 27.11 47.72 24.53L51.97 20.28C54.53 24.06 56 28.73 56 33.75C56 47 45.25 57.75 32 57.75V52Z" fill="currentColor"/>
              </svg>
            </div>
            <h3 class="upload-title">拖拽文件到此处</h3>
            <p class="upload-desc">支持 JPG, PNG, PDF, MP4, MP3 等格式，最大 100MB</p>
            <button class="btn btn-outline" @click="selectFile">
              选择文件
            </button>
            <input
              ref="fileInputRef"
              type="file"
              class="file-input"
              @change="handleFileSelect"
            />
          </div>

          <!-- 文件预览 -->
          <div v-if="uploadedFile" class="file-preview">
            <div class="file-icon">
              <svg v-if="uploading" width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" class="spin">
                <path d="M16 2C8.28 2 2 8.28 2 16C2 23.72 8.28 30 16 30C23.72 30 30 23.72 30 16C30 8.28 23.72 2 16 2ZM14 22H10V18H14V22ZM14 16H10V10H14V16ZM22 22H18V18H22V22ZM22 16H18V10H22V16Z" fill="currentColor"/>
              </svg>
              <svg v-else width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 2C4.9 2 4 2.9 4 4V28C4 29.1 4.9 30 6 30H26C27.1 30 28 29.1 28 28V10L20 2H6ZM19 12V4L26.5 11.5L19 12Z" fill="currentColor"/>
              </svg>
            </div>
            <div class="file-info">
              <div class="file-name">{{ uploadedFile.name }}</div>
              <div class="file-size">{{ formatFileSize(uploadedFile.size) }}</div>
              <div v-if="uploading" class="upload-status">上传中...</div>
              <div v-else-if="form.fileHash" class="hash-display">
                <span class="hash-label">哈希:</span>
                <span class="hash-value">{{ form.fileHash.substring(0, 16) }}...</span>
              </div>
            </div>
            <button class="btn-remove" @click="removeFile" :disabled="uploading">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 5H10V4H8V5H4V7H14V5ZM5 8V14C5 15.1 5.9 16 7 16H11C12.1 16 13 15.1 13 14V8H5ZM8 10H10V14H8V10Z" fill="currentColor"/>
              </svg>
            </button>
          </div>

          <!-- 或手动输入哈希 -->
          <div class="divider">
            <span>或手动输入文件哈希</span>
          </div>

          <div class="field-group">
            <label class="field-label">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 1C4.58 1 1 4.58 1 9C1 13.42 4.58 17 9 17C13.42 17 17 13.42 17 9C17 4.58 13.42 1 9 1ZM9 15C5.69 15 3 12.31 3 9C3 5.69 5.69 3 9 3C12.31 3 15 5.69 15 9C15 12.31 12.31 15 9 15ZM9.5 5H8.5V9.25L5.5 11.15L6 12L10.5 9.25V5Z" fill="currentColor"/>
              </svg>
              文件哈希值 <span class="required">*</span>
            </label>
            <div class="hash-input-group">
              <input
                v-model="form.fileHash"
                type="text"
                class="field-input"
                placeholder="请输入IPFS哈希值（Qm...）或SHA256哈希值（64位十六进制）"
                maxlength="200"
              />
              <button class="btn-generate" @click="generateHash">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM11 9H9V12H7V9H5V7H7V4H9V7H11V9Z" fill="currentColor"/>
                </svg>
                生成示例
              </button>
            </div>
            <div class="field-tip">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M7 0C3.13 0 0 3.13 0 7C0 10.87 3.13 14 7 14C10.87 14 14 10.87 14 7C14 3.13 10.87 0 7 0ZM7.7 10.5H6.3V9.1H7.7V10.5ZM7.7 7.7H6.3V3.5H7.7V7.7Z" fill="currentColor"/>
              </svg>
              文件哈希用于验证文件完整性。支持IPFS哈希（Qm开头）或SHA256哈希（64位十六进制）。上传文件后将自动生成哈希值。
            </div>
            <div v-if="errors.fileHash" class="field-error">{{ errors.fileHash }}</div>
          </div>
        </div>
      </div>

      <!-- 步骤3: 确认信息 -->
      <div v-show="currentStep === 2" class="step-content">
        <div class="content-header">
          <h2 class="content-title">确认信息</h2>
          <p class="content-desc">请核对版权信息，确认无误后提交</p>
        </div>

        <div class="confirm-section">
          <div class="confirm-card">
            <div class="confirm-header">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 1L3 5V11C3 16.55 6.84 21.74 12 23C17.16 21.74 21 16.55 21 11V5L12 1Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <h3>版权信息确认</h3>
            </div>
            <div class="confirm-list">
              <div class="confirm-item">
                <span class="confirm-label">版权标题</span>
                <span class="confirm-value">{{ form.title }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">作者/创作者</span>
                <span class="confirm-value">{{ form.author }}</span>
              </div>
              <div class="confirm-item">
                <span class="confirm-label">文件哈希</span>
                <span class="confirm-value hash-value">{{ form.fileHash }}</span>
              </div>
              <div class="confirm-item full">
                <span class="confirm-label">作品描述</span>
                <span class="confirm-value">{{ form.description }}</span>
              </div>
            </div>
          </div>

          <div class="notice-box">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 1C5.03 1 1 5.03 1 10C1 14.97 5.03 19 10 19C14.97 19 19 14.97 19 10C19 5.03 14.97 1 10 1ZM11 15H9V14H11V15ZM11 13H9V5H11V13Z" fill="currentColor"/>
            </svg>
            <div class="notice-content">
              <strong>重要提示：</strong>版权信息将永久记录在区块链上，提交后无法修改。请确保所有信息准确无误。
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <button
          v-if="currentStep > 0"
          class="btn btn-secondary"
          @click="prevStep"
        >
          上一步
        </button>
        <button
          v-if="currentStep < steps.length - 1"
          class="btn btn-primary"
          @click="nextStep"
        >
          下一步
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6.5 4L12.5 9L6.5 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          v-if="currentStep === steps.length - 1"
          class="btn btn-primary"
          :disabled="submitting"
          @click="handleSubmit"
        >
          <svg v-if="submitting" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" class="spin">
            <path d="M16.5 9C16.5 13.14 13.14 16.5 9 16.5C4.86 16.5 1.5 13.14 1.5 9C1.5 4.86 4.86 1.5 9 1.5C13.14 1.5 16.5 4.86 16.5 9ZM3 9C3 12.31 5.69 15 9 15C12.31 15 15 12.31 15 9C15 5.69 12.31 3 9 3C5.69 3 3 5.69 3 9Z" fill="currentColor"/>
          </svg>
          {{ submitting ? '提交中...' : '确认提交' }}
        </button>
      </div>
    </div>

    <!-- 侧边信息栏 -->
    <div class="info-sidebar">
      <div class="info-card">
        <h4 class="info-title">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 1C5.03 1 1 5.03 1 10C1 14.97 5.03 19 10 19C14.97 19 19 14.97 19 10C19 5.03 14.97 1 10 1ZM11 15H9V14H11V15ZM11 13H9V5H11V13Z" fill="currentColor"/>
          </svg>
          注册说明
        </h4>
        <ul class="info-list">
          <li>版权信息将永久记录在区块链上，不可篡改</li>
          <li>请确保填写的标题和作者信息准确无误</li>
          <li>文件哈希用于验证作品的完整性和唯一性</li>
          <li>注册成功后将生成唯一的版权ID</li>
        </ul>
      </div>

      <div class="info-card" v-if="recentCopyrights.length > 0">
        <h4 class="info-title">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 1C5.58 1 1 5.58 1 10C1 14.42 5.58 17 10 17C14.42 17 17 14.42 17 10C17 5.58 14.42 1 10 1ZM10 15C6.69 15 3 12.31 3 9C3 5.69 6.69 3 10 3C12.31 3 15 5.69 15 9C15 12.31 12.31 15 10 15ZM10 5C8.34 5 7 6.34 7 8C7 9.66 8.34 11 10 11C11.66 11 13 9.66 13 8C13 6.34 11.66 5 10 5ZM10 13C7.33 13 5 14.33 5 16V17H15V16C15 14.33 12.67 13 10 13Z" fill="currentColor"/>
          </svg>
          最近注册
        </h4>
        <div class="recent-list">
          <div v-for="item in recentCopyrights" :key="item.id" class="recent-item">
            <div class="recent-title">{{ item.title }}</div>
            <div class="recent-time">{{ formatTime(item.registerTime) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { registerCopyright, getCopyrightList } from '@/api/copyright'
import { uploadFile } from '@/api/file'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const currentStep = ref(0)
const submitting = ref(false)
const isDragOver = ref(false)
const uploadedFile = ref(null)
const fileInputRef = ref(null)
const recentCopyrights = ref([])
const uploading = ref(false)

const steps = [
  { title: '基本信息', description: '填写标题和作者' },
  { title: '文件信息', description: '上传文件或哈希' },
  { title: '确认提交', description: '核对并提交' }
]

const form = reactive({
  title: '',
  author: '',
  fileHash: '',
  description: ''
})

const errors = reactive({
  title: '',
  author: '',
  fileHash: '',
  description: ''
})

// 权限检查
onBeforeMount(() => {
  const canRegister = userStore.isContentOwner || userStore.isLicenseManager || userStore.isSystemAdmin
  if (!canRegister) {
    ElMessage.warning('您没有权限注册版权')
    router.push('/copyrights')
  }
})

const formatTime = (timestamp) => {
  return dayjs(timestamp * 1000).format('MM-DD HH:mm')
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const validateStep = (step) => {
  // 清空错误
  Object.keys(errors).forEach(key => errors[key] = '')

  if (step === 0) {
    if (!form.title) {
      errors.title = '请输入版权标题'
      return false
    }
    if (form.title.length < 2) {
      errors.title = '标题至少需要2个字符'
      return false
    }
    if (!form.author) {
      errors.author = '请输入作者名称'
      return false
    }
    if (form.author.length < 2) {
      errors.author = '作者名称至少需要2个字符'
      return false
    }
    if (!form.description) {
      errors.description = '请输入作品描述'
      return false
    }
    if (form.description.length < 10) {
      errors.description = '描述至少需要10个字符'
      return false
    }
  }

  if (step === 1) {
    if (!form.fileHash) {
      errors.fileHash = '请输入文件哈希值'
      return false
    }
    // 支持两种格式：
    // 1. IPFS CID (Qm开头或ba开头，后面跟base58字符)
    // 2. SHA256哈希 (64位十六进制字符串)
    const ipfsPattern = /^Qm[a-zA-Z0-9]{44,}|^ba[a-zA-Z0-9]{44,}/
    const sha256Pattern = /^[a-fA-F0-9]{64}$/

    if (!ipfsPattern.test(form.fileHash) && !sha256Pattern.test(form.fileHash)) {
      errors.fileHash = '请输入有效的IPFS哈希值（Qm开头）或SHA256哈希值（64位十六进制）'
      return false
    }
  }

  return true
}

const nextStep = () => {
  if (validateStep(currentStep.value)) {
    currentStep.value++
  }
}

const prevStep = () => {
  currentStep.value--
}

const selectFile = () => {
  fileInputRef.value?.click()
}

const handleFileSelect = async (event) => {
  const file = event.target.files?.[0]
  if (file) {
    await uploadFileToServer(file)
  }
}

const handleDrop = async (event) => {
  isDragOver.value = false
  const file = event.dataTransfer.files?.[0]
  if (file) {
    await uploadFileToServer(file)
  }
}

// 上传文件到服务器并获取哈希
const uploadFileToServer = async (file) => {
  // 验证文件大小（100MB限制）
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 100MB')
    return
  }

  // 显示文件信息
  uploadedFile.value = file
  uploading.value = true

  try {
    ElMessage.info({
      message: '正在上传文件，请稍候...',
      duration: 2000
    })

    // 调用上传API
    const data = await uploadFile(file)

    if (data && data.fileHash) {
      // 自动填充文件哈希
      form.fileHash = data.fileHash

      ElMessage.success({
        message: `文件上传成功！存储方式: ${data.storage === 'ipfs' ? 'IPFS' : '本地'}`,
        duration: 3000
      })

      console.log('文件上传成功:', data)
    } else {
      ElMessage.error('文件上传失败，请手动输入哈希值')
    }
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error({
      message: error.message || '文件上传失败，请手动输入哈希值',
      duration: 3000
    })
  } finally {
    uploading.value = false
  }
}

const removeFile = () => {
  if (uploading.value) {
    ElMessage.warning('文件上传中，请稍候...')
    return
  }
  uploadedFile.value = null
  form.fileHash = ''
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

const generateHash = () => {
  // 生成 SHA256 格式的示例哈希（64位十六进制）
  const chars = '0123456789abcdef'
  const hash = Array.from({ length: 64 }, () =>
    chars[Math.floor(Math.random() * chars.length)]
  ).join('')
  form.fileHash = hash
  ElMessage.success('已生成SHA256示例哈希值（64位十六进制）')
}

const handleSubmit = async () => {
  if (!validateStep(2)) return

  try {
    await ElMessageBox.confirm(
      '确认提交版权注册？提交后信息将记录在区块链上，不可修改。',
      '确认提交',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = await registerCopyright(form)
    ElMessage.success({
      message: `版权注册成功！版权ID: ${data.contractId}`,
      duration: 3000
    })

    // 刷新最近注册
    await loadRecentCopyrights()

    setTimeout(() => {
      ElMessageBox.confirm(
        '是否跳转到版权详情页面？',
        '注册成功',
        {
          confirmButtonText: '查看详情',
          cancelButtonText: '继续注册',
          type: 'success'
        }
      ).then(() => {
        router.push(`/copyrights/detail/${data.id}`)
      }).catch(() => {
        // 重置表单
        resetForm()
        currentStep.value = 0
      })
    }, 1000)
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error({
      message: error.message || '注册失败，请重试',
      duration: 3000
    })
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  Object.keys(form).forEach(key => form[key] = '')
  Object.keys(errors).forEach(key => errors[key] = '')
  uploadedFile.value = null
}

const goBack = () => {
  router.push('/copyrights')
}

const loadRecentCopyrights = async () => {
  try {
    const data = await getCopyrightList({ pageNum: 1, pageSize: 5 })
    recentCopyrights.value = data.records || []
    console.log('最近注册加载成功:', recentCopyrights.value)
  } catch (error) {
    console.error('加载最近注册失败:', error)
  }
}

onMounted(() => {
  loadRecentCopyrights()
})
</script>

<style lang="scss" scoped>
.register-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 32px;
}

// 页面头部
.page-header {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f9fafb;
    color: #667eea;
    border-color: #667eea;
  }
}

.header-content {
  flex: 1;

  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 4px 0;
  }

  .page-subtitle {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

// 步骤指示器
.steps-container {
  grid-column: 1 / -1;
  background: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.steps-wrapper {
  display: flex;
  justify-content: space-between;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 24px;
    left: 40px;
    right: 40px;
    height: 2px;
    background: #e5e7eb;
    z-index: 0;
  }
}

.step-item {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  &.active {
    .step-number {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      border-color: transparent;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }

    .step-title {
      color: #667eea;
    }
  }

  &.completed {
    .step-number {
      background: #22c55e;
      color: white;
      border-color: transparent;
    }

    .step-title {
      color: #22c55e;
    }
  }
}

.step-number {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: white;
  border: 2px solid #e5e7eb;
  color: #9ca3af;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.step-info {
  text-align: center;

  .step-title {
    font-size: 14px;
    font-weight: 600;
    color: #6b7280;
    margin-bottom: 4px;
    transition: color 0.3s;
  }

  .step-desc {
    font-size: 12px;
    color: #9ca3af;
  }
}

// 表单容器
.form-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(249, 250, 251, 0.98) 100%);
  backdrop-filter: blur(20px);
  padding: 32px;
  border-radius: 20px;
  border: 1px solid rgba(102, 126, 234, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;

  // 添加顶部光效边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  }

  // 暗色模式适配
  @media (prefers-color-scheme: dark) {
    background: linear-gradient(135deg, rgba(30, 30, 54, 0.98) 0%, rgba(26, 26, 46, 0.98) 100%);
    border-color: rgba(102, 126, 234, 0.3);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  }
}

.step-content {
  .content-header {
    margin-bottom: 32px;
    padding-bottom: 20px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);

    .content-title {
      font-size: 24px;
      font-weight: 700;
      color: var(--text-primary);
      margin: 0 0 8px 0;
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .content-desc {
      font-size: 14px;
      color: #6b7280;
      margin: 0;
    }
  }
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .field-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 600;
    color: #374151;

    svg {
      color: #667eea;
    }

    .required {
      color: #ef4444;
    }
  }

  .field-input {
    width: 100%;
    padding: 12px 16px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    font-size: 14px;
    color: #1f2937;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: #667eea;
      background: white;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
    }

    &::placeholder {
      color: #9ca3af;
    }
  }

  .field-textarea {
    width: 100%;
    padding: 12px 16px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    font-size: 14px;
    color: #1f2937;
    resize: vertical;
    font-family: inherit;
    line-height: 1.6;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: #667eea;
      background: white;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
    }
  }

  .field-counter {
    text-align: right;
    font-size: 12px;
    color: #9ca3af;
  }

  .field-error {
    font-size: 12px;
    color: #ef4444;
  }

  .field-tip {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #6b7280;

    svg {
      color: #f97316;
      flex-shrink: 0;
    }
  }
}

.hash-input-group {
  display: flex;
  gap: 12px;

  .field-input {
    flex: 1;
  }

  .btn-generate {
    padding: 12px 16px;
    background: rgba(102, 126, 234, 0.1);
    border: 1px solid rgba(102, 126, 234, 0.3);
    border-radius: 10px;
    color: #667eea;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
      background: rgba(102, 126, 234, 0.15);
    }
  }
}

// 上传区域
.upload-area {
  padding: 48px;
  background: #f9fafb;
  border: 2px dashed #e5e7eb;
  border-radius: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;

  &:hover, &.drag-over {
    border-color: #667eea;
    background: rgba(102, 126, 234, 0.05);
  }

  .upload-icon {
    color: #d1d5db;
    margin-bottom: 16px;
  }

  .upload-title {
    font-size: 16px;
    font-weight: 600;
    color: #374151;
    margin: 0 0 8px 0;
  }

  .upload-desc {
    font-size: 14px;
    color: #9ca3af;
    margin: 0 0 20px 0;
  }

  .file-input {
    display: none;
  }
}

.btn-outline {
  padding: 10px 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f9fafb;
    color: #667eea;
    border-color: #667eea;
  }
}

// 文件预览
.file-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;

  .file-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
    border-radius: 10px;
  }

  .file-info {
    flex: 1;

    .file-name {
      font-size: 14px;
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 4px;
    }

    .file-size {
      font-size: 12px;
      color: #9ca3af;
    }
  }

  .btn-remove {
    padding: 8px;
    background: rgba(239, 68, 68, 0.1);
    border: none;
    border-radius: 8px;
    color: #ef4444;
    cursor: pointer;
    transition: all 0.2s;

    &:hover:not(:disabled) {
      background: rgba(239, 68, 68, 0.2);
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .upload-status {
    font-size: 12px;
    color: #667eea;
    font-weight: 500;
  }

  .hash-display {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 11px;

    .hash-label {
      color: #9ca3af;
    }

    .hash-value {
      font-family: 'Courier New', monospace;
      color: #22c55e;
      font-weight: 500;
    }
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: #e5e7eb;
  }

  span {
    padding: 0 16px;
    font-size: 13px;
    color: #9ca3af;
  }
}

// 确认信息
.confirm-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.confirm-card {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.05) 0%, rgba(16, 185, 129, 0.03) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(34, 197, 94, 0.15);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(34, 197, 94, 0.08);
  position: relative;
  overflow: hidden;

  // 添加顶部光效边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #22c55e 0%, #16a34a 100%);
  }

  .confirm-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    color: #667eea;

    h3 {
      font-size: 18px;
      font-weight: 600;
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
    align-items: flex-start;
    gap: 12px;

    &.full {
      flex-direction: column;
      gap: 8px;
    }

    .confirm-label {
      min-width: 100px;
      font-size: 14px;
      font-weight: 600;
      color: #6b7280;
    }

    .confirm-value {
      flex: 1;
      font-size: 14px;
      color: #1f2937;
      word-break: break-all;

      &.hash-value {
        font-family: 'Courier New', monospace;
        font-size: 13px;
      }
    }
  }
}

.notice-box {
  display: flex;
  align-items: start;
  gap: 12px;
  padding: 16px;
  background: rgba(249, 115, 22, 0.1);
  border: 1px solid rgba(249, 115, 22, 0.2);
  border-radius: 12px;
  color: #f97316;

  .notice-content {
    flex: 1;
    font-size: 14px;
    line-height: 1.6;

    strong {
      font-weight: 600;
    }
  }
}

// 操作按钮
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.btn {
  padding: 12px 28px;
  font-size: 15px;
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
    background: white;
    color: #6b7280;
    border: 1px solid #e5e7eb;

    &:hover {
      background: #f9fafb;
      color: #667eea;
      border-color: #667eea;
    }
  }

  .spin {
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 侧边信息栏
.info-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;

  // 添加顶部光效边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  }

  .info-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 16px 0;
    position: relative;
    padding-left: 12px;

    // 添加左侧装饰线
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 16px;
      background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
      border-radius: 2px;
    }

    svg {
      color: #667eea;
    }
  }

  .info-list {
    margin: 0;
    padding-left: 20px;
    list-style: none;

    li {
      position: relative;
      margin-bottom: 12px;
      padding-left: 16px;
      font-size: 13px;
      color: #6b7280;
      line-height: 1.6;

      &::before {
        content: '•';
        position: absolute;
        left: 0;
        color: #667eea;
        font-size: 18px;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .recent-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .recent-item {
    padding: 12px 16px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(249, 250, 251, 0.8) 100%);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(102, 126, 234, 0.1);
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    &:hover {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
      border-color: rgba(102, 126, 234, 0.3);
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    }

    .recent-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .recent-time {
      font-size: 12px;
      color: var(--text-tertiary);
    }
  }
}

@media (max-width: 1024px) {
  .register-page {
    grid-template-columns: 1fr;
  }

  .info-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .steps-wrapper {
    &::before {
      display: none;
    }
  }

  .step-item {
    flex-direction: row;
    justify-content: flex-start;
    gap: 12px;
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
