import React from 'react';
import { Link } from "react-router-dom";

const Header = ({ isLoggedIn, user, onLogout }) => {

    // Determine if the user is an admin for the admin link
    const isAdmin = isLoggedIn && user && user.role === 'ROLE_ADMIN';

    // !!!!!!!
    // Log the user object and the result of the check
   // console.log("Header Props: ", { isLoggedIn, user });
   //  console.log("Header Role Check:", isAdmin, "Actual Role:", user?.role);

    return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white shadow-sm mb-4 sticky-top">
        <div className="container-fluid container-lg">
            <Link 
                className="navbar-brand h3 fw-bold text-primary mb-0 cursor-pointer" 
                to={isLoggedIn ? '/dashboard' : '/events'}
            >
                Event Manager
            </Link>
            <div className="d-flex align-items-center">
                {/* Event List link visible to everyone */}
                    <Link to="/events" className="btn btn-sm btn-outline-secondary me-3 d-none d-sm-inline">
                        Browse Events
                    </Link>

                    {/* ADMIN LINK */}
                    {isAdmin && (
                        <>
                          <Link to="/admin" className="btn btn-sm btn-warning me-3 fw-semibold">
                              Admin Management
                          </Link>
                          <Link to="/speakers" className="btn btn-sm btn-info me-3 fw-semibold">
                              Manage Speakers
                          </Link>
                        </>
                    )}
                {isLoggedIn ? (
                    <div className="d-flex align-items-center">
                        <span className="text-secondary fw-medium me-3 d-none d-sm-inline">
                            Logged in as: {user.name} ({user.role})
                        </span>
                        <button 
                            onClick={onLogout}
                            className="btn btn-danger fw-semibold rounded-2"
                        >
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="d-flex gap-2">
                        <Link
                            to="/login"
                            className="btn btn-outline-primary fw-semibold rounded-2"
                        >
                            Login
                        </Link>
                        <Link
                            to="/register"
                            className="btn btn-primary fw-semibold rounded-2"
                        >
                            Register
                        </Link>
                    </div>
                )}
            </div>
          </div>
    </nav>
);
}

export default Header;

