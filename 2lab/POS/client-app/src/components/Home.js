import React, { useEffect, useState } from 'react';
import './Home.css';

export const Home = () => {
    const [user, setUser] = useState(null);
    const [showDetails, setShowDetails] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            return;
        }

        fetch('http://localhost:8080/users/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            credentials: 'include',
        })
            .then((response) => (response.ok ? response.json() : Promise.reject(response)))
            .then((data) => setUser(data))
            .catch((error) => console.error('Failed to fetch user info:', error));
    }, []);

    return (
        <div className="home-container">
            {user && (
                <div className="user-box">
                    <div className="user-header">
                        <span className="user-name">
                            {user.firstName} {user.lastName}
                        </span>
                        <button className="details-button" onClick={() => setShowDetails(!showDetails)}>
                            ...
                        </button>
                    </div>
                    {showDetails && (
                        <div className="details-box">
                            <p>Email: {user.email}</p>
                            <p>
                                Role: <strong>{user.role}</strong>
                            </p>
                            <p>
                                Merchant:{' '}
                                <strong>
                                    {user.merchantId ? user.merchantId : 'Unassigned'}
                                </strong>
                            </p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};
