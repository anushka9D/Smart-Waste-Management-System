import { BrowserRouter, Routes, Route } from "react-router-dom";

import Landing from "./Common/Landing";
import Header from "./Common/Header";
import Login from "./Common/auth/login";
import Register from "./Common/auth/Register";
import Userdashboard from "./protected/user/User-dashboard";
import Admindashboard from "./protected/admin/Admin-dashboard";
import Driverdashboard from "./protected/driver/Driver-dashboard";

//protected route componet
import ProtectedRoute from "./Common/auth/Protected-Route";

import { AuthProvider } from "./Common/auth/AuthProvider";

function App() {

  return (
    <BrowserRouter>
      <Header />
      <AuthProvider>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/common/auth/login" element={<Login />} />
        <Route path="/common/auth/register" element={<Register />} />

        <Route path="/protected/user/dashboard"
          element={
            <ProtectedRoute>
              <Userdashboard />
            </ProtectedRoute>
          } />

          <Route path="/protected/admin/dashboard"
          element={
            <ProtectedRoute>
              <Admindashboard/>
            </ProtectedRoute>
          } /> 

          <Route path="/protected/driver/dashboard"
          element={
            <ProtectedRoute>
              <Driverdashboard/>
            </ProtectedRoute>
          } />
      </Routes>
      </AuthProvider>
    </BrowserRouter>


  )

}

export default App;