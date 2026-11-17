import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})

// Setting	                   Purpose
// /api	            Forwards API requests to Spring Boot
// /uploads	        Forwards car image requests to Spring Boot
// target           The backend address during local development
// changeOrigin	    Adjusts the request origin when forwarding