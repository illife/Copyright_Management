<template>
  <div class="search-bar" :class="`search-bar--${size}`">
    <div class="search-bar__input-wrapper">
      <!-- Search Icon -->
      <el-icon class="search-bar__icon">
        <Search />
      </el-icon>

      <!-- Input -->
      <el-input
        ref="inputRef"
        v-model="searchValue"
        :placeholder="placeholder"
        :size="size"
        :clearable="clearable"
        :disabled="disabled"
        @input="handleInput"
        @clear="handleClear"
        @keyup.enter="handleSearch"
        class="search-bar__input"
      />

      <!-- Search Button -->
      <el-button
        v-if="showButton"
        :type="buttonType"
        :size="size"
        :loading="loading"
        @click="handleSearch"
        class="search-bar__button"
      >
        {{ buttonText }}
      </el-button>

      <!-- Clear Button (when no clearable prop) -->
      <el-button
        v-else-if="searchValue && !clearable"
        :icon="CircleClose"
        :size="size"
        circle
        @click="handleClear"
        class="search-bar__clear"
      />
    </div>

    <!-- Filters Slot -->
    <div v-if="$slots.filters" class="search-bar__filters">
      <slot name="filters"></slot>
    </div>

    <!-- Advanced Toggle -->
    <div v-if="advanced || $slots.advanced" class="search-bar__advanced">
      <el-button
        :icon="isAdvancedOpen ? ArrowUp : ArrowDown"
        text
        @click="toggleAdvanced"
        class="search-bar__advanced-toggle"
      >
        {{ isAdvancedOpen ? '收起' : '高级搜索' }}
      </el-button>
      <el-collapse-transition>
        <div v-show="isAdvancedOpen" class="search-bar__advanced-content">
          <slot name="advanced"></slot>
        </div>
      </el-collapse-transition>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { Search, CircleClose, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { useDebounceFn } from '@/utils/performance'

const props = defineProps({
  // v-model
  modelValue: {
    type: String,
    default: ''
  },
  // Input placeholder
  placeholder: {
    type: String,
    default: '搜索...'
  },
  // Input size
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // Show search button
  showButton: {
    type: Boolean,
    default: false
  },
  // Button text
  buttonText: {
    type: String,
    default: '搜索'
  },
  // Button type
  buttonType: {
    type: String,
    default: 'primary'
  },
  // Enable clearable
  clearable: {
    type: Boolean,
    default: true
  },
  // Disabled state
  disabled: {
    type: Boolean,
    default: false
  },
  // Loading state
  loading: {
    type: Boolean,
    default: false
  },
  // Debounce delay (ms)
  debounce: {
    type: Number,
    default: 300
  },
  // Enable advanced search
  advanced: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'update:modelValue',
  'search',
  'clear',
  'input'
])

const inputRef = ref(null)
const searchValue = ref(props.modelValue)
const isAdvancedOpen = ref(false)

// Sync with modelValue
watch(() => props.modelValue, (newValue) => {
  searchValue.value = newValue
})

// Debounced search handler
const debouncedSearch = useDebounceFn((value) => {
  emit('search', value)
}, props.debounce)

const handleInput = (value) => {
  emit('update:modelValue', value)
  emit('input', value)

  if (props.debounce > 0) {
    debouncedSearch(value)
  }
}

const handleSearch = () => {
  emit('search', searchValue.value)
}

const handleClear = () => {
  searchValue.value = ''
  emit('update:modelValue', '')
  emit('clear')
  emit('search', '')
}

const toggleAdvanced = () => {
  isAdvancedOpen.value = !isAdvancedOpen.value
}

// Focus input
const focus = () => {
  inputRef.value?.focus()
}

// Expose methods
defineExpose({
  focus,
  toggleAdvanced
})
</script>

<style lang="scss" scoped>
.search-bar {
  width: 100%;

  &__input-wrapper {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: var(--radius-lg);
    padding: var(--spacing-sm) var(--spacing-md);
    transition: all var(--transition-base);

    &:hover {
      border-color: var(--border-hover);
    }

    &:focus-within {
      border-color: var(--color-primary-600);
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
  }

  &__icon {
    font-size: 18px;
    color: var(--text-tertiary);
    flex-shrink: 0;
    transition: color var(--transition-base);
  }

  &__input {
    flex: 1;

    :deep(.el-input__wrapper) {
      background: transparent;
      border: none;
      box-shadow: none;
      padding: 0;
    }

    :deep(.el-input__inner) {
      color: var(--text-primary);
      font-size: var(--font-size-base);

      &::placeholder {
        color: var(--text-tertiary);
      }
    }
  }

  &__button {
    flex-shrink: 0;
  }

  &__clear {
    flex-shrink: 0;
    color: var(--text-tertiary);
    transition: all var(--transition-base);

    &:hover {
      color: var(--text-secondary);
    }
  }

  &__filters {
    margin-top: var(--spacing-lg);
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-md);
    align-items: center;
  }

  &__advanced {
    margin-top: var(--spacing-md);

    &-toggle {
      font-size: var(--font-size-sm);
      color: var(--text-secondary);
    }

    &-content {
      margin-top: var(--spacing-lg);
      padding: var(--spacing-lg);
      background: var(--bg-secondary);
      border-radius: var(--radius-lg);
    }
  }

  // Size variants
  &--small {
    .search-bar__input-wrapper {
      padding: var(--spacing-xs) var(--spacing-sm);
    }

    .search-bar__icon {
      font-size: 16px;
    }
  }

  &--large {
    .search-bar__input-wrapper {
      padding: var(--spacing-md) var(--spacing-lg);
    }

    .search-bar__icon {
      font-size: 20px;
    }

    :deep(.el-input__inner) {
      font-size: var(--font-size-md);
    }
  }
}

// Reduced motion
@media (prefers-reduced-motion: reduce) {
  .search-bar__input-wrapper {
    transition: none;
  }
}

// Responsive
@media (max-width: 768px) {
  .search-bar {
    &__input-wrapper {
      padding: var(--spacing-xs) var(--spacing-sm);
      border-radius: var(--radius-md);
    }

    &__button {
      :deep(.el-button__text) {
        display: none;
      }

      :deep(.el-button__icon) {
        margin: 0;
      }
    }

    &__filters {
      flex-direction: column;
      align-items: stretch;
    }
  }
}
</style>
