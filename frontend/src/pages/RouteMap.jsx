import { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getRouteStops, markRouteStopAsCompleted, markBinAsCollected, isAuthenticated, updateRouteStatus } from '../services/api';
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
  const [currentStopIndex, setCurrentStopIndex] = useState(0);
  const [navigationStarted, setNavigationStarted] = useState(false);

  useEffect(() => {
    // Check if user is authenticated
    const checkAuth = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }
    };
    
    checkAuth();
    
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
  }, [location.state, routeId, navigate]);

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

  const startNavigation = () => {
    setNavigationStarted(true);
    setCurrentStopIndex(0);
  };

  const markBinAsCollectedHandler = async () => {
    if (!currentStop) return;
    
    try {
      const stopId = currentStop.stopId;
      const binId = currentStop.binId;
      
      console.log('Marking bin as collected:', binId, 'Stop ID:', stopId);
      
      // Mark the route stop as completed
      console.log('Calling markRouteStopAsCompleted with stopId:', stopId);
      await markRouteStopAsCompleted(stopId);
      
      // Mark the bin as collected (emptied)
      console.log('Calling markBinAsCollected with binId:', binId);
      await markBinAsCollected(binId);
      
      // Update the stop status to COMPLETED
      setStopsData(prevStops => 
        prevStops.map(stop => 
          stop.stopId === stopId ? { ...stop, status: 'COMPLETED', collectionTime: new Date().toISOString() } : stop
        )
      );
      
      // Check if all stops are completed and update route status if needed
      const updatedStops = stopsData.map(stop => 
        stop.stopId === stopId ? { ...stop, status: 'COMPLETED', collectionTime: new Date().toISOString() } : stop
      );
      
      const allStopsCompleted = updatedStops.every(stop => stop.status === 'COMPLETED');
      if (allStopsCompleted && routeData && routeData.routeId && routeData.status !== 'COMPLETED') {
        try {
          await updateRouteStatus(routeData.routeId, 'COMPLETED');
          // Update local route data
          setRouteData(prevRouteData => ({
            ...prevRouteData,
            status: 'COMPLETED'
          }));
          console.log('Route status updated to COMPLETED');
        } catch (err) {
          console.error('Failed to update route status:', err);
        }
      }
      
      // Move to the next stop if available
      if (currentStopIndex < sortedStops.length - 1) {
        setCurrentStopIndex(prevIndex => prevIndex + 1);
      }
      
      // Show a success message (in a real app, you might use a toast notification)
      console.log(`Bin ${binId} marked as collected and route stop completed`);
    } catch (err) {
      console.error('Error marking bin as collected:', err);
      
      // If it's an authentication error, redirect to login
      if (err.message && err.message.includes('Unauthorized')) {
        localStorage.removeItem('token');
        navigate('/login');
        return;
      }
      
      // Show an error message in a temporary banner instead of blocking the whole page
      const errorMessage = err.message || 'Please try again. You may need to log in again.';
      setError('Failed to mark bin as collected: ' + errorMessage);
      // Clear the error after 5 seconds
      setTimeout(() => {
        setError(null);
      }, 5000);
    }
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
            <div className="mt-4 flex space-x-4">
              <button
                onClick={handleBack}
                className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
              >
                Back to Dashboard
              </button>
              <button
                onClick={() => window.location.reload()}
                className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
              >
                Reload Page
              </button>
            </div>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  // Get current stop data
  const sortedStops = stopsData.sort((a, b) => a.sequenceOrder - b.sequenceOrder);
  const currentStop = sortedStops.length > 0 && navigationStarted ? sortedStops[currentStopIndex] : null;

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
                {/* Error banner */}
                {error && (
                  <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                    <strong className="font-bold">Error: </strong>
                    <span className="block sm:inline">{error}</span>
                    <button 
                      onClick={() => setError(null)} 
                      className="absolute top-0 bottom-0 right-0 px-4 py-3"
                    >
                      <span className="text-red-700">&times;</span>
                    </button>
                  </div>
                )}
                
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
                    {navigationStarted && (
                      <p><span className="font-medium">Current Stop:</span> {currentStop ? `#${currentStop.sequenceOrder} - ${currentStop.binId}` : 'N/A'}</p>
                    )}
                  </div>
                </div>
                
                {/* Navigation Controls */}
                <div className="mb-6">
                  {!navigationStarted ? (
                    <button
                      onClick={startNavigation}
                      className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
                    >
                      Start Navigation
                    </button>
                  ) : (
                    <div className="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-4">
                      <div className="bg-blue-100 text-blue-800 px-4 py-2 rounded-lg">
                        <span className="font-medium">Next Stop:</span> {currentStop ? `#${currentStop.sequenceOrder} - ${currentStop.binId}` : 'No more stops'}
                      </div>
                      {currentStop && currentStop.status !== 'COMPLETED' && (
                        <button
                          onClick={markBinAsCollectedHandler}
                          className="bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
                        >
                          Mark as Collected
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            )}

            <div className="mb-8">
              <h2 className="text-2xl font-semibold text-gray-800 mb-4">Route Map</h2>
              
              {/* Map Container with Route Visualization */}
              <div className="bg-gray-100 border-2 border-dashed border-gray-300 rounded-lg h-96 flex items-center justify-center relative">
                {navigationStarted && stopsData.length > 0 && currentStop && (
                  <div className="absolute inset-0">
                    {/* Simplified route visualization */}
                    <div className="absolute inset-0 flex items-center justify-center">
                      <div className="relative w-full h-full">
                        {/* Blue line showing the route to the current stop */}
                        <div className="absolute top-1/2 left-1/4 w-1/2 h-1 bg-blue-500 transform -translate-y-1/2"></div>
                        
                        {/* Depot marker */}
                        <div className="absolute top-1/2 left-1/4 transform -translate-x-1/2 -translate-y-1/2">
                          <div className="w-6 h-6 bg-gray-800 rounded-full flex items-center justify-center">
                            <span className="text-white text-xs font-bold">D</span>
                          </div>
                          <div className="text-xs text-center mt-1">Depot</div>
                        </div>
                        
                        {/* Current stop marker */}
                        <div 
                          className="absolute top-1/2 left-3/4 transform -translate-x-1/2 -translate-y-1/2"
                          style={{
                            left: `${75}%`,
                            top: `${50}%`
                          }}
                        >
                          <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center animate-pulse">
                            <span className="text-white text-xs font-bold">{currentStop.sequenceOrder}</span>
                          </div>
                          <div className="text-xs text-center mt-1 font-medium">
                            {currentStop.binId}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
                
                {!navigationStarted && (
                  <div className="text-center">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                    </svg>
                    <p className="mt-2 text-gray-500">Interactive map would be displayed here</p>
                    <p className="text-sm text-gray-400">Showing {stopsData.length} stops along the route</p>
                    {navigationStarted && (
                      <p className="text-sm text-blue-500 mt-2">Navigation started. Current stop: {currentStop ? `#${currentStop.sequenceOrder}` : 'N/A'}</p>
                    )}
                  </div>
                )}
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
                      <th className="py-2 px-4 border-b text-left">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sortedStops.map((stop, index) => (
                      <tr 
                        key={stop.stopId} 
                        className={`${index % 2 === 0 ? 'bg-white' : 'bg-gray-50'} ${stop.stopId === currentStop?.stopId ? 'ring-2 ring-blue-500' : ''}`}
                      >
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
                        <td className="py-2 px-4 border-b">
                          {navigationStarted && stop.stopId === currentStop?.stopId && stop.status !== 'COMPLETED' && (
                            <button
                              onClick={markBinAsCollectedHandler}
                              className="bg-green-500 hover:bg-green-600 text-white text-xs font-medium py-1 px-2 rounded transition duration-200 ease-in-out"
                            >
                              Mark Collected
                            </button>
                          )}
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