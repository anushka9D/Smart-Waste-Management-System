import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';

function ReportBinRequest() {
  const [location, setLocation] = useState(null);
  const [address, setAddress] = useState('');
  const [loading, setLoading] = useState(false);
  const [mapLoading, setMapLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const mapRef = useRef(null);
  const markerRef = useRef(null);
  const mapInstanceRef = useRef(null);

  useEffect(() => {
    loadOpenStreetMap();
    getCurrentLocation();
  }, []);

  const loadOpenStreetMap = () => {
    if (window.L) {
      initializeMap();
      return;
    }

    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.css';
    document.head.appendChild(link);

    const script = document.createElement('script');
    script.src = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.js';
    script.onload = initializeMap;
    script.onerror = () => {
      setMapLoading(false);
      alert('Failed to load map. Please check your internet connection.');
    };
    document.head.appendChild(script);
  };

  const initializeMap = () => {
    if (!window.L) {
      setMapLoading(false);
      return;
    }

    try {
      // Default to Colombo coordinates
      const defaultLat = 6.9271;
      const defaultLng = 79.8612;

      const map = window.L.map(mapRef.current).setView([defaultLat, defaultLng], 13);

      window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(map);

      const marker = window.L.marker([defaultLat, defaultLng], {
        draggable: true
      }).addTo(map);

      markerRef.current = marker;
      mapInstanceRef.current = map;

      setLocation({ latitude: defaultLat, longitude: defaultLng });
      reverseGeocode(defaultLat, defaultLng);

      // Update location when marker is dragged
      marker.on('dragend', function() {
        const position = marker.getLatLng();
        setLocation({ latitude: position.lat, longitude: position.lng });
        reverseGeocode(position.lat, position.lng);
      });

      // Update marker on map click
      map.on('click', function(e) {
        marker.setLatLng(e.latlng);
        setLocation({ latitude: e.latlng.lat, longitude: e.latlng.lng });
        reverseGeocode(e.latlng.lat, e.latlng.lng);
      });

      setMapLoading(false);
    } catch (error) {
      console.error('Error initializing map:', error);
      setMapLoading(false);
    }
  };

  const getCurrentLocation = () => {
    if (!navigator.geolocation) {
      alert('Geolocation is not supported by your browser');
      return;
    }

    setLoading(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setLocation({ latitude, longitude });
        
        if (mapInstanceRef.current && markerRef.current) {
          mapInstanceRef.current.setView([latitude, longitude], 15);
          markerRef.current.setLatLng([latitude, longitude]);
        }
        
        reverseGeocode(latitude, longitude);
        setLoading(false);
      },
      (error) => {
        console.error('Error getting location:', error);
        alert('Unable to get your current location. Please select manually on the map.');
        setLoading(false);
      }
    );
  };

  const reverseGeocode = async (lat, lng) => {
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`
      );
      const data = await response.json();
      
      if (data && data.display_name) {
        setAddress(data.display_name);
      } else {
        setAddress('Address not found');
      }
    } catch (error) {
      console.error('Reverse geocoding error:', error);
      setAddress('Unable to fetch address');
    }
  };

  const searchLocation = async () => {
    if (!searchQuery.trim()) return;

    setLoading(true);
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(searchQuery)}&limit=1`
      );
      const data = await response.json();
      
      if (data && data.length > 0) {
        const { lat, lon, display_name } = data[0];
        const latitude = parseFloat(lat);
        const longitude = parseFloat(lon);
        
        setLocation({ latitude, longitude });
        setAddress(display_name);
        
        if (mapInstanceRef.current && markerRef.current) {
          mapInstanceRef.current.setView([latitude, longitude], 15);
          markerRef.current.setLatLng([latitude, longitude]);
        }
      } else {
        alert('Location not found. Please try a different search term.');
      }
    } catch (error) {
      console.error('Search error:', error);
      alert('Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleNext = () => {
    if (!location) {
      alert('Please select a location on the map');
      return;
    }

    localStorage.setItem('selectedLocation', JSON.stringify({
      latitude: location.latitude,
      longitude: location.longitude,
      address: address
    }));

    navigate('/citizen/report-bin-form');
  };

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-8 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Report Bin / Request Pickup</h1>
            
            {/* Location Controls */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
              <button
                onClick={getCurrentLocation}
                disabled={loading}
                className="flex items-center justify-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
              >
                {loading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Getting Location...
                  </>
                ) : (
                  '📍 Use Current Location'
                )}
              </button>
              
              <div className="flex gap-2">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && searchLocation()}
                  placeholder="Search for a location..."
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
                <button
                  onClick={searchLocation}
                  disabled={loading}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:bg-gray-400"
                >
                  Search
                </button>
              </div>
            </div>

            {/* Map Container */}
            <div className="mb-6">
              <div 
                ref={mapRef} 
                className="w-full h-70 rounded-lg border border-gray-300 relative"
              >
                {mapLoading && (
                  <div className="absolute inset-0 flex items-center justify-center bg-gray-100 rounded-lg">
                    <div className="text-center">
                      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mx-auto"></div>
                      <p className="mt-2 text-gray-600">Loading map...</p>
                    </div>
                  </div>
                )}
              </div>
              <p className="text-sm text-gray-500 mt-0">
                Click on the map or drag the marker to select exact location
              </p>
            </div>

            {/* Selected Location Display */}
            <div className="bg-gray-50 rounded-lg p-4 mb-3">
              <h3 className="font-medium text-gray-700 mb-2">Selected Location:</h3>
              {address ? (
                <div className="space-y-1">
                  <p className="text-gray-900 flex items-center">
                    <span className="mr-2">📍</span>
                    {address}
                  </p>
                  {location && (
                    <p className="text-sm text-gray-600">
                      Lat: {location.latitude.toFixed(4)}, Lng: {location.longitude.toFixed(4)}
                    </p>
                  )}
                </div>
              ) : (
                <p className="text-gray-500">No location selected</p>
              )}
            </div>

            {/* Action Buttons */}
            <div className="flex justify-between pt-1">
              <button
                onClick={() => navigate('/citizen-dashboard')}
                className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
              >
                Cancel
              </button>
              <button
                onClick={handleNext}
                disabled={!location}
                className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
              >
                Next →
              </button>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default ReportBinRequest;