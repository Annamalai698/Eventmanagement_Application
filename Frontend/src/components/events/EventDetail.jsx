import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { get, post, setAuthToken } from '../../assets/utils/api.js'


const EventDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');

    // Set token once when component mounts
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setAuthToken(token);
        }
    }, []);

    useEffect(() => {
        const fetchEvent = async () => {
            try {
                const response = await get(`/events/${id}`);
                setEvent(response.data);
            } catch (error) {
                console.error('Error fetching event details:', error);
                setMessage('Event not found or an error occurred.');  
            } finally{
                setLoading(false);
            }
        };
        fetchEvent();
    }, [id]);

    const handleRegister = async () => {
        const loggedInUser = JSON.parse(localStorage.getItem("user"));
        const userId = loggedInUser?.id;

        if (!userId) {
          alert("You must be logged in to register.");
          return;
        }
        try {
            const response = await post("/registrations/register", { userId, eventId: id });
            setMessage('🎉 Registration successful!');
        } catch (error) {
            console.error('Registration error:', error);
            setMessage(error.response?.data || 'Registration failed. You may already be registered.');
        }
    };

    if (loading) return (
         <div className="text-center mt-5">
             <div className="spinner-border text-primary">
             </div>
             <p>Loading...</p>
        </div>
    );
    if (message && message.includes('not found')) return (
              <div className="alert alert-danger mt-5">{message}</div>
    );
    if (!event) return (
              <div className="alert alert-danger mt-5">Event details unavailable.</div>
    );

    return (
        <div className="row mt-5">
            <div className="col-lg-8">
                <h1 className="fw-bold mb-3">{event.title}</h1>
                <p className="text-muted lead">{event.category}</p>
                
                {message && (
                    <div className={`alert ${message.includes('successful') ? 'alert-success' : 'alert-warning'}`}>
                        {message}
                    </div>
                )}

                <div className="card shadow-sm p-4 mb-4 border-0">
                    <h2>Description</h2>
                    <p>{event.description}</p>
                </div>

                <div className="card shadow-sm p-4 mb-4 border-0">
                    <h2>Speakers</h2>
                    {event.speakers && event.speakers.length > 0 ? (
                        <ul className="list-group list-group-flush">
                            {event.speakers.map(speaker => (
                                <li key={speaker.id} className="list-group-item">
                                    <strong>{speaker.name}</strong>: {speaker.bio}
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No speakers announced yet.</p>
                    )}
                </div>
            </div>
            
            <div className="col-lg-4">
                <div className="card shadow-lg p-4 bg-light border-primary">
                    <h4 className="text-primary mb-3">Event Details</h4>
                    <p><strong>🗓️ Date:</strong> {new Date(event.eventDateTime).toLocaleDateString()}</p>
                    <p><strong>🕒 Time:</strong> {new Date(event.eventDateTime).toLocaleTimeString()}</p>
                    <p><strong>📍 Venue:</strong> {event.venue}</p>
                    
                    <button 
                        className="btn btn-success btn-lg mt-3" 
                        onClick={handleRegister}
                        disabled={!localStorage.getItem('token')}
                        /*disabled={!token}*/
                    >
                        {localStorage.getItem('token') ? 'Register Now' : 'Log in to Register'}
                        {/*{token ? 'Register Now' : 'Log in to Register'}*/}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default EventDetail;