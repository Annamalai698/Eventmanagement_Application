import React, { useEffect, useState } from "react";
import { get, post } from "../../assets/utils/api.js";
import Dashboard from "./Dashboard.jsx"; // UI component of dashboard page

const DashboardPage = ({ user, handleLogout }) => {
  const [events, setEvents] = useState([]);
  const [newEventTitle, setNewEventTitle] = useState("");
  const [adminMessage, setAdminMessage] = useState("");

  // Fetch all events
  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await get("/events");
        setEvents(response.data || []);
      } catch (error) {
        console.error("Error fetching events:", error);
      }
    };
    fetchEvents();
  }, []);

  // Handle input changes
  const handleNewEventChange = (e) => setNewEventTitle(e.target.value);

  return (
    <Dashboard
      user={user}
      handleLogout={handleLogout}
      events={events}
      newEventTitle={newEventTitle}
      handleNewEventChange={handleNewEventChange}
      adminMessage={adminMessage}
    />
  );
};

export default DashboardPage;
