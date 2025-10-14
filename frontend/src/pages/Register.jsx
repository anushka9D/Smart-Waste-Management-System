import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  registerCitizen,
  registerCityAuthority,
  registerDriver,
  registerWasteCollectionStaff,
  registerSensorManager
} from '../services/api';
import GuestHeader from '../components/GuestHeader';
import GuestFooter from '../components/GuestFooter';

function Register() {
  const [step, setStep] = useState(1); // Step 1: Select user type, Step 2: Fill form
  const [userType, setUserType] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    // Citizen specific
    userType: '',
    age: '',
    // City Authority specific
    employeeId: '',
    department: '',
    // Driver specific
    licenseNumber: '',
    vehicleType: '',
    // Waste Collection Staff specific
    routeArea: '',
    // Sensor Manager specific
    assignedZone: '',
  });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleUserTypeSelect = (type) => {
    setUserType(type);
    setStep(2);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate password confirmation
    if (formData.password !== formData.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    setLoading(true);

    try {
      let response;
      const { confirmPassword, ...dataToSend } = formData;

      switch (userType) {
        case 'CITIZEN':
          response = await registerCitizen({
            name: dataToSend.name,
            email: dataToSend.email,
            phone: dataToSend.phone,
            password: dataToSend.password,
            age: parseInt(dataToSend.age),
          });
          break;

        case 'CITY_AUTHORITY':
          response = await registerCityAuthority({
            name: dataToSend.name,
            email: dataToSend.email,
            phone: dataToSend.phone,
            password: dataToSend.password,
            employeeId: dataToSend.employeeId,
            department: dataToSend.department,
          });
          break;

        case 'DRIVER':
          response = await registerDriver({
            name: dataToSend.name,
            email: dataToSend.email,
            phone: dataToSend.phone,
            password: dataToSend.password,
            licenseNumber: dataToSend.licenseNumber,
            vehicleType: dataToSend.vehicleType,
          });
          break;

        case 'WASTE_COLLECTION_STAFF':
          response = await registerWasteCollectionStaff({
            name: dataToSend.name,
            email: dataToSend.email,
            phone: dataToSend.phone,
            password: dataToSend.password,
            employeeId: dataToSend.employeeId,
            routeArea: dataToSend.routeArea,
          });
          break;

        case 'SENSOR_MANAGER':
          response = await registerSensorManager({
            name: dataToSend.name,
            email: dataToSend.email,
            phone: dataToSend.phone,
            password: dataToSend.password,
            employeeId: dataToSend.employeeId,
            assignedZone: dataToSend.assignedZone,
          });
          break;

        default:
          alert('Please select a user type');
          return;
      }

      if (response.success) {
        const userData = response.data;
        login(userData);

        // Redirect based on user type
        switch (userData.userType) {
          case 'CITIZEN':
            navigate('/citizen-dashboard');
            break;
          case 'CITY_AUTHORITY':
            navigate('/city-authority-dashboard');
            break;
          case 'DRIVER':
            navigate('/driver-dashboard');
            break;
          case 'WASTE_COLLECTION_STAFF':
            navigate('/waste-collection-staff-dashboard');
            break;
          default:
            navigate('/');
        }
      } else {
        alert(response.message || 'Registration failed');
      }
    } catch (error) {
      alert(error.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const renderUserTypeSelection = () => (
    <div className="max-w-4xl w-full bg-white rounded-lg shadow-md p-8">
      <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">
        Select User Type
      </h2>

      <div className="grid md:grid-cols-2 gap-6">
        <button
          onClick={() => handleUserTypeSelect('CITIZEN')}
          className="p-6 border-2 border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
        >
          <div className="text-4xl mb-4">👤</div>
          <h3 className="text-xl font-bold mb-2">Citizen</h3>
          <p className="text-gray-600 text-sm">Register as a citizen to report bins and request services</p>
        </button>

        <button
          onClick={() => handleUserTypeSelect('CITY_AUTHORITY')}
          className="p-6 border-2 border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
        >
          <div className="text-4xl mb-4">🏛️</div>
          <h3 className="text-xl font-bold mb-2">City Authority</h3>
          <p className="text-gray-600 text-sm">Register as a city authority to manage operations</p>
        </button>

        <button
          onClick={() => handleUserTypeSelect('DRIVER')}
          className="p-6 border-2 border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
        >
          <div className="text-4xl mb-4">🚛</div>
          <h3 className="text-xl font-bold mb-2">Driver</h3>
          <p className="text-gray-600 text-sm">Register as a driver for waste collection routes</p>
        </button>

        <button
          onClick={() => handleUserTypeSelect('WASTE_COLLECTION_STAFF')}
          className="p-6 border-2 border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
        >
          <div className="text-4xl mb-4">👷</div>
          <h3 className="text-xl font-bold mb-2">Waste Collection Staff</h3>
          <p className="text-gray-600 text-sm">Register as waste collection staff member</p>
        </button>

        <button
          onClick={() => handleUserTypeSelect('SENSOR_MANAGER')}
          className="p-6 border-2 border-gray-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
        >
          <div className="text-4xl mb-4">📡</div>
          <h3 className="text-xl font-bold mb-2">Sensor Manager</h3>
          <p className="text-gray-600 text-sm">Register as a sensor manager to monitor IoT devices</p>
        </button>
      </div>

      <p className="text-center mt-8 text-gray-600">
        Already have an account?{' '}
        <Link to="/login" className="text-green-600 hover:text-green-700 font-medium">
          Login here
        </Link>
      </p>
    </div>
  );

  const renderRegistrationForm = () => (
    <div className="max-w-2xl w-full bg-white rounded-lg shadow-md p-8">
      <button
        onClick={() => setStep(1)}
        className="text-green-600 hover:text-green-700 mb-4 flex items-center"
      >
        ← Back to user type selection
      </button>

      <h2 className="text-3xl font-bold text-center text-gray-800 mb-2">
        Register as {userType.replace('_', ' ')}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4 mt-6">
        {/* Common fields */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Name</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="Enter your name"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="Enter your email"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Phone</label>
          <input
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="Enter your phone number"
          />
        </div>

        {/* User type specific fields */}
        {userType === 'CITIZEN' && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Age</label>
              <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your age"
              />
            </div>
          </>
        )}

        {userType === 'CITY_AUTHORITY' && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Employee ID</label>
              <input
                type="text"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your employee ID"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Department</label>
              <input
                type="text"
                name="department"
                value={formData.department}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your department"
              />
            </div>
          </>
        )}

        {userType === 'DRIVER' && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">License Number</label>
              <input
                type="text"
                name="licenseNumber"
                value={formData.licenseNumber}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your license number"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Vehicle Type</label>
              <input
                type="text"
                name="vehicleType"
                value={formData.vehicleType}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter vehicle type"
              />
            </div>
          </>
        )}

        {userType === 'WASTE_COLLECTION_STAFF' && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Employee ID</label>
              <input
                type="text"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your employee ID"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Route Area</label>
              <input
                type="text"
                name="routeArea"
                value={formData.routeArea}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your route area"
              />
            </div>
          </>
        )}

        {userType === 'SENSOR_MANAGER' && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Employee ID</label>
              <input
                type="text"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your employee ID"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Assigned Zone</label>
              <input
                type="text"
                name="assignedZone"
                value={formData.assignedZone}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                placeholder="Enter your assigned zone"
              />
            </div>
          </>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Password</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="Enter your password"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Confirm Password</label>
          <input
            type="password"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="Confirm your password"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition font-medium disabled:bg-gray-400 mt-6"
        >
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>

      <p className="text-center mt-6 text-gray-600">
        Already have an account?{' '}
        <Link to="/login" className="text-green-600 hover:text-green-700 font-medium">
          Login here
        </Link>
      </p>
    </div>
  );

  return (
    <div className="min-h-screen flex flex-col">
      <GuestHeader />

      <main className="flex-grow flex items-center justify-center bg-gray-50 py-12 px-4">
        {step === 1 ? renderUserTypeSelection() : renderRegistrationForm()}
      </main>

      <GuestFooter />
    </div>
  );
}

export default Register;