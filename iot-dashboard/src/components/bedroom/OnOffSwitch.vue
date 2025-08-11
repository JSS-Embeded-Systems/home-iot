<script setup lang="ts">
import ToggleSwitch from '../accessories/ToggleSwitch.vue';

defineProps({
  title: String,
})
</script>

<template>
  <div class="flex flex-col items-center">
    <h2 class="text-amber-50 pt-2 pb-4">{{title}}</h2>
    <div class="h-auto w-auto">
      <ToggleSwitch instance="bedroom-lamp"/>
    </div>
  </div>
</template>
