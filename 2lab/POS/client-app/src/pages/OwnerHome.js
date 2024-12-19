import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/OwnerHome.css";
import { getCurrentUser, assignMerchantToUser } from "../api/users";
import { createMerchant, getMerchant } from "../api/merchants";

import {
    getAllProductCategories,
    createProductCategory,
    getAllProducts,
    createProduct,
    getProductVariations,
    createProductVariation,
    createInventoryLog
} from "../api/products";

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

    // States for product/category/variation management
    const [categories, setCategories] = useState([]);
    const [showCategoryModal, setShowCategoryModal] = useState(false);
    const [newCategoryName, setNewCategoryName] = useState("");

    const [products, setProducts] = useState([]);
    const [showProductModal, setShowProductModal] = useState(false);
    const [newProductData, setNewProductData] = useState({
        name: "",
        price: "",
        quantity: 0,
        categoryId: "",
        chargeIds: []
    });

    const [selectedProductId, setSelectedProductId] = useState(null);
    const [productVariations, setProductVariations] = useState([]);
    const [showVariationModal, setShowVariationModal] = useState(false);
    const [newVariationData, setNewVariationData] = useState({
        name: "",
        price: "",
        quantity: 0
    });

    const [showInventoryLogModal, setShowInventoryLogModal] = useState(false);
    const [inventoryLogData, setInventoryLogData] = useState({
        product: null,
        productVariation: null,
        order: null,
        adjustment: 0
    });

    useEffect(() => {
        async function init() {
            try {
                const currentUser = await getCurrentUser(token);
                setUser(currentUser);

                if (currentUser?.merchantId) {
                    const merchant = await getMerchant(token, currentUser.merchantId);
                    setAssignedMerchantName(merchant.name);
                    await loadCategories();
                    await loadProducts();
                }
            } catch (error) {
                console.error("Initialization error:", error);
                setErrorMessage("Failed to load user data. Please try again.");
            }
        }
        init();
    }, [token]);

    const loadCategories = async () => {
        try {
            const catData = await getAllProductCategories(token);
            setCategories(catData);
        } catch (error) {
            console.error("Error loading categories:", error);
            setErrorMessage("Failed to load categories.");
        }
    };

    const loadProducts = async () => {
        try {
            const prodData = await getAllProducts(token, {});
            // Assuming prodData has a 'content' field containing the products
            if (prodData && prodData.content) {
                setProducts(prodData.content);
            } else {
                setProducts([]);
            }
        } catch (error) {
            console.error("Error loading products:", error);
            setErrorMessage("Failed to load products.");
        }
    };

    const loadProductVariationsForProduct = async (productId) => {
        try {
            if (!productId) return;
            const vars = await getProductVariations(token, productId);
            setProductVariations(vars);
        } catch (error) {
            console.error("Error loading variations:", error);
            setErrorMessage("Failed to load product variations.");
        }
    };

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

            // Load categories and products since we have a merchant now
            await loadCategories();
            await loadProducts();
            setErrorMessage("");
        } catch (error) {
            setErrorMessage("Failed to create business. Please try again.");
            console.error("Business creation error:", error);
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    const handleCreateCategory = async () => {
        if (!newCategoryName.trim()) return;
        try {
            await createProductCategory(token, { name: newCategoryName });
            await loadCategories();
            setNewCategoryName("");
            setShowCategoryModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error creating category:", error);
            setErrorMessage("Failed to create category.");
        }
    };

    const handleCreateProduct = async () => {
        if (!newProductData.name || !newProductData.price || !newProductData.categoryId) {
            setErrorMessage("Name, Price, and Category are required for creating a product.");
            return;
        }
        try {
            const pData = {
                name: newProductData.name,
                price: parseFloat(newProductData.price),
                quantity: parseInt(newProductData.quantity, 10),
                categoryId: newProductData.categoryId,
                chargeIds: [] // Update this if you plan to assign charges
            };
            await createProduct(token, pData);
            await loadProducts();
            setShowProductModal(false);
            setNewProductData({
                name: "",
                price: "",
                quantity: 0,
                categoryId: "",
                chargeIds: []
            });
            setErrorMessage("");
        } catch (error) {
            console.error("Error creating product:", error);
            setErrorMessage("Failed to create product.");
        }
    };

    const handleSelectProduct = async (productId) => {
        setSelectedProductId(productId);
        await loadProductVariationsForProduct(productId);
    };

    const handleCreateVariation = async () => {
        if (!selectedProductId) return;
        if (!newVariationData.name || !newVariationData.price) {
            setErrorMessage("Name and Price are required for creating a variation.");
            return;
        }
        try {
            const vData = {
                name: newVariationData.name,
                price: parseFloat(newVariationData.price),
                quantity: parseInt(newVariationData.quantity, 10)
            };
            await createProductVariation(token, selectedProductId, vData);
            await loadProductVariationsForProduct(selectedProductId);
            setShowVariationModal(false);
            setNewVariationData({
                name: "",
                price: "",
                quantity: 0
            });
            setErrorMessage("");
        } catch (error) {
            console.error("Error creating variation:", error);
            setErrorMessage("Failed to create variation.");
        }
    };

    const handleCreateInventoryLog = async () => {
        const data = { ...inventoryLogData };
        data.adjustment = parseInt(data.adjustment, 10);
        if (!data.adjustment) {
            setErrorMessage("Adjustment value is required.");
            return;
        }

        // Ensure either product or productVariation is selected
        if (!data.product && !data.productVariation) {
            setErrorMessage("Either a Product or Product Variation must be selected.");
            return;
        }

        try {
            await createInventoryLog(token, data);
            setShowInventoryLogModal(false);
            setInventoryLogData({
                product: null,
                productVariation: null,
                order: null,
                adjustment: 0
            });
            setErrorMessage("");
            alert("Inventory log created successfully.");
        } catch (error) {
            console.error("Error creating inventory log:", error);
            setErrorMessage("Failed to create inventory log.");
        }
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
                        placeholder="Business Name"
                    />
                    <input
                        type="text"
                        name="phone"
                        value={formData.phone}
                        onChange={handleInputChange}
                        required
                        placeholder="Phone Number"
                    />
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                        placeholder="Business Email"
                    />
                    <input
                        type="text"
                        name="currency"
                        value={formData.currency}
                        onChange={handleInputChange}
                        required
                        placeholder="Currency (e.g. USD)"
                    />
                    <input
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleInputChange}
                        required
                        placeholder="Address"
                    />
                    <input
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleInputChange}
                        required
                        placeholder="City"
                    />
                    <input
                        type="text"
                        name="country"
                        value={formData.country}
                        onChange={handleInputChange}
                        required
                        placeholder="Country"
                    />
                    <input
                        type="text"
                        name="postcode"
                        value={formData.postcode}
                        onChange={handleInputChange}
                        required
                        placeholder="Postcode"
                    />
                    <button className="create-button" onClick={handleCreateBusiness}>
                        Create Business
                    </button>
                </div>
            ) : (
                user && (
                    <div className="owner-dashboard">
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
                                    <p>Role: <strong>{user.role}</strong></p>
                                    <p>Assigned Merchant: <strong>{assignedMerchantName || "None"}</strong></p>
                                    <button
                                        className="switch-button"
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>

                        <div className="management-section">
                            <h2>Manage Your Inventory</h2>

                            <div className="categories-section">
                                <h3>Categories</h3>
                                <button onClick={() => setShowCategoryModal(true)}>Create Category</button>
                                <ul>
                                    {categories.map((c) => (
                                        <li key={c.id}>{c.name}</li>
                                    ))}
                                </ul>
                            </div>

                            <div className="products-section">
                                <h3>Products</h3>
                                <button onClick={() => setShowProductModal(true)}>Create Product</button>
                                <ul>
                                    {products.map((p) => (
                                        <li key={p.id} onClick={() => handleSelectProduct(p.id)}>
                                            {p.name} - Qty: {p.quantity}
                                        </li>
                                    ))}
                                </ul>
                            </div>

                            {selectedProductId && (
                                <div className="variations-section">
                                    <h3>Variations for Selected Product</h3>
                                    <button onClick={() => setShowVariationModal(true)}>Create Variation</button>
                                    <ul>
                                        {productVariations.map((v) => (
                                            <li key={v.id}>{v.name} - Qty: {v.quantity}</li>
                                        ))}
                                    </ul>
                                </div>
                            )}

                            {/* Inventory Logs Section (Currently Commented Out) */}
                            {/*<div className="inventory-logs-section">*/}
                            {/*    <h3>Inventory Logs</h3>*/}
                            {/*    <button onClick={() => setShowInventoryLogModal(true)}>Create Inventory Log</button>*/}
                            {/*</div>*/}
                        </div>
                    </div>
                )
            )}

            {/* Create Category Modal */}
            {showCategoryModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Category</h2>
                        <input
                            type="text"
                            placeholder="Category Name"
                            value={newCategoryName}
                            onChange={(e) => setNewCategoryName(e.target.value)}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleCreateCategory}>Save</button>
                            <button onClick={() => setShowCategoryModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Product Modal */}
            {showProductModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Product</h2>
                        <input
                            type="text"
                            placeholder="Name"
                            value={newProductData.name}
                            onChange={(e) => setNewProductData({ ...newProductData, name: e.target.value })}
                        />
                        <input
                            type="number"
                            placeholder="Price"
                            value={newProductData.price}
                            onChange={(e) => setNewProductData({ ...newProductData, price: e.target.value })}
                        />
                        <input
                            type="number"
                            placeholder="Quantity"
                            value={newProductData.quantity}
                            onChange={(e) => setNewProductData({ ...newProductData, quantity: e.target.value })}
                        />
                        <select
                            value={newProductData.categoryId}
                            onChange={(e) => setNewProductData({ ...newProductData, categoryId: e.target.value })}
                        >
                            <option value="">Select Category</option>
                            {categories.map((cat) => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        <div className="modal-buttons">
                            <button onClick={handleCreateProduct}>Save</button>
                            <button onClick={() => setShowProductModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Variation Modal */}
            {showVariationModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Variation</h2>
                        <input
                            type="text"
                            placeholder="Name"
                            value={newVariationData.name}
                            onChange={(e) => setNewVariationData({ ...newVariationData, name: e.target.value })}
                        />
                        <input
                            type="number"
                            placeholder="Price"
                            value={newVariationData.price}
                            onChange={(e) => setNewVariationData({ ...newVariationData, price: e.target.value })}
                        />
                        <input
                            type="number"
                            placeholder="Quantity"
                            value={newVariationData.quantity}
                            onChange={(e) => setNewVariationData({ ...newVariationData, quantity: e.target.value })}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleCreateVariation}>Save</button>
                            <button onClick={() => setShowVariationModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Inventory Log Modal */}
            {/* {showInventoryLogModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Inventory Log</h2>
                        <p>Either select a Product or Product Variation:</p>
                        <select onChange={(e) => setInventoryLogData({ ...inventoryLogData, product: e.target.value || null, productVariation: null })}>
                            <option value="">Select Product</option>
                            {products.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
                        </select>
                        {selectedProductId && productVariations.length > 0 && (
                            <select onChange={(e) => setInventoryLogData({ ...inventoryLogData, product: null, productVariation: e.target.value || null })}>
                                <option value="">Select Variation</option>
                                {productVariations.map((v) => <option key={v.id} value={v.id}>{v.name}</option>)}
                            </select>
                        )}
                        <input
                            type="number"
                            placeholder="Adjustment"
                            value={inventoryLogData.adjustment}
                            onChange={(e) => setInventoryLogData({ ...inventoryLogData, adjustment: e.target.value })}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleCreateInventoryLog}>Save</button>
                            <button onClick={() => setShowInventoryLogModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )} */}
        </div>
    );
};
