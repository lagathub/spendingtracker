import axios from 'axios';

//Base URL config for my Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api/spending';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;