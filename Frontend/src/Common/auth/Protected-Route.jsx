import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "./AuthProvider";

export default function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="h-screen flex items-center justify-center text-white">
        Loading…
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/common/auth/login" replace state={{ from: location }} />;
  }

  return children;
}
