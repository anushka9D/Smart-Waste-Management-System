import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';
import { Plus, MapPin, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const API_BASE_URL = 'http://localhost:8080/api/v1';

function AddSmartBin() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    location: '',
    latitude: '',
    longitude: '',
    capacity: '100'
  });
  const [notification, setNotification] = useState(null);
  const [loading, setLoading] = useState(false);

  const showNotification = (message, type = 'success') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 3000);
  };

  const handleSubmit = async () => {
    if (!formData.location || !formData.latitude || !formData.longitude || !formData.capacity) {
      showNotification('Please fill in all required fields', 'error');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/smart-bins`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          location: formData.location,
          latitude: parseFloat(formData.latitude),
          longitude: parseFloat(formData.longitude),
          capacity: parseFloat(formData.capacity)
        })
      });

      if (response.ok) {
        showNotification('Smart bin created successfully!');
        setTimeout(() => {
          navigate('/smart-bin-monitoring');
        }, 1500);
      } else {
        showNotification('Failed to create smart bin', 'error');
      }
    } catch (error) {
      showNotification('Error creating smart bin: ' + error.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
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

      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          {/* Back Button */}
          <button
            onClick={() => navigate('/smart-bin-monitoring')}
            className="flex items-center space-x-2 text-gray-600 hover:text-gray-800 mb-6 transition"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>Back to Dashboard</span>
          </button>

          {/* User Info Card */}
          <div className="bg-indigo-50 border-l-4 border-indigo-500 p-6 mb-6 rounded-lg">
            <h2 className="text-xl font-semibold text-indigo-800 mb-4">
              Welcome, {user?.name}!
            </h2>
            
            <div className="space-y-2 text-gray-700">
              <p><span className="font-medium">Email:</span> {user?.email}</p>
              <p><span className="font-medium">Phone:</span> {user?.phone}</p>
              <p><span className="font-medium">User ID:</span> {user?.userId}</p>
              <p><span className="font-medium">Role:</span> {user?.userType}</p>
            </div>
          </div>

          {/* Main Form Card */}
          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="flex items-center space-x-3 mb-6">
              <Plus className="w-8 h-8 text-green-600" />
              <h1 className="text-3xl font-bold text-gray-800">Create New Smart Bin</h1>
            </div>

            <p className="text-gray-600 mb-8">
              Add a new smart waste bin to the monitoring system. Fill in the details below to register the bin and its sensor.
            </p>

            <div className="space-y-6">
              {/* Location Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Location <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <MapPin className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    type="text"
                    value={formData.location}
                    onChange={(e) => handleInputChange('location', e.target.value)}
                    placeholder="e.g., Main Street, Colombo"
                    className="w-full pl-10 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1">Enter the physical location of the bin</p>
              </div>

              {/* GPS Coordinates */}
              <div className="grid md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Latitude <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    step="any"
                    value={formData.latitude}
                    onChange={(e) => handleInputChange('latitude', e.target.value)}
                    placeholder="e.g., 6.9271"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  />
                  <p className="text-xs text-gray-500 mt-1">GPS latitude coordinate</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Longitude <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    step="any"
                    value={formData.longitude}
                    onChange={(e) => handleInputChange('longitude', e.target.value)}
                    placeholder="e.g., 79.8612"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  />
                  <p className="text-xs text-gray-500 mt-1">GPS longitude coordinate</p>
                </div>
              </div>

              {/* Capacity Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Capacity (Liters) <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  value={formData.capacity}
                  onChange={(e) => handleInputChange('capacity', e.target.value)}
                  placeholder="100"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  min="10"
                  max="500"
                />
                <p className="text-xs text-gray-500 mt-1">Bin capacity in liters (10-500)</p>
              </div>

              {/* Information Box */}
              <div className="bg-blue-50 border-l-4 border-blue-500 p-4 rounded">
                <div className="flex">
                  <div className="flex-shrink-0">
                    <svg className="h-5 w-5 text-blue-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="ml-3">
                    <h3 className="text-sm font-medium text-blue-800">What happens next?</h3>
                    <div className="mt-2 text-sm text-blue-700">
                      <ul className="list-disc list-inside space-y-1">
                        <li>A smart bin sensor will be automatically created</li>
                        <li>Initial fill level will be set to 0% (Empty)</li>
                        <li>Status will be marked as GREEN (Empty)</li>
                        <li>The bin will appear on the monitoring dashboard</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4 pt-4">
                <button
                  onClick={handleSubmit}
                  disabled={loading}
                  className={`flex-1 flex items-center justify-center space-x-2 px-6 py-3 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 transition ${
                    loading ? 'opacity-50 cursor-not-allowed' : ''
                  }`}
                >
                  {loading ? (
                    <>
                      <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      <span>Creating...</span>
                    </>
                  ) : (
                    <>
                      <Plus className="w-5 h-5" />
                      <span>Create Smart Bin</span>
                    </>
                  )}
                </button>

                <button
                  onClick={() => navigate('/smart-bin-monitoring')}
                  disabled={loading}
                  className="flex-1 px-6 py-3 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>

          {/* Feature Cards */}
          <div className="grid md:grid-cols-2 gap-6 mt-8">
            <div className="bg-green-50 p-6 rounded-lg border border-green-200">
              <h3 className="text-lg font-semibold text-green-800 mb-2">Real-Time Monitoring</h3>
              <p className="text-gray-600 text-sm">Track bin fill levels in real-time with IoT sensor integration</p>
            </div>

            <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
              <h3 className="text-lg font-semibold text-blue-800 mb-2">Smart Alerts</h3>
              <p className="text-gray-600 text-sm">Receive automatic notifications when bins reach capacity</p>
            </div>

            <div className="bg-yellow-50 p-6 rounded-lg border border-yellow-200">
              <h3 className="text-lg font-semibold text-yellow-800 mb-2">GPS Tracking</h3>
              <p className="text-gray-600 text-sm">Precise location mapping for efficient collection routes</p>
            </div>

            <div className="bg-purple-50 p-6 rounded-lg border border-purple-200">
              <h3 className="text-lg font-semibold text-purple-800 mb-2">Data Analytics</h3>
              <p className="text-gray-600 text-sm">Generate insights from collection patterns and usage data</p>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default AddSmartBin;