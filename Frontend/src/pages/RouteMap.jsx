import { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getRouteStops } from '../services/api';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';

function RouteMap() {
  const location = useLocation();
  const navigate = useNavigate();
  const { routeId } = useParams();
  const [routeData, setRouteData] = useState(null);
  const [stopsData, setStopsData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Get route data from location state or fetch it
    if (location.state?.routeData) {
      setRouteData(location.state.routeData);
      fetchStopsData(location.state.routeData.stopIds);
    } else if (routeId) {
      // If no route data in state, we would need to fetch it by routeId
      // For now, we'll just show an error
      setError('Route data not available');
      setLoading(false);
    } else {
      setError('No route data provided');
      setLoading(false);
    }
  }, [location.state, routeId]);

  const fetchStopsData = async (stopIds) => {
    try {
      setLoading(true);
      // Fetch the actual stop data from the backend
      const response = await getRouteStops(stopIds);
      setStopsData(response);
    } catch (err) {
      setError('Failed to fetch stops data: ' + err.message);
      console.error('Error fetching stops data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate('/driver-dashboard');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow flex items-center justify-center bg-gray-50 py-12 px-4">
          <div className="bg-white rounded-lg shadow-md p-8 max-w-4xl w-full">
            <p>Loading route map...</p>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow flex items-center justify-center bg-gray-50 py-12 px-4">
          <div className="bg-white rounded-lg shadow-md p-8 max-w-4xl w-full">
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
              <strong className="font-bold">Error: </strong>
              <span className="block sm:inline">{error}</span>
            </div>
            <div className="mt-4">
              <button
                onClick={handleBack}
                className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
              >
                Back to Dashboard
              </button>
            </div>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="flex justify-between items-center mb-6">
              <h1 className="text-3xl font-bold text-gray-800">Route Map</h1>
              <button
                onClick={handleBack}
                className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
              >
                Back to Dashboard
              </button>
            </div>

            {routeData && (
              <div className="mb-8">
                <h2 className="text-2xl font-semibold text-gray-800 mb-4">
                  Route #{routeData.routeId?.slice(-6) || 'N/A'}
                </h2>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                  <div className="bg-blue-50 p-4 rounded-lg">
                    <h3 className="font-medium text-blue-800 mb-2">Route Information</h3>
                    <p><span className="font-medium">Status:</span> {routeData.status || 'N/A'}</p>
                    <p><span className="font-medium">Truck:</span> {routeData.assignedTruckId || 'N/A'}</p>
                    <p><span className="font-medium">Date:</span> {routeData.date ? new Date(routeData.date).toLocaleString() : 'N/A'}</p>
                  </div>
                  
                  <div className="bg-green-50 p-4 rounded-lg">
                    <h3 className="font-medium text-green-800 mb-2">Route Metrics</h3>
                    <p><span className="font-medium">Total Distance:</span> {routeData.totalDistance ? routeData.totalDistance.toFixed(2) + ' km' : 'N/A'}</p>
                    <p><span className="font-medium">Estimated Time:</span> {routeData.estimatedTime ? routeData.estimatedTime + ' minutes' : 'N/A'}</p>
                    <p><span className="font-medium">Number of Stops:</span> {routeData.stopIds ? routeData.stopIds.length : 'N/A'}</p>
                  </div>
                  
                  <div className="bg-purple-50 p-4 rounded-lg">
                    <h3 className="font-medium text-purple-800 mb-2">Progress</h3>
                    <p><span className="font-medium">Completed Stops:</span> {stopsData.filter(stop => stop.status === 'COMPLETED').length}</p>
                    <p><span className="font-medium">Pending Stops:</span> {stopsData.filter(stop => stop.status === 'PENDING').length}</p>
                  </div>
                </div>
              </div>
            )}

            <div className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-800 mb-4">Route Map</h2>
              
              {/* Map Container */}
              <div className="bg-gray-100 border-2 border-dashed border-gray-300 rounded-lg h-96 flex items-center justify-center">
                <div className="text-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                  </svg>
                  <p className="mt-2 text-gray-500">Interactive map would be displayed here</p>
                  <p className="text-sm text-gray-400">Showing {stopsData.length} stops along the route</p>
                </div>
              </div>
            </div>

            <div>
              <h2 className="text-2xl font-semibold text-gray-800 mb-4">Stop Information</h2>
              
              <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-200">
                  <thead>
                    <tr className="bg-gray-100">
                      <th className="py-2 px-4 border-b text-left">Stop #</th>
                      <th className="py-2 px-4 border-b text-left">Bin ID</th>
                      <th className="py-2 px-4 border-b text-left">Location</th>
                      <th className="py-2 px-4 border-b text-left">Status</th>
                      <th className="py-2 px-4 border-b text-left">Collection Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stopsData
                      .sort((a, b) => a.sequenceOrder - b.sequenceOrder)
                      .map((stop, index) => (
                        <tr key={stop.stopId} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                          <td className="py-2 px-4 border-b">{stop.sequenceOrder}</td>
                          <td className="py-2 px-4 border-b">{stop.binId || 'N/A'}</td>
                          <td className="py-2 px-4 border-b">
                            {stop.coordinates ? 
                              `${stop.coordinates.latitude.toFixed(6)}, ${stop.coordinates.longitude.toFixed(6)}` : 
                              'N/A'}
                          </td>
                          <td className="py-2 px-4 border-b">
                            <span className={`px-2 py-1 rounded text-xs ${
                              stop.status === 'COMPLETED' ? 'bg-green-100 text-green-800' : 
                              stop.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' : 
                              'bg-gray-100 text-gray-800'
                            }`}>
                              {stop.status || 'N/A'}
                            </span>
                          </td>
                          <td className="py-2 px-4 border-b">
                            {stop.collectionTime ? new Date(stop.collectionTime).toLocaleTimeString() : 'N/A'}
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </main>
      <AuthFooter />
    </div>
  );
}

export default RouteMap;