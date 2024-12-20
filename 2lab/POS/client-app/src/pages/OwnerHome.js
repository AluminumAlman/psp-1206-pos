import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/OwnerHome.css";
import { getCurrentUser, assignMerchantToUser, getUsers } from "../api/users";
import { createMerchant, getMerchant } from "../api/merchants";

import {
    getAllProductCategories,
    createProductCategory,
    updateCategory,
    deleteCategory,
    getAllProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    adjustProductQuantity,
    getProductVariations,
    createProductVariation,
    updateProductVariation,
    deleteProductVariation,
    adjustProductVariationQuantity
} from "../api/products";

import {
    getServices,
    createService,
    updateService,
    deleteService
} from "../api/services";

export const OwnerHome = () => {
    const token = localStorage.getItem("jwt-token");
    const navigate = useNavigate();

    const [user, setUser] = useState(null);
    const [formData, setFormData] = useState({
        name: "",
        phone: "",
        email: "",
        currency: "",
        address: "",
        city: "",
        country: "",
        postcode: ""
    });
    const [assignedMerchantName, setAssignedMerchantName] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [showDetails, setShowDetails] = useState(false);

    // Schedule selection for merchant creation
    const [isRegularSchedule, setIsRegularSchedule] = useState(true);
    const [schedule, setSchedule] = useState({
        MONDAY: null,
        TUESDAY: null,
        WEDNESDAY: null,
        THURSDAY: null,
        FRIDAY: null,
        SATURDAY: null,
        SUNDAY: null
    });

    // Categories
    const [categories, setCategories] = useState([]);
    const [showCategoryModal, setShowCategoryModal] = useState(false);
    const [newCategoryName, setNewCategoryName] = useState("");
    const [selectedCategoryId, setSelectedCategoryId] = useState("");
    const [showEditCategoryModal, setShowEditCategoryModal] = useState(false);
    const [editCategoryData, setEditCategoryData] = useState({ id: "", name: "" });

    // Products
    const [products, setProducts] = useState([]);
    const [showProductModal, setShowProductModal] = useState(false);
    const [newProductData, setNewProductData] = useState({
        name: "",
        price: "",
        quantity: 0,
        categoryId: "",
        chargeIds: []
    });
    const [showEditProductModal, setShowEditProductModal] = useState(false);
    const [editProductData, setEditProductData] = useState({
        id: "",
        name: "",
        price: "",
        quantity: 0,
        categoryId: ""
    });
    const [showAdjustQtyModal, setShowAdjustQtyModal] = useState(false);
    const [adjustQtyData, setAdjustQtyData] = useState({ id: "", adjustment: 0 });

    const [selectedProductId, setSelectedProductId] = useState(null);

    // Variations
    const [productVariations, setProductVariations] = useState([]);
    const [showVariationModal, setShowVariationModal] = useState(false);
    const [newVariationData, setNewVariationData] = useState({
        name: "",
        price: "",
        quantity: 0
    });
    const [showEditVariationModal, setShowEditVariationModal] = useState(false);
    const [editVariationData, setEditVariationData] = useState({ id: "", name: "", price: "", quantity: 0 });
    const [showAdjustVariationQtyModal, setShowAdjustVariationQtyModal] = useState(false);
    const [adjustVariationQtyData, setAdjustVariationQtyData] = useState({ variationId: "", adjustment: 0 });

    // Services
    const [services, setServices] = useState([]);
    const [showServiceModal, setShowServiceModal] = useState(false);
    const [newServiceData, setNewServiceData] = useState({
        name: "",
        price: "",
        duration: 60,
        employeeIds: []
    });
    const [showServiceEditModal, setShowServiceEditModal] = useState(false);
    const [editServiceData, setEditServiceData] = useState({
        id: "",
        name: "",
        price: "",
        duration: 60,
        employeeIds: []
    });

    const [employees, setEmployees] = useState([]);

    useEffect(() => {
        async function init() {
            try {
                const currentUser = await getCurrentUser(token);
                setUser(currentUser);
                if (currentUser?.merchantId) {
                    const merchant = await getMerchant(token, currentUser.merchantId);
                    setAssignedMerchantName(merchant.name);
                    await loadCategories();
                    await loadServicesList();
                    await loadEmployees();
                }
            } catch (error) {
                console.error("Initialization error:", error);
                setErrorMessage("Failed to load user data. Please try again.");
            }
        }
        init();
    }, [token]);

    // Load products whenever selectedCategoryId changes
    useEffect(() => {
        async function loadProductsForCategory() {
            if (selectedCategoryId) {
                try {
                    const filters = { categoryId: selectedCategoryId };
                    const prodData = await getAllProducts(token, filters);
                    if (prodData && prodData.content) {
                        setProducts(prodData.content);
                    } else {
                        setProducts([]);
                    }
                } catch (error) {
                    console.error("Error loading products:", error);
                    setErrorMessage("Failed to load products.");
                }
            } else {
                // No category selected, no products
                setProducts([]);
                setSelectedProductId(null);
                setProductVariations([]);
            }
        }
        loadProductsForCategory();
    }, [selectedCategoryId, token]);

    // Load variations whenever selectedProductId changes
    useEffect(() => {
        async function loadVariationsForProduct() {
            if (selectedProductId) {
                try {
                    const vars = await getProductVariations(token, selectedProductId);
                    setProductVariations(vars);
                } catch (error) {
                    console.error("Error loading variations:", error);
                    setErrorMessage("Failed to load product variations.");
                }
            } else {
                // No product selected, no variations
                setProductVariations([]);
            }
        }
        loadVariationsForProduct();
    }, [selectedProductId, token]);

    async function loadCategories() {
        try {
            const catData = await getAllProductCategories(token);
            setCategories(catData);
        } catch (error) {
            console.error("Error loading categories:", error);
            setErrorMessage("Failed to load categories.");
        }
    }

    async function loadServicesList() {
        try {
            const serviceData = await getServices(token, {});
            if (serviceData && serviceData.content) {
                setServices(serviceData.content);
            } else {
                setServices([]);
            }
        } catch (error) {
            console.error("Error loading services:", error);
            setErrorMessage("Failed to load services.");
        }
    }

    async function loadEmployees() {
        try {
            const allUsers = await getUsers(token, {});
            const emp = allUsers.filter(u => u.role === "EMPLOYEE");
            if (emp.length === 0) {
                emp.push(user);
            }
            setEmployees(emp);
        } catch (error) {
            console.error("Error loading employees:", error);
        }
    }

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Handle checkbox change for regular schedule
    const handleRegularScheduleChange = (e) => {
        const checked = e.target.checked;
        setIsRegularSchedule(checked);
        if (checked) {
            // Set regular schedule: Mon-Fri 08:00-17:00, Sat-Sun null
            setSchedule({
                MONDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                TUESDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                WEDNESDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                THURSDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                FRIDAY: { startTime: "08:00:00", endTime: "17:00:00" },
                SATURDAY: null,
                SUNDAY: null
            });
        } else {
            // Reset schedule to allow manual input
            setSchedule({
                MONDAY: null,
                TUESDAY: null,
                WEDNESDAY: null,
                THURSDAY: null,
                FRIDAY: null,
                SATURDAY: null,
                SUNDAY: null
            });
        }
    };

    // Handle schedule input changes when not using regular schedule
    const handleScheduleChange = (day, field, value) => {
        setSchedule(prevSchedule => ({
            ...prevSchedule,
            [day]: {
                ...(prevSchedule[day] || {}),
                [field]: value ? `${value}:00` : null
            }
        }));
    };

    const buildFinalSchedule = () => {
        if (isRegularSchedule) {
            return schedule; // Already set to the regular schedule
        } else {
            // If not regular schedule, transform days to null if no valid times
            const finalSchedule = {};
            for (const day in schedule) {
                const dayData = schedule[day];
                if (dayData && dayData.startTime && dayData.endTime) {
                    finalSchedule[day] = {
                        startTime: dayData.startTime,
                        endTime: dayData.endTime
                    };
                } else {
                    finalSchedule[day] = null;
                }
            }
            return finalSchedule;
        }
    };

    const handleCreateBusiness = async () => {
        if (Object.values(formData).some((field) => !field)) {
            setErrorMessage("All fields are required.");
            return;
        }

        // Validate schedule if not regular
        if (!isRegularSchedule) {
            for (const day in schedule) {
                const dayData = schedule[day];
                if (dayData && (!dayData.startTime || !dayData.endTime)) {
                    setErrorMessage(`Please provide both start and end times for ${day}, or leave it blank.`);
                    return;
                }
            }
        }

        try {
            // Include schedule in the merchant creation request
            const requestBody = {
                name: formData.name,
                phone: formData.phone,
                email: formData.email,
                currency: formData.currency,
                address: formData.address,
                city: formData.city,
                country: formData.country,
                postcode: formData.postcode,
                schedule: buildFinalSchedule()
            };

            const createdMerchant = await createMerchant(token, requestBody);
            await assignMerchantToUser(token, user.id, createdMerchant.id);
            const updatedUser = await getCurrentUser(token);
            setUser(updatedUser);
            setAssignedMerchantName(createdMerchant.name);

            await loadCategories();
            await loadServicesList();
            await loadEmployees();

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

    // Category CRUD
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

    const handleEditCategory = (c) => {
        setEditCategoryData({ id: c.id, name: c.name });
        setShowEditCategoryModal(true);
    };

    const handleUpdateCategory = async () => {
        if (!editCategoryData.name.trim()) {
            setErrorMessage("Category name is required.");
            return;
        }
        try {
            await updateCategory(token, editCategoryData.id, { name: editCategoryData.name });
            await loadCategories();
            setShowEditCategoryModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error updating category:", error);
            setErrorMessage("Failed to update category.");
        }
    };

    const handleDeleteCategory = async (id) => {
        try {
            await deleteCategory(token, id);
            if (selectedCategoryId === id) {
                setSelectedCategoryId("");
            }
            await loadCategories();
        } catch (error) {
            console.error("Error deleting category:", error);
            setErrorMessage("Failed to delete category.");
        }
    };

    const handleCategorySelect = (catId) => {
        setSelectedCategoryId(catId);
        setSelectedProductId(null); // reset product and variations when category changes
    };

    // Product CRUD
    const handleCreateProduct = async () => {
        if (!newProductData.name || !newProductData.price || !newProductData.categoryId) {
            setErrorMessage("Name, Price, and Category are required.");
            return;
        }
        try {
            const pData = {
                name: newProductData.name,
                price: parseFloat(newProductData.price),
                quantity: parseInt(newProductData.quantity, 10),
                categoryId: newProductData.categoryId,
                chargeIds: []
            };
            await createProduct(token, pData);
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

    const handleEditProduct = (p) => {
        setEditProductData({
            id: p.id,
            name: p.name,
            price: p.price.toString(),
            quantity: p.quantity,
            categoryId: p.categoryId
        });
        setShowEditProductModal(true);
    };

    const handleUpdateProduct = async () => {
        if (!editProductData.name || !editProductData.price) {
            setErrorMessage("Name and Price are required.");
            return;
        }
        try {
            const updateData = {
                name: editProductData.name,
                price: parseFloat(editProductData.price),
                quantity: parseInt(editProductData.quantity, 10),
                categoryId: editProductData.categoryId,
                chargeIds: []
            };
            await updateProduct(token, editProductData.id, updateData);
            setShowEditProductModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error updating product:", error);
            setErrorMessage("Failed to update product.");
        }
    };

    const handleDeleteProduct = async (id) => {
        try {
            await deleteProduct(token, id);
            if (selectedProductId === id) {
                setSelectedProductId(null);
                setProductVariations([]);
            }
        } catch (error) {
            console.error("Error deleting product:", error);
            setErrorMessage("Failed to delete product.");
        }
    };

    const handleAdjustQuantity = (p) => {
        setAdjustQtyData({ id: p.id, adjustment: 0 });
        setShowAdjustQtyModal(true);
    };

    const handleAdjustQtySave = async () => {
        if (!adjustQtyData.adjustment) {
            setErrorMessage("Adjustment is required.");
            return;
        }
        try {
            await adjustProductQuantity(token, adjustQtyData.id, parseInt(adjustQtyData.adjustment, 10));
            setShowAdjustQtyModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error adjusting product quantity:", error);
            setErrorMessage("Failed to adjust product quantity.");
        }
    };

    const handleSelectProduct = (productId) => {
        setSelectedProductId(productId);
    };

    // Variations
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
            setShowVariationModal(false);
            setNewVariationData({ name: "", price: "", quantity: 0 });
            setErrorMessage("");
        } catch (error) {
            console.error("Error creating variation:", error);
            setErrorMessage("Failed to create variation.");
        }
    };

    const handleEditVariation = (v) => {
        setEditVariationData({
            id: v.id,
            name: v.name,
            price: v.price.toString(),
            quantity: v.quantity
        });
        setShowEditVariationModal(true);
    };

    const handleUpdateVariation = async () => {
        if (!editVariationData.name || !editVariationData.price) {
            setErrorMessage("Name and Price are required.");
            return;
        }
        try {
            const vData = {
                name: editVariationData.name,
                price: parseFloat(editVariationData.price),
                quantity: parseInt(editVariationData.quantity, 10)
            };
            await updateProductVariation(token, selectedProductId, editVariationData.id, vData);
            setShowEditVariationModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error updating variation:", error);
            setErrorMessage("Failed to update variation.");
        }
    };

    const handleDeleteVariation = async (variationId) => {
        try {
            await deleteProductVariation(token, selectedProductId, variationId);
        } catch (error) {
            console.error("Error deleting variation:", error);
            setErrorMessage("Failed to delete variation.");
        }
    };

    const handleAdjustVariationQuantity = (v) => {
        setAdjustVariationQtyData({ variationId: v.id, adjustment: 0 });
        setShowAdjustVariationQtyModal(true);
    };

    const handleAdjustVariationQtySave = async () => {
        if (!adjustVariationQtyData.adjustment) {
            setErrorMessage("Adjustment is required.");
            return;
        }
        try {
            await adjustProductVariationQuantity(token, selectedProductId, adjustVariationQtyData.variationId, parseInt(adjustVariationQtyData.adjustment, 10));
            setShowAdjustVariationQtyModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error adjusting variation quantity:", error);
            setErrorMessage("Failed to adjust variation quantity.");
        }
    };

    // Services
    const handleCreateService = async () => {
        if (!newServiceData.name || !newServiceData.price || !newServiceData.duration) {
            setErrorMessage("Name, Price, and Duration are required for creating a service.");
            return;
        }
        if (employees.length === 0) {
            setErrorMessage("No employees available to assign to this service.");
            return;
        }
        try {
            const sData = {
                name: newServiceData.name,
                price: parseFloat(newServiceData.price),
                duration: parseInt(newServiceData.duration, 10),
                employeeIds: [employees[0].id]
            };
            await createService(token, sData);
            setShowServiceModal(false);
            setNewServiceData({ name: "", price: "", duration: 60, employeeIds: [] });
            setErrorMessage("");
        } catch (error) {
            console.error("Error creating service:", error);
            setErrorMessage("Failed to create service.");
        }

        window.location.reload();
    };

    const handleEditService = (service) => {
        setEditServiceData({
            id: service.id,
            name: service.name,
            price: service.price.toString(),
            duration: service.duration,
            employeeIds: []
        });
        setShowServiceEditModal(true);
    };

    const handleUpdateService = async () => {
        if (!editServiceData.name || !editServiceData.price || !editServiceData.duration) {
            setErrorMessage("Name, Price, and Duration are required for updating a service.");
            return;
        }
        if (employees.length === 0) {
            setErrorMessage("No employees available.");
            return;
        }
        try {
            const sData = {
                name: editServiceData.name,
                price: parseFloat(editServiceData.price),
                duration: parseInt(editServiceData.duration, 10),
                employeeIds: [employees[0].id]
            };
            await updateService(token, editServiceData.id, sData);
            setShowServiceEditModal(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Error updating service:", error);
            setErrorMessage("Failed to update service.");
        }
    };

    const handleDeleteService = async (serviceId) => {
        try {
            await deleteService(token, serviceId);
        } catch (error) {
            console.error("Error deleting service:", error);
            setErrorMessage("Failed to delete service.");
        }
        window.location.reload();
    };

    return (
        <div className="home-container owner-content">
            {user && !user.merchantId ? (
                <div className="business-modal">
                    <h2>Create Your Business</h2>
                    {errorMessage && <p className="error-message">{errorMessage}</p>}
                    <input type="text" name="name" value={formData.name} onChange={handleInputChange} required placeholder="Business Name" />
                    <input type="text" name="phone" value={formData.phone} onChange={handleInputChange} required placeholder="Phone Number" />
                    <input type="email" name="email" value={formData.email} onChange={handleInputChange} required placeholder="Business Email" />
                    <input type="text" name="currency" value={formData.currency} onChange={handleInputChange} required placeholder="Currency (e.g. USD)" />
                    <input type="text" name="address" value={formData.address} onChange={handleInputChange} required placeholder="Address" />
                    <input type="text" name="city" value={formData.city} onChange={handleInputChange} required placeholder="City" />
                    <input type="text" name="country" value={formData.country} onChange={handleInputChange} required placeholder="Country" />
                    <input type="text" name="postcode" value={formData.postcode} onChange={handleInputChange} required placeholder="Postcode" />

                    {/* Schedule Selection for Merchant */}
                    <div className="schedule-selection">
                        <label>
                            <input
                                type="checkbox"
                                checked={isRegularSchedule}
                                onChange={handleRegularScheduleChange}
                            />
                            Regular work days (Mon-Fri 08:00-17:00)
                        </label>
                    </div>
                    {!isRegularSchedule && (
                        <div className="custom-schedule">
                            <h3>Set Merchant's Work Schedule</h3>
                            {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(day => (
                                <div key={day} className="schedule-day">
                                    <label>{day.charAt(0) + day.slice(1).toLowerCase()}</label>
                                    <div className="time-inputs">
                                        <input
                                            type="time"
                                            placeholder="Start Time"
                                            value={schedule[day] && schedule[day].startTime ? schedule[day].startTime.substring(0,5) : ''}
                                            onChange={(e) => handleScheduleChange(day, 'startTime', e.target.value)}
                                        />
                                        <span>to</span>
                                        <input
                                            type="time"
                                            placeholder="End Time"
                                            value={schedule[day] && schedule[day].endTime ? schedule[day].endTime.substring(0,5) : ''}
                                            onChange={(e) => handleScheduleChange(day, 'endTime', e.target.value)}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    <button className="create-button" onClick={handleCreateBusiness}>Create Business</button>
                </div>
            ) : (
                user && (
                    <div className="owner-dashboard">
                        <div className="user-box" onClick={() => setShowDetails(!showDetails)}>
                            <div className="user-header">
                                <span className="user-name">{user.firstName} {user.lastName} (Owner)</span>
                            </div>
                            {showDetails && (
                                <div className="details-box">
                                    <p>Email: {user.email}</p>
                                    <p>Role: <strong>{user.role}</strong></p>
                                    <p>Assigned Merchant: <strong>{assignedMerchantName || "None"}</strong></p>
                                    <button className="switch-button" onClick={handleLogout}>Logout</button>
                                </div>
                            )}
                        </div>

                        <div className="management-section">
                            <h2>Manage Your Inventory and Services</h2>

                            <div className="categories-section">
                                <h3>Categories</h3>
                                <button onClick={() => setShowCategoryModal(true)}>Create Category</button>
                                <ul>
                                    {categories.map((c) => (
                                        <li key={c.id} style={{ background: selectedCategoryId === c.id ? '#d0ebff' : '' }}>
                                            <span onClick={() => handleCategorySelect(c.id)} style={{ flexGrow: 1, cursor: 'pointer' }}>{c.name}</span>
                                            <div>
                                                <button onClick={() => handleEditCategory(c)}>Edit</button>
                                                <button onClick={() => handleDeleteCategory(c.id)}>Delete</button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>

                            {selectedCategoryId && (
                                <div className="products-section">
                                    <h3>Products (Filtered by Selected Category)</h3>
                                    <button onClick={() => setShowProductModal(true)}>Create Product</button>
                                    <ul>
                                        {products.map((p) => (
                                            <li key={p.id}>
                                                <span onClick={() => handleSelectProduct(p.id)} style={{ flexGrow: 1, cursor: 'pointer' }}>
                                                    {p.name} - Qty: {p.quantity}
                                                </span>
                                                <div>
                                                    <button onClick={() => handleEditProduct(p)}>Edit</button>
                                                    <button onClick={() => handleDeleteProduct(p.id)}>Delete</button>
                                                    <button onClick={() => handleAdjustQuantity(p)}>Adjust Qty</button>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}

                            {selectedProductId && (
                                <div className="variations-section">
                                    <h3>Variations for Selected Product</h3>
                                    <button onClick={() => setShowVariationModal(true)}>Create Variation</button>
                                    <ul>
                                        {productVariations.map((v) => (
                                            <li key={v.id}>
                                                <span>{v.name} - Qty: {v.quantity}</span>
                                                <div>
                                                    <button onClick={() => handleEditVariation(v)}>Edit</button>
                                                    <button onClick={() => handleDeleteVariation(v.id)}>Delete</button>
                                                    <button onClick={() => handleAdjustVariationQuantity(v)}>Adjust Qty</button>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}

                            <div className="services-section">
                                <h3>Services</h3>
                                <button onClick={() => setShowServiceModal(true)}>Create Service</button>
                                <ul>
                                    {services.map((s) => (
                                        <li key={s.id}>
                                            <span>{s.name} - {s.price} USD, {s.duration}s</span>
                                            <div>
                                                <button onClick={() => handleEditService(s)}>Edit</button>
                                                <button onClick={() => handleDeleteService(s.id)}>Delete</button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
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

            {/* Edit Category Modal */}
            {showEditCategoryModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Update Category</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input
                            type="text"
                            placeholder="Category Name"
                            value={editCategoryData.name}
                            onChange={(e) => setEditCategoryData({ ...editCategoryData, name: e.target.value })}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleUpdateCategory}>Save</button>
                            <button onClick={() => setShowEditCategoryModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Product Modal */}
            {showProductModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Product</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={newProductData.name} onChange={(e) => setNewProductData({ ...newProductData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={newProductData.price} onChange={(e) => setNewProductData({ ...newProductData, price: e.target.value })} />
                        <input type="number" placeholder="Quantity" value={newProductData.quantity} onChange={(e) => setNewProductData({ ...newProductData, quantity: e.target.value })} />
                        <select value={newProductData.categoryId} onChange={(e) => setNewProductData({ ...newProductData, categoryId: e.target.value })}>
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

            {/* Edit Product Modal */}
            {showEditProductModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Update Product</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={editProductData.name} onChange={(e) => setEditProductData({ ...editProductData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={editProductData.price} onChange={(e) => setEditProductData({ ...editProductData, price: e.target.value })} />
                        <input type="number" placeholder="Quantity" value={editProductData.quantity} onChange={(e) => setEditProductData({ ...editProductData, quantity: e.target.value })} />
                        <select value={editProductData.categoryId} onChange={(e) => setEditProductData({ ...editProductData, categoryId: e.target.value })}>
                            <option value="">Select Category</option>
                            {categories.map((cat) => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        <div className="modal-buttons">
                            <button onClick={handleUpdateProduct}>Save</button>
                            <button onClick={() => setShowEditProductModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Adjust Product Quantity Modal */}
            {showAdjustQtyModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Adjust Product Quantity</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input
                            type="number"
                            placeholder="Adjustment"
                            value={adjustQtyData.adjustment}
                            onChange={(e) => setAdjustQtyData({ ...adjustQtyData, adjustment: e.target.value })}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleAdjustQtySave}>Save</button>
                            <button onClick={() => setShowAdjustQtyModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Variation Modal */}
            {showVariationModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Variation</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={newVariationData.name} onChange={(e) => setNewVariationData({ ...newVariationData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={newVariationData.price} onChange={(e) => setNewVariationData({ ...newVariationData, price: e.target.value })} />
                        <input type="number" placeholder="Quantity" value={newVariationData.quantity} onChange={(e) => setNewVariationData({ ...newVariationData, quantity: e.target.value })} />
                        <div className="modal-buttons">
                            <button onClick={handleCreateVariation}>Save</button>
                            <button onClick={() => setShowVariationModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Edit Variation Modal */}
            {showEditVariationModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Update Variation</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={editVariationData.name} onChange={(e) => setEditVariationData({ ...editVariationData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={editVariationData.price} onChange={(e) => setEditVariationData({ ...editVariationData, price: e.target.value })} />
                        <input type="number" placeholder="Quantity" value={editVariationData.quantity} onChange={(e) => setEditVariationData({ ...editVariationData, quantity: e.target.value })} />
                        <div className="modal-buttons">
                            <button onClick={handleUpdateVariation}>Save</button>
                            <button onClick={() => setShowEditVariationModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Adjust Variation Quantity Modal */}
            {showAdjustVariationQtyModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Adjust Variation Quantity</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input
                            type="number"
                            placeholder="Adjustment"
                            value={adjustVariationQtyData.adjustment}
                            onChange={(e) => setAdjustVariationQtyData({ ...adjustVariationQtyData, adjustment: e.target.value })}
                        />
                        <div className="modal-buttons">
                            <button onClick={handleAdjustVariationQtySave}>Save</button>
                            <button onClick={() => setShowAdjustVariationQtyModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Service Modal */}
            {showServiceModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Create Service</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={newServiceData.name} onChange={(e) => setNewServiceData({ ...newServiceData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={newServiceData.price} onChange={(e) => setNewServiceData({ ...newServiceData, price: e.target.value })} />
                        <input type="number" placeholder="Duration (seconds)" value={newServiceData.duration} onChange={(e) => setNewServiceData({ ...newServiceData, duration: e.target.value })} />
                        <div className="modal-buttons">
                            <button onClick={handleCreateService}>Save</button>
                            <button onClick={() => setShowServiceModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Edit Service Modal */}
            {showServiceEditModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Update Service</h2>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        <input type="text" placeholder="Name" value={editServiceData.name} onChange={(e) => setEditServiceData({ ...editServiceData, name: e.target.value })} />
                        <input type="number" placeholder="Price" value={editServiceData.price} onChange={(e) => setEditServiceData({ ...editServiceData, price: e.target.value })} />
                        <input type="number" placeholder="Duration (seconds)" value={editServiceData.duration} onChange={(e) => setEditServiceData({ ...editServiceData, duration: e.target.value })} />
                        <div className="modal-buttons">
                            <button onClick={handleUpdateService}>Save</button>
                            <button onClick={() => setShowServiceEditModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
