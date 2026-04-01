/**
 * Performance Utility Functions
 * Debounce, throttle, and other performance optimizations
 */

/**
 * Creates a debounced function that delays invoking func until after wait milliseconds
 * @param {Function} func - The function to debounce
 * @param {number} wait - The number of milliseconds to delay
 * @param {Object} options - Options object
 * @param {boolean} options.leading - Invoke on the leading edge
 * @param {boolean} options.trailing - Invoke on the trailing edge
 * @returns {Function} Returns the new debounced function
 */
export function debounce(func, wait = 300, options = {}) {
  let timeoutId
  let lastArgs
  let lastThis
  let { leading = false, trailing = true } = options

  const invokeFunc = (time) => {
    const args = lastArgs
    const thisArg = lastThis

    lastArgs = undefined
    lastThis = undefined

    func.apply(thisArg, args)
  }

  const startTimer = (pendingFunc, wait) => {
    return setTimeout(pendingFunc, wait)
  }

  const remainingWait = (wait) => {
    const start = timeoutId?.startTime || Date.now()
    return wait - (Date.now() - start)
  }

  const shouldInvoke = (time) => {
    const timeSinceLast = timeoutId ? time - timeoutId.lastCallTime : 0
    return !timeoutId || timeSinceLast >= wait
  }

  const trailingEdge = (time) => {
    timeoutId = undefined

    if (trailing) {
      invokeFunc(time)
    }
  }

  const timerExpired = () => {
    const time = Date.now()
    if (shouldInvoke(time)) {
      trailingEdge(time)
    } else {
      timeoutId = startTimer(timerExpired, remainingWait(wait))
      timeoutId.startTime = time
    }
  }

  const leadingEdge = (time) => {
    timeoutId = {
      startTime: time,
      lastCallTime: time
    }

    if (leading) {
      invokeFunc(time)
    }

    return startTimer(timerExpired, wait)
  }

  const debounced = function (...args) {
    const time = Date.now()
    const isInvoking = shouldInvoke(time)

    lastArgs = args
    lastThis = this

    if (isInvoking) {
      if (!timeoutId) {
        return leadingEdge(time)
      }

      clearTimeout(timeoutId)
      timeoutId = startTimer(timerExpired, wait)
      timeoutId.startTime = time
    }

    return timeoutId
  }

  debounced.cancel = () => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = undefined
    lastArgs = undefined
    lastThis = undefined
  }

  debounced.flush = () => {
    if (timeoutId) {
      const time = Date.now()
      return trailingEdge(time)
    }
  }

  return debounced
}

/**
 * Creates a throttled function that only invokes func at most once per every wait milliseconds
 * @param {Function} func - The function to throttle
 * @param {number} wait - The number of milliseconds to throttle
 * @param {Object} options - Options object
 * @param {boolean} options.leading - Invoke on the leading edge
 * @param {boolean} options.trailing - Invoke on the trailing edge
 * @returns {Function} Returns the new throttled function
 */
export function throttle(func, wait = 300, options = {}) {
  let leading = true
  let trailing = true

  if (typeof options === 'function') {
    options = {}
  } else {
    leading = 'leading' in options ? !!options.leading : leading
    trailing = 'trailing' in options ? !!options.trailing : trailing
  }

  return debounce(func, wait, {
    leading,
    trailing
  })
}

/**
 * Vue 3 composable for debounced values
 * @param {Ref} value - The ref value to debounce
 * @param {number} delay - Debounce delay in milliseconds
 * @returns {Object} - { debouncedValue, flush }
 */
export function useDebounce(value, delay = 300) {
  const { ref, watch } = require('vue')

  const debouncedValue = ref(value.value)
  let timeout = null

  const updateDebouncedValue = (newValue) => {
    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      debouncedValue.value = newValue
      timeout = null
    }, delay)
  }

  const flush = () => {
    if (timeout) {
      clearTimeout(timeout)
      debouncedValue.value = value.value
      timeout = null
    }
  }

  watch(value, updateDebouncedValue, { immediate: true })

  return {
    debouncedValue,
    flush
  }
}

/**
 * Vue 3 composable for debounced functions
 * @param {Function} fn - Function to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {Function} Debounced function
 */
export function useDebounceFn(fn, delay = 300) {
  let timeout = null
  let lastArgs = null
  let lastThis = null

  return function (...args) {
    lastArgs = args
    lastThis = this

    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      fn.apply(lastThis, lastArgs)
      timeout = null
      lastArgs = null
      lastThis = null
    }, delay)
  }
}

/**
 * Vue 3 composable for throttled functions
 * @param {Function} fn - Function to throttle
 * @param {number} limit - Throttle limit in milliseconds
 * @returns {Function} Throttled function
 */
export function useThrottleFn(fn, limit = 300) {
  let inThrottle = false
  let lastResult = null
  let lastArgs = null
  let lastThis = null

  return function (...args) {
    if (!inThrottle) {
      lastResult = fn.apply(this, args)
      inThrottle = true

      setTimeout(() => {
        inThrottle = false
        if (lastArgs) {
          lastResult = fn.apply(lastThis, lastArgs)
          lastArgs = null
          lastThis = null
        }
      }, limit)
    } else {
      lastArgs = args
      lastThis = this
    }

    return lastResult
  }
}

/**
 * Request animation frame throttle
 * @param {Function} fn - Function to throttle
 * @returns {Function} Throttled function
 */
export function rafThrottle(fn) {
  let pending = false
  let lastArgs = null

  return function (...args) {
    lastArgs = args

    if (!pending) {
      pending = true

      requestAnimationFrame(() => {
        fn.apply(this, lastArgs)
        pending = false
        lastArgs = null
      })
    }
  }
}

/**
 * Batch updates to avoid excessive re-renders
 * @param {Function} fn - Function to batch
 * @param {number} wait - Batch wait time
 * @returns {Function} Batched function
 */
export function batchUpdate(fn, wait = 0) {
  let updates = []
  let timeout = null

  return function (update) {
    updates.push(update)

    if (!timeout) {
      timeout = setTimeout(() => {
        fn(updates)
        updates = []
        timeout = null
      }, wait)
    }
  }
}

/**
 * Performance monitoring utility
 * @param {string} label - Performance label
 * @returns {Object} - Performance measurement API
 */
export function measurePerformance(label) {
  const start = performance.now()

  return {
    end: () => {
      const end = performance.now()
      const duration = end - start

      if (process.env.NODE_ENV === 'development') {
        console.log(`[Performance] ${label}: ${duration.toFixed(2)}ms`)
      }

      return duration
    },
    mark: (markLabel) => {
      performance.mark(`${label}-${markLabel}`)
    },
    measure: (measureLabel, startMark, endMark) => {
      performance.measure(`${label}-${measureLabel}`, `${label}-${startMark}`, `${label}-${endMark}`)
      const measure = performance.getEntriesByName(`${label}-${measureLabel}`)[0]
      return measure.duration
    }
  }
}

/**
 * Lazy load images using Intersection Observer
 * @param {string} selector - Image selector
 * @param {Object} options - Observer options
 */
export function lazyLoadImages(selector = 'img[data-lazy]', options = {}) {
  const defaultOptions = {
    rootMargin: '50px',
    threshold: 0.1,
    ...options
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const img = entry.target
        const src = img.dataset.lazy

        if (src) {
          img.src = src
          img.removeAttribute('data-lazy')
          observer.unobserve(img)
        }
      }
    })
  }, defaultOptions)

  document.querySelectorAll(selector).forEach(img => {
    observer.observe(img)
  })

  return observer
}

/**
 * Debounce for Vue ref (composition API style)
 * @param {Ref} source - Source ref
 * @param {number} delay - Delay in ms
 * @returns {Ref} Debounced ref
 */
export function refDebounce(source, delay = 300) {
  const { ref, watch, onUnmounted } = require('vue')
  const debounced = ref(source.value)
  let timeout = null

  const update = () => {
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      debounced.value = source.value
    }, delay)
  }

  watch(source, update)

  onUnmounted(() => {
    clearTimeout(timeout)
  })

  return debounced
}

export default {
  debounce,
  throttle,
  useDebounce,
  useDebounceFn,
  useThrottleFn,
  rafThrottle,
  batchUpdate,
  measurePerformance,
  lazyLoadImages,
  refDebounce
}
