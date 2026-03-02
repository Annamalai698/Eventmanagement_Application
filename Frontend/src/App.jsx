import React from 'react';
import { useEffect } from "react";
import { HashRouter, Routes, Route } from 'react-router-dom';
import MainRouterPage from './MainRouter';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css'
import { setAuthToken } from './assets/utils/api.js';

function App() {
  
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
    //  console.log("Restoring token to Axios:", token);
      setAuthToken(token);
    }
  }, []);

  return(
    <HashRouter>
      <Routes>
            <Route path="*" element={<MainRouterPage />} />
        </Routes>
    </HashRouter>
  );
}

export default App


{/*function App() {

  return (
      <Router>
      <Navbar />
      <div className="container-fluid mt-4">
        <Routes>
          {/* Public Routes */}
          {/*<Route path="/" element={<EventList />} />
          <Route path="/events" element={<EventList />} />
          <Route path="/events/:id" element={<EventDetail />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Admin Protected Route */}
          {/* NOTE: ProtectedRoute component logic must be implemented to check role */}
          {/*<Route 
            path="/admin" 
            element={
              <ProtectedRoute roles={['ROLE_ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  )
}

export default App */}
