<script setup lang="ts">
import { PrimeIcons } from '@primevue/core/api';

  const links = [
    { name: 'Dashboard', icon: PrimeIcons.CHART_BAR, href: '/' },
    { name: 'Data', icon: PrimeIcons.COG, href: '/devices' },
    { name: 'Calibre', icon: PrimeIcons.BOOK, href: 'http://nilss-mac-mini:8083' },
    { name: 'Book Downloads', icon: PrimeIcons.DOWNLOAD, href: 'http://nilss-mac-mini:8084' },
  ]
</script>

<template>
  <aside class="h-screen w-64 bg-gray-900 text-white flex flex-col">
    <div class="text-2xl font-bold p-4 border-b border-gray-700">
      IoT Menu
    </div>
    <nav class="flex-1 p-2">
      <ul class="space-y-2">
        <li v-for="link in links" :key="link.name">
          <a
            :href="link.href"
            class="flex items-center space-x-2 p-2 rounded hover:bg-gray-700 transition"
          >
            <i :class=link.icon></i>
            <span>{{ link.name }}</span>
          </a>
        </li>
      </ul>
    </nav>
    <div class="p-4 border-t border-gray-700 text-sm text-gray-400">
      &copy; 2025 – IoT Control
    </div>
  </aside>
</template>

<style scoped>

</style>
