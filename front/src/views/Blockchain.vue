<template>
  <div class="blockchain-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">系统信息</h1>
        <p class="page-subtitle">FISCO BCOS联盟链版权管理系统</p>
      </div>
    </div>

    <!-- 系统介绍卡片 -->
    <div class="intro-section">
      <div class="intro-card">
        <div class="intro-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24 4L4 14V24C4 34 12.6 42.8 24 46C35.4 42.8 44 34 44 24V14L24 4Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 24L22 30L32 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="intro-content">
          <h3 class="intro-title">基于区块链的版权保护</h3>
          <p class="intro-description">利用FISCO BCOS联盟链技术，为数字作品提供不可篡改、可追溯的版权存证服务</p>
        </div>
      </div>
    </div>

    <!-- 核心功能 -->
    <div class="features-section">
      <div class="section-header">
        <h2 class="section-title">核心功能</h2>
        <p class="section-subtitle">系统提供的主要功能模块</p>
      </div>

      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon feature-purple">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6C4.9 2 4 2.9 4 4V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 13H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 17H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="feature-title">版权注册</h3>
          <p class="feature-description">将原创作品信息上链存证，获得不可篡改的区块链证书</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon feature-blue">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 1L3 5V11C3 16.55 6.84 21.74 12 23C17.16 21.74 21 16.55 21 11V5L12 1Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="feature-title">授权管理</h3>
          <p class="feature-description">支持普通授权和独家授权，灵活控制作品使用权限</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon feature-green">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
              <path d="M12 6V12L16 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="feature-title">版税管理</h3>
          <p class="feature-description">记录版税支付信息，为版权交易提供透明可信的账本</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon feature-orange">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M3 9H21" stroke="currentColor" stroke-width="2"/>
              <path d="M9 21V9" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h3 class="feature-title">交易查询</h3>
          <p class="feature-description">查询所有区块链交易记录，完整追溯操作历史</p>
        </div>
      </div>
    </div>

    <!-- 业务流程 -->
    <div class="process-section">
      <div class="section-header">
        <h2 class="section-title">业务流程</h2>
        <p class="section-subtitle">了解系统的工作流程</p>
      </div>

      <div class="process-container">
        <!-- 版权注册流程 -->
        <div class="process-card">
          <div class="process-header">
            <div class="process-badge process-purple">版权注册</div>
            <h3>如何注册版权</h3>
          </div>
          <div class="process-steps">
            <div class="process-step" v-for="(step, index) in copyrightSteps" :key="index">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-desc">{{ step.description }}</div>
              </div>
            </div>
          </div>
          <div class="process-actions">
            <button class="btn btn-primary" @click="goToRegister">立即注册</button>
          </div>
        </div>

        <!-- 授权管理流程 -->
        <div class="process-card">
          <div class="process-header">
            <div class="process-badge process-blue">授权管理</div>
            <h3>如何申请授权</h3>
          </div>
          <div class="process-steps">
            <div class="process-step" v-for="(step, index) in licenseSteps" :key="index">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-desc">{{ step.description }}</div>
              </div>
            </div>
          </div>
          <div class="process-actions">
            <button class="btn btn-secondary" @click="goToLicense">浏览授权</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 技术架构 -->
    <div class="tech-section">
      <div class="section-header">
        <h2 class="section-title">技术架构</h2>
        <p class="section-subtitle">系统采用的技术栈</p>
      </div>

      <div class="tech-grid">
        <div class="tech-item">
          <div class="tech-label">区块链平台</div>
          <div class="tech-value">FISCO BCOS 2.9.1</div>
        </div>
        <div class="tech-item">
          <div class="tech-label">智能合约</div>
          <div class="tech-value">Solidity</div>
        </div>
        <div class="tech-item">
          <div class="tech-label">后端框架</div>
          <div class="tech-value">Spring Boot 2.5.6</div>
        </div>
        <div class="tech-item">
          <div class="tech-label">前端框架</div>
          <div class="tech-value">Vue 3 + Element Plus</div>
        </div>
        <div class="tech-item">
          <div class="tech-label">数据库</div>
          <div class="tech-value">MySQL 8.0+</div>
        </div>
        <div class="tech-item">
          <div class="tech-label">存储系统</div>
          <div class="tech-value">IPFS</div>
        </div>
      </div>
    </div>

    <!-- 帮助链接 -->
    <div class="help-section">
      <div class="section-header">
        <h2 class="section-title">帮助与支持</h2>
        <p class="section-subtitle">快速链接和文档</p>
      </div>

      <div class="help-grid">
        <a href="https://fisco-bcos-documentation.readthedocs.io/" target="_blank" class="help-link">
          <div class="help-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="help-content">
            <div class="help-title">FISCO BCOS 文档</div>
            <div class="help-desc">查看区块链平台官方文档</div>
          </div>
          <svg class="help-arrow" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </a>

        <a href="https://docs.soliditylang.org/" target="_blank" class="help-link">
          <div class="help-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6C4.9 2 4 2.9 4 4V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 13H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 17H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="help-content">
            <div class="help-title">Solidity 文档</div>
            <div class="help-desc">智能合约开发语言文档</div>
          </div>
          <svg class="help-arrow" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </a>

        <a href="https://ipfs.io/" target="_blank" class="help-link">
          <div class="help-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              <path d="M12 1V4M12 20V23M1 12H4M20 12H23M4.93 4.93L7.05 7.05M16.95 16.95L19.07 19.07M4.93 19.07L7.05 16.95M16.95 7.05L19.07 4.93" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="help-content">
            <div class="help-title">IPFS 官网</div>
            <div class="help-desc">去中心化存储系统</div>
          </div>
          <svg class="help-arrow" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </a>

        <div class="help-link" @click="goToSettings">
          <div class="help-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              <path d="M19.4 15C19.1392 15.6919 18.9204 16.3989 18.7454 17.1176C18.6169 17.6431 18.2893 18.1056 17.8293 18.4045C17.3692 18.7034 16.8144 18.8158 16.2767 18.7184L15.6346 18.6029C14.9404 18.4806 14.2274 18.5695 13.5764 18.8595C12.9253 19.1494 12.3635 19.6276 11.9713 20.2312L11.5998 20.8034C11.2887 21.2796 10.7929 21.6032 10.2367 21.6932C9.68052 21.7832 9.11354 21.6315 8.67156 21.2742L8.15756 20.8603C7.58534 20.3975 7.16231 19.7738 6.94557 19.0695C6.72882 18.3653 6.72921 17.6122 6.94669 16.9081L7.13295 16.3087C7.34224 15.6293 7.34224 14.9018 7.13295 14.2224L6.94669 13.623C6.72921 12.9189 6.72882 12.1658 6.94557 11.4615C7.16231 10.7572 7.58534 10.1335 8.15756 9.67077L8.67156 9.25684C9.11354 8.89951 9.68052 8.74782 10.2367 8.83782C10.7929 8.92783 11.2887 9.25143 11.5998 9.72757L11.9713 10.2998C12.3635 10.9034 12.9253 11.3816 13.5764 11.6715C14.2274 11.9615 14.9404 12.0504 15.6346 11.9281L16.2767 11.8126C16.8144 11.7152 17.3692 11.8276 17.8293 12.1265C18.2893 12.4254 18.6169 12.8879 18.7454 13.4134C18.9204 14.1321 19.1392 14.8391 19.4 15.5401M4.60003 15C4.86082 14.299 5.07962 13.592 5.25462 12.8733C5.3831 12.3478 5.71072 11.8853 6.17075 11.5864C6.63078 11.2875 7.1856 11.1751 7.72329 11.2725L8.36542 11.388C9.05958 11.5103 9.77262 11.4214 10.4236 11.1315C11.0747 10.8415 11.6365 10.3634 12.0287 9.75976L12.4002 9.18758C12.7113 8.71143 13.2071 8.38783 13.7633 8.29783C14.3195 8.20782 14.8865 8.35951 15.3284 8.71684L15.8424 9.13077C16.4147 9.59352 16.8377 10.2172 17.0544 10.9215C17.2712 11.6257 17.2708 12.3788 17.0533 13.0829L16.8671 13.6823C16.6578 14.3617 16.6578 15.0892 16.8671 15.7686L17.0533 16.368C17.2708 17.0721 17.2712 17.8252 17.0544 18.5295C16.8377 19.2338 16.4147 19.8575 15.8424 20.3202L15.3284 20.7342C14.8865 21.0915 14.3195 21.2432 13.7633 21.1532C13.2071 21.0632 12.7113 20.7396 12.4002 20.2634L12.0287 19.6912C11.6365 19.0876 11.0747 18.6095 10.4236 18.3195C9.77262 18.0296 9.05958 17.9407 8.36542 18.063L7.72329 18.1785C7.1856 18.2759 6.63078 18.1635 6.17075 17.8646C5.71072 17.5657 5.3831 17.1032 5.25462 16.5777C5.07962 15.859 4.86082 15.152 4.60003 14.451" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="help-content">
            <div class="help-title">账户设置</div>
            <div class="help-desc">管理您的个人信息和偏好</div>
          </div>
          <svg class="help-arrow" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const copyrightSteps = [
  {
    title: '准备作品',
    description: '准备好您的原创作品文件和相关信息'
  },
  {
    title: '填写信息',
    description: '填写作品标题、作者、描述等基本信息'
  },
  {
    title: '上传文件',
    description: '上传作品文件到IPFS分布式存储系统'
  },
  {
    title: '区块链存证',
    description: '系统自动将版权信息上链，生成不可篡改的证书'
  }
]

const licenseSteps = [
  {
    title: '浏览版权',
    description: '在版权列表中查找您想要授权的作品'
  },
  {
    title: '提交申请',
    description: '选择授权类型（普通/独家）和使用期限'
  },
  {
    title: '等待审批',
    description: '版权拥有者审核您的授权申请'
  },
  {
    title: '激活授权',
    description: '审批通过后激活授权协议开始使用'
  }
]

const goToRegister = () => {
  router.push('/copyrights/register')
}

const goToLicense = () => {
  router.push('/licenses')
}

const goToSettings = () => {
  router.push('/settings')
}
</script>

<style lang="scss" scoped>
.blockchain-page {
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

// 介绍部分
.intro-section {
  margin-bottom: 40px;
}

.intro-card {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 16px;
}

.intro-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-600);
  flex-shrink: 0;
}

.intro-content {
  flex: 1;

  .intro-title {
    font-size: 22px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  .intro-description {
    font-size: 15px;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.6;
  }
}

// 通用section样式
.section-header {
  margin-bottom: 28px;

  .section-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  .section-subtitle {
    font-size: 14px;
    color: var(--text-secondary);
    margin: 0;
  }
}

// 功能特性
.features-section {
  margin-bottom: 48px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.feature-card {
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  transition: all 0.2s;

  &:hover {
    border-color: var(--color-primary-600);
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(102, 126, 234, 0.1);
  }

  .feature-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;

    &.feature-purple {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;
    }

    &.feature-blue {
      background: rgba(59, 130, 246, 0.1);
      color: #3b82f6;
    }

    &.feature-green {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
    }

    &.feature-orange {
      background: rgba(249, 115, 22, 0.1);
      color: #f97316;
    }
  }

  .feature-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 8px 0;
  }

  .feature-description {
    font-size: 13px;
    color: var(--text-secondary);
    line-height: 1.5;
    margin: 0;
  }
}

// 业务流程
.process-section {
  margin-bottom: 48px;
}

.process-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.process-card {
  padding: 28px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 16px;
}

.process-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-tertiary);

  .process-badge {
    padding: 6px 12px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
    white-space: nowrap;

    &.process-purple {
      background: rgba(102, 126, 234, 0.1);
      color: #667eea;
    }

    &.process-blue {
      background: rgba(59, 130, 246, 0.1);
      color: #3b82f6;
    }
  }

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.process-steps {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.process-step {
  display: flex;
  align-items: flex-start;
  gap: 16px;

  .step-number {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: var(--gradient-primary);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .step-content {
    flex: 1;
    padding-top: 2px;

    .step-title {
      font-size: 15px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .step-desc {
      font-size: 13px;
      color: var(--text-secondary);
      line-height: 1.5;
    }
  }
}

.process-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid var(--border-tertiary);
}

.btn {
  flex: 1;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;

  &.btn-primary {
    background: var(--gradient-primary);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
  }

  &.btn-secondary {
    background: var(--bg-card);
    color: var(--text-secondary);
    border: 1px solid var(--border-primary);

    &:hover {
      border-color: var(--color-primary-600);
      color: var(--color-primary-600);
    }
  }
}

// 技术架构
.tech-section {
  margin-bottom: 48px;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.tech-item {
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  .tech-label {
    font-size: 13px;
    color: var(--text-secondary);
  }

  .tech-value {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
  }
}

// 帮助支持
.help-section {
  margin-bottom: 48px;
}

.help-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.help-link {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;

  &:hover {
    border-color: var(--color-primary-600);
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(102, 126, 234, 0.1);

    .help-arrow {
      color: var(--color-primary-600);
      transform: translateX(4px);
    }
  }

  .help-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .help-content {
    flex: 1;

    .help-title {
      font-size: 15px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 4px;
    }

    .help-desc {
      font-size: 13px;
      color: var(--text-secondary);
    }
  }

  .help-arrow {
    color: var(--text-tertiary);
    transition: all 0.2s;
    flex-shrink: 0;
  }
}

// 响应式
@media (max-width: 768px) {
  .intro-card {
    flex-direction: column;
    text-align: center;
  }

  .process-container {
    grid-template-columns: 1fr;
  }
}
</style>
