import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';

function WasteCollectionStaffDashboard() {
  const { user } = useAuth();

  // Mock data for route details - in a real implementation, this would come from an API
  const routeDetails = {
    date: "16/10/2025, 22:53:32",
    status: "COMPLETED",
    totalDistance: "14067.09 km",
    estimatedTime: "28194 minutes",
    completedAt: "N/A",
    truckId: "TRUCK001",
    driverId: "1b8ab14b-950e-4c9b-b97f-ad389c4aa5ec",
    staffIds: ["cff2be77-7c9c-48bc-bc94-803d6574b1a8"],
    numberOfStops: 2
  };

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">Waste Collection Staff Dashboard</h1>
            
            <div className="bg-purple-50 border-l-4 border-purple-500 p-6 mb-6">
              <h2 className="text-xl font-semibold text-purple-800 mb-4">
                Welcome, {user?.name}!
              </h2>
              
              <div className="space-y-2 text-gray-700">
                <p><span className="font-medium">Email:</span> {user?.email}</p>
                <p><span className="font-medium">Phone:</span> {user?.phone}</p>
                <p><span className="font-medium">User ID:</span> {user?.userId}</p>
                <p><span className="font-medium">Role:</span> {user?.userType}</p>
              </div>
            </div>

            {/* Route Details Section */}
            <div className="bg-blue-50 border-l-4 border-blue-500 p-6 mb-6">
              <h2 className="text-xl font-semibold text-blue-800 mb-4">Route Details</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <p><span className="font-medium">Date:</span> {routeDetails.date}</p>
                  <p><span className="font-medium">Status:</span> <span className="font-bold text-green-600">{routeDetails.status}</span></p>
                  <p><span className="font-medium">Total Distance:</span> {routeDetails.totalDistance}</p>
                  <p><span className="font-medium">Estimated Time:</span> {routeDetails.estimatedTime}</p>
                  <p><span className="font-medium">Completed At:</span> {routeDetails.completedAt}</p>
                </div>
                
                <div className="space-y-2">
                  <p><span className="font-medium">Truck ID:</span> {routeDetails.truckId}</p>
                  <p><span className="font-medium">Driver ID:</span> {routeDetails.driverId}</p>
                  <p><span className="font-medium">Staff IDs:</span> {routeDetails.staffIds.join(', ')}</p>
                  <p><span className="font-medium">Number of Stops:</span> {routeDetails.numberOfStops}</p>
                </div>
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              <div className="bg-green-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-green-800 mb-2">Assigned Area</h3>
                <p className="text-gray-600 text-sm">View your collection area details</p>
              </div>

              <div className="bg-blue-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-blue-800 mb-2">Schedule</h3>
                <p className="text-gray-600 text-sm">Check today's collection schedule</p>
              </div>

              <div className="bg-orange-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-orange-800 mb-2">Bin Status</h3>
                <p className="text-gray-600 text-sm">Update bin collection status</p>
              </div>

              <div className="bg-red-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-red-800 mb-2">Report Issue</h3>
                <p className="text-gray-600 text-sm">Report issues during collection</p>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default WasteCollectionStaffDashboard;