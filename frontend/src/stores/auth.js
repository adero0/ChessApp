import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore
    = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null);
    const username = ref(localStorage.getItem('username') || null)

    const isLoggedIn = computed(() => !!token.value);

    function setUserData(newToken, newUsername) {
        token.value = newToken;
        username.value = newUsername
        if (newToken) {
            localStorage.setItem('token', newToken);
            localStorage.setItem('username', newUsername)
        } else {
            localStorage.removeItem('token');
            localStorage.removeItem('username')
        }
    }

    return {
        token,
        username,
        isLoggedIn,
        setUserData,
    };
});