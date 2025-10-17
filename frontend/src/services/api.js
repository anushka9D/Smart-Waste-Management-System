import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

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
  try {
    const response = await api.post('/auth/register/citizen', data);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const registerCityAuthority = async (data) => {
  try {
    const response = await api.post('/auth/register/city-authority', data);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const registerDriver = async (data) => {
  try {
    const response = await api.post('/auth/register/driver', data);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const registerWasteCollectionStaff = async (data) => {
  try {
    const response = await api.post('/auth/register/waste-collection-staff', data);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const registerSensorManager = async (data) => {
  try {
    const response = await api.post('/auth/register/sensor-manager', data);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const login = async (credentials) => {
  try {
    const response = await api.post('/auth/login', credentials);
    if (response.data && response.data.data && response.data.data.token) {
      localStorage.setItem('token', response.data.data.token);
    }
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
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
  try {
    const response = await api.post('/citizen/waste-disposal-requests', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const getCitizenRequests = async (page = 0, size = 15) => {
  try {
    const response = await api.get(`/citizen/waste-disposal-requests?page=${page}&size=${size}`);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const getRequestDetails = async (requestId) => {
  try {
    const response = await api.get(`/citizen/waste-disposal-requests/${requestId}`);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const getRequestUpdates = async (requestId) => {
  try {
    const response = await api.get(`/citizen/waste-disposal-requests/${requestId}/updates`);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

export const cancelRequest = async (requestId) => {
  try {
    const response = await api.put(`/citizen/waste-disposal-requests/${requestId}/cancel`);
    return response.data;
  } catch (error) {
    return error.response ? error.response.data : { success: false, message: 'Network error' };
  }
};

// Logout
export const logout = () => {
  localStorage.removeItem('token');
};

export default api;