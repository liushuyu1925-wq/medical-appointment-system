import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    open: true,
    fs: {
      allow: [
        resolve(__dirname),
        resolve(__dirname, '../..')
      ]
    },
    proxy: {
      '/api': {
        target: 'http://localhost:1236', // 确保这里指向正确的后端服务地址，例如如果后端在8080端口，改为http://localhost:8080
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        silenceDeprecations: ['legacy-js-api']
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 1100,
    rollupOptions: {
      onwarn(warning, warn) {
        if (warning.code === 'INVALID_ANNOTATION' && warning.id?.includes('@vueuse/core')) {
          return
        }
        warn(warning)
      },
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          elementPlus: ['element-plus', '@element-plus/icons-vue']
        }
      }
    }
  },
  // 定义环境变量
  define: {
    'import.meta.env.VITE_API_BASE_URL': JSON.stringify('/api')
  }
}) 
