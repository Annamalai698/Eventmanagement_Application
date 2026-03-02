import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { post, get } from '../../assets/utils/api.js';

const EventList = () => {
    const [events, setEvents] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);
    const [registeringId, setRegisteringId] = useState(null);

    useEffect(() => {
        const fetchEvents = async () => {
            try {
               const response = await get('/events', { headers: {} });
               console.log(response.data);
                setEvents(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching events:', error);
                setLoading(false);
            }
        };
        fetchEvents();
    }, []);

    const handleRegister = async (eventId) => {
    try {
      setRegisteringId(eventId);
      const user = JSON.parse(localStorage.getItem('user'));
      if (!user) throw new Error("You must be logged in to register.");

      await post(`/registrations/register`, {
        userId: user.id,
        eventId: eventId
      });

      alert('Registered successfully!');
      setRegisteringId(null);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || err.message || 'Registration failed.');
      setRegisteringId(null);
    }
  };

    // Simple client-side filtering (You can implement server-side search later)
    const filteredEvents = events.filter(event =>
        event.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.venue.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return (
    <div className="text-center mt-5">
      <div className="spinner-border text-primary" role="status"></div>
      <p>Loading Events...</p>
    </div>
    );

    return (
        <div className="event-list w-75">
            <h1 className="mb-4 text-center">Upcoming Events</h1>
            
            {/* Search Input */}
            <div className="mb-4">
                <input
                    type="text"
                    className="form-control form-control-lg shadow-sm"
                    placeholder="Search events by title, venue, or category..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <div className="row">
                {filteredEvents.length > 0 ? (
                    filteredEvents.map(event => (
                        <div key={event.id} className="col-lg-4 col-md-6 mb-4">
                            <div className="card h-100 shadow-sm border-0">
                                <div className="card-body d-flex flex-column">
                                    <h5 className="card-title fw-bold">{event.title}</h5>
                                    <p className="card-text text-muted small mb-1">
                                        <i className="bi bi-geo-alt me-1"></i> {event.venue}
                                    </p>
                                    <p className="card-text text-primary small">
                                        <i className="bi bi-calendar me-1"></i> 
                                        {new Date(event.eventDateTime).toLocaleString()}
                                    </p>
                                    <p className="card-text flex-grow-1">{event.description.substring(0, 80)}...</p>
                                    
                                    <div className="d-flex justify-content-center mt-3">
                                       <Link to={`/events/${event.id}`} className="btn btn-primary mt-3">
                                          View Details
                                       </Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="col-12">
                        <div className="alert alert-info text-center">No events found matching your search.</div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default EventList;