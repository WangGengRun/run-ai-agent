import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

const API_BASE = process.env.VITE_API_BASE || 'http://localhost:8123/api';

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: API_BASE,
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, ''),
      },
    },
  },
});

