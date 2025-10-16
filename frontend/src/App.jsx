import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './utils/PrivateRoute';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import CitizenDashboard from './pages/CitizenDashboard';
import CityAuthorityDashboard from './pages/CityAuthorityDashboard';
import DriverDashboard from './pages/DriverDashboard';
import WasteCollectionStaffDashboard from './pages/WasteCollectionStaffDashboard';
import SensorManagerDashboard from './pages/SensorManagerDashboard';
import AssignedRoutes from './pages/AssignedRoutes';
import AddSmartBin from './pages/AddSmartBin';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected Routes */}
          <Route
            path="/citizen-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['CITIZEN']}>
                <CitizenDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/city-authority-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['CITY_AUTHORITY']}>
                <CityAuthorityDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/assigned-routes"
            element={
              <PrivateRoute allowedUserTypes={['CITY_AUTHORITY']}>
                <AssignedRoutes />
              </PrivateRoute>
            }
          />
          <Route
            path="/driver-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['DRIVER']}>
                <DriverDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/waste-collection-staff-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['WASTE_COLLECTION_STAFF']}>
                <WasteCollectionStaffDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/sensor-manager-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['SENSOR_MANAGER']}>
                <SensorManagerDashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/add-smart-bin"
            element={
              <PrivateRoute allowedUserTypes={['SENSOR_MANAGER']}>
                <AddSmartBin />
              </PrivateRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;