import { Link } from 'react-router-dom';

function GuestHeader() {
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
        
        <div className="flex space-x-4">
          <Link 
            to="/login" 
            className="bg-white text-green-600 px-4 py-2 rounded hover:bg-green-50 transition font-medium"
          >
            Login
          </Link>
          <Link 
            to="/register" 
            className="bg-green-700 px-4 py-2 rounded hover:bg-green-800 transition font-medium"
          >
            Register
          </Link>
        </div>
      </div>
    </header>
  );
}

export default GuestHeader;