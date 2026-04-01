<template>
  <div class="stat-card" :class="[`stat-card--${variant}`, { 'stat-card--clickable': clickable }]">
    <!-- Loading Skeleton -->
    <div v-if="loading" class="stat-card__skeleton">
      <div class="stat-card__skeleton-icon"></div>
      <div class="stat-card__skeleton-content">
        <div class="stat-card__skeleton-title"></div>
        <div class="stat-card__skeleton-value"></div>
      </div>
    </div>

    <!-- Content -->
    <template v-else>
      <!-- Icon Section -->
      <div v-if="icon || $slots.icon" class="stat-card__icon" :class="`stat-card__icon--${color}`">
        <slot name="icon">
          <el-icon :size="24">
            <component :is="icon" />
          </el-icon>
        </slot>
      </div>

      <!-- Content Section -->
      <div class="stat-card__content">
        <div class="stat-card__label">{{ title }}</div>
        <div class="stat-card__value">
          <span class="stat-card__number" ref="numberRef">{{ formattedValue }}</span>
          <span v-if="unit" class="stat-card__unit">{{ unit }}</span>
        </div>

        <!-- Trend Indicator -->
        <div v-if="trend !== undefined" class="stat-card__trend" :class="`stat-card__trend--${trend >= 0 ? 'up' : 'down'}`">
          <el-icon>
            <component :is="trend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
          </el-icon>
          <span>{{ Math.abs(trend) }}%</span>
          <span class="stat-card__trend-label">{{ trendLabel }}</span>
        </div>

        <!-- Extra Slot -->
        <div v-if="$slots.extra" class="stat-card__extra">
          <slot name="extra"></slot>
        </div>
      </div>

      <!-- Decorative Elements -->
      <div class="stat-card__decoration" :class="`stat-card__decoration--${color}`"></div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useCountUp } from '@/composables/useCountUp'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    default: 0
  },
  unit: {
    type: String,
    default: ''
  },
  icon: {
    type: [String, Object],
    default: null
  },
  color: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'warning', 'danger', 'info'].includes(value)
  },
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'gradient', 'glass'].includes(value)
  },
  trend: {
    type: Number,
    default: undefined
  },
  trendLabel: {
    type: String,
    default: '较上周'
  },
  loading: {
    type: Boolean,
    default: false
  },
  clickable: {
    type: Boolean,
    default: false
  },
  animate: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['click'])

const numberRef = ref(null)
const animatedValue = ref(props.animate ? 0 : props.value)

const formattedValue = computed(() => {
  if (typeof props.value === 'string') return props.value

  // Format large numbers
  if (props.value >= 1000000) {
    return (props.value / 1000000).toFixed(1) + 'M'
  } else if (props.value >= 1000) {
    return (props.value / 1000).toFixed(1) + 'K'
  }

  return animatedValue.value.toLocaleString()
})

// Animate number on mount
onMounted(() => {
  if (props.animate && typeof props.value === 'number') {
    const duration = 1000
    const startTime = Date.now()
    const startValue = 0
    const endValue = props.value

    const animate = () => {
      const elapsed = Date.now() - startTime
      const progress = Math.min(elapsed / duration, 1)

      // Easing function (ease-out cubic)
      const eased = 1 - Math.pow(1 - progress, 3)

      animatedValue.value = Math.floor(startValue + (endValue - startValue) * eased)

      if (progress < 1) {
        requestAnimationFrame(animate)
      } else {
        animatedValue.value = endValue
      }
    }

    requestAnimationFrame(animate)
  }
})

// Re-animate when value changes
watch(() => props.value, (newValue) => {
  if (props.animate && typeof newValue === 'number') {
    animatedValue.value = 0
    const duration = 1000
    const startTime = Date.now()
    const startValue = 0
    const endValue = newValue

    const animate = () => {
      const elapsed = Date.now() - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = 1 - Math.pow(1 - progress, 3)
      animatedValue.value = Math.floor(startValue + (endValue - startValue) * eased)

      if (progress < 1) {
        requestAnimationFrame(animate)
      } else {
        animatedValue.value = endValue
      }
    }

    requestAnimationFrame(animate)
  }
})
</script>

<style lang="scss" scoped>
.stat-card {
  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-lg);
  overflow: hidden;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);
  max-width: 100%;
  width: 100%;
  box-sizing: border-box;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--color-primary-600);

    .stat-card__decoration {
      transform: scale(1.1);
    }
  }

  &--clickable {
    cursor: pointer;

    &:active {
      transform: translateY(-2px);
    }
  }

  &--gradient {
    background: var(--gradient-card);
    border-color: transparent;

    &:hover {
      border-color: var(--color-primary-600);
    }
  }

  &--glass {
    background: var(--glass-bg);
    backdrop-filter: var(--glass-blur);
    border: 1px solid var(--glass-border);
    box-shadow: var(--glass-shadow);
  }
}

.stat-card__icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
  transition: all var(--transition-base);

  &--primary {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
    color: var(--color-primary-600);
  }

  &--success {
    background: var(--color-success-light);
    color: var(--color-success);
  }

  &--warning {
    background: var(--color-warning-light);
    color: var(--color-warning);
  }

  &--danger {
    background: var(--color-danger-light);
    color: var(--color-danger);
  }

  &--info {
    background: var(--color-info-light);
    color: var(--color-info);
  }
}

.stat-card__content {
  flex: 1;
  min-width: 0;
}

.stat-card__label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-tertiary);
  margin-bottom: var(--spacing-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-card__value {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.stat-card__number {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  line-height: 1;
  font-feature-settings: 'tnum' 1;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.stat-card__unit {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-tertiary);
}

.stat-card__trend {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);

  &--up {
    background: var(--color-success-light);
    color: var(--color-success);
  }

  &--down {
    background: var(--color-danger-light);
    color: var(--color-danger);
  }

  .el-icon {
    font-size: 14px;
  }

  &-label {
    color: var(--text-tertiary);
    margin-left: var(--spacing-xs);
  }
}

.stat-card__extra {
  margin-top: var(--spacing-sm);
}

.stat-card__decoration {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  opacity: 0.05;
  transition: transform var(--transition-slow);
  pointer-events: none;

  &--primary {
    background: var(--gradient-primary);
  }

  &--success {
    background: var(--gradient-success);
  }

  &--warning {
    background: linear-gradient(135deg, #f97316, #fb923c);
  }

  &--danger {
    background: linear-gradient(135deg, #ef4444, #f87171);
  }

  &--info {
    background: linear-gradient(135deg, #3b82f6, #60a5fa);
  }
}

// Skeleton Loading
.stat-card__skeleton {
  display: flex;
  gap: var(--spacing-lg);
  width: 100%;
}

.stat-card__skeleton-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  background: linear-gradient(
    90deg,
    var(--bg-tertiary) 25%,
    var(--bg-secondary) 50%,
    var(--bg-tertiary) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  flex-shrink: 0;
}

.stat-card__skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.stat-card__skeleton-title {
  width: 60%;
  height: 14px;
  border-radius: var(--radius-sm);
  background: linear-gradient(
    90deg,
    var(--bg-tertiary) 25%,
    var(--bg-secondary) 50%,
    var(--bg-tertiary) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.stat-card__skeleton-value {
  width: 40%;
  height: 30px;
  border-radius: var(--radius-sm);
  background: linear-gradient(
    90deg,
    var(--bg-tertiary) 25%,
    var(--bg-secondary) 50%,
    var(--bg-tertiary) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  animation-delay: 0.2s;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

// Reduced motion
@media (prefers-reduced-motion: reduce) {
  .stat-card {
    &:hover {
      transform: none;
    }

    &--clickable {
      &:active {
        transform: none;
      }
    }
  }

  .stat-card__skeleton-icon,
  .stat-card__skeleton-title,
  .stat-card__skeleton-value {
    animation: none;
  }
}

// Responsive
@media (max-width: 768px) {
  .stat-card {
    padding: var(--spacing-lg);
  }

  .stat-card__icon {
    width: 48px;
    height: 48px;
  }

  .stat-card__number {
    font-size: var(--font-size-2xl);
  }
}
</style>
