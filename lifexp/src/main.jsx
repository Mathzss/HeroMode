import React, { useState } from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import Login from "./pages/Login.jsx";
import "./index.css";

function Root(){
    const [userId, setUserId] = useState(
        localStorage.getItem("userId")
            ? Number(localStorage.getItem("userId"))
            : null
    );

    const handleLogin = (id) => setUserId(Number(id));

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        setUserId(null);
    };

    if(!userId) return <login onLogin={handleLogin} />;
    return <App userId={userId} onLogout={handleLogout}/>

}

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
