import React, { useEffect, useState } from "react";
import "../styles/Home.css";
import "../styles/AdminHome.css"; // New CSS for Admin page
import { getCurrentUser, getUsers, updateUser, deleteUser } from "../api/users";
import { getMerchants, deleteMerchant } from "../api/merchants";

export const AdminHome = () => {
    const token = localStorage.getItem('jwt-token');
    const [user, setUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [merchants, setMerchantsData] = useState([]);
    const [showDetails, setShowDetails] = useState(false);

    const [editingUser, setEditingUser] = useState(null);
    const [newUserRole, setNewUserRole] = useState('');

    useEffect(() => {
        async function init() {
            const cu = await getCurrentUser(token);
            setUser(cu);
            const us = await getUsers(token);
            setUsers(us);
            const ms = await getMerchants(token);
            setMerchantsData(ms);
        }
        init();
    }, [token]);

    const handleUserUpdate = async (userId) => {
        await updateUser(token, userId, { role: newUserRole });
        const refreshed = await getUsers(token);
        setUsers(refreshed);
        setEditingUser(null);
        setNewUserRole('');
    };

    const handleUserDelete = async (userId) => {
        await deleteUser(token, userId);
        setUsers(users.filter(u => u.id !== userId));
    };

    const handleMerchantDelete = async (merchantId) => {
        await deleteMerchant(token, merchantId);
        setMerchantsData(merchants.filter(m => m.id !== merchantId));
    };

    return (
        <div className="home-container admin-content">
            {user && (
                <div className="user-box">
                    <div className="user-header">
                        <span className="user-name">
                          {user.firstName} {user.lastName} (Admin)
                        </span>
                        <button className="details-button" onClick={() => setShowDetails(!showDetails)}>...</button>
                    </div>
                    {showDetails && (
                        <div className="details-box">
                            <p>Email: {user.email}</p>
                            <p>Role: <strong>{user.role}</strong></p>
                        </div>
                    )}
                </div>
            )}

            <h2>All Users</h2>
            <table className="admin-table">
                <thead>
                <tr>
                    <th>Name</th><th>Email</th><th>Role</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map(u => (
                    <tr key={u.id}>
                        <td>{u.firstName} {u.lastName}</td>
                        <td>{u.email}</td>
                        <td>{u.role}</td>
                        <td>
                            {editingUser === u.id ? (
                                <>
                                    <select value={newUserRole} onChange={(e) => setNewUserRole(e.target.value)}>
                                        <option value="">Select role</option>
                                        <option value="SUPER_ADMIN">SUPER_ADMIN</option>
                                        <option value="MERCHANT_OWNER">MERCHANT_OWNER</option>
                                        <option value="EMPLOYEE">EMPLOYEE</option>
                                    </select>
                                    <button onClick={() => handleUserUpdate(u.id)}>Save</button>
                                    <button onClick={() => setEditingUser(null)}>Cancel</button>
                                </>
                            ) : (
                                <>
                                    <button onClick={() => setEditingUser(u.id)}>Edit</button>
                                    <button onClick={() => handleUserDelete(u.id)}>Delete</button>
                                </>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <h2>Merchants</h2>
            <table className="admin-table">
                <thead>
                <tr>
                    <th>Name</th><th>Email</th><th>City</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {merchants.map(m => (
                    <tr key={m.id}>
                        <td>{m.name}</td>
                        <td>{m.email}</td>
                        <td>{m.city}</td>
                        <td>
                            <button onClick={() => handleMerchantDelete(m.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};
