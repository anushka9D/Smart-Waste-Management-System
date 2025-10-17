import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';
import { AlertCircle, Trash2, MapPin, Clock, RefreshCw, Eye, CheckCircle, Plus } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

const API_BASE_URL = 'http://localhost:8080/api/v1';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

const createCustomIcon = (color) => {
  return new L.Icon({
    iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${color}.png`,
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });
};

const markerIcons = {
  RED: createCustomIcon('red'),
  BLUE: createCustomIcon('blue'),
  GREEN: createCustomIcon('green')
};

const api = {
  getAllBins: async () => {
    const response = await fetch(`${API_BASE_URL}/smartbin/all`);
    return response.json();
  },
  getFullBins: async () => {
    const response = await fetch(`${API_BASE_URL}/smartbin/status/full`);
    return response.json();
  },
  getEmptyBins: async () => {
    const response = await fetch(`${API_BASE_URL}/smartbin/status/empty`);
    return response.json();
  },
  getHalfFullBins: async () => {
    const response = await fetch(`${API_BASE_URL}/smartbin/status/half-full`);
    return response.json();
  },
  updateBinLevel: async (data) => {
    const response = await fetch(`${API_BASE_URL}/smartbin/update-level`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return response.json();
  },
  markAsCollected: async (binId) => {
    const response = await fetch(`${API_BASE_URL}/smartbin/${binId}/collected`, {
      method: 'PUT'
    });
    return response.json();
  },
  deleteBin: async (binId) => {
    await fetch(`${API_BASE_URL}/smartbin/${binId}`, {
      method: 'DELETE'
    });
  },
  getUnreviewedAlerts: async () => {
    const response = await fetch(`${API_BASE_URL}/alerts/unreviewed`);
    return response.json();
  },
  markAlertAsReviewed: async (binId) => {
    await fetch(`${API_BASE_URL}/alerts/${binId}/review`, {
      method: 'PUT'
    });
  }
};

function SensorManagerDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [bins, setBins] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState('all');
  const [notification, setNotification] = useState(null);

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 30000);
    return () => clearInterval(interval);
  }, [filterStatus]);

  const loadData = async () => {
    setLoading(true);
    try {
      let binsData;
      switch (filterStatus) {
        case 'full':
          binsData = await api.getFullBins();
          break;
        case 'empty':
          binsData = await api.getEmptyBins();
          break;
        case 'half':
          binsData = await api.getHalfFullBins();
          break;
        default:
          binsData = await api.getAllBins();
      }
      setBins(binsData);
      const alertsData = await api.getUnreviewedAlerts();
      setAlerts(alertsData);
    } catch (error) {
      showNotification('Error loading data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message, type = 'success') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 3000);
  };

  const handleUpdateLevel = async (binId, level) => {
    await api.updateBinLevel({ binId, currentLevel: level });
    showNotification('Bin level updated successfully');
    loadData();
  };

  const handleMarkCollected = async (binId) => {
    await api.markAsCollected(binId);
    showNotification('Bin marked as collected');
    loadData();
  };

  const handleDelete = async (binId) => {
    if (window.confirm('Are you sure you want to delete this bin?')) {
      await api.deleteBin(binId);
      showNotification('Bin deleted successfully');
      loadData();
    }
  };

  const handleMarkReviewed = async (binId) => {
    await api.markAlertAsReviewed(binId);
    showNotification('Alert marked as reviewed');
    loadData();
  };

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      {notification && (
        <div className={`fixed top-20 right-4 z-50 px-6 py-3 rounded-lg shadow-lg ${
          notification.type === 'success' ? 'bg-green-500' : 'bg-red-500'
        } text-white`}>
          {notification.message}
        </div>
      )}

      <main className="flex-grow bg-gray-50">
        <div className="container mx-auto px-4 py-6">
          {/* User Info Section */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold text-gray-800 mb-2">
                  Smart Bin Monitoring System
                </h1>
                <div className="text-sm text-gray-600">
                  <span className="font-medium">Logged in as:</span> {user?.name} ({user?.userType})
                </div>
              </div>
              <div className="flex items-center space-x-4">
                <button
                  onClick={loadData}
                  className="flex items-center space-x-2 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition"
                >
                  <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                  <span>Refresh</span>
                </button>
                {alerts.length > 0 && (
                  <div className="flex items-center space-x-2 px-4 py-2 bg-red-100 text-red-700 rounded-lg">
                    <AlertCircle className="w-4 h-4" />
                    <span className="font-semibold">{alerts.length} Alerts</span>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Map View Section */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Bin Locations Map</h2>
            <div className="h-96 rounded-lg overflow-hidden border-2 border-gray-200">
              <MapContainer
                center={[6.9271, 79.8612]} // Default Colombo
                zoom={13}
                style={{ height: '100%', width: '100%' }}
              >
                <TileLayer
                  attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {bins.map((bin) => (
                  <Marker
                    key={bin.binId}
                    position={[bin.coordinates.latitude, bin.coordinates.longitude]}
                    icon={markerIcons[bin.binColor] || markerIcons.GREEN}
                  >
                    <Popup>
                      <div className="p-2">
                        <h3 className="font-bold text-lg mb-2">{bin.binId}</h3>
                        <p className="text-sm text-gray-600 mb-1">
                          <strong>Location:</strong> {bin.location}
                        </p>
                        <p className="text-sm text-gray-600 mb-1">
                          <strong>Status:</strong> 
                          <span className={`ml-1 px-2 py-0.5 rounded text-xs ${
                            bin.status === 'FULL' ? 'bg-red-100 text-red-800' :
                            bin.status === 'HALF_FULL' ? 'bg-blue-100 text-blue-800' :
                            'bg-green-100 text-green-800'
                          }`}>
                            {bin.status.replace('_', ' ')}
                          </span>
                        </p>
                        <p className="text-sm text-gray-600 mb-1">
                          <strong>Fill Level:</strong> {((bin.currentLevel / bin.capacity) * 100).toFixed(0)}%
                        </p>
                        <p className="text-sm text-gray-600">
                          <strong>Capacity:</strong> {bin.currentLevel.toFixed(1)} / {bin.capacity} L
                        </p>
                      </div>
                    </Popup>
                  </Marker>
                ))}
              </MapContainer>
            </div>
            <div className="mt-4 flex items-center justify-center space-x-6 text-sm">
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                <span>Empty</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                <span>Half Full</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                <span>Full</span>
              </div>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="bg-white rounded-t-lg shadow-sm">
            <div className="flex space-x-8 px-6">
              {['dashboard', 'alerts'].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`py-4 px-2 border-b-2 font-medium text-sm transition ${
                    activeTab === tab
                      ? 'border-green-500 text-green-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  {tab.charAt(0).toUpperCase() + tab.slice(1)}
                </button>
              ))}
              <button
                onClick={() => navigate('/add-smart-bin')}
                className="ml-auto my-2 flex items-center space-x-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
              >
                <Plus className="w-4 h-4" />
                <span>Add New Bin</span>
              </button>
            </div>
          </div>

          {/* Content Area */}
          <div className="bg-white rounded-b-lg shadow-md p-6">
            {activeTab === 'dashboard' && (
              <DashboardView 
                bins={bins} 
                loading={loading}
                filterStatus={filterStatus}
                setFilterStatus={setFilterStatus}
                onUpdateLevel={handleUpdateLevel}
                onMarkCollected={handleMarkCollected}
                onDelete={handleDelete}
              />
            )}
            
            {activeTab === 'alerts' && (
              <AlertsView 
                alerts={alerts}
                onMarkReviewed={handleMarkReviewed}
              />
            )}
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

const DashboardView = ({ bins, loading, filterStatus, setFilterStatus, onUpdateLevel, onMarkCollected, onDelete }) => {
  const stats = {
    total: bins.length,
    full: bins.filter(b => b.status === 'FULL').length,
    halfFull: bins.filter(b => b.status === 'HALF_FULL').length,
    empty: bins.filter(b => b.status === 'EMPTY').length
  };

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard title="Total Bins" value={stats.total} color="blue" active={filterStatus === 'all'} onClick={() => setFilterStatus('all')} />
        <StatCard title="Full Bins" value={stats.full} color="red" active={filterStatus === 'full'} onClick={() => setFilterStatus('full')} />
        <StatCard title="Half Full" value={stats.halfFull} color="yellow" active={filterStatus === 'half'} onClick={() => setFilterStatus('half')} />
        <StatCard title="Empty Bins" value={stats.empty} color="green" active={filterStatus === 'empty'} onClick={() => setFilterStatus('empty')} />
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-12">
          <RefreshCw className="w-8 h-8 animate-spin text-green-600" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {bins.map(bin => (
            <BinCard 
              key={bin.binId} 
              bin={bin} 
              onUpdateLevel={onUpdateLevel}
              onMarkCollected={onMarkCollected}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}

      {bins.length === 0 && !loading && (
        <div className="text-center py-12 text-gray-500">
          No bins found for the selected filter
        </div>
      )}
    </div>
  );
};

const StatCard = ({ title, value, color, active, onClick }) => {
  const colorClasses = {
    blue: 'bg-blue-50 text-blue-600 border-blue-200',
    red: 'bg-red-50 text-red-600 border-red-200',
    yellow: 'bg-yellow-50 text-yellow-600 border-yellow-200',
    green: 'bg-green-50 text-green-600 border-green-200'
  };

  return (
    <div 
      onClick={onClick}
      className={`p-6 rounded-lg border-2 cursor-pointer transition ${
        active ? `${colorClasses[color]} shadow-md` : 'bg-white border-gray-200 hover:shadow-md'
      }`}
    >
      <div className="text-sm font-medium text-gray-600">{title}</div>
      <div className={`text-3xl font-bold mt-2 ${active ? '' : 'text-gray-900'}`}>{value}</div>
    </div>
  );
};

const BinCard = ({ bin, onMarkCollected, onDelete }) => {

  const getStatusColor = (status) => {
    switch (status) {
      case 'FULL': return 'bg-red-100 text-red-800 border-red-300';
      case 'HALF_FULL': return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'EMPTY': return 'bg-green-100 text-green-800 border-green-300';
      default: return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  const getColorIndicator = (color) => {
    switch (color) {
      case 'RED': return 'bg-red-500';
      case 'BLUE': return 'bg-blue-500';
      case 'GREEN': return 'bg-green-500';
      default: return 'bg-gray-500';
    }
  };

  const fillPercentage = (bin.currentLevel / bin.capacity) * 100;

  return (
    <>
      <div className="bg-white rounded-lg shadow border border-gray-200 overflow-hidden hover:shadow-lg transition">
        <div className="p-4 border-b bg-gray-50">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${getColorIndicator(bin.binColor)}`} />
              <h3 className="font-semibold text-gray-900">{bin.binId}</h3>
            </div>
            <span className={`px-3 py-1 rounded-full text-xs font-medium border ${getStatusColor(bin.status)}`}>
              {bin.status.replace('_', ' ')}
            </span>
          </div>
        </div>

        <div className="p-4 space-y-4">
          <div className="flex items-start space-x-2 text-sm">
            <MapPin className="w-4 h-4 text-gray-400 mt-0.5" />
            <div>
              <div className="text-gray-900 font-medium">{bin.location}</div>
              <div className="text-gray-500 text-xs">
                {bin.coordinates.latitude.toFixed(4)}, {bin.coordinates.longitude.toFixed(4)}
              </div>
            </div>
          </div>

          <div>
            <div className="flex justify-between text-sm mb-2">
              <span className="text-gray-600">Fill Level</span>
              <span className="font-semibold text-gray-900">
                {bin.currentLevel.toFixed(1)} / {bin.capacity} L ({fillPercentage.toFixed(0)}%)
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
              <div 
                className={`h-full transition-all duration-500 ${
                  fillPercentage >= 80 ? 'bg-red-500' :
                  fillPercentage >= 50 ? 'bg-blue-500' : 'bg-green-500'
                }`}
                style={{ width: `${Math.min(fillPercentage, 100)}%` }}
              />
            </div>
          </div>

          <div className="flex items-center space-x-2 text-sm text-gray-500">
            <Clock className="w-4 h-4" />
            <span>Last collected: {new Date(bin.lastCollected).toLocaleDateString()}</span>
          </div>
        </div>

        <div className="p-4 bg-gray-50 border-t flex gap-2">
          {bin.status === 'FULL' && (
            <button
              onClick={() => onMarkCollected(bin.binId)}
              className="flex-1 px-3 py-2 text-sm bg-green-600 text-white rounded hover:bg-green-700 transition"
            >
              Mark Collected
            </button>
          )}
          <button
            onClick={() => onDelete(bin.binId)}
            className="px-3 py-2 text-sm bg-red-600 text-white rounded hover:bg-red-700 transition"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </>
  );
};

const AlertsView = ({ alerts, onMarkReviewed }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Active Alerts</h2>
        <div className="px-4 py-2 bg-red-100 text-red-700 rounded-lg font-semibold">
          {alerts.length} Unreviewed
        </div>
      </div>

      {alerts.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border">
          <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">All Clear!</h3>
          <p className="text-gray-500">No pending alerts at this time</p>
        </div>
      ) : (
        <div className="space-y-3">
          {alerts.map(alert => (
            <div key={alert.alertId} className="bg-white rounded-lg shadow border border-red-200 p-4">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center space-x-2 mb-2">
                    <AlertCircle className="w-5 h-5 text-red-500" />
                    <h3 className="font-semibold text-gray-900">{alert.alertTitle}</h3>
                  </div>
                  <p className="text-gray-700 mb-2">{alert.message}</p>
                  <div className="flex items-center space-x-4 text-sm text-gray-500">
                    <span className="flex items-center space-x-1">
                      <MapPin className="w-4 h-4" />
                      <span>{alert.location}</span>
                    </span>
                    <span className="flex items-center space-x-1">
                      <Clock className="w-4 h-4" />
                      <span>{new Date(alert.createdAt).toLocaleString()}</span>
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => onMarkReviewed(alert.binId)}
                  className="ml-4 px-4 py-2 bg-green-600 text-white text-sm rounded hover:bg-green-700 transition flex items-center space-x-2"
                >
                  <Eye className="w-4 h-4" />
                  <span>Mark Reviewed</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default SensorManagerDashboard;