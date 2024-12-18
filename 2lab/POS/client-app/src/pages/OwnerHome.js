import React, { useEffect, useState } from "react";
import "../styles/Home.css";
import "../styles/OwnerHome.css"; // CSS for Owner page
import { getCurrentUser } from "../api/users";
import { getMerchant } from "../api/merchants";
import { getAllProductCategories, createProductCategory } from "../api/products";

export const OwnerHome = () => {
    const token = localStorage.getItem('jwt-token');
    const [user, setUser] = useState(null);
    const [showDetails, setShowDetails] = useState(false);
    const [merchant, setMerchantData] = useState(null);
    const [categories, setCategories] = useState([]);
    const [newCategoryName, setNewCategoryName] = useState("");

    useEffect(() => {
        async function init() {
            const u = await getCurrentUser(token);
            setUser(u);
            if (u.merchantId) {
                const m = await getMerchant(token, u.merchantId);
                setMerchantData(m);

                const c = await getAllProductCategories(token);
                setCategories(c.filter(cat => cat.merchantId === u.merchantId));
            }
        }
        init();
    }, [token]);

    const handleCreateCategory = async () => {
        if (!newCategoryName.trim() || !user || !user.merchantId) return;
        await createProductCategory(token, {
            name: newCategoryName,
            merchantId: user.merchantId
        });
        const c = await getAllProductCategories(token);
        setCategories(c.filter(cat => cat.merchantId === user.merchantId));
        setNewCategoryName("");
    };

    return (
        <div className="home-container owner-content">
            {user && (
                <div className="user-box">
                    <div className="user-header">
                        <span className="user-name">
                            {user.firstName} {user.lastName} (Owner)
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
            {merchant && (
                <div className="owner-merchant-box">
                    <h2>Your Merchant</h2>
                    <p>Name: {merchant.name}</p>
                    <p>Email: {merchant.email}</p>
                    <p>City: {merchant.city}</p>
                </div>
            )}

            <div className="owner-category-box">
                <h2>Product Categories</h2>
                <ul>
                    {categories.map(cat => (
                        <li key={cat.id}>{cat.name}</li>
                    ))}
                </ul>
                <input
                    type="text"
                    placeholder="New category name"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                />
                <button onClick={handleCreateCategory}>Create</button>
            </div>
        </div>
    );
};
