import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/AdminHome.css";
import { getCurrentUser, assignMerchantToUser, switchMerchant } from "../api/users";
import { getAllMerchants } from "../api/merchants";

export const AdminHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [merchants, setMerchants] = useState([]);
    const [selectedMerchant, setSelectedMerchant] = useState('');
    const [showMerchantModal, setShowMerchantModal] = useState(false);
    const [showDetails, setShowDetails] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const merchantsData = await getAllMerchants(token);
            setMerchants(merchantsData);

            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (!currentUser?.merchantId) {
                setShowMerchantModal(true);
            }
        }
        init();
    }, [token]);

    const handleMerchantAssign = async () => {
        if (!selectedMerchant) return;

        await assignMerchantToUser(token, user.id, selectedMerchant);
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        setShowMerchantModal(false);
    };

    const handleMerchantUnassign = async () => {
        await switchMerchant(token);
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        navigate('/');
    };

    const handleSwitchMerchant = async () => {
        setShowMerchantModal(true);
    };

    return (
        <div className="home-container admin-content">
            {showMerchantModal ? (
                <div className="merchant-modal">
                    <h2>Select a Merchant</h2>
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
                        onClick={handleMerchantAssign}
                        disabled={!selectedMerchant}
                    >
                        Assign Merchant
                    </button>
                </div>
            ) : (
                user && (
                    <div className="user-box" onClick={() => setShowDetails(!showDetails)}>
                        <div className="user-header">
                            <span className="user-name">
                                {user.firstName} {user.lastName} (Admin)
                            </span>
                        </div>
                        {showDetails && (
                            <div className="details-box">
                                <p>Email: {user.email}</p>
                                <p>Role: <strong>{user.role}</strong></p>
                                <p>Assigned Merchant: <strong>{merchants.find(m => m.id === user.merchantId)?.name || "None"}</strong></p>
                                <button className="switch-button" onClick={handleSwitchMerchant}>
                                    Switch Merchant
                                </button>
                                <button className="switch-button" onClick={handleMerchantUnassign}>
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
