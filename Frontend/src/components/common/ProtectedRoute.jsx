import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, roles = [] }) => {
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user"));

  if (!token || !user) {
    alert("Please log in first!");
    return <Navigate to="/login" replace />;
  }

  // Role check that supports ROLE_ prefix
  const hasAccess = roles.some((role) => user.role?.includes(role));

  if (!hasAccess) {
    alert("Access denied. You do not have the necessary permissions.");
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
