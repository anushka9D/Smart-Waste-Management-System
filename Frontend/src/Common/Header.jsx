import React from "react";
import { Link } from "react-router-dom";
import { useState,useEffect } from "react";
import { onAuthStateChanged, signOut } from "firebase/auth";
import { auth } from "../config/firebase-config";
import { useNavigate } from "react-router-dom";

function Header() {

  const [user, setUser] = useState(null);

  const navigate = useNavigate();

   useEffect(() => {
    const unsub = onAuthStateChanged(auth, (u) => setUser(u));
    return unsub; 
  }, []);

   const handleLogout = async () => {
    try {
      await signOut(auth);
      navigate("/", { replace: true }); 
    } catch (e) {
      console.error("Logout failed", e);
    }
  };

  return (
    <header className="fixed top-0 left-0 w-full z-50 backdrop-blur-md shadow-lg">
      <div className="mx-auto flex h-25  items-center justify-between px-2">

        <Link to='/'>
          <img
            src='/image.png'
            alt="recyling logo"
            width={100}
            height={10}
            className="rounded-2xl"
          />
        </Link>


        <nav className="hidden lg:flex space-x-10 text-gray-200 font-bold uppercase tracking-wider">
          <Link to="/home" className="hover:text-green-600 transition-all">Home</Link>
          <Link to="/about" className="hover:text-green-600 transition-all">About</Link>
          <Link to="/contact" className="hover:text-green-600 transition-all">Contact</Link>
        </nav>


        <div className="hidden lg:flex space-x-6">
          {user ? (
            <>
              <button
              onClick={handleLogout}
                to="/common/auth/login"
                className="
              relative inline-flex items-center justify-center overflow-hidden uppercase
              rounded-2xl px-6 py-2 font-semibold
              border border-green-500 text-green-500
              shadow-md transition-colors
              hover:text-white
              before:content-[''] before:absolute before:inset-0
              before:bg-green-500 before:-translate-x-full
              before:transition-transform before:duration-500
              hover:before:translate-x-0
            "
              >
                <span className="relative z-10">Log out</span>
              </button></>
          ) : (
            <>
              <Link
                to="/common/auth/login"
                className="
              relative inline-flex items-center justify-center overflow-hidden uppercase
              rounded-2xl px-6 py-2 font-semibold
              border border-green-500 text-green-500
              shadow-md transition-colors
              hover:text-white
              before:content-[''] before:absolute before:inset-0
              before:bg-green-500 before:-translate-x-full
              before:transition-transform before:duration-500
              hover:before:translate-x-0
            "
              >
                <span className="relative z-10">Login</span>
              </Link>

              <Link
                to="/common/auth/register"
                className="
              relative inline-flex items-center justify-center overflow-hidden uppercase
              rounded-2xl px-6 py-2 font-semibold
              border border-green-500 text-green-500
              shadow-md transition-colors
              hover:text-white
              before:content-[''] before:absolute before:inset-0
              before:bg-green-500 before:-translate-x-full
              before:transition-transform before:duration-500
              hover:before:translate-x-0
            "
              >
                <span className="relative z-10">Register</span>
              </Link></>

          )}



        </div>

      </div>
    </header>
  );
}

export default Header;
