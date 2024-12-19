import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/EmployeeHome.css";
import { getCurrentUser } from "../api/users";
import { getAllMerchants } from "../api/merchants";
import { assignMerchantToUser } from "../api/users";

export const EmployeeHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [merchants, setMerchants] = useState([]);
    const [selectedMerchant, setSelectedMerchant] = useState("");
    const [showMerchantModal, setShowMerchantModal] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (!currentUser?.merchantId) {
                const merchantsData = await getAllMerchants(token);
                setMerchants(merchantsData);
                setShowMerchantModal(true);
            }
        }
        init();
    }, [token]);

    const handleAssignMerchant = async () => {
        if (!selectedMerchant) return;

        await assignMerchantToUser(token, user.id, selectedMerchant);
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        setShowMerchantModal(false);
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    return (
        <div className="home-container employee-content">
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
                        onClick={handleAssignMerchant}
                        disabled={!selectedMerchant}
                    >
                        Assign Merchant
                    </button>
                </div>
            ) : (
                user && (
                    <div className="user-box">
                        <h1>Welcome, {user.firstName}!</h1>
                        <button onClick={handleLogout} className="logout-button">Logout</button>
                    </div>
                )
            )}
        </div>
    );
};
