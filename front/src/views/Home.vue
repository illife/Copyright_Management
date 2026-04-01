<template>
  <div class="landing-page">
    <!-- 顶部导航栏 -->
    <header class="top-navbar">
      <div class="nav-container">
        <!-- Logo -->
        <div class="nav-logo" @click="scrollToTop">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="logo-text">版权链</span>
        </div>

        <!-- 导航链接 -->
        <nav class="nav-links">
          <a href="#features" class="nav-link" @click.prevent="scrollToFeatures">核心功能</a>
          <a href="#how-it-works" class="nav-link" @click.prevent="scrollToHowItWorks">工作原理</a>
          <a href="#cases" class="nav-link" @click.prevent="scrollToCases">用户案例</a>
          <a href="#contact" class="nav-link" @click.prevent="scrollToContact">联系我们</a>
        </nav>

        <!-- 右侧按钮 -->
        <div class="nav-actions">
          <button class="btn btn-text" @click="goToLogin">登录</button>
          <button class="btn btn-primary" @click="goToRegister">立即注册</button>
        </div>
      </div>
    </header>

    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <div class="hero-badge">
            <span class="badge-dot"></span>
            <span>基于 FISCO BCOS 联盟链技术</span>
          </div>

          <h1 class="hero-title">
            为您的创意作品<br>
            <span class="gradient-text">提供永久保护</span>
          </h1>

          <p class="hero-description">
            基于区块链技术，为您的原创作品提供不可篡改的版权登记、授权管理和版税分发服务。
            <strong>安全、透明、高效</strong>，让每一个创意都得到应有的保护。
          </p>

          <div class="hero-actions">
            <button class="btn btn-large btn-primary" @click="goToRegister">
              立即注册
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="btn btn-large btn-outline" @click="scrollToFeatures">
              了解更多
            </button>
          </div>

          <!-- 信任指标 -->
          <div class="hero-metrics">
            <div class="metric-item">
              <div class="metric-value">10,000+</div>
              <div class="metric-label">已注册作品</div>
            </div>
            <div class="metric-divider"></div>
            <div class="metric-item">
              <div class="metric-value">¥500万+</div>
              <div class="metric-label">版税分发</div>
            </div>
            <div class="metric-divider"></div>
            <div class="metric-item">
              <div class="metric-value">99.9%</div>
              <div class="metric-label">系统可用性</div>
            </div>
          </div>
        </div>

        <!-- 右侧插图 -->
        <div class="hero-visual">
          <div class="visual-card">
            <div class="visual-icon">©</div>
            <div class="visual-dots">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 核心功能区 -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">核心功能</h2>
          <p class="section-subtitle">全方位的版权保护解决方案，满足您的所有需求</p>
        </div>

        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <div class="feature-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="16" y1="13" x2="8" y2="13"/>
                  <line x1="16" y1="17" x2="8" y2="17"/>
                  <polyline points="10 9 9 9 8 9"/>
                </svg>
              </div>
            </div>
            <h3 class="feature-title">版权登记</h3>
            <p class="feature-description">
              一键上链存储，为您的作品生成不可篡改的区块链存证证书，确立所有权。
            </p>
            <ul class="feature-list">
              <li>哈希值验证</li>
              <li>永久保存</li>
              <li>法律效力</li>
            </ul>
          </div>

          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <div class="feature-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </div>
            </div>
            <h3 class="feature-title">授权管理</h3>
            <p class="feature-description">
              灵活配置授权方案，支持时间、地域、用途等多维度管理，智能合约自动执行。
            </p>
            <ul class="feature-list">
              <li>自定义条款</li>
              <li>自动执行</li>
              <li>实时追踪</li>
            </ul>
          </div>

          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <div class="feature-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="1" x2="12" y2="23"/>
                  <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                </svg>
              </div>
            </div>
            <h3 class="feature-title">版税分发</h3>
            <p class="feature-description">
              透明的版税计算与自动分发机制，确保版权人获得应有收益，每一笔都可追溯。
            </p>
            <ul class="feature-list">
              <li>自动计算</li>
              <li>实时到账</li>
              <li>完整记录</li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- 工作原理 -->
    <section id="how-it-works" class="how-it-works-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">工作原理</h2>
          <p class="section-subtitle">简单三步，完成作品版权保护</p>
        </div>

        <div class="steps-container">
          <div class="step-item">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3 class="step-title">提交作品</h3>
              <p class="step-description">上传您的作品信息，系统自动生成唯一指纹哈希值</p>
            </div>
          </div>

          <div class="step-item">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3 class="step-title">区块链存证</h3>
              <p class="step-description">作品信息上链存储，生成不可篡改的存证证书</p>
            </div>
          </div>

          <div class="step-item">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3 class="step-title">授权管理</h3>
              <p class="step-description">灵活配置授权方案，自动管理版税分发</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 用户案例 -->
    <section id="cases" class="cases-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">用户案例</h2>
          <p class="section-subtitle">看看他们如何使用我们的平台保护创意</p>
        </div>

        <div class="cases-grid">
          <div class="case-card">
            <div class="case-header">
              <div class="case-avatar">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 18V5l12-2v13"/>
                  <circle cx="6" cy="18" r="3"/>
                  <circle cx="18" cy="16" r="3"/>
                </svg>
              </div>
              <div class="case-info">
                <h4 class="case-name">音乐人小王</h4>
                <p class="case-role">独立音乐创作者</p>
              </div>
            </div>
            <p class="case-quote">
              "通过平台成功登记了20首原创歌曲，并通过授权机制获得稳定收益，年收入超过50万元。"
            </p>
            <div class="case-metrics">
              <div class="case-metric">
                <span class="metric-num">20</span>
                <span class="metric-text">首歌曲</span>
              </div>
              <div class="case-metric">
                <span class="metric-num">¥50万</span>
                <span class="metric-text">年收入</span>
              </div>
            </div>
          </div>

          <div class="case-card">
            <div class="case-header">
              <div class="case-avatar">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                  <circle cx="8.5" cy="8.5" r="1.5"/>
                  <polyline points="21 15 16 10 5 21"/>
                </svg>
              </div>
              <div class="case-info">
                <h4 class="case-name">数字艺术平台</h4>
                <p class="case-role">艺术品交易平台</p>
              </div>
            </div>
            <p class="case-quote">
              "集成版权系统后，为平台上万件数字艺术品提供版权确权服务，交易额突破500万元。"
            </p>
            <div class="case-metrics">
              <div class="case-metric">
                <span class="metric-num">10000+</span>
                <span class="metric-text">艺术品</span>
              </div>
              <div class="case-metric">
                <span class="metric-num">¥500万</span>
                <span class="metric-text">交易额</span>
              </div>
            </div>
          </div>

          <div class="case-card">
            <div class="case-header">
              <div class="case-avatar">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
                  <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
                </svg>
              </div>
              <div class="case-info">
                <h4 class="case-name">在线教育机构</h4>
                <p class="case-role">知识付费平台</p>
              </div>
            </div>
            <p class="case-quote">
              "使用平台保护500+精品课程，通过授权机制拓展内容分发渠道，合作方超过100家。"
            </p>
            <div class="case-metrics">
              <div class="case-metric">
                <span class="metric-num">500+</span>
                <span class="metric-text">课程</span>
              </div>
              <div class="case-metric">
                <span class="metric-num">100+</span>
                <span class="metric-text">合作方</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA 区域 -->
    <section class="cta-section">
      <div class="cta-container">
        <h2 class="cta-title">准备好保护您的作品了吗？</h2>
        <p class="cta-description">
          立即注册，开始使用区块链技术保护您的创意作品
        </p>
        <div class="cta-actions">
          <button class="btn btn-large btn-primary" @click="goToRegister">
            免费开始使用
          </button>
          <button class="btn btn-large btn-primary" @click="scrollToContact">
            联系我们
          </button>
        </div>
      </div>
    </section>

    <!-- 底部区域 -->
    <footer id="contact" class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-brand">
            <div class="footer-logo">
              <div class="logo-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="logo-text">版权链</span>
            </div>
            <p class="footer-description">
              基于 FISCO BCOS 联盟链技术，为创作者提供安全、透明、高效的版权保护服务。
            </p>
          </div>

          <div class="footer-links-group">
            <div class="footer-links-section">
              <h4 class="footer-title">产品</h4>
              <ul class="footer-links">
                <li><a href="#features" @click.prevent="scrollToFeatures">核心功能</a></li>
                <li><a href="#how-it-works" @click.prevent="scrollToHowItWorks">工作原理</a></li>
                <li><a href="#cases" @click.prevent="scrollToCases">用户案例</a></li>
                <li><a href="#" @click.prevent="goToLogin">登录系统</a></li>
              </ul>
            </div>

            <div class="footer-links-section">
              <h4 class="footer-title">资源</h4>
              <ul class="footer-links">
                <li><a href="#">开发文档</a></li>
                <li><a href="#">API 接口</a></li>
                <li><a href="#">帮助中心</a></li>
                <li><a href="#" @click.prevent="goToRegister">注册申请</a></li>
              </ul>
            </div>

            <div class="footer-links-section">
              <h4 class="footer-title">联系我们</h4>
              <ul class="footer-contact">
                <li>
                  <svg class="contact-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                  <span>contact@copyright.com</span>
                </li>
                <li>
                  <svg class="contact-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                  </svg>
                  <span>400-123-4567</span>
                </li>
                <li>
                  <svg class="contact-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                  <span>中国·深圳</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div class="footer-bottom">
          <p>&copy; 2024 联盟链版权管理系统. All rights reserved.</p>
          <p>Powered by FISCO BCOS</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/registration')
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const scrollToFeatures = () => {
  document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToHowItWorks = () => {
  document.getElementById('how-it-works')?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToCases = () => {
  document.getElementById('cases')?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToContact = () => {
  document.getElementById('contact')?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style scoped>
/* ========== 全局样式 ========== */
.landing-page {
  min-height: 100vh;
  background: var(--bg-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

/* ========== 顶部导航栏 ========== */
.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: var(--bg-backdrop);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-primary);
}

.nav-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.nav-links {
  display: flex;
  gap: 40px;
}

.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
  cursor: pointer;
}

.nav-link:hover {
  color: var(--color-primary-600);
}

.nav-actions {
  display: flex;
  gap: 12px;
}

/* ========== 按钮样式 ========== */
.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.btn-text {
  background: transparent;
  color: var(--text-secondary);
}

.btn-text:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.btn-primary {
  background: var(--gradient-primary);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.btn-outline {
  background: var(--bg-primary);
  color: var(--color-primary-600);
  border: 2px solid var(--color-primary-600);
}

.btn-outline:hover {
  background: var(--color-primary-600);
  color: white;
}

.btn-outline-white {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-outline-white:hover {
  background: white;
  color: #667eea;
}

.btn-large {
  padding: 14px 28px;
  font-size: 16px;
}

/* ========== 英雄区域 ========== */
.hero-section {
  padding: 140px 24px 80px;
  background: var(--bg-primary);
}

.hero-container {
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.hero-content {
  max-width: 600px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-primary);
  border-radius: 100px;
  font-size: 14px;
  color: var(--color-primary-600);
  margin-bottom: 32px;
  box-shadow: var(--shadow-sm);
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: var(--color-primary-600);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  line-height: 1.2;
  color: var(--text-primary);
  margin-bottom: 24px;
}

.gradient-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 18px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin-bottom: 40px;
}

.hero-description strong {
  color: var(--color-primary-600);
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 60px;
}

.hero-metrics {
  display: flex;
  gap: 40px;
  padding-top: 40px;
  border-top: 1px solid var(--border-primary);
}

.metric-item {
  text-align: left;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.metric-divider {
  width: 1px;
  background: var(--border-primary);
}

.hero-visual {
  display: flex;
  justify-content: center;
  align-items: center;
}

.visual-card {
  width: 400px;
  height: 400px;
  background: var(--bg-card);
  border-radius: 24px;
  box-shadow: var(--shadow-xl);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.visual-icon {
  font-size: 120px;
  font-weight: 700;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.visual-dots {
  display: flex;
  gap: 12px;
  margin-top: 40px;
}

.visual-dots span {
  width: 12px;
  height: 12px;
  background: var(--color-primary-600);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.visual-dots span:nth-child(1) { animation-delay: -0.32s; }
.visual-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* ========== 核心功能区 ========== */
.features-section {
  padding: 100px 24px;
  background: var(--bg-primary);
}

.section-container {
  max-width: 1280px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 42px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 18px;
  color: var(--text-secondary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.feature-card {
  padding: 40px 32px;
  background: var(--bg-card);
  border: 2px solid var(--border-secondary);
  border-radius: 16px;
  transition: all 0.3s;
}

.feature-card:hover {
  border-color: var(--color-primary-600);
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.15);
}

.feature-icon-wrapper {
  width: 64px;
  height: 64px;
  background: var(--gradient-primary);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.feature-icon {
  color: white;
}

.feature-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.feature-description {
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  padding-left: 24px;
  position: relative;
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--color-primary-600);
  font-weight: 700;
}

/* ========== 工作原理 ========== */
.how-it-works-section {
  padding: 100px 24px;
  background: var(--bg-primary);
}

.steps-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  position: relative;
}

.step-item {
  text-align: center;
  position: relative;
}

.step-number {
  width: 64px;
  height: 64px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 800;
  color: white;
  margin: 0 auto 24px;
}

.step-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.step-description {
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-secondary);
}

/* ========== 用户案例 ========== */
.cases-section {
  padding: 100px 24px;
  background: var(--bg-primary);
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.case-card {
  padding: 32px;
  background: var(--bg-card);
  border: 2px solid var(--border-secondary);
  border-radius: 16px;
  transition: all 0.3s;
}

.case-card:hover {
  border-color: var(--color-primary-600);
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.15);
}

.case-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.case-avatar {
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.case-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.case-role {
  font-size: 14px;
  color: var(--text-secondary);
}

.case-quote {
  font-size: 15px;
  line-height: 1.6;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.case-metrics {
  display: flex;
  gap: 24px;
}

.case-metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-num {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary-600);
}

.metric-text {
  font-size: 12px;
  color: var(--text-secondary);
}

/* ========== CTA 区域 ========== */
.cta-section {
  padding: 100px 24px;
  background: var(--gradient-primary);
}

.cta-container {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.cta-title {
  font-size: 42px;
  font-weight: 800;
  color: white;
  margin-bottom: 16px;
}

.cta-description {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
}

.cta-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.cta-actions .btn {
  min-width: 180px;
  width: 180px;
  justify-content: center;
}

/* ========== 底部区域 ========== */
.footer {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  padding: 80px 24px 32px;
}

.footer-container {
  max-width: 1280px;
  margin: 0 auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 60px;
  margin-bottom: 60px;
}

.footer-brand {
  max-width: 300px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.footer-description {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-secondary);
}

.footer-links-group {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.footer-title {
  font-size: 16px;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
  cursor: pointer;
}

.footer-links a:hover {
  color: var(--text-primary);
}

.footer-contact {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-contact li {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
}

.footer-contact li span {
  color: var(--text-secondary);
}

.contact-icon {
  flex-shrink: 0;
  stroke: var(--color-primary-600) !important;
  width: 16px !important;
  height: 16px !important;
}

.footer-bottom {
  padding-top: 32px;
  border-top: 1px solid var(--border-primary);
  text-align: center;
}

.footer-bottom p {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

/* ========== 响应式设计 ========== */
@media (max-width: 1024px) {
  .hero-container {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .hero-content {
    max-width: 100%;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-metrics {
    justify-content: center;
  }

  .visual-card {
    display: none;
  }

  .features-grid,
  .cases-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .steps-container {
    grid-template-columns: 1fr;
  }

  .footer-content {
    grid-template-columns: 1fr;
  }

  .footer-links-group {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-description {
    font-size: 16px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .hero-metrics {
    flex-direction: column;
    gap: 20px;
  }

  .metric-divider {
    display: none;
  }

  .features-grid,
  .cases-grid {
    grid-template-columns: 1fr;
  }

  .footer-links-group {
    grid-template-columns: 1fr;
  }

  .cta-actions {
    flex-direction: column;
  }

  .section-title {
    font-size: 32px;
  }

  .footer {
    background: var(--bg-secondary);
  }
}
</style>
