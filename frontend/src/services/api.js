import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

// Add request interceptor to include JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth APIs
export const registerCitizen = async (data) => {
  const response = await api.post('/auth/register/citizen', data);
  return response.data;
};

export const registerCityAuthority = async (data) => {
  const response = await api.post('/auth/register/city-authority', data);
  return response.data;
};

export const registerDriver = async (data) => {
  const response = await api.post('/auth/register/driver', data);
  return response.data;
};

export const registerWasteCollectionStaff = async (data) => {
  const response = await api.post('/auth/register/waste-collection-staff', data);
  return response.data;
};

export const registerSensorManager = async (data) => {
  const response = await api.post('/auth/register/sensor-manager', data);
  return response.data;
};

export const login = async (credentials) => {
  const response = await api.post('/auth/login', credentials);
  if (response.data.data?.token) {
    localStorage.setItem('token', response.data.data.token);
  }
  return response.data;
};

export const validateToken = async (token) => {
  try {
    const response = await api.get('/auth/validate', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data.data;
  } catch (error) {
    return false;
  }
};

// Waste Disposal APIs
export const createWasteRequest = async (formData) => {
  const response = await api.post('/citizen/waste-disposal-requests', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const getCitizenRequests = async (page = 0, size = 15) => {
  const response = await api.get(`/citizen/waste-disposal-requests?page=${page}&size=${size}`);
  return response.data;
};

export const getRequestDetails = async (requestId) => {
  const response = await api.get(`/citizen/waste-disposal-requests/${requestId}`);
  return response.data;
};

export const getRequestUpdates = async (requestId) => {
  const response = await api.get(`/citizen/waste-disposal-requests/${requestId}/updates`);
  return response.data;
};

export const cancelRequest = async (requestId) => {
  const response = await api.put(`/citizen/waste-disposal-requests/${requestId}/cancel`);
  return response.data;
};

// Logout
export const logout = () => {
  localStorage.removeItem('token');
};

export default api;