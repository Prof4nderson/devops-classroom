import { defineConfig } from 'vite';

export default defineConfig({
  define: {
    // Corrige o erro "global is not defined" do sockjs-client
    global: 'window',
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
      },
      '/ws': {
        target: 'http://127.0.0.1:8080',
        ws: true,
      },
    },
  },
});