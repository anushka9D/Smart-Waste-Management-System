import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
  signInWithEmailAndPassword,
  GoogleAuthProvider,
  signInWithPopup,
} from "firebase/auth";
import { auth } from "../../config/firebase-config";
import LandingImage from "../../../public/landingpage.jpg";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  
  const routeByRole = async () => {
    const u = auth.currentUser;
    if (!u) return;

   
    const token = await u.getIdTokenResult(true);
    const role = token.claims.role || "user";

    if (role === "admin") {
      navigate("/protected/admin/dashboard", { replace: true });
    } else {
    
      const from = location.state?.from?.pathname || "/protected/user/dashboard";
      navigate(from, { replace: true });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await signInWithEmailAndPassword(auth, email.trim(), password);
      await routeByRole();
    } catch (err) {
      const code = err?.code || "";
      if (code === "auth/invalid-credential" || code === "auth/wrong-password") {
        setError("Invalid email or password.");
      } else if (code === "auth/user-not-found") {
        setError("No user found with this email.");
      } else if (code === "auth/too-many-requests") {
        setError("Too many attempts. Try again later.");
      } else {
        setError("Login failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGoogle = async () => {
    setError(null);
    setLoading(true);
    try {
      const provider = new GoogleAuthProvider();
      await signInWithPopup(auth, provider);
      await routeByRole();
    } catch (err) {
      setError("Google sign-in failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="relative h-screen bg-center bg-cover flex justify-center items-center"
      style={{
        backgroundImage: `linear-gradient(rgba(0,0,0,.6), rgba(0,0,0,.6)), url(${LandingImage})`,
      }}
    >
      <form
        onSubmit={handleSubmit}
        className="z-10 border max-w-md w-full border-white/20 shadow-xl backdrop-blur-lg p-6 rounded-lg space-y-4"
      >
        <h1 className="text-2xl font-bold text-center text-white">Login</h1>

        {error && (
          <p className="text-red-400 text-sm text-center" role="alert">
            {error}
          </p>
        )}

        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          autoComplete="email"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          autoComplete="current-password"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-green-600 hover:bg-green-700 text-white py-2 rounded font-semibold disabled:opacity-60"
        >
          {loading ? "Signing in..." : "Login"}
        </button>

        <div className="flex items-center gap-3">
          <div className="h-px flex-1 bg-white/20" />
          <span className="text-white/70 text-xs">or</span>
          <div className="h-px flex-1 bg-white/20" />
        </div>

        <button
          type="button"
          onClick={handleGoogle}
          disabled={loading}
          className="w-full py-2 rounded-lg font-semibold border border-white/30 text-white hover:bg-white/10 disabled:opacity-60"
        >
          Continue with Google
        </button>

        <p className="text-center text-white/80 text-sm">
          Don&apos;t have an account?{" "}
          <Link
            to="/common/auth/register"
            className="text-green-400 underline underline-offset-4"
          >
            Register
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Login;
