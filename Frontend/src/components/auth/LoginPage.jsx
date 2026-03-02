import React from 'react';
import { Link } from 'react-router-dom';

const LoginPage = ({ form, handleChange, handleLogin, message }) => {

    return (
        <div className="card p-4 shadow-lg w-100" style={{ maxWidth: '400px', maxHeight: '800px' }}>
            <h2 className="h3 fw-bolder text-dark mb-4 text-center">
                Login to Your Account
            </h2>
            <form onSubmit={handleLogin} className="d-grid gap-3">
                <input
                    type="email"
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                    placeholder="Email"
                    required
                    className="form-control form-control-lg rounded-2"
                />
                <input
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    placeholder="Password"
                    required
                    className="form-control form-control-lg rounded-2"
                />
                <button
                    type="submit"
                    className="btn btn-primary btn-lg fw-semibold rounded-2 mt-2"
                >
                    Login
                </button>
            </form>
            <p className="mt-3 text-center text-sm text-secondary">
                Don't have an account? 
                <Link to="/register" className="btn btn-link p-0 ms-1 text-decoration-none">
                    Register now
                </Link>
            </p>
        </div>
    );
};
export default LoginPage;

