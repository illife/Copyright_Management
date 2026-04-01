<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  document.title = '联盟链版权管理系统'
})
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
}
</style>
