import React, { useState } from "react";
import ReactDOM from "react-dom/client";
import { GoogleOAuthProvider } from "@react-oauth/google";
import App from "./App.jsx";
import Login from "./pages/Login.jsx";
import "./index.css";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID || "";

function Root(){
    const [userId, setUserId] = useState(() => {
        const stored = localStorage.getItem("userId");
        return stored ? Number(stored) : null;
    });

    const handleLogin = (id) => {
        localStorage.setItem("userId", String(id));
        setUserId(Number(id));
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        setUserId(null);
    };

    if (!userId) return <Login onLogin={handleLogin} />;
    return <App userId={userId} onLogout={handleLogout} />;
}

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <Root />
    </GoogleOAuthProvider>
  </React.StrictMode>
);
