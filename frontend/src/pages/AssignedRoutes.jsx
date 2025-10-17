import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function AssignedRoutes() {
  const { user } = useAuth();
  const [assignedRoutes, setAssignedRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch assigned routes
  useEffect(() => {
    const fetchAssignedRoutes = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Use the correct endpoint for all assigned routes (no authentication required)
        const response = await fetch('http://localhost:8080/api/routes/assigned');
        
        const data = await response.json();
        
        if (data.success) {
          setAssignedRoutes(data.data || []);
        } else {
          setError(data.message || 'Failed to fetch assigned routes');
        }
      } catch (err) {
        setError('Failed to connect to server');
        console.error('Error fetching assigned routes:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAssignedRoutes();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        
        <main className="flex-grow bg-gray-50 py-12 px-4">
          <div className="container mx-auto max-w-4xl">
            <div className="bg-white rounded-lg shadow-md p-8">
              <h1 className="text-3xl font-bold text-gray-800 mb-6">Assigned Routes</h1>
              <p>Loading assigned routes...</p>
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
              <h1 className="text-3xl font-bold text-gray-800">Assigned Routes</h1>
              <Link 
                to="/city-authority-dashboard" 
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
              >
                Back to Route Planning
              </Link>
            </div>
            
            <div className="bg-blue-50 border-l-4 border-blue-500 p-6 mb-6">
              <h2 className="text-xl font-semibold text-blue-800 mb-4">
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
              </div>
            ) : assignedRoutes.length === 0 ? (
              <div className="bg-yellow-50 border-l-4 border-yellow-400 p-6">
                <h3 className="text-lg font-medium text-yellow-800 mb-2">No Assigned Routes</h3>
                <p className="text-yellow-700">
                  There are currently no assigned routes. Create new routes from the Route Planning page.
                </p>
                <Link 
                  to="/city-authority-dashboard" 
                  className="mt-4 inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                >
                  Go to Route Planning
                </Link>
              </div>
            ) : (
              <div className="space-y-6">
                {assignedRoutes.map((route, index) => (
                  <div key={route.routeId || index} className="border border-gray-200 rounded-lg p-6 shadow-sm">
                    <h3 className="text-xl font-semibold text-gray-800 mb-4">Route #{route.routeId?.substring(0, 8) || index + 1}</h3>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <h4 className="font-medium text-gray-700 mb-2">Route Details</h4>
                        <div className="space-y-2 text-sm">
                          <p><span className="font-medium">Date:</span> {route.date ? new Date(route.date).toLocaleString() : 'N/A'}</p>
                          <p><span className="font-medium">Status:</span> {route.status || 'N/A'}</p>
                          <p><span className="font-medium">Total Distance:</span> {route.totalDistance ? route.totalDistance.toFixed(2) + ' km' : 'N/A'}</p>
                          <p><span className="font-medium">Estimated Time:</span> {route.estimatedTime ? route.estimatedTime + ' minutes' : 'N/A'}</p>
                        </div>
                      </div>
                      
                      <div>
                        <h4 className="font-medium text-gray-700 mb-2">Assigned Resources</h4>
                        <div className="space-y-2 text-sm">
                          <p><span className="font-medium">Truck ID:</span> {route.assignedTruckId || 'Not assigned'}</p>
                          <p><span className="font-medium">Driver ID:</span> {route.assignedDriverId || 'Not assigned'}</p>
                          <p><span className="font-medium">Staff IDs:</span> {route.assignedStaffIds && route.assignedStaffIds.length > 0 ? route.assignedStaffIds.join(', ') : 'Not assigned'}</p>
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

export default AssignedRoutes;