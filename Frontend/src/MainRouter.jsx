import React, { useState, useEffect } from 'react';
import { Routes, Route, useNavigate, Link } from 'react-router-dom';
import { post, get, setAuthToken } from './assets/utils/api.js'; 
import Header from './components/Header.jsx';
import LoginPage from './components/auth/LoginPage.jsx';
import RegisterPage from './components/auth/RegisterPage.jsx';
import DashboardPage from './components/events/DashboardPage.jsx'; // Logic + API fetching of dashboard page
import EventList from './components/events/EventList.jsx';
import EventDetail from './components/events/EventDetail.jsx';

import ProtectedRoute from './components/common/ProtectedRoute.jsx'; 
import AdminDashboard from './components/admin/AdminDashboard.jsx';
import SpeakersPage from './components/admin/SpeakersPage.jsx';

const MainRouter = () => {
    const navigate = useNavigate();

    // Load initial auth state from localStorage
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')));
    const [isLoggedIn, setIsLoggedIn] = useState(!!token && !!user);
    const [message, setMessage] = useState('');
    
    // Form state is now consolidated
    const [form, setForm] = useState({
        name: '',
        email: '',
        password: '',
        role: 'ROLE_USER',
    });

    // State for testing Admin/User features
     const [adminMessage, setAdminMessage] = useState('Attempt to access admin function...');

    useEffect(() => {
        
        if (token) {
            setAuthToken(token);
        }
        else{
            setAuthToken(null);     
    }
    }, [token]); // Added navigate as a dependency

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage('Logging in...');
        try {
            const response = await post('/auth/signin', {
                email: form.email,
                password: form.password,
            });

            // Save token and set Authorization header globally
            localStorage.setItem('token', response.data.token);
            setAuthToken(response.data.token);

            // Assuming your backend sends 'username'
            const { accessToken, id, email, username, role } = response.data;

            if (!accessToken) {
            alert("No token returned from server!");
            return;
            }

            const userData = { id, name: username, email, role };

            // Set token using the key ProtectedRoute expects
            localStorage.setItem('token', accessToken); 
            localStorage.setItem('user', JSON.stringify(userData));
        
            
            setAuthToken(accessToken);
            setToken(accessToken);
            setUser(userData);
            setIsLoggedIn(true);
            setMessage(`Login successful! Welcome, ${username} (${role})`);

            // Redirect based on role
            if (role === 'ROLE_ADMIN') navigate('/admin');
            else navigate('/dashboard');

            navigate('/dashboard'); // Use router navigation
            setForm({ name: '', email: '', password: '', role: 'ROLE_USER' }); // Clear form

        } catch (error) {
            setMessage(`Login Failed: ${error.response?.data?.message || 'Check credentials'}`);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('Registering...');
       // console.log(form);
        try {
            const response = await post('/auth/signup', form);
            console.log(response.data.message);
            setMessage(response.data.message || 'Registration successful! Please log in.');
            // setMessage(`Registration successful! ${response.data.message} Please log in.`);
            navigate('/login'); // Use router navigation
            setForm({ name: '', email: '', password: '', role: 'ROLE_USER' }); // Clear form
        } catch (error) {
            setMessage(`Registration Failed: ${error.response?.data?.message || 'An error occurred.'}`);
            console.log(error);
        }
    };

    const handleLogout = () => {

        // Clear token and remove from headers
        setAuthToken(null);

        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user');
        
        setAuthToken(null);
        setToken(null);
        setUser(null);
        setIsLoggedIn(false);
        setMessage('Logged out successfully.');
        setAdminMessage('Attempt to access admin function...');
        navigate('/login');
    };

    // Feature Testing Functions 
    const fetchEvents = async () => {
        try {
            if (!token) return; 
           // const response = await get('/speakers/event/{eventId}'); 
            const response = await get('/events'); 
            setEvents(response.data);
        } catch (error) {
            setMessage(`Error fetching events: ${error.response?.data?.message || 'Authentication required.'}`);
            setEvents([]);
        }
    };

    const createEvent = async () => {
        setAdminMessage('Attempting to create event...');
        if (!newEventTitle) {
            return setAdminMessage('Event title is required.');
        }

        try {
            const eventData = {
                title: newEventTitle,
                description: "This is a test event created by the Admin.",
                eventDateTime: new Date().toISOString(),
                venue: "Online Venue",
                category: "Testing",
            };
            
            const response = await post('/events', eventData);
            setAdminMessage(`SUCCESS! Event created: ${response.data.title}`);
            setNewEventTitle('');
            setNewEventDescription('');
            fetchEvents(); // Refresh event list
        } catch (error) {
            const status = error.response?.status;
            const statusText = error.response?.statusText || 'Error';
            
            setAdminMessage('FAILURE: ' + (err.response?.data?.message || 'Server error'));
        }
    };

    // Determine alert class for fixed status message
    const alertClass = message.match(/(Failed|Error)/)
        ? 'text-bg-danger'
        : 'text-bg-success';


    return (
        <div className="min-vh-100 bg-light d-flex flex-column font-sans">
            <Header 
                isLoggedIn={isLoggedIn} 
                user={user} 
                onLogout={handleLogout} 
            />
            
            <main className="flex-grow-1 d-flex justify-content-center pt-2 pb-5 px-3">
                <Routes>
                    {/* Public Routes */}
                    <Route path="/" element={<p className="text-muted">Loading...</p>} />
                    <Route path="/login" element={
                            <LoginPage
                            form={form}
                            handleChange={handleChange}
                            handleLogin={handleLogin}
                            message={message}
                        />
                    } />
                    <Route path="/register" element={
                        <RegisterPage
                            form={form}
                            handleChange={handleChange}
                            handleRegister={handleRegister}
                            message={message}
                        />
                    } />

                    {/* Standard Event Browsing (Usually public or viewable by all logged-in users) */}
                    <Route path="/events" element={<EventList />} />
                    <Route path="/events/:id" element={<EventDetail />} />

                    {/* PROTECTED USER DASHBOARD (Accessible by USER or ADMIN) */}
                    <Route path="/dashboard" element={ <ProtectedRoute roles={['USER', 'ADMIN']}>
                            <DashboardPage
                                user={user}
                                handleLogout={handleLogout}
                            />
                            </ProtectedRoute> } />
                    {/* PROTECTED ADMIN MANAGEMENT (Only ADMIN) */}
                    <Route path="/admin" element={
                    <ProtectedRoute roles={['ADMIN']}>
                        <AdminDashboard user={user} onLogout={handleLogout} /> 
                    </ProtectedRoute>
                    } />
                    <Route path="/speakers" element={
                    <ProtectedRoute roles={['ADMIN']}>
                          <SpeakersPage />
                    </ProtectedRoute>
                    } />
                    
                    {/* 404 handler */}
                    <Route path="*" element={
                        <div className="alert alert-danger">404 - Page Not Found. <Link to="/login" className="alert-link">Go to Login</Link></div>
                    } />
                </Routes>
            </main>
            
            {/* Status Message */}
            <div className={`p-3 w-100 text-center fw-medium fixed-bottom ${alertClass}`} role="alert">
                {message || (isLoggedIn ? `Dashboard View - Current Role: ${user?.role}` : 'Please login or register.')}
            </div>
        </div>
    );
};

export default MainRouter;