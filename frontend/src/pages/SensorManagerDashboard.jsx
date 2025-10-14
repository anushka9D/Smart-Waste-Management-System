import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';

function SensorManagerDashboard() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">Sensor Manager Dashboard</h1>
            
            <div className="bg-indigo-50 border-l-4 border-indigo-500 p-6 mb-6">
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

            <div className="grid md:grid-cols-2 gap-6">
              <div className="bg-blue-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-blue-800 mb-2">Sensor Monitoring</h3>
                <p className="text-gray-600 text-sm">Monitor IoT sensors in your zone</p>
              </div>

              <div className="bg-green-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-green-800 mb-2">Sensor Health</h3>
                <p className="text-gray-600 text-sm">Check sensor status and connectivity</p>
              </div>

              <div className="bg-yellow-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-yellow-800 mb-2">Data Analytics</h3>
                <p className="text-gray-600 text-sm">View sensor data and trends</p>
              </div>

              <div className="bg-red-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-red-800 mb-2">Maintenance</h3>
                <p className="text-gray-600 text-sm">Schedule sensor maintenance</p>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default SensorManagerDashboard;