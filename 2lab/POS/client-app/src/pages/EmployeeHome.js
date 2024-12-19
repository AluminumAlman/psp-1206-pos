import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/EmployeeHome.css";
import { getCurrentUser, assignMerchantToUser } from "../api/users";
import { getAllMerchants, getMerchant } from "../api/merchants";

export const EmployeeHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [merchants, setMerchants] = useState([]);
    const [assignedMerchantName, setAssignedMerchantName] = useState("");
    const [selectedMerchant, setSelectedMerchant] = useState("");
    const [showMerchantModal, setShowMerchantModal] = useState(false);
    const [showDetails, setShowDetails] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (currentUser?.merchantId) {
                const merchant = await getMerchant(token, currentUser.merchantId);
                setAssignedMerchantName(merchant.name);
            } else {
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
        const merchant = await getMerchant(token, selectedMerchant);
        setUser(updatedUser);
        setAssignedMerchantName(merchant.name);
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
                )
            )}
        </div>
    );
};
