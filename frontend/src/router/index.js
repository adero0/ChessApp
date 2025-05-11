import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import Guest from '../components/Guest.vue';
import Play from '../components/Play.vue';
import Chat from '../components/Chat.vue';
import ChessBoard from "../components/ChessBoard.vue";
import NotFound from "../components/NotFound.vue";
import MainScreen from "../components/MainScreen.vue";
import PlayAI from "../components/PlayAI.vue";

const routes = [
    { path: '/', component: Guest },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/main', component: MainScreen, meta: { requiresAuth: true } },
    { path: '/play', component: Play, meta: { requiresAuth: true } },
    { path: '/chat', component: Chat, meta: { requiresAuth: true } },
    { path: '/game/bot_game', component: ChessBoard, meta: {requiresAuth: true } },
    { path: '/game', component: ChessBoard, meta: {requiresAuth: true } },
    { path: '/playAI', component: PlayAI, meta: {requiresAuth: true } },
    {
        path: '/:pathMatch(.*)*',
        component: NotFound,
        name: 'NotFound'
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const isLoggedIn = !!localStorage.getItem('token');
    if (to.meta.requiresAuth && !isLoggedIn) {
        next('/login');
    } else {
        next();
    }
});

export default router;