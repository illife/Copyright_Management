import { ref, watch, onMounted } from 'vue'

const THEME_KEY = 'app-theme'

export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  AUTO: 'auto'
}

// Global theme state
const currentTheme = ref(THEMES.LIGHT)
const isDark = ref(false)

// Get system theme preference
const getSystemTheme = () => {
  if (typeof window === 'undefined') return THEMES.LIGHT
  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? THEMES.DARK
    : THEMES.LIGHT
}

// Apply theme to document
const applyTheme = (theme) => {
  if (typeof document === 'undefined') return

  let actualTheme = theme

  if (theme === THEMES.AUTO) {
    actualTheme = getSystemTheme()
  }

  document.documentElement.setAttribute('data-theme', actualTheme)
  isDark.value = actualTheme === THEMES.DARK
}

export function useTheme() {
  // Initialize theme
  const initTheme = () => {
    if (typeof window === 'undefined') return

    const savedTheme = localStorage.getItem(THEME_KEY) || THEMES.LIGHT
    currentTheme.value = savedTheme
    applyTheme(savedTheme)
  }

  // Set theme
  const setTheme = (theme) => {
    currentTheme.value = theme
    if (typeof window !== 'undefined') {
      localStorage.setItem(THEME_KEY, theme)
    }
    applyTheme(theme)
  }

  // Toggle theme (light/dark)
  const toggleTheme = () => {
    const newTheme = isDark.value ? THEMES.LIGHT : THEMES.DARK
    setTheme(newTheme)
  }

  // Listen for system theme changes
  const listenSystemTheme = () => {
    if (typeof window === 'undefined') return () => {}

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')

    const handleChange = (e) => {
      if (currentTheme.value === THEMES.AUTO) {
        applyTheme(THEMES.AUTO)
      }
    }

    // Modern browsers
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', handleChange)
      return () => mediaQuery.removeEventListener('change', handleChange)
    }
    // Older browsers
    else if (mediaQuery.addListener) {
      mediaQuery.addListener(handleChange)
      return () => mediaQuery.removeListener(handleChange)
    }

    return () => {}
  }

  // Auto-initialize on first use
  if (typeof window !== 'undefined') {
    onMounted(() => {
      initTheme()
      const stopListen = listenSystemTheme()

      // Cleanup on component unmount
      return stopListen
    })
  }

  return {
    theme: currentTheme,
    isDark,
    THEMES,
    setTheme,
    toggleTheme,
    initTheme
  }
}

// Export singleton instance for non-composable usage
export const themeState = {
  theme: currentTheme,
  isDark,
  setTheme: (theme) => {
    currentTheme.value = theme
    if (typeof window !== 'undefined') {
      localStorage.setItem(THEME_KEY, theme)
    }
    applyTheme(theme)
  },
  toggleTheme: () => {
    const newTheme = isDark.value ? THEMES.LIGHT : THEMES.DARK
    themeState.setTheme(newTheme)
  },
  initTheme: () => {
    applyTheme(currentTheme.value)
  }
}
