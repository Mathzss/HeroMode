import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api' // Onde o seu Spring Boot está ouvindo
});

export default api;