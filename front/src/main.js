import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// Import styles in correct order
// 1. Variables first (CSS custom properties)
import './assets/styles/variables.scss'
// 2. Global styles
import './assets/styles/global.scss'
// 3. Responsive styles
import './assets/styles/responsive.scss'

const app = createApp(App)
const pinia = createPinia()

// Register all Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// Initialize theme system after mount (ensures DOM is ready)
app.mount('#app')

// Initialize theme after app is mounted
import { themeState } from './composables/useTheme'
setTimeout(() => {
  themeState.initTheme()
}, 0)

