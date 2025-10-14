import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function AuthHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="bg-green-600 text-white shadow-md">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link to="/" className="text-2xl font-bold">
          SWMS
        </Link>
        
        <nav className="hidden md:flex space-x-6">
          <a href="/#" className="hover:text-green-200 transition">Home</a>
          <a href="/#" className="hover:text-green-200 transition">About</a>
          <a href="/#" className="hover:text-green-200 transition">Contact</a>
        </nav>
        
        <div className="flex items-center space-x-4">
          <span className="text-sm">Welcome, {user?.name}</span>
          <button 
            onClick={handleLogout}
            className="bg-red-500 px-4 py-2 rounded hover:bg-red-600 transition font-medium"
          >
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}

export default AuthHeader;