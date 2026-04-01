<template>
  <el-dropdown trigger="click" @command="handleThemeChange" class="theme-switcher">
    <div class="theme-toggle-btn">
      <el-icon class="theme-icon" :class="{ 'rotate': isDark }">
        <component :is="isDark ? 'Moon' : 'Sunny'" />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu class="theme-dropdown">
        <el-dropdown-item
          v-for="option in themeOptions"
          :key="option.value"
          :command="option.value"
          :class="{ 'is-active': theme === option.value }"
        >
          <el-icon class="theme-option-icon">
            <component :is="option.icon" />
          </el-icon>
          <span>{{ option.label }}</span>
          <el-icon v-if="theme === option.value" class="check-icon">
            <Check />
          </el-icon>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { Sunny, Moon, Monitor, Check } from '@element-plus/icons-vue'
import { useTheme, THEMES } from '@/composables/useTheme'

const { theme, isDark, setTheme } = useTheme()

const themeOptions = [
  { value: THEMES.LIGHT, label: '浅色模式', icon: 'Sunny' },
  { value: THEMES.DARK, label: '深色模式', icon: 'Moon' },
  { value: THEMES.AUTO, label: '跟随系统', icon: 'Monitor' }
]

const handleThemeChange = (newTheme) => {
  setTheme(newTheme)
}
</script>

<style lang="scss" scoped>
.theme-switcher {
  display: inline-block;
}

.theme-toggle-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  cursor: pointer;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: var(--gradient-primary);
    opacity: 0;
    transition: opacity var(--transition-base);
  }

  &:hover {
    border-color: var(--color-primary-600);
    box-shadow: var(--shadow-glow);
    transform: translateY(-2px);

    &::before {
      opacity: 0.1;
    }
  }

  &:active {
    transform: translateY(0);
  }
}

.theme-icon {
  font-size: 20px;
  color: var(--text-secondary);
  position: relative;
  z-index: 1;
  transition: all var(--transition-slow);

  &.rotate {
    transform: rotate(180deg);
  }
}

:deep(.theme-dropdown) {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-sm);
  box-shadow: var(--shadow-xl);
  min-width: 160px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
  position: relative;

  &:hover {
    background: var(--bg-tertiary);
    color: var(--text-primary);
  }

  &.is-active {
    background: var(--color-success-light);
    color: var(--color-success);

    .theme-option-icon {
      color: var(--color-success);
    }
  }

  .theme-option-icon {
    font-size: 18px;
    color: var(--text-tertiary);
    transition: color var(--transition-fast);
  }

  span {
    flex: 1;
  }

  .check-icon {
    font-size: 16px;
  }
}

// Reduced motion support
@media (prefers-reduced-motion: reduce) {
  .theme-icon {
    transition: none;

    &.rotate {
      transform: none;
    }
  }

  .theme-toggle-btn:hover {
    transform: none;
  }
}
</style>
