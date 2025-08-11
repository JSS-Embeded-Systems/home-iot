<script setup lang="ts">
import { ref } from 'vue';
import ToggleSwitch from '@/components/accessories/ToggleSwitch.vue'

defineProps({
  title: String,
})

const color = ref('00000');

function setColor(value: string) {
  color.value = value;
}
</script>

<template>
  <div>
    <h2>{{title}}</h2>
    <div class="flex flex-row">
      <div @click="setColor('ff0000')" class="h-6 w-6 rounded bg-red-600 m-2 cursor-pointer"></div>
      <div @click="setColor('f97316')" class="h-6 w-6 rounded bg-orange-400 m-2 cursor-pointer"></div>
      <div @click="setColor('4ade80')" class="h-6 w-6 rounded bg-green-400 m-2 cursor-pointer"></div>
      <div @click="setColor('22d3ee')" class="h-6 w-6 rounded bg-cyan-400 m-2 cursor-pointer"></div>
      <div @click="setColor('60a5fa')" class="h-6 w-6 rounded bg-blue-400 m-2 cursor-pointer"></div>
      <div @click="setColor('8b5cf6')" class="h-6 w-6 rounded bg-violet-600 m-2 cursor-pointer"></div>
    </div>
    <div class="flex items-center space-x-2 mb-2">
      <label>Color (hex): </label>
      <input v-model="color" class="max-w-20">
      <div :style="{ backgroundColor: '#' + color}" class="h-6 w-6 rounded m-2"></div>
    </div>
    <ToggleSwitch instance="bedroom-led" />
  </div>
</template>

<script lang="ts">

</script>
