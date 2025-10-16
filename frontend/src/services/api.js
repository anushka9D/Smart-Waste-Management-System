import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to automatically add the Authorization header
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log('API Request:', config.url, 'Token:', token ? 'Present' : 'Missing');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

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
  
  // If login is successful, store the token in localStorage
  if (response.data.success && response.data.data?.token) {
    localStorage.setItem('token', response.data.data.token);
  }
  
  return response.data;
};

// Get route preview
export const getRoutePreview = async () => {
  const response = await api.get('/routes/preview');
  return response.data;
};

// Get suitable trucks for a route
export const getSuitableTrucks = async (requiredCapacity) => {
  const response = await api.get(`/routes/suitable-trucks?requiredCapacity=${requiredCapacity}`);
  return response.data;
};

// Get available drivers
export const getAvailableDrivers = async () => {
  const response = await api.get('/routes/available-drivers');
  return response.data;
};

// Get available staff
export const getAvailableStaff = async () => {
  const response = await api.get('/routes/available-staff');
  return response.data;
};

// Create route with selected resources
export const createRouteWithResources = async (routeIndex, resources) => {
  const response = await api.post(`/routes/create-with-resources/${routeIndex}`, resources);
  return response.data;
};

// Get authenticated driver details
export const getAuthenticatedDriverDetails = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No authentication token found');
    }
    
    const response = await api.get('/routes/driver/details');
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch driver details');
  }
};

// Get routes assigned to the authenticated driver
export const getAuthenticatedDriverRoutes = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No authentication token found');
    }
    
    const response = await api.get('/routes/assigned/driver');
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch driver routes');
  }
};

// Get assigned route for a driver
export const getAssignedRouteForDriver = async (driverId) => {
  try {
    const response = await api.get(`/routes/assigned?driverId=${driverId}`);
    return response.data;
  } catch (error) {
    return { success: false, message: error.message };
  }
};

// Get route stops details
export const getRouteStops = async (stopIds) => {
  try {
    const response = await api.post('/routes/stops/by-ids', stopIds);
    if (response.data.success) {
      return response.data.data;
    } else {
      throw new Error(response.data.message || 'Failed to fetch route stops');
    }
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch route stops');
  }
};

// Validate Token
export const validateToken = async (token) => {
  try {
    const response = await api.get('/auth/validate');
    return response.data.data;
  } catch (error) {
    return false;
  }
};

export default api;