<script setup lang="ts">
  const links = [
    { name: 'Dashboard', icon: '🏠', href: '/' },
    { name: 'Devices', icon: '🔌', href: '/devices' },
    { name: 'Network', icon: '🌐', href: '/network' },
    { name: 'Settings', icon: '⚙️', href: '/settings' },
  ]
</script>

<template>
  <aside class="h-screen w-64 bg-gray-900 text-white flex flex-col">
    <div class="text-2xl font-bold p-4 border-b border-gray-700">
      IoT Dashboard
    </div>
    <nav class="flex-1 p-2">
      <ul class="space-y-2">
        <li v-for="link in links" :key="link.name">
          <a
            :href="link.href"
            class="flex items-center space-x-2 p-2 rounded hover:bg-gray-700 transition"
          >
            <span>{{ link.icon }}</span>
            <span>{{ link.name }}</span>
          </a>
        </li>
      </ul>
    </nav>
    <div class="p-4 border-t border-gray-700 text-sm text-gray-400">
      &copy; 2025 – Nils' IoT Control
    </div>
  </aside>
</template>

<style scoped>

</style>
