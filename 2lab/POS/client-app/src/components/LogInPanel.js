// src/components/LogInPanel.js
import React, { useState } from 'react';
import './LogInPanel.css';

export const LogInPanel = () => {
    const [showDialog, setShowDialog] = useState(false);
    const [formType, setFormType] = useState('login'); // 'login' or 'register'
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');

    const handleLoginSubmit = (e) => {
        e.preventDefault();
        // Implement login logic
    };

    const handleRegisterSubmit = (e) => {
        e.preventDefault();
        // Implement registration logic
    };

    const switchForm = (type) => {
        setFormType(type);
        setShowDialog(true);
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
                                <input type="email" placeholder="Email" onChange={e => setEmail(e.target.value)}/>
                                <input type="password" placeholder="Password"
                                       onChange={e => setPassword(e.target.value)}/>
                                <button className="submit-button" type="submit">Log In</button>
                                <button onClick={() => setShowDialog(false)} className="close-dialog">Close</button>
                                <p className="register-link">Not a user? <span onClick={() => switchForm('register')}>Register here</span></p>
                            </form>
                        ) : (
                            <form onSubmit={handleRegisterSubmit} className="register-form">
                                <h2>Register</h2>
                                <input type="text" placeholder="Name" onChange={e => setName(e.target.value)}/>
                                <input type="text" placeholder="Surname" onChange={e => setSurname(e.target.value)}/>
                                <input type="email" placeholder="Email" onChange={e => setEmail(e.target.value)}/>
                                <input type="password" placeholder="Password"
                                       onChange={e => setPassword(e.target.value)}/>
                                <input type="password" placeholder="Repeat Password"
                                       onChange={e => setRepeatPassword(e.target.value)}/>
                                <button className="submit-button" type="submit">Register</button>
                                <button onClick={() => setShowDialog(false)} className="close-dialog">Close</button>
                                <p className="register-link">Already a user? <span onClick={() => switchForm('login')}>Login here</span></p>
                            </form>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default LogInPanel;
