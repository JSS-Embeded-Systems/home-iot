<script setup>

defineProps({
  msg: String,
})

</script>

<template>
  <div>
    <h1>{{msg}}</h1>
  </div>
</template>

<script>
export default {
  name: "Title.vue"
}
</script>

<style scoped>

</style>