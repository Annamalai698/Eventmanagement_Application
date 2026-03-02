import React from 'react';
import { Link } from 'react-router-dom';

const RegisterPage = ({ form, handleChange, handleRegister, message }) => {
    return (
        <div className="card p-4 shadow-lg w-100" style={{ maxWidth: '400px' }}>
            <h2 className="h3 fw-bolder text-dark mb-4 text-center">
                Create New Account
            </h2>
            <form onSubmit={handleRegister} className="d-grid gap-3">
                <input
                    type="text"
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                    placeholder="Full Name"
                    required
                    className="form-control form-control-lg rounded-2"
                />
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
                
                <div>
                    <label htmlFor="role" className="form-label text-secondary mb-1">Select Role</label>
                    <select
                        name="role"
                        value={form.role}
                        onChange={handleChange}
                        id="role"
                        className="form-select rounded-2"
                    >
                        <option value="ROLE_USER">User</option>
                        <option value="ROLE_ADMIN">Admin</option>
                    </select>
                </div>

                <button
                    type="submit"
                    className="btn btn-primary btn-lg fw-semibold rounded-2 mt-2"
                >
                    Register
                </button>

            </form>
            <p className="mt-3 text-center text-sm text-secondary">
                Already have an account? 
                <Link to="/login" className="btn btn-link p-0 ms-1 text-decoration-none">
                    Login in
                </Link>
            </p>
            {message && (
                <div className={`mt-3 alert ${message.includes('Failed') ? 'alert-danger' : 'alert-success'}`} role="alert">
                    {message}
                </div>
            )}
        </div>
    );
};

export default RegisterPage;
