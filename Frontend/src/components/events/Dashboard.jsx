import React from "react";

const Dashboard = ({ user, handleLogout, events, adminMessage, newEventTitle, handleNewEventChange, createEvent }) => {
    
    if (!user) {
    return <div className="text-center mt-5">Loading dashboard...</div>;
    }

    const isAdmin = user?.role === 'ROLE_ADMIN';
    
    // Determine card class based on admin status
    const adminCardClass = isAdmin ? 'border-primary bg-light' : 'border-secondary-subtle bg-light';
    const adminButtonClass = isAdmin ? 'btn-success' : 'btn-secondary disabled';

    return (
        <div className="card p-4 shadow-lg w-100 mx-auto" style={{ maxWidth: '900px' }}>
            <h1 className="h3 fw-bold text-dark border-bottom pb-3 mb-4">
                Welcome to the Dashboard, {user?.name || 'Guest'}!
            </h1>

            {/* Public Event List (Viewable by All Authenticated) */}
            <div className="mb-4">
                <h2 className="h4 fw-semibold text-secondary mb-3">Available Upcoming Events</h2>
                {(!events || events.length === 0) ? (
                    <p className="text-muted">No events found. Admin must create one.</p>
                ) : (
                    <div className="row row-cols-1 row-cols-md-2 g-3">
                        {events.map((event, index) => (
                            <div key={event.id || index} className="col">
                                <div className="card h-100 p-3 shadow-sm border-light">
                                    <h3 className="h6 fw-bold text-primary">{event.title || 'Untitled Event'}</h3>
                                    <p className="small text-muted mb-1 text-truncate">{event.description || 'No description'}</p>
                                    <p className="text-xs text-secondary mb-0">
                                        <small>Date: {event.eventDateTime ? new Date(event.eventDateTime).toLocaleDateString() : 'N/A'}</small>
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            
            <button
                onClick={handleLogout}
                className="btn btn-danger fw-semibold rounded-2 mt-3 w-auto"
            >
                Logout
            </button>
        </div>
    );
};

export default Dashboard;