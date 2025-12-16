import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import routes from './router';
import './styles.css';

const router = createRouter({
  history: createWebHistory(),
  routes,
});

createApp(App).use(router).mount('#app');

const sendBeacon = (data) => {
  const url = (import.meta.env.VITE_MONITOR_URL || '/monitor').replace(/\/$/, '');
  try {
    const payload = JSON.stringify({ ts: Date.now(), ...data });
    navigator.sendBeacon?.(url, payload);
  } catch (e) {
    console.info('monitor skipped', e);
  }
};

router.afterEach((to) => {
  sendBeacon({ event: 'pageview', path: to.fullPath });
});

window.addEventListener('error', (e) => {
  sendBeacon({ event: 'error', message: e?.message, source: e?.filename });
});

