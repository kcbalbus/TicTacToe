<template>
  <div class="container mt-5">
    <div class="row justify-content-center align-items-center">
      <div class="col-md-4">
        <h2 class="mb-4 text-center">Tic Tac Toe</h2>
          <div class="mb-3">
            <h4 class="text-center">Hello {{auth.user.username}}</h4>
          </div>
        <button class="btn btn-primary w-100 mt-3" @click=usernameChosen>Play!</button>
        <button class="btn btn-primary w-100 mt-3" @click="auth.signOut">Sign out</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {useGameStore} from "@/stores/GameStore.js";
import router from "@/router/index.js";
import { Authenticator, useAuthenticator } from '@aws-amplify/ui-vue';

const gameStore = useGameStore();
const auth = useAuthenticator();

const usernameChosen = () => {
  gameStore.resetStore()
  gameStore.setUsername(auth.user.username)
  router.push("/game")
};


</script>

<style scoped>

</style>