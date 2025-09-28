
import { initializeApp, getApps, getApp  } from "firebase/app";
import { getAuth } from "firebase/auth";


const firebaseConfig = {
  apiKey: "AIzaSyDIWu9Fj8rIQtNfG1eXuxfUZpX7Inlyg0c",
  authDomain: "smart-waste-management-s-9f2af.firebaseapp.com",
  projectId: "smart-waste-management-s-9f2af",
  storageBucket: "smart-waste-management-s-9f2af.firebasestorage.app",
  messagingSenderId: "219890471025",
  appId: "1:219890471025:web:0798840e172376870b8bd0",
  measurementId: "G-8ZP7KF64ZT"
};


const app = getApps().length ? getApp() : initializeApp(firebaseConfig);
export const auth = getAuth(app);