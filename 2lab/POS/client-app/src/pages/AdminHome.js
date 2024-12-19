import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/AdminHome.css";
import {
    getCurrentUser,
    assignMerchantToUser,
    switchMerchant,
    getUsers,
    updateUser,
    deleteUser
} from "../api/users";
import {
    getAllMerchants,
    getMerchant,
    updateMerchant,
    deleteMerchant
} from "../api/merchants";

export const AdminHome = () => {
    const token = localStorage.getItem("jwt-token");
    const [user, setUser] = useState(null);
    const [merchants, setMerchants] = useState([]);
    const [selectedMerchant, setSelectedMerchant] = useState('');
    const [showMerchantModal, setShowMerchantModal] = useState(false);
    const [showDetails, setShowDetails] = useState(false);

    const [merchantInfo, setMerchantInfo] = useState(null);
    const [merchantUsers, setMerchantUsers] = useState([]);

    // State for editing merchant
    const [showMerchantEditModal, setShowMerchantEditModal] = useState(false);
    const [merchantForm, setMerchantForm] = useState({
        name: '',
        phone: '',
        email: '',
        currency: '',
        address: '',
        city: '',
        country: '',
        postcode: ''
    });

    // State for editing user
    const [showUserEditModal, setShowUserEditModal] = useState(false);
    const [userForm, setUserForm] = useState({
        id: '',
        firstName: '',
        lastName: '',
        email: '',
        role: ''
    });

    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            const merchantsData = await getAllMerchants(token);
            setMerchants(merchantsData);

            const currentUser = await getCurrentUser(token);
            setUser(currentUser);

            if (!currentUser?.merchantId) {
                // No merchant assigned, show selection modal
                setShowMerchantModal(true);
            } else {
                await loadMerchantData(currentUser.merchantId);
            }
        }
        init();
    }, [token]);

    async function loadMerchantData(merchantId) {
        const merchantData = await getMerchant(token, merchantId);
        setMerchantInfo(merchantData);
        setMerchantForm({
            name: merchantData.name,
            phone: merchantData.phone,
            email: merchantData.email,
            currency: merchantData.currency,
            address: merchantData.address,
            city: merchantData.city,
            country: merchantData.country,
            postcode: merchantData.postcode,
        });

        // Get users filtered by merchantId
        const usersData = await getUsers(token);
        const filteredUsers = usersData.filter(u => u.merchantId === merchantId);
        setMerchantUsers(filteredUsers);
    }

    const handleMerchantAssign = async () => {
        if (!selectedMerchant) return;

        await assignMerchantToUser(token, user.id, selectedMerchant);
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        setShowMerchantModal(false);
        await loadMerchantData(updatedUser.merchantId);
    };

    const handleMerchantUnassign = async () => {
        await switchMerchant(token);
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        setMerchantInfo(null);
        setMerchantUsers([]);
        navigate('/');
    };

    const handleSwitchMerchant = async () => {
        setShowMerchantModal(true);
    };

    const handleMerchantEditOpen = () => {
        setShowMerchantEditModal(true);
    };

    const handleMerchantFormChange = (e) => {
        setMerchantForm({ ...merchantForm, [e.target.name]: e.target.value });
    };

    const handleMerchantUpdate = async () => {
        if (!user.merchantId) return;
        const updated = await updateMerchant(token, user.merchantId, merchantForm);
        setMerchantInfo(updated);
        setShowMerchantEditModal(false);
    };

    const handleMerchantDelete = async () => {
        if (!user.merchantId) return;

        const merchantIdToDelete = user.merchantId;

        // Now the backend handles deleting the merchant and adjusting users
        await deleteMerchant(token, merchantIdToDelete);

        // Refresh current user data after deletion
        const updatedUser = await getCurrentUser(token);
        setUser(updatedUser);
        setMerchantInfo(null);
        setMerchantUsers([]);

        // If current user is SUPER_ADMIN with no merchant, show the merchant selection modal
        if (!updatedUser.merchantId && updatedUser.role === "SUPER_ADMIN") {
            setShowMerchantModal(true);
        }
        window.location.reload();
    };

    // User Edit Handlers
    const handleUserEditOpen = (userToEdit) => {
        setUserForm({
            id: userToEdit.id,
            firstName: userToEdit.firstName,
            lastName: userToEdit.lastName,
            email: userToEdit.email,
            role: userToEdit.role
        });
        setShowUserEditModal(true);
    };

    const handleUserFormChange = (e) => {
        setUserForm({ ...userForm, [e.target.name]: e.target.value });
    };

    const handleUserUpdate = async () => {
        await updateUser(token, userForm.id, {
            firstName: userForm.firstName,
            lastName: userForm.lastName,
            email: userForm.email,
            password: "dummyPassword",
            role: userForm.role
        });
        if (user.merchantId) {
            await loadMerchantData(user.merchantId);
        }
        setShowUserEditModal(false);
    };

    const handleUserDelete = async (userId) => {
        await deleteUser(token, userId);
        if (user.merchantId) {
            await loadMerchantData(user.merchantId);
        }
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
                <>
                    {user && (
                        <div className="user-box" onClick={() => setShowDetails(!showDetails)}>
                            <div className="user-header">
                                <span className="user-name">
                                    {user.firstName} {user.lastName} ({user.role})
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
                    )}

                    {merchantInfo && (
                        <div className="merchant-section">
                            <h2>Merchant Info</h2>
                            <div className="merchant-info">
                                <p>Name: {merchantInfo.name}</p>
                                <p>Phone: {merchantInfo.phone}</p>
                                <p>Email: {merchantInfo.email}</p>
                                <p>Currency: {merchantInfo.currency}</p>
                                <p>Address: {merchantInfo.address}</p>
                                <p>City: {merchantInfo.city}</p>
                                <p>Country: {merchantInfo.country}</p>
                                <p>Postcode: {merchantInfo.postcode}</p>
                                <button onClick={handleMerchantEditOpen}>Edit</button>
                                <button onClick={handleMerchantDelete}>Delete Merchant</button>
                            </div>
                        </div>
                    )}

                    {merchantUsers.length > 0 && (
                        <div className="users-table-section">
                            <h2>Users of this Merchant</h2>
                            <table className="users-table">
                                <thead>
                                <tr>
                                    <th>First Name</th>
                                    <th>Last Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {merchantUsers.map(u => (
                                    <tr key={u.id}>
                                        <td>{u.firstName}</td>
                                        <td>{u.lastName}</td>
                                        <td>{u.email}</td>
                                        <td>{u.role}</td>
                                        <td>
                                            <button onClick={() => handleUserEditOpen(u)}>Edit</button>
                                            <button onClick={() => handleUserDelete(u.id)}>Delete</button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    {/* Merchant Edit Modal */}
                    {showMerchantEditModal && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <h2>Edit Merchant</h2>
                                <form>
                                    <label>Name: <input name="name" value={merchantForm.name} onChange={handleMerchantFormChange} /></label>
                                    <label>Phone: <input name="phone" value={merchantForm.phone} onChange={handleMerchantFormChange} /></label>
                                    <label>Email: <input name="email" value={merchantForm.email} onChange={handleMerchantFormChange} /></label>
                                    <label>Currency: <input name="currency" value={merchantForm.currency} onChange={handleMerchantFormChange} /></label>
                                    <label>Address: <input name="address" value={merchantForm.address} onChange={handleMerchantFormChange} /></label>
                                    <label>City: <input name="city" value={merchantForm.city} onChange={handleMerchantFormChange} /></label>
                                    <label>Country: <input name="country" value={merchantForm.country} onChange={handleMerchantFormChange} /></label>
                                    <label>Postcode: <input name="postcode" value={merchantForm.postcode} onChange={handleMerchantFormChange} /></label>
                                </form>
                                <div className="modal-buttons">
                                    <button onClick={handleMerchantUpdate}>Save</button>
                                    <button onClick={() => setShowMerchantEditModal(false)}>Cancel</button>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* User Edit Modal */}
                    {showUserEditModal && (
                        <div className="modal-overlay">
                            <div className="modal-content">
                                <h2>Edit User</h2>
                                <form>
                                    <label>First Name: <input name="firstName" value={userForm.firstName} onChange={handleUserFormChange} /></label>
                                    <label>Last Name: <input name="lastName" value={userForm.lastName} onChange={handleUserFormChange} /></label>
                                    <label>Email: <input name="email" value={userForm.email} onChange={handleUserFormChange} /></label>
                                    <label>Role:
                                        <select name="role" value={userForm.role} onChange={handleUserFormChange}>
                                            <option value="EMPLOYEE">EMPLOYEE</option>
                                            <option value="MERCHANT_OWNER">MERCHANT_OWNER</option>
                                            <option value="SUPER_ADMIN">SUPER_ADMIN</option>
                                        </select>
                                    </label>
                                </form>
                                <div className="modal-buttons">
                                    <button onClick={handleUserUpdate}>Save</button>
                                    <button onClick={() => setShowUserEditModal(false)}>Cancel</button>
                                </div>
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};
