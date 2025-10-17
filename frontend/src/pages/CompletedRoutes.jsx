import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAuthenticatedDriverRoutes } from '../services/api';

function CompletedRoutes() {
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const [completedRoutes, setCompletedRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch completed routes for the driver
  useEffect(() => {
    const fetchCompletedRoutes = async () => {
      try {
        // Wait for auth context to be ready
        if (authLoading) {
          return;
        }
        
        // Check if user is authenticated
        if (!user) {
          setError('You must be logged in to view completed routes');
          setLoading(false);
          return;
        }
        
        setLoading(true);
        setError(null);
        
        // Fetch all routes for the authenticated driver
        const response = await getAuthenticatedDriverRoutes();
        
        if (response.success) {
          // Filter for completed routes
          const completed = (response.data || []).filter(route => route.status === 'COMPLETED');
          setCompletedRoutes(completed);
        } else {
          setError(response.message || 'Failed to fetch completed routes');
        }
      } catch (err) {
        console.error('Error fetching completed routes:', err);
        if (err.message && err.message.includes('401')) {
          setError('Authentication failed. Please log in again.');
        } else {
          setError(err.message || 'Failed to connect to server');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchCompletedRoutes();
  }, [user, authLoading]);

  const handleBackToDashboard = () => {
    navigate('/driver-dashboard');
  };

  if (authLoading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        
        <main className="flex-grow bg-gray-50 py-12 px-4">
          <div className="container mx-auto max-w-4xl">
            <div className="bg-white rounded-lg shadow-md p-8">
              <h1 className="text-3xl font-bold text-gray-800 mb-6">Completed Routes</h1>
              <p>Checking authentication...</p>
            </div>
          </div>
        </main>
        
        <AuthFooter />
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        
        <main className="flex-grow bg-gray-50 py-12 px-4">
          <div className="container mx-auto max-w-4xl">
            <div className="bg-white rounded-lg shadow-md p-8">
              <h1 className="text-3xl font-bold text-gray-800 mb-6">Completed Routes</h1>
              <p>Loading completed routes...</p>
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
              <h1 className="text-3xl font-bold text-gray-800">Completed Routes</h1>
              <button 
                onClick={handleBackToDashboard}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
              >
                Back to Dashboard
              </button>
            </div>
            
            <div className="bg-green-50 border-l-4 border-green-500 p-6 mb-6">
              <h2 className="text-xl font-semibold text-green-800 mb-4">
                Welcome, {user?.name}!
              </h2>
              
              <div className="space-y-2 text-gray-700">
                <p><span className="font-medium">Email:</span> {user?.email}</p>
                <p><span className="font-medium">Phone:</span> {user?.phone}</p>
                <p><span className="font-medium">User ID:</span> {user?.userId}</p>
                <p><span className="font-medium">Role:</span> {user?.userType}</p>
              </div>
            </div>

            {error ? (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
                <strong className="font-bold">Error: </strong>
                <span className="block sm:inline">{error}</span>
                {error.includes('Authentication failed') && (
                  <button 
                    onClick={() => navigate('/login')}
                    className="mt-2 inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                  >
                    Go to Login
                  </button>
                )}
              </div>
            ) : completedRoutes.length === 0 ? (
              <div className="bg-yellow-50 border-l-4 border-yellow-400 p-6">
                <h3 className="text-lg font-medium text-yellow-800 mb-2">No Completed Routes</h3>
                <p className="text-yellow-700">
                  You haven't completed any routes yet. Complete routes from your dashboard to see them here.
                </p>
                <button 
                  onClick={handleBackToDashboard}
                  className="mt-4 inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                  Go to Dashboard
                </button>
              </div>
            ) : (
              <div className="space-y-6">
                <div className="bg-blue-50 p-4 rounded-lg">
                  <p className="text-blue-800">
                    Showing {completedRoutes.length} completed route{completedRoutes.length !== 1 ? 's' : ''}
                  </p>
                </div>
                
                {completedRoutes.map((route, index) => (
                  <div key={route.routeId || index} className="border border-gray-200 rounded-lg p-6 shadow-sm">
                    <h3 className="text-xl font-semibold text-gray-800 mb-4">Route #{route.routeId?.substring(0, 8) || index + 1}</h3>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <h4 className="font-medium text-gray-700 mb-2">Route Details</h4>
                        <div className="space-y-2 text-sm">
                          <p><span className="font-medium">Date:</span> {route.date ? new Date(route.date).toLocaleString() : 'N/A'}</p>
                          <p><span className="font-medium">Status:</span> 
                            <span className="ml-2 px-2 py-1 rounded text-xs bg-green-100 text-green-800">
                              {route.status || 'COMPLETED'}
                            </span>
                          </p>
                          <p><span className="font-medium">Total Distance:</span> {route.totalDistance ? route.totalDistance.toFixed(2) + ' km' : 'N/A'}</p>
                          <p><span className="font-medium">Estimated Time:</span> {route.estimatedTime ? route.estimatedTime + ' minutes' : 'N/A'}</p>
                          <p><span className="font-medium">Completed At:</span> {route.completedAt ? new Date(route.completedAt).toLocaleString() : 'N/A'}</p>
                        </div>
                      </div>
                      
                      <div>
                        <h4 className="font-medium text-gray-700 mb-2">Assigned Resources</h4>
                        <div className="space-y-2 text-sm">
                          <p><span className="font-medium">Truck ID:</span> {route.assignedTruckId || 'Not assigned'}</p>
                          <p><span className="font-medium">Driver ID:</span> {route.assignedDriverId || 'Not assigned'}</p>
                          <p><span className="font-medium">Staff IDs:</span> {route.assignedStaffIds && route.assignedStaffIds.length > 0 ? route.assignedStaffIds.join(', ') : 'Not assigned'}</p>
                          <p><span className="font-medium">Number of Stops:</span> {route.stopIds ? route.stopIds.length : 'N/A'}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default CompletedRoutes;