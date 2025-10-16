import { createContext, useContext, useState, useEffect } from 'react';
import { validateToken } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      // First check localStorage for token
      let token = localStorage.getItem('token');
      console.log('Token from localStorage:', token ? 'Present' : 'Missing');
      
      // If not found in localStorage, check cookie
      if (!token) {
        token = getTokenFromCookie();
        console.log('Token from cookie:', token ? 'Present' : 'Missing');
      }
      
      if (token) {
        const isValid = await validateToken(token);
        console.log('Token validation result:', isValid);
        if (isValid) {
          const userData = decodeToken(token);
          console.log('Decoded user data:', userData);
          setUser(userData);
        } else {
          console.log('Token is invalid, clearing auth state');
          setUser(null);
          // Clear invalid token
          localStorage.removeItem('token');
        }
      } else {
        console.log('No token found');
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      setUser(null);
      // Clear token on error
      localStorage.removeItem('token');
    } finally {
      setLoading(false);
    }
  };

  const login = (userData) => {
    console.log('Setting user data:', userData);
    setUser(userData);
  };

  const logout = () => {
    console.log('Logging out user');
    setUser(null);
    // Clear localStorage
    localStorage.removeItem('token');
    // Clear cookie
    document.cookie = 'jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
  };

  const getTokenFromCookie = () => {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
      const [name, value] = cookie.trim().split('=');
      if (name === 'jwt') {
        return value;
      }
    }
    return null;
  };

  const decodeToken = (token) => {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};