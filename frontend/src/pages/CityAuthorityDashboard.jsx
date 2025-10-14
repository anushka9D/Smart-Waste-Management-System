import { useAuth } from '../context/AuthContext';
import AuthHeader from '../components/AuthHeader';
import AuthFooter from '../components/AuthFooter';

function CityAuthorityDashboard() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
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

            <div className="grid md:grid-cols-2 gap-6">
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
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default CityAuthorityDashboard;