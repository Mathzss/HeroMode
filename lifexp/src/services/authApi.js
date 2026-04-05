import axios from "axios";

const authApi = axios.create({
    baseURL: import.meta.env.VITE_AUTH_URL || "http://localhost:8080"
});

export default authApi;