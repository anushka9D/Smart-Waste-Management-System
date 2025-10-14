import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';
import GuestHeader from '../components/GuestHeader';
import GuestFooter from '../components/GuestFooter';

function Home() {
  const { user } = useAuth();

  // If user is logged in, redirect to their dashboard
  if (user) {
    switch (user.userType) {
      case 'CITIZEN':
        return <Navigate to="/citizen-dashboard" replace />;
      case 'CITY_AUTHORITY':
        return <Navigate to="/city-authority-dashboard" replace />;
      case 'DRIVER':
        return <Navigate to="/driver-dashboard" replace />;
      case 'WASTE_COLLECTION_STAFF':
        return <Navigate to="/waste-collection-staff-dashboard" replace />;
      case 'SENSOR_MANAGER':
        return <Navigate to="/sensor-manager-dashboard" replace />;
      default:
        break;
    }
  }

  return (
    <div className="min-h-screen flex flex-col">
      <GuestHeader />

      <main className="flex-grow flex items-center justify-center bg-gradient-to-b from-green-50 to-green-100">
        <div className="container mx-auto px-4 py-16 text-center">
          <h1 className="text-5xl font-bold text-green-800 mb-6">
            SWMS - Smart Waste Management System
          </h1>

          <p className="text-xl text-gray-700 max-w-3xl mx-auto mb-8 leading-relaxed">
            The Smart Waste Management System is an integrated solution designed to address
            the growing challenges of urban waste management. Cities often face problems such
            as overflowing bins, inefficient garbage collection routes, and lack of feedback
            mechanisms for citizens. This system combines modern IoT sensors, mobile applications,
            and web platforms to streamline waste management operations and improve urban cleanliness.
          </p>

          <div className="mt-12 grid md:grid-cols-2 lg:grid-cols-4 gap-6 max-w-5xl mx-auto">
            <div className="bg-white p-6 rounded-lg shadow-md">
              <div className="text-4xl mb-4">📊</div>
              <h3 className="font-bold text-lg mb-2">Smart Monitoring</h3>
              <p className="text-gray-600 text-sm">Real-time bin level tracking with IoT sensors</p>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-md">
              <div className="text-4xl mb-4">🗺️</div>
              <h3 className="font-bold text-lg mb-2">Route Optimization</h3>
              <p className="text-gray-600 text-sm">Efficient collection schedules and routes</p>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-md">
              <div className="text-4xl mb-4">👥</div>
              <h3 className="font-bold text-lg mb-2">Citizen Interaction</h3>
              <p className="text-gray-600 text-sm">Report bins and request services easily</p>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-md">
              <div className="text-4xl mb-4">📈</div>
              <h3 className="font-bold text-lg mb-2">Data Analytics</h3>
              <p className="text-gray-600 text-sm">Insights for informed decision making</p>
            </div>
          </div>
        </div>
      </main>

      <GuestFooter />
    </div>
  );
}

export default Home;