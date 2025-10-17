import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});


export const smartBinService = {
  createSmartBin: (binData) => api.post('/bins', binData),
  getAllBins: () => api.get('v1/smartbin/all'),
  getBinById: (id) => api.get(`/bins/${id}`),
  updateBin: (id, binData) => api.put(`/bins/${id}`, binData),
  deleteBin: (id) => api.delete(`/bins/${id}`),
  
  // Analytics endpoints
  getDashboardData: () => api.get('/analytics/dashboard'),
  getWasteByLocation: () => api.get('/analytics/locations/waste'),
  getWasteByLocationFiltered: (wasteType) => {
    console.log(`Fetching filtered location data for waste type: ${wasteType}`);
    return api.get(`/analytics/locations/waste/filtered/${wasteType}`);
  },
  getBinStatus: () => api.get('/analytics/bins/status'),
  getTotalWaste: () => api.get('/analytics/waste/total'),
  getWasteByType: () => api.get('/analytics/waste/type'),
  getWasteByTypeFiltered: (wasteType) => {
    console.log(`Fetching filtered waste type data for waste type: ${wasteType}`);
    return api.get(`/analytics/waste/type/filtered/${wasteType}`);
  },
  getTotalPlasticWaste: () => api.get('/analytics/waste/plastic'),
  getTotalOrganicWaste: () => api.get('/analytics/waste/organic'),
  getTotalMetalWaste: () => api.get('/analytics/waste/metal'),
  getLocationWithMostWaste: () => api.get('/analytics/locations/most-waste'),
  
  // Chart data endpoints
  getLocationChartData: () => api.get('/analytics/charts/locations'),
  getStatusChartData: () => api.get('/analytics/charts/status'),
  getWasteTypeChartData: () => api.get('/analytics/charts/waste-type'),
  
  // Map data endpoints
  getMapBins: () => api.get('/map/bins'),
  getMapBinsByWasteType: (wasteType) => api.get(`/map/bins/waste-type/${wasteType}`),
  getMapBinsByStatus: (status) => api.get(`/map/bins/status/${status}`),
  getMapBinsByLocation: (location) => api.get(`/map/bins/location/${location}`),
  getMapBinsWithFilters: (filters) => {
    const params = {};
    if (filters.wasteType) params.wasteType = filters.wasteType;
    if (filters.status) params.status = filters.status;
    if (filters.location) params.location = filters.location;
    return api.get('/map/bins/filter', { params });
  },
  
};

export default smartBinService;