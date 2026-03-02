import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { get, post, put, del } from '../../assets/utils/api.js'

const AdminDashboard = ({ user, handleLogout }) => {
    const [events, setEvents] = useState([]);
    const [newEvent, setNewEvent] = useState({ title: '', description: '', venue: '', eventDateTime: '', category: '' });
    const [message, setMessage] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedEvent, setSelectedEvent] = useState('');
    const [attendanceList, setAttendanceList] = useState([]);
    const [activeTab, setActiveTab] = useState('events'); 

    const token = localStorage.getItem('token');
    const config = { headers: { Authorization: `Bearer ${token}` } };

    useEffect(() => {
        fetchEvents();
    }, []);

    const fetchEvents = async () => {
        try {
            const response = await get('/events');
            setEvents(response.data);
            console.log(response.data);
        } catch (error) {
            setMessage('Failed to fetch events.');
        }
    };

    // Form input change
    const handleFormChange = (e) => {
    const { name, value } = e.target;
    setNewEvent((prev) => ({
       ...prev,
       [name]: value,
    }));
    };

    // Create event
    const handleCreateEvent = async (e) => {
        e.preventDefault();
        try {
            const response = await post('/events', newEvent, config);
            setMessage(`Event created: ${response.data.title}`);
            setNewEvent({ title: '', description: '', venue: '', eventDateTime: '', category: '' });
            fetchEvents();
        } catch (error) {
            setMessage('Failed to create event: ' + (error.response?.data?.message || 'Server error'));
        }
    };

    // Update event
    const handleUpdateEvent = async (e) => {
        e.preventDefault();

        const formattedEvent = {
        ...newEvent,
        eventDateTime: newEvent.eventDateTime || null,
        };

        try {
            console.log('Updating event:', formattedEvent);
            const response = await put(`/events/${editingId}`, formattedEvent, config);
            setMessage(`Event updated: ${response.data.title}`);
            handleCancelEdit();
            fetchEvents();
        } catch (error) {
            console.error('Update error:', error.response?.data || error);
            setMessage('Failed to update event: ' + (error.response?.data?.message || 'Server error'));
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (isEditing) handleUpdateEvent(e);
        else handleCreateEvent(e);
    };

    const handleDeleteEvent = async (id) => {
        if (!window.confirm('Are you sure?')) return;
        try {
            await del(`/events/${id}`, config);
            setMessage('Event deleted successfully.');
            fetchEvents();
        } catch (error) {
            setMessage('Failed to delete event.');
        }
    };

    const handleEditClick = (event) => {
        setIsEditing(true);
        setEditingId(event.id);
        const formattedDate = event.eventDateTime ? new Date(event.eventDateTime).toISOString().slice(0, 16) : '';
        setNewEvent({
            title: event.title,
            description: event.description,
            venue: event.venue,
            eventDateTime: formattedDate,
            category: event.category
        });
    };

    const handleCancelEdit = () => {
        setIsEditing(false);
        setEditingId(null);
        setNewEvent({ title: '', description: '', venue: '', eventDateTime: '', category: '' });
        setMessage('');
    };

    const handleSearch = async () => {
        try {
            const response = await axios.get(`/events/search?keyword=${searchQuery}`, config);
            setEvents(response.data.data);
        } catch (error) {
            setMessage('Search failed.');
        }
    };

    return (
        <div className="container w-50 mt-4">
            <h1>{isEditing ? `Edit Event: ${newEvent.title}` : 'Admin Dashboard'}</h1>
            {message && <div className={`alert ${message.includes('Failed') ? 'alert-danger' : 'alert-success'}`}>{message}</div>}

            {/* Event Form */}
            <form onSubmit={handleSubmit} className="mb-4">
                <input type='text' name="title" value={newEvent.title} onChange={handleFormChange} placeholder="Title" className="form-control mb-2" required />
                <textarea name="description" value={newEvent.description} onChange={handleFormChange} placeholder="Description" className="form-control mb-2" required />
                <input type='text' name="venue" value={newEvent.venue} onChange={handleFormChange} placeholder="Venue" className="form-control mb-2" required />
                <input name="eventDateTime" type="datetime-local" value={newEvent.eventDateTime} onChange={handleFormChange} className="form-control mb-2" placeholder='Click right side of box to enter dateTime' required />
                <input type='text' name="category" value={newEvent.category} onChange={handleFormChange} placeholder="category" className="form-control mb-2" required />
                <button type="submit" className={`btn ${isEditing ? 'btn-warning' : 'btn-primary'} mb-2 w-100`}>
                    {isEditing ? 'Update Event' : 'Create Event'}
                </button>
                {isEditing && <button type="button" onClick={handleCancelEdit} className="btn btn-secondary w-100">Cancel Edit</button>}
            </form>

            {/* Event List */}
            <ul className="list-group">
                {events.map(event => (
                    <li key={event.id} className="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <strong>{event.title}</strong> <br />
                            <small>{new Date(event.eventDateTime).toLocaleString()}</small>
                        </div>
                        <div>
                            <button className="btn btn-sm btn-info me-2" onClick={() => handleEditClick(event)} disabled={isEditing && editingId !== event.id}>Edit</button>
                            <button className="btn btn-sm btn-danger" onClick={() => handleDeleteEvent(event.id)} disabled={isEditing}>Delete</button>
                        </div>
                    </li>
                ))}
            </ul>

            <button className="btn btn-danger mt-4" onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default AdminDashboard;
