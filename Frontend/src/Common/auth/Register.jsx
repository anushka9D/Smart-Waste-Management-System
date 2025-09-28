import { useState } from "react";
import { createUserWithEmailAndPassword, updateProfile, GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { auth } from "../../config/firebase-config"; 
import LandingImage from "../../../public/landingpage.jpg";
import { Link, useNavigate } from "react-router-dom"; 

function Register() {
  const navigate = useNavigate(); 
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    e.preventDefault();
    setError(null);

    if (password !== confirm) {
      setError("Passwords do not match.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    setLoading(true);
    try {
      const cred = await createUserWithEmailAndPassword(auth, email.trim(), password);
      
      if (name.trim()) {
        await updateProfile(cred.user, { displayName: name.trim() });
      }
     
      navigate("/user/dashboard"); 
    } catch (err) {
      const code = err?.code || "";
      if (code === "auth/email-already-in-use") setError("Email is already registered.");
      else if (code === "auth/invalid-email") setError("Invalid email address.");
      else if (code === "auth/weak-password") setError("Password is too weak.");
      else setError("Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogle = async () => {
    setError(null);
    setLoading(true);
    try {
      await signInWithPopup(auth, new GoogleAuthProvider());
      navigate("/protected/user/dashboard");
    } catch {
      setError("Google sign-in failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="relative h-screen bg-center bg-cover flex items-center justify-center"
      style={{
        backgroundImage: `linear-gradient(rgba(0,0,0,.6), rgba(0,0,0,.6)), url(${LandingImage})`,
      }}
    >
      <form
        onSubmit={handleSubmit}
        className="
          relative z-10 w-full max-w-md space-y-4 p-6 rounded-2xl
          backdrop-blur-lg bg-white/10
          border border-white/20 shadow-xl
        "
      >
        <h1 className="text-2xl font-bold text-center text-white">Create account</h1>

        {error && <p className="text-red-400 text-sm" role="alert">{error}</p>}

        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Full Name"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password (min 6 chars)"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <input
          type="password"
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          placeholder="Confirm Password"
          className="w-full px-3 py-2 rounded-lg text-white placeholder-white/80
                     bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/40"
          required
        />

        <button
          type="submit"
          disabled={loading}
          className="w-full py-2 rounded-lg cursor-pointer font-semibold bg-green-600 hover:bg-green-700 text-white disabled:opacity-60"
        >
          {loading ? "Creating account..." : "Register"}
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
          className="w-full py-2 cursor-pointer rounded-lg font-semibold border border-white/30 text-white hover:bg-white/10 disabled:opacity-60"
        >
          Continue with Google
        </button>

        <p className="text-center text-white/80 text-sm">
          Already have an account?{" "}
          <Link to="/common/auth/login" className="text-green-400 underline underline-offset-4">
            Login
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Register;
