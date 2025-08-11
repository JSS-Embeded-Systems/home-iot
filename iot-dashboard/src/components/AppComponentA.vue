<script setup lang="ts">

import OnOffSwitch from './bedroom/OnOffSwitch.vue'
import RGBSelector from './bedroom/RGBSelector.vue'
</script>

<template>
  <div class="bg-slate-800 p-4 rounded shadow max-h-36 min-h-36 min-w-36">
    <OnOffSwitch title="bedroom lamp" />
  </div>
  <div class="bg-amber-100 p-4 rounded shadow max-h-fit min-w-40">
    <RGBSelector title="LED Bett" />
  </div>
  <div class="bg-amber-100 p-4 rounded shadow">Sleep sensor</div>
  <div class="bg-amber-100 p-4 rounded shadow">View 2 - D</div>
</template>

<style scoped>

</style>
