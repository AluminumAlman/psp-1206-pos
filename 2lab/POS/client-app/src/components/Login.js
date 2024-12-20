import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Login.css';

export const Login = () => {
    const [showDialog, setShowDialog] = useState(false);
    const [formType, setFormType] = useState('login'); // 'login' or 'register'
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const [isJoiningBusiness, setIsJoiningBusiness] = useState(false);
    const [isRegularSchedule, setIsRegularSchedule] = useState(true); // New state for checkbox
    const [schedule, setSchedule] = useState({
        MONDAY: null,
        TUESDAY: null,
        WEDNESDAY: null,
        THURSDAY: null,
        FRIDAY: null,
        SATURDAY: null,
        SUNDAY: null
    }); // New state for schedule
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        // Clear tokens on component mount
        localStorage.removeItem('jwt-token');
        localStorage.removeItem('user-role');
    }, []);

    // Handle checkbox change
    const handleRegularScheduleChange = (e) => {
        const checked = e.target.checked;
        setIsRegularSchedule(checked);
        if (checked) {
            // Set regular schedule: Mon-Fri 08:00-17:00, Sat-Sun null
            setSchedule({
                MONDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                TUESDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                WEDNESDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                THURSDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                FRIDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                SATURDAY: null,
                SUNDAY: null
            });
        } else {
            // Reset schedule to allow manual input
            setSchedule({
                MONDAY: null,
                TUESDAY: null,
                WEDNESDAY: null,
                THURSDAY: null,
                FRIDAY: null,
                SATURDAY: null,
                SUNDAY: null
            });
        }
    };

    // Handle schedule input changes
    const handleScheduleChange = (day, field, value) => {
        setSchedule(prevSchedule => ({
            ...prevSchedule,
            [day]: {
                ...prevSchedule[day],
                [field]: value
            }
        }));
    };

    const fetchUserRoleAndNavigate = async (token) => {
        try {
            const response = await fetch('http://localhost:8080/users/me', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch user role');
            }

            const userData = await response.json();
            const role = userData.role;

            localStorage.setItem('user-role', role);

            if (role === 'SUPER_ADMIN') {
                navigate('/admin-home');
            } else if (role === 'MERCHANT_OWNER') {
                navigate('/owner-home');
            } else if (role === 'EMPLOYEE') {
                navigate('/employee-home');
            } else {
                setErrorMessage('Unknown role. Please contact admin.');
            }
        } catch (error) {
            setErrorMessage('An error occurred while fetching user data.');
            console.error('Error fetching role:', error);
        }
    };

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Login failed. Please try again.');
                return;
            }

            const data = await response.json();
            const jwtToken = data['jwt-token'];

            localStorage.setItem('jwt-token', jwtToken);

            await fetchUserRoleAndNavigate(jwtToken);
        } catch (error) {
            setErrorMessage('An error occurred. Please try again.');
            console.error('Login error:', error);
        }
    };

    const handleRegisterSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        if (password !== repeatPassword) {
            setErrorMessage('Passwords do not match.');
            return;
        }

        // Validate schedule if not using regular schedule
        if (!isRegularSchedule) {
            for (const day in schedule) {
                if (schedule[day]) {
                    const { startTime, endTime } = schedule[day];
                    if (!startTime || !endTime) {
                        setErrorMessage(`Please provide both start and end times for ${day}.`);
                        return;
                    }
                }
            }
        }

        try {
            const body = {
                firstName: name,
                lastName: surname,
                email,
                password,
                role: isJoiningBusiness ? 'EMPLOYEE' : 'MERCHANT_OWNER',
                schedule: isJoiningBusiness ? schedule : schedule // Include schedule only if joining a business
            };

            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(body),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Registration failed. Please try again.');
                return;
            }

            const data = await response.json();
            localStorage.setItem('jwt-token', data['jwt-token']);
            setFormType('login');
            setShowDialog(false);
            setErrorMessage('');
        } catch (error) {
            setErrorMessage('An error occurred. Please try again.');
            console.error('Registration error:', error);
        }
    };

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
                                <div className="role-selection">
                                    <label>
                                        <input
                                            type="checkbox"
                                            checked={isJoiningBusiness}
                                            onChange={(e) => setIsJoiningBusiness(e.target.checked)}
                                        />
                                        Join a business as an employee
                                    </label>
                                </div>
                                {/* New Checkbox for Regular Work Days */}
                                <div className="schedule-selection">
                                    <label>
                                        <input
                                            type="checkbox"
                                            checked={isRegularSchedule}
                                            onChange={handleRegularScheduleChange}
                                        />
                                        I work regular work days (Mon-Fri 08:00-17:00)
                                    </label>
                                </div>
                                {/* Conditional Schedule Inputs */}
                                {!isRegularSchedule && (
                                    <div className="custom-schedule">
                                        <h3>Set Your Work Schedule</h3>
                                        {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(day => (
                                            <div key={day} className="schedule-day">
                                                <label>{day.charAt(0) + day.slice(1).toLowerCase()}</label>
                                                <div className="time-inputs">
                                                    <input
                                                        type="time"
                                                        placeholder="Start Time"
                                                        value={schedule[day] && schedule[day].startTime ? schedule[day].startTime.substring(0,5) : ''}
                                                        onChange={(e) => handleScheduleChange(day, 'startTime', e.target.value ? `${e.target.value}:00` : null)}
                                                    />
                                                    <span>to</span>
                                                    <input
                                                        type="time"
                                                        placeholder="End Time"
                                                        value={schedule[day] && schedule[day].endTime ? schedule[day].endTime.substring(0,5) : ''}
                                                        onChange={(e) => handleScheduleChange(day, 'endTime', e.target.value ? `${e.target.value}:00` : null)}
                                                    />
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
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
