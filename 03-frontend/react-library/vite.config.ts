import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    https: {
      key: fs.readFileSync("ssl-localhost/localhost.key"),
      cert: fs.readFileSync("ssl-localhost/localhost.crt"),
     
    },
   
  }
})
