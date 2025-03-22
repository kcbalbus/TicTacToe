<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { Authenticator } from "@aws-amplify/ui-vue";
</script>

<template>
  <div id="app">
    <authenticator :sign-up-attributes="['email']">
      <router-view></router-view>
    </authenticator>
  </div>
</template>

<script>
</script>

<style scoped>

</style>
