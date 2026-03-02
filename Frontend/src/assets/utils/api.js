import axios from 'axios';

// Replace with your actual backend URL
const API_BASE_URL = 'http://localhost:8080/api';

export const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Attach token from localStorage automatically for every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      // Always attach with Bearer
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);


// Sets or clears the Authorization header for all subsequent API requests.
 
// Explicitly export this utility function
export const setAuthToken = (token) => {
    if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
        delete api.defaults.headers.common['Authorization'];
        delete axios.defaults.headers.common['Authorization'];
    }
};

// Generic request wrappers
// Explicitly export these request functions
export const get = (url, config = {}) => api.get(url, config);
export const post = (url, data, config = {}) => api.post(url, data, config);
export const put = (url, data, config = {}) => api.put(url, data, config);
export const del = (url, config = {}) => api.delete(url, config); 


// You can optionally keep the default export if other parts of your app use it:
export default api;