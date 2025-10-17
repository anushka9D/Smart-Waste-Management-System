import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAuthenticatedDriverDetails, getAuthenticatedDriverRoutes } from '../services/api';

function DriverDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [driverDetails, setDriverDetails] = useState(null);
  const [allRoutes, setAllRoutes] = useState([]); // Store all routes including completed ones
  const [assignedRoutes, setAssignedRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Fetch the driver's details and assigned routes
  const fetchDriverData = async (isRefresh = false) => {
    try {
      if (isRefresh) {
        setRefreshing(true);
      } else {
        setLoading(true);
      }
      setError(null);
      setSuccessMessage(null);
      
      // Check if we have a token first
      const token = localStorage.getItem('token');
      console.log('Token in fetchDriverData:', token ? 'Present' : 'Missing');
      
      if (!token) {
        setError('Authentication required. Please log in.');
        if (!isRefresh) setLoading(false);
        setRefreshing(false);
        return;
      }
      
      // Fetch driver details
      const driverResponse = await getAuthenticatedDriverDetails();
      console.log('Driver details response:', driverResponse);
      if (driverResponse.success) {
        setDriverDetails(driverResponse.data);
      } else {
        setError(driverResponse.message || 'Failed to fetch driver details');
      }
      
      // Fetch assigned routes
      const routesResponse = await getAuthenticatedDriverRoutes();
      console.log('Routes response:', routesResponse);
      if (routesResponse.success) {
        // Store all routes
        setAllRoutes(routesResponse.data || []);
        
        // Filter out completed routes from the display
        const activeRoutes = (routesResponse.data || []).filter(route => route.status !== 'COMPLETED');
        setAssignedRoutes(activeRoutes);
        
        // Show success message when refreshing
        if (isRefresh) {
          setSuccessMessage('Data refreshed successfully!');
          // Clear the success message after 3 seconds
          setTimeout(() => setSuccessMessage(null), 3000);
        }
      } else {
        setError(routesResponse.message || 'Failed to fetch assigned routes');
      }
    } catch (err) {
      console.error('Error fetching driver data:', err);
      setError('Failed to connect to server or authentication failed');
    } finally {
      if (isRefresh) {
        setRefreshing(false);
      } else {
        setLoading(false);
      }
    }
  };

  // Fetch the driver's details and assigned routes when component mounts
  useEffect(() => {
    fetchDriverData();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="flex justify-between items-center mb-6">
              <h1 className="text-3xl font-bold text-gray-800">Driver Dashboard</h1>
              <div className="flex space-x-2">
                <button 
                  onClick={() => navigate('/completed-routes')}
                  className="bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
                >
                  Completed Routes
                </button>
                <button 
                  onClick={() => fetchDriverData(true)}
                  disabled={refreshing}
                  className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg disabled:opacity-50 transition duration-200 ease-in-out flex items-center"
                >
                  {refreshing ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      Refreshing...
                    </>
                  ) : 'Refresh Data'}
                </button>
              </div>
            </div>
            
            {loading ? (
              <div className="bg-gray-50 p-6 rounded-lg">
                <p>Loading your information...</p>
              </div>
            ) : error ? (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
                <strong className="font-bold">Error: </strong>
                <span className="block sm:inline">{error}</span>
              </div>
            ) : successMessage ? (
              <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative" role="alert">
                <span className="block sm:inline">{successMessage}</span>
              </div>
            ) : (
              <>
                {/* Driver Details Section */}
                <div className="bg-orange-50 border-l-4 border-orange-500 p-6 mb-6">
                  <h2 className="text-xl font-semibold text-orange-800 mb-4">
                    Welcome, {driverDetails?.name || user?.name}!
                  </h2>
                  
                  <div className="space-y-2 text-gray-700">
                    <p><span className="font-medium">Name:</span> {driverDetails?.name || 'N/A'}</p>
                    <p><span className="font-medium">Email:</span> {driverDetails?.email || user?.email || 'N/A'}</p>
                    <p><span className="font-medium">Phone:</span> {driverDetails?.phone || user?.phone || 'N/A'}</p>
                    <p><span className="font-medium">User ID:</span> {driverDetails?.userId || user?.userId || 'N/A'}</p>
                    <p><span className="font-medium">License Number:</span> {driverDetails?.licenseNumber || 'N/A'}</p>
                    <p><span className="font-medium">Vehicle Type:</span> {driverDetails?.vehicleType || 'N/A'}</p>
                    <p><span className="font-medium">Availability:</span> 
                      <span className={driverDetails?.availability !== undefined ? 
                        (driverDetails.availability ? ' text-green-600' : ' text-red-600') : ''}>
                        {driverDetails?.availability !== undefined ? 
                          (driverDetails.availability ? ' Available' : ' Unavailable') : ' N/A'}
                      </span>
                    </p>
                  </div>
                </div>

                {/* Completed Routes Summary */}
                <div className="bg-green-50 border-l-4 border-green-500 p-6 mb-6">
                  <div className="flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-green-800">Your Completed Routes</h2>
                    <button 
                      onClick={() => navigate('/completed-routes')}
                      className="bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
                    >
                      View All Completed Routes
                    </button>
                  </div>
                  <p className="mt-2 text-green-700">
                    You have completed {allRoutes.filter(route => route.status === 'COMPLETED').length} route(s) so far.
                  </p>
                </div>

                {/* Assigned Routes Section */}
                <div className="bg-blue-50 border-l-4 border-blue-500 p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-semibold text-blue-800">Your Assigned Routes</h2>
                    <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                      {assignedRoutes?.length || 0} route{assignedRoutes?.length !== 1 ? 's' : ''}
                    </span>
                  </div>
                  
                  {assignedRoutes && assignedRoutes.length > 0 ? (
                    <div className="space-y-4">
                      {assignedRoutes.map((route, index) => (
                        <div key={route.routeId || route._id || index} className="bg-white p-4 rounded-lg shadow">
                          <h3 className="font-medium text-lg text-gray-800 mb-2">Route #{index + 1}</h3>
                          
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                              <h4 className="font-medium text-gray-700 mb-2">Route Information</h4>
                              <div className="space-y-1 text-sm">
                                <p><span className="font-medium">Route ID:</span> {route.routeId || route._id || 'N/A'}</p>
                                <p><span className="font-medium">Date:</span> {route.date ? new Date(route.date).toLocaleString() : 'N/A'}</p>
                                <p><span className="font-medium">Status:</span> 
                                  <span className={`ml-2 px-2 py-1 rounded text-xs ${
                                    route.status === 'ASSIGNED' ? 'bg-green-100 text-green-800' :
                                    route.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                                    route.status === 'COMPLETED' ? 'bg-blue-100 text-blue-800' :
                                    'bg-gray-100 text-gray-800'
                                  }`}>
                                    {route.status || 'N/A'}
                                  </span>
                                </p>
                                <p><span className="font-medium">Truck:</span> {route.assignedTruckId || 'N/A'}</p>
                              </div>
                            </div>
                            
                            <div>
                              <h4 className="font-medium text-gray-700 mb-2">Route Metrics</h4>
                              <div className="space-y-1 text-sm">
                                <p><span className="font-medium">Total Distance:</span> {route.totalDistance ? route.totalDistance.toFixed(2) + ' km' : 'N/A'}</p>
                                <p><span className="font-medium">Estimated Time:</span> {route.estimatedTime ? route.estimatedTime + ' minutes' : 'N/A'}</p>
                                <p><span className="font-medium">Number of Stops:</span> {route.stopIds ? route.stopIds.length : 'N/A'}</p>
                              </div>
                              
                              {/* View on Map Button */}
                              <button
                                onClick={() => navigate(`/route-map/${route.routeId || route._id}`, { 
                                  state: { routeData: route } 
                                })}
                                className="mt-4 w-full bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 ease-in-out"
                              >
                                View Route on Map
                              </button>
                            </div>
                          </div>
                          
                          {/* Staff Information */}
                          {route.assignedStaffIds && route.assignedStaffIds.length > 0 && (
                            <div className="mt-4">
                              <h4 className="font-medium text-gray-700 mb-2">Assigned Staff</h4>
                              <div className="flex flex-wrap gap-2">
                                {route.assignedStaffIds.map((staffId, staffIndex) => (
                                  <span key={staffIndex} className="bg-gray-200 text-gray-800 px-2 py-1 rounded text-xs">
                                    {staffId}
                                  </span>
                                ))}
                              </div>
                            </div>
                          )}
                        </div>
                      ))}
                      
                      {/* Show message if all routes are completed */}
                      {assignedRoutes.length === 0 && allRoutes.some(route => route.status === 'COMPLETED') && (
                        <div className="bg-green-50 border border-green-200 p-4 rounded">
                          <p className="text-green-700">
                            All your routes have been completed! Great job!
                          </p>
                          <button
                            onClick={() => navigate('/completed-routes')}
                            className="mt-2 text-green-800 hover:text-green-900 font-medium"
                          >
                            View your completed routes
                          </button>
                        </div>
                      )}
                    </div>
                  ) : allRoutes.some(route => route.status === 'COMPLETED') ? (
                    <div className="bg-green-50 border border-green-200 p-4 rounded">
                      <p className="text-green-700">
                        All your routes have been completed! Great job!
                      </p>
                      <button
                        onClick={() => navigate('/completed-routes')}
                        className="mt-2 text-green-800 hover:text-green-900 font-medium"
                      >
                        View your completed routes
                      </button>
                    </div>
                  ) : (
                    <div className="bg-yellow-50 border border-yellow-200 p-4 rounded">
                      <p className="text-yellow-700">
                        You don't have any assigned routes at the moment. Please check back later or contact your supervisor.
                      </p>
                      <p className="text-yellow-600 text-sm mt-2">
                        Note: Routes are typically assigned by city authorities. If you believe this is an error, please contact your supervisor.
                      </p>
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default DriverDashboard;