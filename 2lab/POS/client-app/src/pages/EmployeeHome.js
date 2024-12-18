import React, { useEffect, useState } from 'react';
import "../styles/Home.css";
import "../styles/EmployeeHome.css"; // New CSS file for Employee page
import { getCurrentUser } from "../api/users";
import { getServices, createService } from "../api/services"; // createService not necessarily needed here
import { createReservation } from "../api/reservations";

export const EmployeeHome = () => {
    const token = localStorage.getItem('jwt-token');
    const [user, setUser] = useState(null);
    const [showDetails, setShowDetails] = useState(false);
    const [services, setServicesData] = useState([]);
    const [selectedService, setSelectedService] = useState('');
    const [reservationForm, setReservationForm] = useState({
        firstName: '',
        lastName: '',
        phone: '',
        appointedAt: '',
    });

    useEffect(() => {
        async function init() {
            if (!token) return;
            const u = await getCurrentUser(token);
            setUser(u);
            if (u.merchantId) {
                // Get services with merchant filter if available:
                // The provided endpoints don't have merchantId param directly for GET /services
                // But we can just fetch all and filter if needed. We'll assume we can filter by merchantId if supported.
                const svcData = await getServices(token, {limit:50});
                // If merchant-based filtering is required and not supported by backend,
                // you'd filter here. For now, we just use svcData.content.
                setServicesData(svcData.content || []);
            }
        }
        init();
    }, [token]);

    const handleCreateReservation = async () => {
        if (!selectedService || !reservationForm.firstName || !reservationForm.lastName || !reservationForm.appointedAt || !user) return;
        await createReservation(token, {
            serviceId: selectedService,
            employeeId: user.id,
            appointedAt: reservationForm.appointedAt,
            firstName: reservationForm.firstName,
            lastName: reservationForm.lastName,
            phone: reservationForm.phone || '+10000000000'
        });
        setReservationForm({ firstName: '', lastName: '', phone: '', appointedAt: '' });
    };

    return (
        <div className="home-container employee-content">
            {user && (
                <div className="user-box">
                    <div className="user-header">
                        <span className="user-name">{user.firstName} {user.lastName} (Employee)</span>
                        <button className="details-button" onClick={() => setShowDetails(!showDetails)}>...</button>
                    </div>
                    {showDetails && (
                        <div className="details-box">
                            <p>Email: {user.email}</p>
                            <p>
                                Role: <strong>{user.role}</strong>
                            </p>
                            <p>
                                Merchant: <strong>{user.merchantId || 'Unassigned'}</strong>
                            </p>
                        </div>
                    )}
                </div>
            )}

            <div className="employee-services-section">
                <h2>Services</h2>
                {services.length === 0 ? <p>No services found.</p> : (
                    <select value={selectedService} onChange={(e) => setSelectedService(e.target.value)}>
                        <option value="">Select a service</option>
                        {services.map(svc => (
                            <option key={svc.id} value={svc.id}>{svc.name}</option>
                        ))}
                    </select>
                )}

                <h3>Create Reservation</h3>
                <input
                    type="text"
                    placeholder="Customer First Name"
                    value={reservationForm.firstName}
                    onChange={(e) => setReservationForm({ ...reservationForm, firstName: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Customer Last Name"
                    value={reservationForm.lastName}
                    onChange={(e) => setReservationForm({ ...reservationForm, lastName: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Phone (+10000000000)"
                    value={reservationForm.phone}
                    onChange={(e) => setReservationForm({ ...reservationForm, phone: e.target.value })}
                />
                <input
                    type="datetime-local"
                    value={reservationForm.appointedAt}
                    onChange={(e) => setReservationForm({ ...reservationForm, appointedAt: e.target.value })}
                />
                <button onClick={handleCreateReservation}>Create Reservation</button>
            </div>
        </div>
    );
};
