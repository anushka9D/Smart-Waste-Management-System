import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';

function CitizenDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6 text-center">Citizen Dashboard</h1>
            
            <div className="bg-green-50 border-l-4 border-green-500 p-6 mb-6">
              <h2 className="text-xl font-semibold text-green-800 mb-4">
                Welcome, {user?.name}!
              </h2>
              
              <div className="space-y-2 text-gray-700">
                <p><span className="font-medium">Email:</span> {user?.email}</p>
                <p><span className="font-medium">Phone:</span> {user?.phone}</p>
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              <button
                onClick={() => navigate('/citizen/report-bin-request')}
                className="bg-blue-50 p-6 rounded-lg hover:bg-blue-100 transition text-left"
              >
                <h3 className="text-lg font-semibold text-blue-800 mb-2">
                  Report Bin / Request Pickup
                </h3>
                <p className="text-gray-600 text-sm">Report overflowing bins in your area</p>
              </button>

              <button
                onClick={() => navigate('/citizen/track-requests')}
                className="bg-purple-50 p-6 rounded-lg hover:bg-purple-100 transition text-left"
              >
                <h3 className="text-lg font-semibold text-purple-800 mb-2">
                  Track Requests
                </h3>
                <p className="text-gray-600 text-sm">View your submitted requests status</p>
              </button>

              <div className="bg-yellow-50 p-6 rounded-lg opacity-60 cursor-not-allowed">
                <h3 className="text-lg font-semibold text-yellow-800 mb-2">My Reports</h3>
                <p className="text-gray-600 text-sm">View your submitted reports</p>
                <p className="text-xs text-gray-500 mt-2 italic">Coming soon...</p>
              </div>

              <div className="bg-pink-50 p-6 rounded-lg opacity-60 cursor-not-allowed">
                <h3 className="text-lg font-semibold text-pink-800 mb-2">Feedback</h3>
                <p className="text-gray-600 text-sm">Provide feedback on services</p>
                <p className="text-xs text-gray-500 mt-2 italic">Coming soon...</p>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default CitizenDashboard;