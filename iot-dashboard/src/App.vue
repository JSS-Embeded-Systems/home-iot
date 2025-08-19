<script setup lang="ts">
  import AppContainer from './components/AppContainer.vue'
  import { onMounted } from 'vue'
  import AppComponentHome from './components/AppComponentHome.vue'
  import AppComponentA from './components/AppComponentA.vue'
  import AppComponentB from './components/AppComponentB.vue'
  import { useComponentSwitcher } from './stores/useComponentSwitcher.ts'
  import AppSidebar from './components/AppSidebar.vue'

  const { currentComponent, switchTo, init } = useComponentSwitcher();

  // Initialize with your components
  onMounted(() => {
    init({
      home: AppComponentA,
      dashboard: AppComponentHome,
      settings: AppComponentB
    });
  });
</script>

<template>
  <header>
    <div class="bg-gray-100 p-2">
      <nav class="bg-gray-900 text-white rounded px-6 py-4 flex items-center justify-between shadow">
        <div>
          <h1 class="text-xl font-bold">142</h1>
          <p  class="text-sm text-gray-400"></p>
        </div>
        <div class="space-x-4">
          <button @click="switchTo('home')" href="#" class="hover:underline">Home</button>
          <button @click="switchTo('dashboard')" href="#" class="hover:underline">Devices</button>
          <button @click="switchTo('settings')" href="#" class="hover:underline">Settings</button>
        </div>
      </nav>
    </div>
  </header>

  <main>
    <div class="w-screen flex-1 flex flex-row overflow-hidden">
      <div class="w-64 h-screen bg-gray-100 p-4">
        <AppSidebar />

      </div>
      <AppContainer class="flex-1">
        <component :is="currentComponent"/>
      </AppContainer>
    </div>
  </main>
</template>


<style scoped>
</style>
