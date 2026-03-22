import path from "path";
import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  optimizeDeps: {
    /*
    force: true,
    
    include: [
      "date-fns",
      "react-day-picker",
      "@radix-ui/react-popover",
      // anything else you constantly use
    ],*/
    exclude: ["vaul", "@radix-ui/react-popover"],
    //exclude: ["sonner", "shadcn"],
  },
});
