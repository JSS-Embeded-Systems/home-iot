<script setup lang="ts">
import ToggleSwitch from '../accessories/ToggleSwitch.vue';
import { ref, onMounted } from 'vue';
import { setBedroomLamp } from '@/api/iotClient';

defineProps({
  title: String,
})

const isOn = ref(false);

async function fetchLampStatus() {
  try {
    const res = await fetch('/bedroom/lamp/status', {
      method: 'GET',
      cache: 'no-store',
    });
    if (!res.ok) {
      const body = await res.text().catch(() => '');
      console.error(`Status request failed (${res.status}): ${body || res.statusText}`);
      return;
    }
    const data = await res.json();
    isOn.value = !!data?.ison;
  } catch (e) {
    console.error('Failed to fetch lamp status:', e);
  }
}

onMounted(fetchLampStatus);

function normalizeToBoolean(input: any): boolean {
  if (typeof input === 'boolean') return input;
  if (input && typeof input === 'object') {
    // Vue change event or custom emits
    if (typeof (input.detail) === 'boolean') return input.detail;
    if (input.target && typeof input.target.checked === 'boolean') return input.target.checked;
    if (typeof (input.value) === 'boolean') return input.value;
    if (typeof (input.value) === 'string') return input.value.toLowerCase() === 'true';
  }
  if (typeof input === 'string') return input.toLowerCase() === 'true' || input === '1' || input === 'on';
  if (typeof input === 'number') return input !== 0;
  return !!input; // fallback
}

async function handleToggle(payload: any) {
  const isOnNow = normalizeToBoolean(payload);
  try {
    await setBedroomLamp(isOnNow ? 'on' : 'off');
    isOn.value = isOnNow;
  } catch (e) {
    console.error('Lamp request error:', e);
  }
}
</script>

<template>
  <div class="flex flex-col items-center">
    <h2 class="text-amber-50 pt-2 pb-4">{{title}}</h2>
    <div class="h-auto w-auto">
      <ToggleSwitch
        instance="bedroom-lamp"
        :model-value="isOn"
        @change="handleToggle"
        @update:modelValue="handleToggle"
      />
    </div>
  </div>
</template>
