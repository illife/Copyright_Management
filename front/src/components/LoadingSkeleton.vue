<template>
  <div class="loading-skeleton" :class="[`loading-skeleton--${variant}`, { 'loading-skeleton--pulse': pulse }]">
    <!-- Card Skeleton -->
    <template v-if="variant === 'card'">
      <div class="skeleton-card">
        <div class="skeleton-card__header">
          <div class="skeleton-card__avatar"></div>
          <div class="skeleton-card__header-text">
            <div class="skeleton-card__title"></div>
            <div class="skeleton-card__subtitle"></div>
          </div>
        </div>
        <div class="skeleton-card__body">
          <div v-for="i in 3" :key="i" class="skeleton-card__line"></div>
        </div>
      </div>
    </template>

    <!-- Table Skeleton -->
    <template v-else-if="variant === 'table'">
      <div class="skeleton-table">
        <!-- Header -->
        <div class="skeleton-table__header">
          <div v-for="i in columns" :key="`header-${i}`" class="skeleton-table__header-cell"></div>
        </div>
        <!-- Rows -->
        <div v-for="row in rows" :key="`row-${row}`" class="skeleton-table__row">
          <div v-for="i in columns" :key="`cell-${row}-${i}`" class="skeleton-table__cell"></div>
        </div>
      </div>
    </template>

    <!-- List Skeleton -->
    <template v-else-if="variant === 'list'">
      <div class="skeleton-list">
        <div v-for="i in items" :key="i" class="skeleton-list__item">
          <div class="skeleton-list__avatar"></div>
          <div class="skeleton-list__content">
            <div class="skeleton-list__title"></div>
            <div class="skeleton-list__subtitle"></div>
          </div>
        </div>
      </div>
    </template>

    <!-- Stat Card Skeleton -->
    <template v-else-if="variant === 'stat'">
      <div class="skeleton-stat">
        <div class="skeleton-stat__icon"></div>
        <div class="skeleton-stat__content">
          <div class="skeleton-stat__label"></div>
          <div class="skeleton-stat__value"></div>
        </div>
      </div>
    </template>

    <!-- Default Skeleton -->
    <template v-else>
      <div class="skeleton-default">
        <div v-for="i in items" :key="i" class="skeleton-default__item" :style="{ width: getWidth(i) }"></div>
      </div>
    </template>
  </div>
</template>

<script setup>
const props = defineProps({
  // Skeleton variant
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'card', 'table', 'list', 'stat'].includes(value)
  },
  // Number of items/rows to show
  items: {
    type: Number,
    default: 3
  },
  // For table variant: number of columns
  columns: {
    type: Number,
    default: 5
  },
  // For table variant: number of rows
  rows: {
    type: Number,
    default: 5
  },
  // Pulse animation
  pulse: {
    type: Boolean,
    default: true
  }
})

const getWidth = (index) => {
  // Vary widths for more natural look
  const widths = ['100%', '85%', '92%', '78%', '95%']
  return widths[(index - 1) % widths.length]
}
</script>

<style lang="scss" scoped>
.loading-skeleton {
  --skeleton-base: var(--bg-tertiary);
  --skeleton-highlight: var(--bg-secondary);
}

// Base shimmer animation
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.skeleton-base {
  background: linear-gradient(
    90deg,
    var(--skeleton-base) 0%,
    var(--skeleton-highlight) 50%,
    var(--skeleton-base) 100%
  );
  background-size: 200% 100%;
  border-radius: var(--radius-md);

  .loading-skeleton--pulse & {
    animation: shimmer 1.5s ease-in-out infinite;
  }
}

// Default Skeleton
.skeleton-default {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);

  &__item {
    height: 16px;
    @extend .skeleton-base;
  }
}

// Card Skeleton
.skeleton-card {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);

  &__header {
    display: flex;
    gap: var(--spacing-lg);
    margin-bottom: var(--spacing-lg);
  }

  &__avatar {
    width: 48px;
    height: 48px;
    border-radius: var(--radius-full);
    @extend .skeleton-base;
    flex-shrink: 0;
  }

  &__header-text {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  &__title {
    height: 18px;
    width: 60%;
    @extend .skeleton-base;
  }

  &__subtitle {
    height: 14px;
    width: 40%;
    @extend .skeleton-base;
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
  }

  &__line {
    height: 14px;
    @extend .skeleton-base;

    &:nth-child(1) { width: 100%; }
    &:nth-child(2) { width: 85%; }
    &:nth-child(3) { width: 70%; }
  }
}

// Table Skeleton
.skeleton-table {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;

  &__header {
    display: grid;
    grid-template-columns: repeat(var(--columns, 5), 1fr);
    gap: var(--spacing-md);
    padding: var(--spacing-lg);
    background: var(--bg-secondary);
    border-bottom: 1px solid var(--border-primary);
  }

  &__header-cell {
    height: 16px;
    @extend .skeleton-base;
  }

  &__row {
    display: grid;
    grid-template-columns: repeat(var(--columns, 5), 1fr);
    gap: var(--spacing-md);
    padding: var(--spacing-lg);
    border-bottom: 1px solid var(--border-secondary);

    &:last-child {
      border-bottom: none;
    }
  }

  &__cell {
    height: 14px;
    @extend .skeleton-base;
  }
}

// List Skeleton
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);

  &__item {
    display: flex;
    gap: var(--spacing-lg);
    align-items: center;
    padding: var(--spacing-lg);
    background: var(--bg-card);
    border: 1px solid var(--border-primary);
    border-radius: var(--radius-lg);
  }

  &__avatar {
    width: 40px;
    height: 40px;
    border-radius: var(--radius-full);
    @extend .skeleton-base;
    flex-shrink: 0;
  }

  &__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  &__title {
    height: 16px;
    width: 60%;
    @extend .skeleton-base;
  }

  &__subtitle {
    height: 14px;
    width: 40%;
    @extend .skeleton-base;
  }
}

// Stat Card Skeleton
.skeleton-stat {
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
  padding: var(--spacing-xl);
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);

  &__icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-lg);
    @extend .skeleton-base;
    flex-shrink: 0;
  }

  &__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
  }

  &__label {
    height: 14px;
    width: 40%;
    @extend .skeleton-base;
  }

  &__value {
    height: 32px;
    width: 50%;
    @extend .skeleton-base;
  }
}

// Reduced motion support
@media (prefers-reduced-motion: reduce) {
  .loading-skeleton--pulse .skeleton-base {
    animation: none;
  }
}

// Responsive
@media (max-width: 768px) {
  .skeleton-table {
    &__header,
    &__row {
      grid-template-columns: repeat(3, 1fr);
    }
  }

  .skeleton-card {
    padding: var(--spacing-lg);
  }

  .skeleton-stat {
    padding: var(--spacing-lg);

    &__icon {
      width: 48px;
      height: 48px;
    }
  }
}
</style>
