<template>
  <div class="copyright-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          <path d="M12.5 5L7.5 10L12.5 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回列表
      </button>
      <div class="header-content">
        <h1 class="page-title">{{ copyright.title }}</h1>
        <div class="meta-tags">
          <span class="tag tag-id">ID: {{ copyright.contractId }}</span>
          <span class="tag tag-confirmed">已确认</span>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="content-grid">
      <!-- 左侧：版权基本信息 -->
      <div class="main-column">
        <!-- 版权信息卡片 -->
        <div class="card">
          <div class="card-header">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M14 2H6C4.9 2 4 2.9 4 4V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 13H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 17H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 9H8H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h2>版权信息</h2>
          </div>
          <div class="card-body">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">作品标题</span>
                <span class="info-value">{{ copyright.title }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">作者/创作者</span>
                <span class="info-value">{{ copyright.author }}</span>
              </div>
              <div class="info-item full">
                <span class="info-label">版权所有者</span>
                <div class="address-box">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM11.5 7.5L7.5 11.5L5.5 9.5L4.5 10.5L7.5 13.5L12.5 8.5L11.5 7.5Z" fill="currentColor"/>
                  </svg>
                  <span class="address-text">{{ copyright.ownerAddress }}</span>
                  <button class="btn-copy" @click="copyAddress" title="复制地址">
                    <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                      <path d="M4 1.5H3.5C2.12 1.5 1 2.62 1 4V12C1 13.38 2.12 14.5 3.5 14.5H4M12 1.5H12.5C13.88 1.5 15 2.62 15 4V12C15 13.38 13.88 14.5 12.5 14.5H12" stroke="currentColor" stroke-width="1"/>
                      <rect x="4" y="5.5" width="8" height="7" rx="0.5" stroke="currentColor" stroke-width="1"/>
                    </svg>
                  </button>
                </div>
              </div>
              <div class="info-item full">
                <span class="info-label">文件哈希</span>
                <div class="hash-box">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM8 4C7.45 4 7 4.45 7 5C7 5.55 7.45 6 8 6C8.55 6 9 5.55 9 5C9 4.45 8.55 4 8 4ZM11 7H5V8H11V7ZM8 10C6.34 10 5 8.69 5 11H3C3 7.69 5.69 5 9 5C11.31 5 13.5 6.34 14 8H12C11.5 7.17 9.83 6.5 8 6.5C6.17 6.5 4.5 7.17 4 8H2C2.5 6.34 4.17 5 7 5H9C9 5 11 7.69 11 11H9Z" fill="currentColor"/>
                  </svg>
                  <span class="hash-text">{{ copyright.fileHash }}</span>
                  <button class="btn-copy" @click="copyHash" title="复制哈希">
                    <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                      <path d="M4 1.5H3.5C2.12 1.5 1 2.62 1 4V12C1 13.38 2.12 14.5 3.5 14.5H4M12 1.5H12.5C13.88 1.5 15 2.62 15 4V12C15 13.38 13.88 14.5 12.5 14.5H12" stroke="currentColor" stroke-width="1"/>
                      <rect x="4" y="5.5" width="8" height="7" rx="0.5" stroke="currentColor" stroke-width="1"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 文件预览卡片 -->
        <div class="card" v-if="copyright.storageType === 'ipfs'">
          <div class="card-header">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M21 15V19C21 20.1 20.1 21 19 21H5C3.9 21 3 20.1 3 19V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M17 8L12 3L7 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 3V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h2>文件存储</h2>
          </div>
          <div class="card-body">
            <div class="storage-info">
              <div class="storage-type">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path d="M10 1C5.58 1 2 4.58 2 9C2 13.42 5.58 17 10 17C14.42 17 18 13.42 18 9C18 4.58 14.42 1 10 1ZM10 15C6.69 15 4 12.31 4 9C4 5.69 6.69 3 10 3C13.31 3 16 5.69 16 9C16 12.31 13.31 15 10 15Z" fill="currentColor"/>
                </svg>
                <span>存储于 IPFS 分布式网络</span>
              </div>
              <a :href="copyright.ipfsGatewayUrl" target="_blank" class="btn-view-ipfs">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM7 11.5L3.5 8L7 4.5L7.5 5L4.5 8L7.5 11L7 11.5ZM11 4.5L14.5 8L11 11.5L10.5 11L13.5 8L10.5 5L11 4.5Z" fill="currentColor"/>
                </svg>
                在 IPFS 网关中查看
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：区块链交易信息 -->
      <div class="side-column">
        <!-- 区块链信息卡片 -->
        <div class="card blockchain-card">
          <div class="card-header">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h2>区块链交易信息</h2>
            <span class="card-badge">已上链</span>
          </div>
          <div class="card-body">
            <!-- 交易哈希 -->
            <div class="blockchain-item">
              <div class="item-label">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM8.5 5H7.5V9.25L4.5 11.15L5 12L10.5 9.25V5Z" fill="currentColor"/>
                </svg>
                交易哈希
              </div>
              <div class="item-value hash">
                {{ formatHash(copyright.transactionHash) }}
              </div>
              <a v-if="copyright.transactionBrowserUrl" :href="copyright.transactionBrowserUrl" target="_blank" class="item-link">
                在浏览器中查看
              </a>
            </div>

            <!-- 区块信息 -->
            <div class="blockchain-item">
              <div class="item-label">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15Z" fill="currentColor"/>
                </svg>
                区块高度
              </div>
              <div class="item-value number">#{{ copyright.blockNumber?.toLocaleString() }}</div>
            </div>

            <!-- 合约地址 -->
            <div class="blockchain-item">
              <div class="item-label">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM8 7H9V11H8V7ZM7 7H6V11H7V7Z" fill="currentColor"/>
                </svg>
                合约地址
              </div>
              <div class="item-value address">{{ formatAddress(copyright.contractAddress) }}</div>
            </div>

            <!-- Gas信息 -->
            <div class="blockchain-item">
              <div class="item-label">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM8 4C6.34 4 5 5.34 5 7C5 8.66 6.34 10 8 10C9.66 10 11 8.66 11 7C11 5.34 9.66 4 8 4ZM8 9C7.45 9 7 8.55 7 8C7 7.45 7.45 7 8 7C8.55 7 9 7.45 9 8C9 8.55 8.55 9 8 9Z" fill="currentColor"/>
                </svg>
                Gas使用
              </div>
              <div class="item-value number">{{ copyright.gasUsed?.toLocaleString() || '-' }}</div>
            </div>

            <!-- 注册时间 -->
            <div class="blockchain-item">
              <div class="item-label">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C12.42 16 16 12.42 16 8C16 3.58 12.42 0 8 0ZM8 15C4.69 15 2 12.31 2 8C2 3.69 4.69 1 8 1C11.31 1 14 3.69 14 8C14 12.31 11.31 15 8 15ZM11.5 7.5L7.5 11.5L5.5 9.5L4.5 10.5L7.5 13.5L12.5 8.5L11.5 7.5Z" fill="currentColor"/>
                </svg>
                注册时间
              </div>
              <div class="item-value time">{{ formatTimestamp(copyright.registerTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 确认徽章 -->
        <div class="confirmation-card">
          <div class="confirmation-icon">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <circle cx="24" cy="24" r="22" stroke="currentColor" stroke-width="2"/>
              <path d="M14 24L20 30L34 16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>已在区块链确认</h3>
          <p>此版权已永久记录在FISCO BCOS区块链上，具有法律效力</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCopyrightDetail } from '@/api/copyright'

const router = useRouter()
const copyright = ref({
  title: '',
  author: '',
  fileHash: '',
  ownerAddress: '',
  contractId: '',
  transactionHash: '',
  blockNumber: null,
  contractAddress: '',
  gasUsed: null,
  registerTime: null,
  storageType: '',
  ipfsGatewayUrl: '',
  transactionBrowserUrl: ''
})

const loadDetail = async () => {
  try {
    const id = router.currentRoute.value.params.id
    const res = await getCopyrightDetail(id)
    copyright.value = res.data
  } catch (error) {
    console.error('加载版权详情失败:', error)
    ElMessage.error('加载失败')
  }
}

const formatAddress = (address) => {
  if (!address) return '-'
  return `${address.substring(0, 10)}...${address.substring(address.length - 8)}`
}

const formatHash = (hash) => {
  if (!hash) return '-'
  return `${hash.substring(0, 10)}...${hash.substring(hash.length - 8)}`
}

const formatTimestamp = (timestamp) => {
  if (!timestamp) return '-'
  return new Date(timestamp * 1000).toLocaleString('zh-CN')
}

const copyAddress = () => {
  navigator.clipboard.writeText(copyright.value.ownerAddress)
  ElMessage.success('地址已复制')
}

const copyHash = () => {
  navigator.clipboard.writeText(copyright.value.fileHash)
  ElMessage.success('哈希已复制')
}

const goBack = () => {
  router.push('/copyrights')
}

onMounted(() => {
  loadDetail()
})
</script>

<style lang="scss" scoped>
.copyright-detail-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.2s;

  &:hover {
    background: #f9fafb;
    color: #667eea;
    border-color: #667eea;
  }
}

.header-content {
  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 16px 0;
  }

  .meta-tags {
    display: flex;
    gap: 12px;
  }

  .tag {
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;

    &.tag-id {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;
    }

    &.tag-confirmed {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
    }
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  margin-bottom: 24px;
  overflow: hidden;

  .card-header {
    padding: 24px;
    border-bottom: 1px solid #f3f4f6;
    display: flex;
    align-items: center;
    gap: 12px;

    svg {
      color: #667eea;
    }

    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
      margin: 0;
      flex: 1;
    }

    .card-badge {
      padding: 4px 12px;
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
      font-size: 12px;
      font-weight: 600;
      border-radius: 20px;
    }
  }

  .card-body {
    padding: 24px;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 8px;

    &.full {
      grid-column: 1 / -1;
    }

    .info-label {
      font-size: 13px;
      color: #6b7280;
      font-weight: 500;
    }

    .info-value {
      font-size: 15px;
      color: #1f2937;
      font-weight: 500;
    }
  }
}

.address-box,
.hash-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;

  svg {
    color: #667eea;
    flex-shrink: 0;
  }

  .address-text,
  .hash-text {
    flex: 1;
    font-family: 'Courier New', monospace;
    font-size: 13px;
    color: #1f2937;
    word-break: break-all;
  }

  .btn-copy {
    padding: 6px;
    background: transparent;
    border: none;
    color: #9ca3af;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: #667eea;
    }
  }
}

.blockchain-card {
  .card-body {
    padding: 20px;
  }

  .blockchain-item {
    padding: 16px 0;
    border-bottom: 1px solid #f3f4f6;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #6b7280;
      margin-bottom: 8px;
      font-weight: 500;

      svg {
        color: #667eea;
      }
    }

    .item-value {
      font-size: 14px;
      color: #1f2937;
      font-weight: 500;
      font-family: 'Courier New', monospace;

      &.hash,
      &.address {
        word-break: break-all;
        line-height: 1.5;
      }

      &.number {
        font-size: 16px;
        color: #667eea;
      }

      &.time {
        font-family: inherit;
      }
    }

    .item-link {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      margin-top: 8px;
      color: #667eea;
      font-size: 12px;
      font-weight: 500;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.storage-info {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .storage-type {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: rgba(34, 197, 94, 0.1);
    border-radius: 10px;
    color: #22c55e;
    font-size: 14px;
    font-weight: 500;
  }

  .btn-view-ipfs {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    background: #667eea;
    color: white;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 600;
    text-decoration: none;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #5568d3;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }
  }
}

.confirmation-card {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.05), rgba(16, 185, 129, 0.05));
  border: 1px solid rgba(34, 197, 94, 0.2);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;

  .confirmation-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 64px;
    height: 64px;
    background: rgba(34, 197, 94, 0.1);
  border-radius: 50%;
    color: #22c55e;
    margin: 0 auto 16px;
  }

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
    line-height: 1.6;
  }
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
