/**
 * Count Up Animation Composable
 * Animates numbers counting up from 0 to target value
 */

import { ref, onMounted, watch } from 'vue'

export function useCountUp(targetValue, options = {}) {
  const {
    duration = 1000,
    startValue = 0,
    decimalPlaces = 0,
    separator = ',',
    autoStart = true,
    easing = 'easeOutCubic'
  } = options

  const currentValue = ref(startValue)
  const isAnimating = ref(false)

  // Easing functions
  const easingFunctions = {
    linear: (t) => t,
    easeInQuad: (t) => t * t,
    easeOutQuad: (t) => t * (2 - t),
    easeInOutQuad: (t) => t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t,
    easeInCubic: (t) => t * t * t,
    easeOutCubic: (t) => 1 - Math.pow(1 - t, 3),
    easeInOutCubic: (t) => t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1,
    easeInQuart: (t) => t * t * t * t,
    easeOutQuart: (t) => 1 - Math.pow(1 - t, 4),
    easeInOutQuart: (t) => t < 0.5 ? 8 * t * t * t * t : 1 - 8 * Math.pow(1 - t, 4),
    easeOutBounce: (t) => {
      if (t < 1 / 2.75) {
        return 7.5625 * t * t
      } else if (t < 2 / 2.75) {
        return 7.5625 * (t -= 1.5 / 2.75) * t + 0.75
      } else if (t < 2.5 / 2.75) {
        return 7.5625 * (t -= 2.25 / 2.75) * t + 0.9375
      } else {
        return 7.5625 * (t -= 2.625 / 2.75) * t + 0.984375
      }
    }
  }

  const formatNumber = (num) => {
    const fixed = num.toFixed(decimalPlaces)
    const parts = fixed.split('.')
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, separator)
    return parts.join('.')
  }

  const animate = (start, end, duration) => {
    isAnimating.value = true
    const startTime = performance.now()
    const change = end - start
    const easeFn = easingFunctions[easing] || easingFunctions.easeOutCubic

    const step = (currentTime) => {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)

      currentValue.value = start + change * easeFn(progress)

      if (progress < 1) {
        requestAnimationFrame(step)
      } else {
        currentValue.value = end
        isAnimating.value = false
      }
    }

    requestAnimationFrame(step)
  }

  const start = () => {
    animate(startValue, targetValue.value, duration)
  }

  const reset = () => {
    currentValue.value = startValue
  }

  if (autoStart) {
    onMounted(() => {
      start()
    })
  }

  // Watch for target value changes
  watch(targetValue, (newVal) => {
    animate(currentValue.value, newVal, duration)
  })

  return {
    currentValue,
    formattedValue: () => formatNumber(currentValue.value),
    isAnimating,
    start,
    reset
  }
}

/**
 * Usage example:
 *
 * const targetValue = ref(12345)
 * const { currentValue, formattedValue } = useCountUp(targetValue, {
 *   duration: 1500,
 *   decimalPlaces: 2,
 *   separator: ','
 * })
 *
 * In template:
 * <span>{{ formattedValue() }}</span>
 */
