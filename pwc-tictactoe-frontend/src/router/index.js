import {createRouter, createWebHistory} from 'vue-router'
import Home from "../views/Home.vue";
import Game from "../views/TicTacToe.vue"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: Home
        },
        {
            path: '/game',
            name: 'game',
            component: Game
        }
    ]
})

export default router
