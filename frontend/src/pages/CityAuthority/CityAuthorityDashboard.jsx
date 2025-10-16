import { useAuth } from '../../context/AuthContext';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';
import { useState, useEffect } from 'react';
import { getRoutePreview, getSuitableTrucks, getAvailableDrivers, getAvailableStaff, createRouteWithResources } from '../../services/api';
import { Link } from 'react-router-dom';

function CityAuthorityDashboard() {
  const { user } = useAuth();
  const [routePreview, setRoutePreview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [suitableTrucks, setSuitableTrucks] = useState({});
  const [availableDrivers, setAvailableDrivers] = useState([]);
  const [availableStaff, setAvailableStaff] = useState([]);
  const [selectedResources, setSelectedResources] = useState({});

  // Fetch route preview data and available resources
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null); // Clear any previous errors
        
        // Fetch route preview
        const response = await getRoutePreview();
        
        if (response.success) {
          setRoutePreview(response.data);
          
          // Fetch suitable trucks for each route only if there are routes
          if (response.data.routes && response.data.routes.length > 0) {
            const trucksData = {};
            for (const route of response.data.routes) {
              if (route.totalCapacity) {
                try {
                  const truckResponse = await getSuitableTrucks(route.totalCapacity);
                  if (truckResponse.success) {
                    trucksData[route.routeId] = truckResponse.data;
                  }
                } catch (err) {
                  console.error(`Error fetching trucks for route ${route.routeId}:`, err);
                }
              }
            }
            setSuitableTrucks(trucksData);
          }
        } else {
          // Even if response is not successful, we might still want to show the UI
          // Set empty data to allow the UI to render properly
          setRoutePreview({ routes: [], totalRoutes: 0 });
        }
        
        // Fetch available drivers
        try {
          const driversResponse = await getAvailableDrivers();
          if (driversResponse.success) {
            setAvailableDrivers(driversResponse.data);
          }
        } catch (err) {
          console.error('Error fetching drivers:', err);
        }
        
        // Fetch available staff
        try {
          const staffResponse = await getAvailableStaff();
          if (staffResponse.success) {
            setAvailableStaff(staffResponse.data);
          }
        } catch (err) {
          console.error('Error fetching staff:', err);
        }
      } catch (err) {
        console.error('Error fetching data:', err);
        // Set empty data to allow the UI to render properly even when there's an error
        setRoutePreview({ routes: [], totalRoutes: 0 });
        setError('Failed to connect to server. Showing available resources only.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleResourceChange = (routeId, resourceType, value) => {
    setSelectedResources(prev => ({
      ...prev,
      [routeId]: {
        ...prev[routeId],
        [resourceType]: value
      }
    }));
  };

  const handleCreateRoute = async (routeIndex, routeId) => {
    try {
      const resources = selectedResources[routeId] || {};
      
      // Validate that required resources are selected
      if (!resources.truckId || !resources.driverId) {
        alert('Please select both a truck and a driver');
        return;
      }
      
      const response = await createRouteWithResources(routeIndex, resources);
      
      if (response.success) {
        alert('Route created successfully!');
        // Refresh the route preview
        window.location.reload();
      } else {
        alert('Failed to create route: ' + response.message);
      }
    } catch (err) {
      console.error('Error creating route:', err);
      alert('Failed to create route. Please try again.');
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">City Authority Dashboard</h1>
            
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

            <div className="grid md:grid-cols-2 gap-6 mb-8">
              <div className="bg-green-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-green-800 mb-2">Analytics</h3>
                <p className="text-gray-600 text-sm">View waste management analytics</p>
              </div>

              <div className="bg-orange-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-orange-800 mb-2">Reports</h3>
                <p className="text-gray-600 text-sm">Review citizen reports and feedback</p>
              </div>

              <div className="bg-purple-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-purple-800 mb-2">Staff Management</h3>
                <p className="text-gray-600 text-sm">Manage collection staff and drivers</p>
              </div>

              <div className="bg-red-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-red-800 mb-2">Route Planning</h3>
                <p className="text-gray-600 text-sm">Optimize collection routes</p>
              </div>
            </div>

            {/* Route Preview Section */}
            <div className="mt-8">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold text-gray-800">Route Preview</h2>
                <Link 
                  to="/assigned-routes" 
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 text-sm"
                >
                  View Assigned Routes
                </Link>
              </div>

              {loading ? (
                <div className="bg-gray-50 p-6 rounded-lg">
                  <p>Loading route preview...</p>
                </div>
              ) : error ? (
                <div className="bg-yellow-50 border border-yellow-400 text-yellow-700 px-4 py-3 rounded relative" role="alert">
                  <strong className="font-bold">Notice: </strong>
                  <span className="block sm:inline">{error}</span>
                </div>
              ) : routePreview && routePreview.routes && routePreview.routes.length > 0 ? (
                <div className="space-y-6">
                  {routePreview.routes.map((route, index) => (
                    <div key={route.routeId || index} className="border border-gray-200 rounded-lg p-6 shadow-sm">
                      <h3 className="text-xl font-semibold text-gray-800 mb-4">Route #{index + 1}</h3>
                      
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                          <h4 className="font-medium text-gray-700 mb-2">Route Details</h4>
                          <div className="space-y-2 text-sm">
                            <p><span className="font-medium">Status:</span> {route.status || 'N/A'}</p>
                            <p><span className="font-medium">Total Distance:</span> {route.totalDistance ? route.totalDistance.toFixed(2) + ' km' : 'N/A'}</p>
                            <p><span className="font-medium">Estimated Time:</span> {route.estimatedTime ? route.estimatedTime + ' minutes' : 'N/A'}</p>
                            <p><span className="font-medium">Total Capacity:</span> {route.totalCapacity ? route.totalCapacity.toFixed(2) + ' liters' : 'N/A'}</p>
                            <p><span className="font-medium">Bins to Collect:</span> {route.binIds ? route.binIds.length : 'N/A'}</p>
                          </div>
                        </div>
                        
                        <div>
                          <h4 className="font-medium text-gray-700 mb-2">Proposed Resources</h4>
                          <div className="space-y-3 text-sm">
                            <div>
                              <p className="font-medium">Truck:</p>
                              {suitableTrucks[route.routeId] && suitableTrucks[route.routeId].length > 0 ? (
                                <select 
                                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm"
                                  onChange={(e) => handleResourceChange(route.routeId, 'truckId', e.target.value)}
                                  value={selectedResources[route.routeId]?.truckId || ''}
                                >
                                  <option value="">Select a truck</option>
                                  {suitableTrucks[route.routeId].map((truck) => (
                                    <option key={truck.truckId} value={truck.truckId}>
                                      {truck.registrationNumber} (Capacity: {truck.capacity} liters, Status: {truck.currentStatus})
                                    </option>
                                  ))}
                                </select>
                              ) : (
                                <p className="text-gray-500 mt-1">
                                  No suitable trucks available (Need capacity: {route.totalCapacity ? route.totalCapacity.toFixed(2) + ' liters' : 'N/A'})
                                </p>
                              )}
                            </div>
                            
                            <div>
                              <p className="font-medium">Driver:</p>
                              {availableDrivers.length > 0 ? (
                                <select 
                                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm"
                                  onChange={(e) => handleResourceChange(route.routeId, 'driverId', e.target.value)}
                                  value={selectedResources[route.routeId]?.driverId || ''}
                                >
                                  <option value="">Select a driver</option>
                                  {availableDrivers.map((driver) => (
                                    <option key={driver.userId} value={driver.userId}>
                                      {driver.name} ({driver.licenseNumber})
                                    </option>
                                  ))}
                                </select>
                              ) : (
                                <p className="text-gray-500 mt-1">No available drivers</p>
                              )}
                            </div>
                            
                            <div>
                              <p className="font-medium">Staff:</p>
                              {availableStaff.length > 0 ? (
                                <select 
                                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm"
                                  onChange={(e) => handleResourceChange(route.routeId, 'staffIds', [e.target.value])}
                                  value={selectedResources[route.routeId]?.staffIds?.[0] || ''}
                                >
                                  <option value="">Select staff</option>
                                  {availableStaff.map((staff) => (
                                    <option key={staff.userId} value={staff.userId}>
                                      {staff.name} ({staff.employeeId})
                                    </option>
                                  ))}
                                </select>
                              ) : (
                                <p className="text-gray-500 mt-1">No available staff</p>
                              )}
                            </div>
                            
                            <button
                              onClick={() => handleCreateRoute(index, route.routeId)}
                              className="mt-4 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 text-sm"
                            >
                              Create Route
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="bg-yellow-50 border-l-4 border-yellow-400 p-6">
                  <h3 className="text-lg font-medium text-yellow-800 mb-2">No Routes Available</h3>
                  <p className="text-yellow-700">
                    There are currently no routes to display. This could be because no bins are marked as "FULL" in the system.
                  </p>
                  <div className="mt-4">
                    <h4 className="font-medium text-yellow-800 mb-2">Available Resources:</h4>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div className="bg-white p-3 rounded shadow-sm">
                        <h5 className="font-medium text-gray-700">Drivers ({availableDrivers.length})</h5>
                        <ul className="mt-2 text-sm text-gray-600">
                          {availableDrivers.slice(0, 3).map(driver => (
                            <li key={driver.userId}>{driver.name}</li>
                          ))}
                          {availableDrivers.length > 3 && (
                            <li className="text-gray-500">+ {availableDrivers.length - 3} more</li>
                          )}
                        </ul>
                      </div>
                      <div className="bg-white p-3 rounded shadow-sm">
                        <h5 className="font-medium text-gray-700">Staff ({availableStaff.length})</h5>
                        <ul className="mt-2 text-sm text-gray-600">
                          {availableStaff.slice(0, 3).map(staff => (
                            <li key={staff.userId}>{staff.name}</li>
                          ))}
                          {availableStaff.length > 3 && (
                            <li className="text-gray-500">+ {availableStaff.length - 3} more</li>
                          )}
                        </ul>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default CityAuthorityDashboard;