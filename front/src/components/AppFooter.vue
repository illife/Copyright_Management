<template>
  <footer class="app-footer">
    <!-- 主要内容区 -->
    <div class="footer-content">
      <div class="footer-container">
        <!-- 左侧：品牌信息 -->
        <div class="footer-brand">
          <div class="brand-logo">
            <div class="logo-icon">
              <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="32" height="32" rx="6" fill="url(#logo-gradient)"/>
                <path d="M16 8L22 14L16 20L10 14L16 8Z" fill="white" opacity="0.9"/>
                <path d="M16 12L20 16L16 20L12 16L16 12Z" fill="white"/>
                <defs>
                  <linearGradient id="logo-gradient" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#667eea"/>
                    <stop offset="1" stop-color="#764ba2"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <span class="brand-name">联盟链版权管理</span>
          </div>
          <p class="brand-description">
            基于FISCO BCOS联盟链技术的版权保护平台，为创作者提供安全、透明、不可篡改的版权存证服务。
          </p>
          <!-- 社交媒体链接 -->
          <div class="social-links">
            <a href="#" class="social-link" title="微信">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 0C3.58 0 0 3.58 0 8C0 12.42 3.58 16 8 16C8.67 16 9.31 15.92 9.91 15.77L12 18L12.86 15.22C14.64 14.27 16 12.32 16 10C16 4.48 12.42 1 8 1C3.58 1 0 4.48 0 10C0 12.32 1.36 14.27 3.14 15.22L4 18L6.09 15.77C6.69 15.92 7.33 16 8 16Z" fill="currentColor"/>
              </svg>
            </a>
            <a href="#" class="social-link" title="微博">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M8 0C3.58 0 0 2.69 0 6C0 7.89 1.08 9.55 2.78 10.65C2.78 10.65 2.28 12.25 2 13C2 13 3.89 12.05 5.05 11.38C5.97 11.58 6.95 11.72 8 11.72C12.42 11.72 16 9.03 16 5.72C16 2.41 12.42 0 8 0ZM5.5 4.5C6.05 4.5 6.5 4.95 6.5 5.5C6.5 6.05 6.05 6.5 5.5 6.5C4.95 6.5 4.5 6.05 4.5 5.5C4.5 4.95 4.95 4.5 5.5 4.5ZM10.5 4.5C11.05 4.5 11.5 4.95 11.5 5.5C11.5 6.05 11.05 6.5 10.5 6.5C9.95 6.5 9.5 6.05 9.5 5.5C9.5 4.95 9.95 4.5 10.5 4.5Z" fill="currentColor"/>
              </svg>
            </a>
            <a href="#" class="social-link" title="GitHub">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M10 0C4.48 0 0 4.48 0 10C0 14.42 2.87 18.17 6.84 19.4C7.34 19.49 7.5 19.17 7.5 18.89V17.26C5 17.76 4.39 16.11 4.39 16.11C3.91 14.88 3.24 14.56 3.24 14.56C2.29 14.04 3.31 14.05 3.31 14.05C4.36 14.12 4.89 15.13 4.89 15.13C5.82 16.73 7.34 16.25 8.01 15.96C8.11 15.34 8.39 14.92 8.7 14.67C6.34 14.42 3.86 13.48 3.86 9.61C3.86 8.5 4.26 7.59 4.92 6.89C4.81 6.63 4.46 5.59 5.03 4.2C5.03 4.2 5.89 3.93 7.5 5.23C8.29 5.01 9.14 4.9 10 4.9C10.86 4.9 11.71 5.01 12.5 5.23C14.11 3.93 14.97 4.2 14.97 4.2C15.54 5.59 15.19 6.63 15.08 6.89C15.74 7.59 16.14 8.5 16.14 9.61C16.14 13.49 13.66 14.42 11.3 14.67C11.69 14.98 12.03 15.59 12.03 16.53V18.89C12.03 19.17 12.19 19.49 12.69 19.4C16.66 18.16 19.53 14.42 19.53 10C19.53 4.48 15.05 0 10 0Z" fill="currentColor"/>
              </svg>
            </a>
          </div>
        </div>

        <!-- 中间：链接列表 -->
        <div class="footer-links">
          <!-- 产品服务 -->
          <div class="link-group">
            <h4 class="link-title">产品服务</h4>
            <ul class="link-list">
              <li><a href="/copyrights/register">版权注册</a></li>
              <li><a href="/licenses/apply">授权申请</a></li>
              <li><a href="/royalties">版税管理</a></li>
              <li><a href="/blockchain">区块链查询</a></li>
            </ul>
          </div>

          <!-- 帮助支持 -->
          <div class="link-group">
            <h4 class="link-title">帮助支持</h4>
            <ul class="link-list">
              <li><a href="/help">使用指南</a></li>
              <li><a href="/help#faq">常见问题</a></li>
              <li><a href="/help#api">API文档</a></li>
              <li><a href="/help#contact">技术支持</a></li>
            </ul>
          </div>

          <!-- 关于我们 -->
          <div class="link-group">
            <h4 class="link-title">关于我们</h4>
            <ul class="link-list">
              <li><a href="/about">平台介绍</a></li>
              <li><a href="/about#partners">合作伙伴</a></li>
              <li><a href="/about#cases">用户案例</a></li>
              <li><a href="/about#contact">联系我们</a></li>
            </ul>
          </div>
        </div>

        <!-- 右侧：联系信息 -->
        <div class="footer-contact">
          <h4 class="contact-title">联系我们</h4>
          <div class="contact-items">
            <div class="contact-item">
              <svg class="contact-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 2H2C0.9 2 0 2.9 0 4V14C0 15.1 0.9 16 2 16H16C17.1 16 18 15.1 18 14V4C18 2.9 17.1 2 16 2ZM16 4L9 9L2 4H16ZM16 14H2V6L9 11L16 6V14Z" fill="currentColor"/>
              </svg>
              <span>support@copyright-chain.com</span>
            </div>
            <div class="contact-item">
              <svg class="contact-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 2H2C0.9 2 0 2.9 0 4V14C0 15.1 0.9 16 2 16H16C17.1 16 18 15.1 18 14V4C18 2.9 17.1 2 16 2ZM16 4L9 9L2 4H16ZM16 14H2V6L9 11L16 6V14Z" fill="currentColor"/>
              </svg>
              <span>400-888-8888</span>
            </div>
            <div class="contact-item">
              <svg class="contact-icon" width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 0C4.03 0 0 4.03 0 9C0 13.97 4.03 18 9 18C13.97 18 18 13.97 18 9C18 4.03 13.97 0 9 0ZM9 16C5.13 16 2 12.87 2 9C2 5.13 5.13 2 9 2C12.87 2 16 5.13 16 9C16 12.87 12.87 16 9 16ZM9.5 4.5H8.5V9.5L12.75 12.07L13.25 11.27L9.5 9V4.5Z" fill="currentColor"/>
              </svg>
              <span>工作日 9:00 - 18:00</span>
            </div>
          </div>
          <!-- 客服按钮 -->
          <button class="service-btn">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 0C4.03 0 0 4.03 0 9C0 13.97 4.03 18 9 18C13.97 18 18 13.97 18 9C18 4.03 13.97 0 9 0ZM9 16C5.13 16 2 12.87 2 9C2 5.13 5.13 2 9 2C12.87 2 16 5.13 16 9C16 12.87 12.87 16 9 16ZM8 4H10V10H8V4ZM8 12H10V14H8V12Z" fill="currentColor"/>
            </svg>
            在线客服
          </button>
        </div>
      </div>
    </div>

    <!-- 底部版权栏 -->
    <div class="footer-bottom">
      <div class="footer-container">
        <div class="copyright">
          <span>© 2024 联盟链版权管理系统. All rights reserved.</span>
        </div>
        <div class="legal-links">
          <a href="/legal/privacy">隐私政策</a>
          <span class="separator">·</span>
          <a href="/legal/terms">用户协议</a>
          <span class="separator">·</span>
          <a href="/legal/disclaimer">免责声明</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
const handleContact = () => {
  console.log('Contact clicked')
}
</script>

<style lang="scss" scoped>
.app-footer {
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-primary);
  margin-top: auto;
}

.footer-content {
  padding: 60px 0;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 48px;
  align-items: start;
}

.footer-brand {
  .brand-logo {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;

    .logo-icon {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .brand-name {
      font-size: 18px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .brand-description {
    font-size: 14px;
    line-height: 1.6;
    color: var(--text-secondary);
    margin-bottom: 24px;
  }

  .social-links {
    display: flex;
    gap: 12px;
  }

  .social-link {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: 8px;
    color: var(--text-secondary);
    transition: all 0.2s;

    &:hover {
      background: var(--gradient-primary);
      border-color: transparent;
      color: white;
      transform: translateY(-2px);
    }
  }
}

.footer-links {
  display: flex;
  gap: 48px;
}

.link-group {
  .link-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 16px;
  }

  .link-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    a {
      font-size: 14px;
      color: var(--text-secondary);
      text-decoration: none;
      transition: all 0.2s;

      &:hover {
        color: var(--color-primary-600);
        transform: translateX(4px);
        display: inline-block;
      }
    }
  }
}

.footer-contact {
  .contact-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 16px;
  }

  .contact-items {
    margin-bottom: 20px;
  }

  .contact-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
    font-size: 14px;
    color: var(--text-secondary);

    .contact-icon {
      flex-shrink: 0;
      color: var(--color-primary-600);
    }
  }

  .service-btn {
    width: 100%;
    padding: 12px 20px;
    background: var(--gradient-primary);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }
}

.footer-bottom {
  background: var(--bg-tertiary);
  border-top: 1px solid var(--border-primary);
  padding: 20px 0;
}

.footer-bottom .footer-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  gap: 24px;
}

.copyright {
  font-size: 13px;
  color: var(--text-tertiary);
}

.legal-links {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;

  a {
    color: var(--text-tertiary);
    text-decoration: none;
    transition: color 0.2s;

    &:hover {
      color: var(--color-primary-600);
    }
  }

  .separator {
    color: var(--text-disabled);
  }
}

@media (max-width: 1024px) {
  .footer-container {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .footer-links {
    flex-direction: column;
    gap: 32px;
  }

  .footer-bottom .footer-container {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .footer-content {
    padding: 40px 0;
  }

  .footer-container {
    padding: 0 20px;
  }

  .footer-links {
    gap: 24px;
  }
}
</style>
