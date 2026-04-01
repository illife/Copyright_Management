<template>
  <div class="empty-state" :class="`empty-state--${size}`">
    <!-- Illustration -->
    <div class="empty-state__illustration">
      <slot name="illustration">
        <component :is="illustrationIcon" :size="iconSize" />
      </slot>
    </div>

    <!-- Title -->
    <div class="empty-state__title">
      {{ title }}
    </div>

    <!-- Description -->
    <div v-if="description || $slots.description" class="empty-state__description">
      <slot name="description">
        {{ description }}
      </slot>
    </div>

    <!-- Actions -->
    <div v-if="showAction || $slots.action" class="empty-state__action">
      <slot name="action">
        <el-button v-if="actionText" :type="actionType" :icon="actionIcon" @click="handleAction">
          {{ actionText }}
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  DocumentDelete,
  Search,
  Connection,
  Warning,
  Box
} from '@element-plus/icons-vue'

const props = defineProps({
  // Type of empty state
  type: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'search', 'error', 'network', 'no-data'].includes(value)
  },
  // Title text
  title: {
    type: String,
    default: '暂无数据'
  },
  // Description text
  description: {
    type: String,
    default: ''
  },
  // Size variant
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  // Show action button
  showAction: {
    type: Boolean,
    default: false
  },
  // Action button text
  actionText: {
    type: String,
    default: ''
  },
  // Action button type
  actionType: {
    type: String,
    default: 'primary'
  },
  // Action button icon
  actionIcon: {
    type: [String, Object],
    default: null
  }
})

const emit = defineEmits(['action'])

const illustrationIcon = computed(() => {
  const icons = {
    default: Box,
    search: Search,
    error: Warning,
    network: Connection,
    'no-data': DocumentDelete
  }
  return icons[props.type] || icons.default
})

const iconSize = computed(() => {
  const sizes = {
    small: 48,
    medium: 64,
    large: 80
  }
  return sizes[props.size]
})

const handleAction = () => {
  emit('action')
}
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-xl);
  text-align: center;
  min-height: 300px;

  &--small {
    padding: var(--spacing-xl);
    min-height: 200px;
  }

  &--large {
    padding: var(--spacing-3xl) var(--spacing-2xl);
    min-height: 400px;
  }
}

.empty-state__illustration {
  margin-bottom: var(--spacing-xl);
  color: var(--text-tertiary);
  opacity: 0.5;
  transition: all var(--transition-base);

  .empty-state:hover & {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.empty-state__title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.empty-state__description {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  max-width: 400px;
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-xl);
}

.empty-state__action {
  display: flex;
  gap: var(--spacing-md);
}

// Reduced motion support
@media (prefers-reduced-motion: reduce) {
  .empty-state__illustration {
    transition: none;

    .empty-state:hover & {
      transform: none;
    }
  }
}

// Responsive
@media (max-width: 768px) {
  .empty-state {
    padding: var(--spacing-xl) var(--spacing-lg);
    min-height: 250px;
  }

  .empty-state__title {
    font-size: var(--font-size-md);
  }

  .empty-state__description {
    font-size: var(--font-size-sm);
  }
}
</style>
