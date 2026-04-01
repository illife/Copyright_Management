// 动画效果工具类和函数

// CSS动画关键帧
export const keyframes = {
  fadeIn: `
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  `,
  fadeOut: `
    @keyframes fadeOut {
      from {
        opacity: 1;
        transform: translateY(0);
      }
      to {
        opacity: 0;
        transform: translateY(-20px);
      }
    }
  `,
  slideIn: `
    @keyframes slideIn {
      from {
        transform: translateX(-100%);
      }
      to {
        transform: translateX(0);
      }
    }
  `,
  slideOut: `
    @keyframes slideOut {
      from {
        transform: translateX(0);
      }
      to {
        transform: translateX(100%);
      }
    }
  `,
  scaleIn: `
    @keyframes scaleIn {
      from {
        opacity: 0;
        transform: scale(0.9);
      }
      to {
        opacity: 1;
        transform: scale(1);
      }
    }
  `,
  scaleOut: `
    @keyframes scaleOut {
      from {
        opacity: 1;
        transform: scale(1);
      }
      to {
        opacity: 0;
        transform: scale(0.9);
      }
    }
  `,
  rotate: `
    @keyframes rotate {
      from {
        transform: rotate(0deg);
      }
      to {
        transform: rotate(360deg);
      }
    }
  `,
  pulse: `
    @keyframes pulse {
      0%, 100% {
        opacity: 1;
        transform: scale(1);
      }
      50% {
        opacity: 0.5;
        transform: scale(1.05);
      }
    }
  `,
  shimmer: `
    @keyframes shimmer {
      0% {
        background-position: -1000px 0;
      }
      100% {
        background-position: 1000px 0;
      }
    }
  `,
  loading: `
    @keyframes loading {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }
  `,
  bounce: `
    @keyframes bounce {
      0%, 20%, 50%, 80%, 100% {
        transform: translateY(0);
      }
      40% {
        transform: translateY(-10px);
      }
      60% {
        transform: translateY(-5px);
      }
    }
  `,
  shake: `
    @keyframes shake {
      0%, 100% {
        transform: translateX(0);
      }
      10%, 30%, 50%, 70%, 90% {
        transform: translateX(-5px);
      }
      20%, 40%, 60%, 80% {
        transform: translateX(5px);
      }
    }
  `
}

// 动画工具类
export const animationClasses = {
  // 淡入淡出
  'fade-in': 'animation: fadeIn 0.3s ease-out;',
  'fade-out': 'animation: fadeOut 0.3s ease-in;',

  // 滑动
  'slide-in': 'animation: slideIn 0.3s ease-out;',
  'slide-out': 'animation: slideOut 0.3s ease-in;',

  // 缩放
  'scale-in': 'animation: scaleIn 0.3s ease-out;',
  'scale-out': 'animation: scaleOut 0.3s ease-in;',

  // 旋转
  'rotate': 'animation: rotate 1s linear infinite;',

  // 脉冲
  'pulse': 'animation: pulse 2s ease-in-out infinite;',

  // 加载
  'loading': 'animation: loading 1s linear infinite;',

  // 弹跳
  'bounce': 'animation: bounce 1s ease-in-out infinite;',

  // 抖动
  'shake': 'animation: shake 0.5s ease-in-out;'
}

// 过渡效果
export const transitions = {
  // 持续时间
  duration: {
    fast: '150ms',
    normal: '300ms',
    slow: '500ms'
  },

  // 缓动函数
  easing: {
    ease: 'ease',
    easeIn: 'ease-in',
    easeOut: 'ease-out',
    easeInOut: 'ease-in-out',
    cubic: 'cubic-bezier(0.4, 0, 0.2, 1)'
  }
}

// 创建带动画的元素
export function createAnimatedElement(element, animationName, duration = 300) {
  if (!element) return

  element.style.animation = `${animationName} ${duration}ms ease-out`

  // 动画结束后移除动画属性
  element.addEventListener('animationend', () => {
    element.style.animation = ''
  }, { once: true })
}

// 添加淡入效果
export function fadeIn(element, duration = 300) {
  if (!element) return

  element.style.opacity = '0'
  element.style.transform = 'translateY(20px)'
  element.style.transition = `opacity ${duration}ms ease-out, transform ${duration}ms ease-out`

  // 强制重排
  element.offsetHeight

  requestAnimationFrame(() => {
    element.style.opacity = '1'
    element.style.transform = 'translateY(0)'
  })
}

// 添加淡出效果
export function fadeOut(element, duration = 300) {
  if (!element) return

  element.style.transition = `opacity ${duration}ms ease-in, transform ${duration}ms ease-in`
  element.style.opacity = '0'
  element.style.transform = 'translateY(-20px)'

  return new Promise(resolve => {
    setTimeout(() => {
      resolve()
    }, duration)
  })
}

// 添加缩放效果
export function scaleIn(element, duration = 300) {
  if (!element) return

  element.style.opacity = '0'
  element.style.transform = 'scale(0.9)'
  element.style.transition = `opacity ${duration}ms ease-out, transform ${duration}ms ease-out`

  // 强制重排
  element.offsetHeight

  requestAnimationFrame(() => {
    element.style.opacity = '1'
    element.style.transform = 'scale(1)'
  })
}

// 添加滑入效果
export function slideIn(element, direction = 'left', duration = 300) {
  if (!element) return

  const transforms = {
    left: 'translateX(-100%)',
    right: 'translateX(100%)',
    up: 'translateY(-100%)',
    down: 'translateY(100%)'
  }

  element.style.transform = transforms[direction] || transforms.left
  element.style.transition = `transform ${duration}ms ease-out`

  // 强制重排
  element.offsetHeight

  requestAnimationFrame(() => {
    element.style.transform = 'translateX(0) translateY(0)'
  })
}

// 添加脉冲效果
export function pulse(element, duration = 2000, infinite = true) {
  if (!element) return

  element.style.animation = `pulse ${duration}ms ease-in-out${infinite ? ' infinite' : ''}`
}

// 添加旋转加载效果
export function rotateLoading(element, duration = 1000) {
  if (!element) return

  element.style.animation = `loading ${duration}ms linear infinite`
}

// 移除所有动画
export function removeAllAnimations(element) {
  if (!element) return

  element.style.animation = ''
  element.style.transition = ''
  element.style.transform = ''
}

// 延迟执行
export function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// 批量添加动画
export function batchAnimate(elements, animationFn, stagger = 100) {
  if (!elements || elements.length === 0) return

  elements.forEach((element, index) => {
    setTimeout(() => {
      animationFn(element)
    }, index * stagger)
  })
}

// 交错动画
export function staggerAnimate(elements, animationName, stagger = 100, duration = 300) {
  if (!elements || elements.length === 0) return Promise.resolve()

  const promises = elements.map((element, index) => {
    return new Promise(resolve => {
      setTimeout(() => {
        createAnimatedElement(element, animationName, duration)
        setTimeout(resolve, duration)
      }, index * stagger)
    })
  })

  return Promise.all(promises)
}

// 可滚动的容器动画
export function setupScrollAnimation(container, selector = '.animate-on-scroll', threshold = 0.1) {
  if (!container) return

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const element = entry.target
        element.classList.add('animate-in')
        observer.unobserve(element)
      }
    })
  }, { threshold })

  const elements = container.querySelectorAll(selector)
  elements.forEach(el => observer.observe(el))

  // 返回清理函数
  return () => observer.disconnect()
}

// ============================================
// Vue 3 Composables for Animations
// ============================================

/**
 * Stagger animation composable
 * @param {Number} count - Number of items to animate
 * @param {Number} delay - Delay between each item in ms
 * @returns {Function} - Function to get delay for index
 */
export function useStagger(count, delay = 100) {
  const delays = Array.from({ length: count }, (_, i) => i * delay)

  return (index) => delays[index] || 0
}

/**
 * Intersection Observer composable for scroll animations
 * @param {Object} options - Observer options
 * @returns {Object} - { observe, unobserve, isVisible }
 */
export function useIntersectionObserver(options = {}) {
  const { threshold = 0.1, root = null, rootMargin = '0px' } = options
  const isVisible = ref(false)
  const target = ref(null)

  let observer = null

  const observe = (element) => {
    if (!element) return

    target.value = element

    observer = new IntersectionObserver(([entry]) => {
      isVisible.value = entry.isIntersecting
    }, { threshold, root, rootMargin })

    observer.observe(element)
  }

  const unobserve = () => {
    if (observer && target.value) {
      observer.unobserve(target.value)
      observer.disconnect()
    }
  }

  // Auto cleanup
  onUnmounted(() => {
    unobserve()
  })

  return {
    observe,
    unobserve,
    isVisible,
    target
  }
}

/**
 * Page transition composable
 * @returns {Object} - Transition configuration
 */
export function usePageTransition() {
  const onBeforeEnter = (el) => {
    el.style.opacity = '0'
    el.style.transform = 'translateY(20px)'
  }

  const onEnter = (el, done) => {
    const duration = 300
    el.style.transition = `opacity ${duration}ms ease-out, transform ${duration}ms ease-out`

    requestAnimationFrame(() => {
      el.style.opacity = '1'
      el.style.transform = 'translateY(0)'
    })

    setTimeout(done, duration)
  }

  const onLeave = (el, done) => {
    const duration = 200
    el.style.transition = `opacity ${duration}ms ease-in, transform ${duration}ms ease-in`
    el.style.opacity = '0'
    el.style.transform = 'translateY(-20px)'

    setTimeout(done, duration)
  }

  return {
    onBeforeEnter,
    onEnter,
    onLeave,
    css: false
  }
}

/**
 * Hover effect composable
 * @param {Ref} element - Element ref
 * @param {Object} options - Effect options
 * @returns {Object} - Effect methods
 */
export function useHoverEffect(element, options = {}) {
  const {
    scale = 1.05,
    duration = 200,
    shadow = true
  } = options

  const onMouseEnter = () => {
    if (element.value) {
      element.value.style.transition = `all ${duration}ms cubic-bezier(0.4, 0, 0.2, 1)`
      element.value.style.transform = `scale(${scale})`
      if (shadow) {
        element.value.style.boxShadow = '0 10px 40px rgba(0, 0, 0, 0.15)'
      }
    }
  }

  const onMouseLeave = () => {
    if (element.value) {
      element.value.style.transform = 'scale(1)'
      if (shadow) {
        element.value.style.boxShadow = ''
      }
    }
  }

  onMounted(() => {
    if (element.value) {
      element.value.addEventListener('mouseenter', onMouseEnter)
      element.value.addEventListener('mouseleave', onMouseLeave)
    }
  })

  onUnmounted(() => {
    if (element.value) {
      element.value.removeEventListener('mouseenter', onMouseEnter)
      element.value.removeEventListener('mouseleave', onMouseLeave)
    }
  })

  return {
    onMouseEnter,
    onMouseLeave
  }
}

// Export all utilities
export default {
  keyframes,
  animationClasses,
  transitions,
  createAnimatedElement,
  fadeIn,
  fadeOut,
  scaleIn,
  slideIn,
  pulse,
  rotateLoading,
  removeAllAnimations,
  delay,
  batchAnimate,
  staggerAnimate,
  setupScrollAnimation,
  useStagger,
  useIntersectionObserver,
  usePageTransition,
  useHoverEffect
}
