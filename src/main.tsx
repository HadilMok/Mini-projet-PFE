import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { AuthProvider } from "./context/AuthProvider.tsx";
import { StepperProvider } from "./context/StepperProvider.tsx";
import { PopupProvider } from "./context/PopupProvider.tsx";
import StoreProvider from "./context/redux.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <StoreProvider>
      <AuthProvider>
        <PopupProvider>
          <StepperProvider>
            <App />
          </StepperProvider>
        </PopupProvider>
      </AuthProvider>
    </StoreProvider>
  </StrictMode>
);
