import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css';

export const Login = () => {
    const [showDialog, setShowDialog] = useState(false);
    const [formType, setFormType] = useState('login'); // 'login' or 'register'
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    // Clear JWT token when the component is first rendered
    useEffect(() => {
        localStorage.removeItem('jwt-token');
    }, []);

    // Function to handle login form submission
    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // Includes cookies
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Login failed. Please try again.');
                return;
            }

            const data = await response.json();
            localStorage.setItem('jwt-token', data['jwt-token']);
            navigate('/home'); // Navigate to the home page
        } catch (error) {
            setErrorMessage('An error occurred. Please try again.');
            console.error('Login error:', error);
        }
    };

    // Function to handle registration form submission
    const handleRegisterSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        // Password matching validation
        if (password !== repeatPassword) {
            setErrorMessage('Passwords do not match.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // Includes cookies
                body: JSON.stringify({
                    firstName: name,
                    lastName: surname,
                    email,
                    password,
                    role: 'EMPLOYEE', // You can change this based on requirements
                }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Registration failed. Please try again.');
                return;
            }

            const data = await response.json();
            localStorage.setItem('jwt-token', data['jwt-token']);
            setFormType('login'); // Switch to login after successful registration
        } catch (error) {
            setErrorMessage('An error occurred. Please try again.');
            console.error('Registration error:', error);
        }
    };

    // Switch between login and registration forms
    const switchForm = (type) => {
        setFormType(type);
        setShowDialog(true);
        setErrorMessage('');
    };

    return (
        <div className="login-panel">
            <h1 className="shop-title">Shopipy</h1>
            <button onClick={() => switchForm('login')} className="login-button">Login</button>
            {showDialog && (
                <div className="dialog">
                    <div className="dialog-content">
                        {formType === 'login' ? (
                            <form onSubmit={handleLoginSubmit} className="login-form">
                                <h2>Login</h2>
                                {errorMessage && <p className="error-message">{errorMessage}</p>}
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                                <button className="submit-button" type="submit">Log In</button>
                                <p className="register-link">
                                    Not a user? <span onClick={() => switchForm('register')}>Register here</span>
                                </p>
                                <button onClick={() => setShowDialog(false)} className="close-dialog">Close</button>
                            </form>
                        ) : (
                            <form onSubmit={handleRegisterSubmit} className="register-form">
                                <h2>Register</h2>
                                {errorMessage && <p className="error-message">{errorMessage}</p>}
                                <input
                                    type="text"
                                    placeholder="Name"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                                <input
                                    type="text"
                                    placeholder="Surname"
                                    value={surname}
                                    onChange={(e) => setSurname(e.target.value)}
                                    required
                                />
                                <input
                                    type="email"
                                    placeholder="Email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                                <input
                                    type="password"
                                    placeholder="Repeat Password"
                                    value={repeatPassword}
                                    onChange={(e) => setRepeatPassword(e.target.value)}
                                    required
                                />
                                <button className="submit-button" type="submit">Register</button>
                                <p className="register-link">
                                    Already a user? <span onClick={() => switchForm('login')}>Login here</span>
                                </p>
                                <button onClick={() => setShowDialog(false)} className="close-dialog">Close</button>
                            </form>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};
