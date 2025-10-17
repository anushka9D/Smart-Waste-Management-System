import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './utils/PrivateRoute';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import CitizenDashboard from './pages/CitizenDashboard';
import CityAuthorityDashboard from './pages/CityAuthority/CityAuthorityDashboard';
import DriverDashboard from './pages/DriverDashboard';
import WasteCollectionStaffDashboard from './pages/WasteCollectionStaffDashboard';
import SensorManagerDashboard from './pages/SensorManagerDashboard';
import AssignedRoutes from './pages/AssignedRoutes';

import Shell from './pages/CityAuthority/Shell'
import Reports from './pages/CityAuthority/Reports'
import Analytics from './pages/CityAuthority/Analytics'
import Dashboard from './pages/CityAuthority/Dashboard'
import Map from './pages/CityAuthority/Map';



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
          {/* <Route
            path="/city-authority-dashboard"
            element={
              <PrivateRoute allowedUserTypes={['CITY_AUTHORITY']}>
                <CityAuthorityDashboard />
              </PrivateRoute>
            }
          /> */}

          <Route
            path="/city-authority-dashboard"
            element={
              <PrivateRoute allowedUserTypes={["CITY_AUTHORITY"]}>
                <Shell />
              </PrivateRoute>
            }
          >

            <Route index element={<CityAuthorityDashboard />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="reports" element={<Reports />} />
            <Route path="analytics" element={<Analytics />} />
            <Route path="map" element={<Map />} />
          </Route>

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
        </Routes>


      </Router>
    </AuthProvider>
  );
}

export default App;