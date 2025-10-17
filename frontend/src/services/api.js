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
    console.log('API Request:', config.method?.toUpperCase(), config.url, 'Token:', token ? 'Present' : 'Missing');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Authorization header set:', config.headers.Authorization);
    } else {
      console.log('No token found in localStorage');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to log responses
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.config.method?.toUpperCase(), response.config.url, response.status);
    return response;
  },
  (error) => {
    console.log('API Error:', error.config?.method?.toUpperCase(), error.config?.url, error.response?.status, error.message);
    return Promise.reject(error);
  }
);

// Register Citizen
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
  const response = await api.post('/auth/login', credentials);
  
  // If login is successful, store the token in localStorage
  if (response.data.success && response.data.data?.token) {
    console.log('Storing token in localStorage:', response.data.data.token.substring(0, 20) + '...');
    localStorage.setItem('token', response.data.data.token);
    // Also store in cookie for backup
    document.cookie = `jwt=${response.data.data.token}; path=/`;
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

// Get waste collection staff by ID
export const getWasteCollectionStaffById = async (staffId) => {
  try {
    const response = await api.get(`/waste-collection-staff/${staffId}`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch staff details');
  }
};

// Get routes assigned to the authenticated driver
export const getAuthenticatedDriverRoutes = async () => {
  try {
    const token = localStorage.getItem('token');
    console.log('Fetching driver routes with token:', token ? 'Present' : 'Missing');
    
    if (!token) {
      throw new Error('No authentication token found');
    }
    
    const response = await api.get('/routes/assigned/driver');
    console.log('Driver routes response:', response);
    return response.data;
  } catch (error) {
    console.error('Error fetching driver routes:', error.response || error);
    if (error.response && error.response.status === 401) {
      throw new Error('Unauthorized: Please log in again');
    }
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

// Mark route stop as completed
export const markRouteStopAsCompleted = async (stopId) => {
  try {
    const response = await api.put(`/routes/stops/${stopId}/complete`);
    return response.data;
  } catch (error) {
    console.error('API Error - markRouteStopAsCompleted:', error.response || error);
    if (error.response && error.response.status === 401) {
      throw new Error('Unauthorized: Please log in again');
    }
    throw new Error(error.response?.data?.message || error.message || 'Failed to mark route stop as completed');
  }
};

// Mark bin as collected (emptied)
export const markBinAsCollected = async (binId) => {
  try {
    const response = await api.put(`/v1/smartbin/${binId}/collect`);
    return response.data;
  } catch (error) {
    console.error('API Error - markBinAsCollected:', error.response || error);
    if (error.response && error.response.status === 401) {
      throw new Error('Unauthorized: Please log in again');
    }
    throw new Error(error.response?.data?.message || error.message || 'Failed to mark bin as collected');
  }
};

// Update route status
export const updateRouteStatus = async (routeId, status) => {
  try {
    console.log(`Calling updateRouteStatus with routeId: ${routeId}, status: ${status}`);
    const response = await api.put(`/routes/${routeId}/status?status=${status}`);
    console.log(`Route status updated successfully for routeId: ${routeId}`, response.data);
    return response.data;
  } catch (error) {
    console.error('API Error - updateRouteStatus:', error.response || error);
    if (error.response && error.response.status === 401) {
      throw new Error('Unauthorized: Please log in again');
    }
    throw new Error(error.response?.data?.message || error.message || 'Failed to update route status');
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