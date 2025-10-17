import React, { useState, useEffect } from 'react';
import { smartBinService } from '../../services/smartBinService';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';


delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

function Map() {
  const [bins, setBins] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedWasteType, setSelectedWasteType] = useState('all');
  const [mapCenter, setMapCenter] = useState([6.9271, 79.8612]); 
  const [mapZoom, setMapZoom] = useState(13);

  // Waste type colors for markers
  const wasteTypeColors = {
    'Plastic': '#3b82f6', // blue
    'Organic': '#10b981', // green
    'Metal': '#f59e0b',   // amber
    'Default': '#6b7280'  // gray
  };

  // Waste type icons
  const getMarkerIcon = (wasteType) => {
    const color = wasteTypeColors[wasteType] || wasteTypeColors.default;
    
    return L.divIcon({
      className: 'custom-icon',
      html: `<div style="background-color: ${color}; width: 24px; height: 24px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 5px rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 12px;">
        ${wasteType ? wasteType.charAt(0).toUpperCase() : 'B'}
      </div>`,
      iconSize: [24, 24],
      iconAnchor: [12, 12],
    });
  };

  useEffect(() => {
    fetchData();
  }, [selectedWasteType]);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (selectedWasteType === 'all') {
        response = await smartBinService.getMapBins();
      } else {
        response = await smartBinService.getMapBinsByWasteType(selectedWasteType);
      }
      
      setBins(response.data || []);
      
      
      if (response.data && response.data.length > 0 && response.data[0].latitude && response.data[0].longitude) {
        setMapCenter([response.data[0].latitude, response.data[0].longitude]);
      }
    } catch (err) {
      console.error('Error fetching map data:', err);
      setError('Failed to fetch map data');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    fetchData();
  };

  const getWasteTypeLabel = (wasteType) => {
    if (!wasteType) return 'Unknown';
    return wasteType.charAt(0).toUpperCase() + wasteType.slice(1);
  };

  const getFillLevelPercentage = (currentLevel, capacity) => {
    if (!capacity) return 0;
    return Math.min(100, Math.round((currentLevel / capacity) * 100));
  };

  const getFillLevelColor = (percentage) => {
    if (percentage >= 80) return 'text-red-600';
    if (percentage >= 60) return 'text-orange-500';
    if (percentage >= 40) return 'text-yellow-500';
    return 'text-green-500';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
        <strong className="font-bold">Error! </strong>
        <span className="block sm:inline">{error}</span>
        <button 
          onClick={handleRefresh}
          className="mt-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <h1 className="text-3xl font-bold text-gray-800">Smart Bin Map</h1>
        <div className="flex flex-wrap gap-3">
          <button 
            onClick={handleRefresh}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Refresh Data
          </button>
        </div>
      </div>
      
      {/* Filter Options */}
      <div className="bg-white rounded-lg shadow p-6 mb-8">
        <h2 className="text-xl font-bold text-gray-800 mb-4">Filter Options</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Waste Type Filter</label>
            <select
              value={selectedWasteType}
              onChange={(e) => setSelectedWasteType(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="all">All Waste Types</option>
              <option value="Plastic">Plastic</option>
              <option value="Organic">Organic</option>
              <option value="Metal">Metal</option>
            </select>
          </div>
        </div>
      </div>
      
      {/* Map Legend */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <h3 className="text-lg font-bold text-gray-800 mb-2">Map Legend</h3>
        <div className="flex flex-wrap gap-4">
          <div className="flex items-center">
            <div className="w-4 h-4 rounded-full bg-blue-500 mr-2"></div>
            <span>Plastic</span>
          </div>
          <div className="flex items-center">
            <div className="w-4 h-4 rounded-full bg-green-500 mr-2"></div>
            <span>Organic</span>
          </div>
          <div className="flex items-center">
            <div className="w-4 h-4 rounded-full bg-amber-500 mr-2"></div>
            <span>Metal</span>
          </div>
          <div className="flex items-center">
            <div className="w-4 h-4 rounded-full bg-gray-500 mr-2"></div>
            <span>Unknown</span>
          </div>
        </div>
      </div>
      
      {/* Map Container */}
      <div className="bg-white rounded-lg shadow p-4">
        {bins.length > 0 ? (
          <MapContainer 
            center={mapCenter} 
            zoom={mapZoom} 
            style={{ height: '600px', width: '100%' }}
            className="rounded-lg"
          >
            <TileLayer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            
            {bins.map((bin) => (
              <Marker
                key={bin.id}
                position={[bin.latitude, bin.longitude]}
                icon={getMarkerIcon(bin.wasteType)}
              >
                <Popup>
                  <div className="min-w-[200px]">
                    <h3 className="font-bold text-lg mb-2">{bin.location || 'Unknown Location'}</h3>
                    <div className="space-y-1">
                      <p><span className="font-medium">ID:</span> {bin.id}</p>
                      <p><span className="font-medium">Waste Type:</span> 
                        <span className="ml-1 px-2 py-1 rounded text-xs bg-gray-100">
                          {getWasteTypeLabel(bin.wasteType)}
                        </span>
                      </p>
                      <p><span className="font-medium">Status:</span> 
                        <span className="ml-1 px-2 py-1 rounded text-xs bg-gray-100">
                          {bin.status || 'Unknown'}
                        </span>
                      </p>
                      <p><span className="font-medium">Fill Level:</span> 
                        <span className={`ml-1 font-bold ${getFillLevelColor(getFillLevelPercentage(bin.currentLevel, bin.capacity))}`}>
                          {bin.currentLevel || 0}/{bin.capacity || 0} kg
                        </span>
                        <span className="block text-sm text-gray-600">
                          ({getFillLevelPercentage(bin.currentLevel, bin.capacity)}% full)
                        </span>
                      </p>
                    </div>
                  </div>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        ) : (
          <div className="flex flex-col items-center justify-center h-64 bg-gray-50 rounded-lg">
            <div className="text-gray-500 text-lg mb-2">No bins found</div>
            <div className="text-gray-400 text-sm">
              {selectedWasteType === 'all' 
                ? 'No smart bins have been added yet' 
                : `No bins found for ${selectedWasteType} waste type`}
            </div>
          </div>
        )}
        
        {/* Map Stats */}
        <div className="mt-4 flex justify-between items-center">
          <div className="text-gray-600">
            Showing {bins.length} smart bin{bins.length !== 1 ? 's' : ''}
          </div>
          <div className="text-sm text-gray-500">
            Click on markers for bin details
          </div>
        </div>
      </div>
    </div>
  );
}

export default Map;