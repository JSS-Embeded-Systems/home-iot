<script setup lang="ts">

defineProps({
  instance: String,
})
</script>

<template>
  <div class="neo-toggle-container">
    <input class="neo-toggle-input" :id="instance" type="checkbox" />
    <label class="neo-toggle" :for="instance">
      <div class="neo-track">
        <div class="neo-background-layer"></div>
        <div class="neo-grid-layer"></div>
        <div class="neo-spectrum-analyzer">
          <div class="neo-spectrum-bar"></div>
          <div class="neo-spectrum-bar"></div>
          <div class="neo-spectrum-bar"></div>
          <div class="neo-spectrum-bar"></div>
          <div class="neo-spectrum-bar"></div>
        </div>
        <div class="neo-track-highlight"></div>
      </div>

      <div class="neo-thumb">
        <div class="neo-thumb-ring"></div>
        <div class="neo-thumb-core">
          <div class="neo-thumb-icon">
            <div class="neo-thumb-wave"></div>
            <div class="neo-thumb-pulse"></div>
          </div>
        </div>
      </div>

      <div class="neo-gesture-area"></div>

      <div class="neo-interaction-feedback">
        <div class="neo-ripple"></div>
        <div class="neo-progress-arc"></div>
      </div>

      <div class="neo-status">
        <div class="neo-status-indicator">
          <div class="neo-status-dot"></div>
          <div class="neo-status-text"></div>
        </div>
      </div>
    </label>
  </div>
</template>

<style scoped>

</style>
