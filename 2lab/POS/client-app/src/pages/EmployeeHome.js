import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/EmployeeHome.css";
import { getCurrentUser, assignMerchantToUser } from "../api/users";
import { getAllMerchants, getMerchant } from "../api/merchants";
import { getServices, getAvailableSlots } from "../api/services";
import { createReservation } from "../api/reservations";

export const EmployeeHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [merchants, setMerchants] = useState([]);
    const [assignedMerchantName, setAssignedMerchantName] = useState("");
    const [selectedMerchant, setSelectedMerchant] = useState("");
    const [showMerchantModal, setShowMerchantModal] = useState(false);
    const [showDetails, setShowDetails] = useState(false);

    const [services, setServices] = useState([]);
    const [selectedServiceId, setSelectedServiceId] = useState("");
    const [selectedDate, setSelectedDate] = useState("");
    const [availableSlots, setAvailableSlots] = useState([]);

    // Reservation modal states
    const [showReservationModal, setShowReservationModal] = useState(false);
    const [reservationSlot, setReservationSlot] = useState(null);
    const [reservationData, setReservationData] = useState({
        firstName: "",
        lastName: "",
        phone: ""
    });
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (currentUser?.merchantId) {
                const merchant = await getMerchant(token, currentUser.merchantId);
                setAssignedMerchantName(merchant.name);
                await loadServices();
            } else {
                const merchantsData = await getAllMerchants(token);
                setMerchants(merchantsData);
                setShowMerchantModal(true);
            }
        }
        init();
    }, [token]);

    const loadServices = async () => {
        const serviceData = await getServices(token, {});
        if (serviceData && serviceData.content) {
            setServices(serviceData.content);
        }
    };

    const handleAssignMerchant = async () => {
        if (!selectedMerchant) return;

        await assignMerchantToUser(token, user.id, selectedMerchant);
        const updatedUser = await getCurrentUser(token);
        const merchant = await getMerchant(token, selectedMerchant);
        setUser(updatedUser);
        setAssignedMerchantName(merchant.name);
        setShowMerchantModal(false);

        await loadServices();
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    const handleCheckSlots = async () => {
        if (!selectedServiceId || !selectedDate || !user) return;
        try {
            const date = new Date(selectedDate);
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const day = date.getDate().toString().padStart(2, '0');
            const formattedDate = `${year}-${month}-${day}`;

            const slotsResponse = await getAvailableSlots(token, selectedServiceId, formattedDate, user.id);
            if (slotsResponse && slotsResponse.items) {
                setAvailableSlots(slotsResponse.items);
            } else {
                setAvailableSlots([]);
            }
            setErrorMessage("");
            setSuccessMessage("");
        } catch (error) {
            console.error("Error fetching slots:", error);
            setErrorMessage("Failed to fetch available slots.");
        }
    };

    const handleOpenReservationModal = (slot) => {
        setReservationSlot(slot);
        setReservationData({ firstName: "", lastName: "", phone: "" });
        setShowReservationModal(true);
        setErrorMessage("");
        setSuccessMessage("");
    };

    const handleReservationInputChange = (e) => {
        const { name, value } = e.target;
        setReservationData(prev => ({ ...prev, [name]: value }));
    };

    const handleCreateReservation = async (e) => {
        e.preventDefault();
        const { firstName, lastName, phone } = reservationData;

        if (!firstName.trim() || !lastName.trim() || !phone.trim()) {
            setErrorMessage("All fields are required.");
            return;
        }

        try {
            const reservationRequest = {
                serviceId: selectedServiceId,
                employeeId: user.id, // The current user is the employee
                appointedAt: reservationSlot.startTime,
                firstName,
                lastName,
                phone
            };

            const response = await createReservation(token, reservationRequest);
            if (response && response.id) {
                setSuccessMessage("Reservation created successfully!");
                setErrorMessage("");
                // Close the modal after a short delay
                setTimeout(() => {
                    setShowReservationModal(false);
                    setSuccessMessage("");
                    setReservationData({ firstName: "", lastName: "", phone: "" });
                }, 2000);
            } else {
                setErrorMessage("Failed to create reservation. Please try again.");
            }
        } catch (error) {
            console.error("Error creating reservation:", error);
            setErrorMessage("Failed to create reservation. Please try again.");
        }
    };

    return (
        <div className="home-container employee-content">
            {showMerchantModal ? (
                <div className="merchant-modal">
                    <h2>Select a Merchant</h2>
                    {errorMessage && <p className="error-message">{errorMessage}</p>}
                    <select
                        value={selectedMerchant}
                        onChange={(e) => setSelectedMerchant(e.target.value)}
                    >
                        <option value="">Choose...</option>
                        {merchants.map((merchant) => (
                            <option key={merchant.id} value={merchant.id}>
                                {merchant.name} ({merchant.city})
                            </option>
                        ))}
                    </select>
                    <button
                        className="assign-button"
                        onClick={handleAssignMerchant}
                        disabled={!selectedMerchant}
                    >
                        Assign Merchant
                    </button>
                </div>
            ) : (
                user && (
                    <div className="employee-dashboard">
                        <div
                            className="user-box"
                            onClick={() => setShowDetails(!showDetails)}
                        >
                            <div className="user-header">
                                <span className="user-name">
                                    {user.firstName} {user.lastName} (Employee)
                                </span>
                            </div>
                            {showDetails && (
                                <div className="details-box">
                                    <p>Email: {user.email}</p>
                                    <p>
                                        Role: <strong>{user.role}</strong>
                                    </p>
                                    <p>
                                        Assigned Merchant:{" "}
                                        <strong>{assignedMerchantName || "None"}</strong>
                                    </p>
                                    <button
                                        className="switch-button"
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>

                        {user.merchantId && (
                            <div className="services-section">
                                <h2>Services</h2>
                                {errorMessage && <p className="error-message">{errorMessage}</p>}
                                {successMessage && <p className="success-message">{successMessage}</p>}
                                <p>Select a service and date to see available slots:</p>
                                <select value={selectedServiceId} onChange={(e)=>setSelectedServiceId(e.target.value)}>
                                    <option value="">Select Service</option>
                                    {services.map(s => (
                                        <option key={s.id} value={s.id}>{s.name} - {s.price} USD, {s.duration}s</option>
                                    ))}
                                </select>

                                <input
                                    type="date"
                                    value={selectedDate}
                                    onChange={(e)=>setSelectedDate(e.target.value)}
                                />
                                <button onClick={handleCheckSlots}>Check Slots</button>

                                {availableSlots.length > 0 && (
                                    <div className="slots-section">
                                        <h3>Available Slots</h3>
                                        <ul>
                                            {availableSlots.map((slot, index) => (
                                                <li key={index}>
                                                    {new Date(slot.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})} - {new Date(slot.endTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                                                    <button className="book-button" onClick={() => handleOpenReservationModal(slot)}>Book</button>
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                )}

                                {availableSlots.length === 0 && selectedServiceId && selectedDate && (
                                    <p>No available slots for the selected service and date.</p>
                                )}
                            </div>
                        )}
                    </div>
                )
            )}

            {showReservationModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Reservation</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        {successMessage && <p className="success-message">{successMessage}</p>}
                        <form onSubmit={handleCreateReservation} className="reservation-form">
                            <input
                                type="text"
                                name="firstName"
                                placeholder="Customer First Name"
                                value={reservationData.firstName}
                                onChange={handleReservationInputChange}
                                required
                            />
                            <input
                                type="text"
                                name="lastName"
                                placeholder="Customer Last Name"
                                value={reservationData.lastName}
                                onChange={handleReservationInputChange}
                                required
                            />
                            <input
                                type="text"
                                name="phone"
                                placeholder="Customer Phone (+xxxxxxxxxxxx)"
                                value={reservationData.phone}
                                onChange={handleReservationInputChange}
                                required
                            />
                            <div className="modal-buttons">
                                {!successMessage && <button type="submit">Confirm</button>}
                                <button type="button" onClick={() => setShowReservationModal(false)}>Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};
