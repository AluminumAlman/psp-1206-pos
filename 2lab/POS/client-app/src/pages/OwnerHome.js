import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/OwnerHome.css";
import { getCurrentUser, assignMerchantToUser } from "../api/users";
import { createMerchant, getMerchant } from "../api/merchants";

export const OwnerHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [formData, setFormData] = useState({
        name: "",
        phone: "",
        email: "",
        currency: "",
        address: "",
        city: "",
        country: "",
        postcode: "",
    });
    const [assignedMerchantName, setAssignedMerchantName] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [showDetails, setShowDetails] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (currentUser?.merchantId) {
                const merchant = await getMerchant(token, currentUser.merchantId);
                setAssignedMerchantName(merchant.name);
            }
        }
        init();
    }, [token]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCreateBusiness = async () => {
        if (Object.values(formData).some((field) => !field)) {
            setErrorMessage("All fields are required.");
            return;
        }

        try {
            // Create the merchant
            const createdMerchant = await createMerchant(token, formData);

            // Assign the merchant to the user
            await assignMerchantToUser(token, user.id, createdMerchant.id);

            // Update user data
            const updatedUser = await getCurrentUser(token);
            setUser(updatedUser);
            setAssignedMerchantName(createdMerchant.name);
        } catch (error) {
            setErrorMessage("Failed to create business. Please try again.");
            console.error("Business creation error:", error);
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    return (
        <div className="home-container owner-content">
            {user && !user.merchantId ? (
                <div className="business-modal">
                    <h2>Create Your Business</h2>
                    {errorMessage && <p className="error-message">{errorMessage}</p>}
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="phone"
                        value={formData.phone}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="currency"
                        value={formData.currency}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="country"
                        value={formData.country}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="postcode"
                        value={formData.postcode}
                        onChange={handleInputChange}
                        required
                    />
                    <button className="create-button" onClick={handleCreateBusiness}>
                        Create Business
                    </button>
                </div>
            ) : (
                user && (
                    <div
                        className="user-box"
                        onClick={() => setShowDetails(!showDetails)}
                    >
                        <div className="user-header">
                            <span className="user-name">
                                {user.firstName} {user.lastName} (Owner)
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
                )
            )}
        </div>
    );
};
