import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Register Citizen
export const registerCitizen = async (data) => {
  const response = await api.post('/auth/register/citizen', data);
  return response.data;
};

// Register City Authority
export const registerCityAuthority = async (data) => {
  const response = await api.post('/auth/register/city-authority', data);
  return response.data;
};

// Register Driver
export const registerDriver = async (data) => {
  const response = await api.post('/auth/register/driver', data);
  return response.data;
};

// Register Waste Collection Staff
export const registerWasteCollectionStaff = async (data) => {
  const response = await api.post('/auth/register/waste-collection-staff', data);
  return response.data;
};

export const registerSensorManager = async (data) => {
  const response = await api.post('/auth/register/sensor-manager', data);
  return response.data;
};

// Login
export const login = async (credentials) => {
  const response = await api.post('/auth/login', credentials);
  return response.data;
};

// Validate Token
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

export default api;