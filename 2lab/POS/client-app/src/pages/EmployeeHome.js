// EmployeeHome.js

import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/EmployeeHome.css";
import { getCurrentUser, assignMerchantToUser } from "../api/users";
import { getAllMerchants, getMerchant } from "../api/merchants";
import { getAllProducts, getProductVariations, getProductVariation, getProduct, adjustProductVariationQuantity, adjustProductQuantity  } from '../api/products';
import { getServices, getAvailableSlots } from "../api/services";
import { getOrders, createOrder, addItemToOrder, getTotalOrderAmount, getOrderItems, updateOrderItem, deleteOrderItem } from "../api/orders";
import { getReservations, createReservation, updateReservation, cancelReservation } from "../api/reservations";

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

    // Reservations state
    const [reservations, setReservations] = useState([]);
    const [loadingReservations, setLoadingReservations] = useState(false);

    // Track if slots have been checked
    const [hasCheckedSlots, setHasCheckedSlots] = useState(false);

    // Orders state
    const [orders, setOrders] = useState([]);
    const [loadingOrders, setLoadingOrders] = useState(false);

    // State for edit order modal
    const [showEditOrderModal, setShowEditOrderModal] = useState(false);
    const [editingOrderId, setEditingOrderId] = useState(null);
    const [availableProducts, setAvailableProducts] = useState([]);
    const [selectedProduct, setSelectedProduct] = useState('');
    const [selectedQuantity, setSelectedQuantity] = useState(1);
    const [availableVariants, setAvailableVariants] = useState(null);
    const [selectedVariant, setSelectedVariant] = useState("");

    // Order details modal states
    const [showDetailsModal, setShowDetailsModal] = useState(false);
    const [selectedOrderDetails, setSelectedOrderDetails] = useState([]);
    const [selectedOrderId, setSelectedOrderId] = useState(null);
    const [orderItems, setOrderItems] = useState([]);
    const [createdReservationId, setCreatedReservationId] = useState(null);



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

    // Update Reservation modal states
    const [showUpdateModal, setShowUpdateModal] = useState(false);
    const [updateReservationId, setUpdateReservationId] = useState(null);
    const [updateData, setUpdateData] = useState({
        firstName: "",
        lastName: "",
        phone: ""
    });

    const navigate = useNavigate();

    useEffect(() => {
        async function init() {
            try {
                const currentUser = await getCurrentUser(token);
                setUser(currentUser);
    
                if (currentUser?.merchantId) {
                    const merchant = await getMerchant(token, currentUser.merchantId);
                    setAssignedMerchantName(merchant.name);
                    await loadOrders();
                    await loadServices(); // still needed for reservations, but now used inside details modal
                    await loadReservations(); // still needed for the reservation list, if you still want to display them somewhere, or you can remove it entirely if not needed.
                } else {
                    const merchantsData = await getAllMerchants(token);
                    setMerchants(merchantsData);
                    setShowMerchantModal(true);
                }
            } catch (error) {
                console.error("Error initializing user:", error);
                setErrorMessage("Failed to load user data.");
            }
        }
        init();
    }, [token]);


    // *** Orders Section ***

    // Function to load orders
    const loadOrders = async () => {
        try {
            setLoadingOrders(true);
            const ordersData = await getOrders(token, { limit: 20, offset: 0 });
            if (ordersData && ordersData.content) {
                const enrichedOrders = await Promise.all(
                    ordersData.content.map(async (order) => {
                        const totalPrice = await getTotalOrderAmount(token, order.id);
                        return { ...order, totalPrice }; // Add totalPrice to each order
                    })
                );
                setOrders(enrichedOrders);
            }
            setLoadingOrders(false);
        } catch (error) {
            console.error("Error loading orders:", error);
            setLoadingOrders(false);
        }
    };




    // Function to create a new order
    const handleCreateOrder = async () => {
        try {
            const newOrder = await createOrder(token, {}); // POST request with an empty body
            if (newOrder && newOrder.id) {
                setOrders(prevOrders => [newOrder, ...prevOrders]); // Add new order to the list
            }
        } catch (error) {
            console.error("Error creating order:", error);
        }
    };

    // Function to adjust the quantity of a product in an order
    const handleQuantityChange = async (orderId, orderItemId, newQuantity) => {
        if (newQuantity <= 0) {
            alert("Quantity must be greater than zero.");
            return;
        }

        try {
            // Call the endpoint to update the quantity
            await updateOrderItem(token, orderId, orderItemId, { quantity: newQuantity });
    
            // Update the local state with the new quantity
            const updatedItems = orderItems.map((item) =>
                item.id === orderItemId ? { ...item, quantity: newQuantity } : item
            );
            setOrderItems(updatedItems);
        } catch (error) {
            console.error("Error updating quantity:", error);
            alert("Failed to update quantity.");
        }
    };

    const handleDeleteItem = async (orderId, orderItemId) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this item?");
        if (!confirmDelete) return;
    
        try {
            await deleteOrderItem(token, orderId, orderItemId);
            const updatedItems = orderItems.filter((item) => item.id !== orderItemId);
            setOrderItems(updatedItems);
            alert("Item deleted successfully!");
        } catch (error) {
            console.error("Error deleting order item:", error);
            alert("Failed to delete item.");
        }
    };
    
    
    

    // Function to load products
    const loadProducts = async () => {
        try {
            const productsData = await getAllProducts(token, { limit: 50 }); // Adjust the limit as needed
            if (productsData && productsData.content) {
                setAvailableProducts(productsData.content);
            }
        } catch (error) {
            console.error("Error loading products:", error);
        }
    };



    // *** Reservations Section ***

    const loadServices = async () => {
        try {
            const serviceData = await getServices(token, { limit: 100, offset: 0 }); // Adjust limit as needed
            if (serviceData && serviceData.content) {
                setServices(serviceData.content);
            }
        } catch (error) {
            console.error("Error loading services:", error);
            setErrorMessage("Failed to load services.");
        }
    };

    const loadReservations = async () => {
        try {
            setLoadingReservations(true);
            const reservationsData = await getReservations(token, { limit: 100, offset: 0 }); // Adjust limit as needed
            if (reservationsData && reservationsData.content) {
                setReservations(reservationsData.content);
            }
            setLoadingReservations(false);
        } catch (error) {
            console.error("Error loading reservations:", error);
            setErrorMessage("Failed to load reservations.");
            setLoadingReservations(false);
        }
    };

    const handleAssignMerchant = async () => {
        if (!selectedMerchant) return;

        try {
            await assignMerchantToUser(token, user.id, selectedMerchant);
            const updatedUser = await getCurrentUser(token);
            const merchant = await getMerchant(token, selectedMerchant);
            setUser(updatedUser);
            setAssignedMerchantName(merchant.name);
            setShowMerchantModal(false);

            await loadServices();
            await loadReservations();
        } catch (error) {
            console.error("Error assigning merchant:", error);
            setErrorMessage("Failed to assign merchant.");
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    const handleCheckSlots = async () => {
        if (!selectedServiceId || !selectedDate || !user) return;
        try {
            // Ensure the date is in YYYY-MM-DD format
            const dateObj = new Date(selectedDate);
            const year = dateObj.getFullYear();
            const month = String(dateObj.getMonth() + 1).padStart(2, '0');
            const day = String(dateObj.getDate()).padStart(2, '0');
            const formattedDate = `${year}-${month}-${day}`;

            const slotsResponse = await getAvailableSlots(token, selectedServiceId, formattedDate, user.id);
            if (slotsResponse && slotsResponse.items) {
                setAvailableSlots(slotsResponse.items);
            } else {
                setAvailableSlots([]);
            }
            setHasCheckedSlots(true); // User has initiated a slot check
            setErrorMessage("");
            setSuccessMessage("");
        } catch (error) {
            console.error("Error fetching slots:", error);
            setErrorMessage(error.message || "Failed to fetch available slots.");
            setHasCheckedSlots(true); // Even on error, consider that a check was attempted
        }
    };

    // Reset hasCheckedSlots when selectedServiceId or selectedDate changes
    useEffect(() => {
        if (selectedServiceId || selectedDate) {
            setHasCheckedSlots(false);
        }
    }, [selectedServiceId, selectedDate]);

    // Check if selectedDate is in the past
    useEffect(() => {
        if (selectedDate) {
            const selected = new Date(selectedDate);
            const today = new Date();
            today.setHours(0, 0, 0, 0); // set to start of today

            if (selected < today) {
                setAvailableSlots([]);
                setHasCheckedSlots(true);
                setErrorMessage("Selected date is in the past. No available slots.");
            } else {
                setHasCheckedSlots(false);
                setErrorMessage("");
            }
        } else {
            // If no date selected
            setHasCheckedSlots(false);
            setAvailableSlots([]);
            setErrorMessage("");
        }
    }, [selectedDate]);

    const handleOpenEditOrderModal = async (orderId) => {
        setEditingOrderId(orderId);
        await loadProducts();
        setSelectedProduct('');
        setAvailableVariants(null);
        setSelectedVariant('');
        setSelectedQuantity(1);
        setShowEditOrderModal(true);
    };

    const handleOpenDetailsModal = async (orderId) => {
        setEditingOrderId(orderId);
        try {
            const items = await getOrderItems(token, orderId);
    
            const enrichedItems = await Promise.all(
                items.map(async (item) => {
                    if (item.productVariationId) {
                        const variation = await getProductVariation(token, item.productId, item.productVariationId);
                        return {
                            ...item,
                            name: variation.name,
                            price: variation.price,
                        };
                    } else if (item.productId) {
                        const product = await getProduct(token, item.productId);
                        return {
                            ...item,
                            name: product.name,
                            price: product.price,
                        };
                    }
                    return item;
                })
            );
    
            setOrderItems(enrichedItems);
            setSelectedOrderId(orderId);
    
            // Reset reservation-related states when opening details modal
            setSelectedServiceId("");
            setSelectedDate("");
            setAvailableSlots([]);
            setHasCheckedSlots(false);
            setErrorMessage("");
            setSuccessMessage("");
    
            setShowDetailsModal(true);
        } catch (error) {
            console.error("Error fetching order items:", error);
            alert("Failed to load order details.");
        }
    };
    
    
    
    

    const handleProductSelection = async (productId) => {
        setSelectedProduct(productId);
        setSelectedVariant('Original'); // Default to "Original" variant
        if (productId) {
            try {
                const variants = await getProductVariations(token, productId);
                setAvailableVariants(variants.length ? variants : null);
            } catch (error) {
                console.error("Error fetching product variations:", error);
                setAvailableVariants(null);
            }
        } else {
            setAvailableVariants(null);
        }
    };
    

    const handleAddItemToOrder = async (reservationId) => {
        if (selectedQuantity <= 0) {
            alert("Please select a product and enter a valid quantity. QTY: ", selectedQuantity);
            return;
        }

        if (editingOrderId === null) {
            alert("No order selected for adding item.");
            return;
        }

        try {
            const payload = {
                quantity: selectedQuantity,
            };
            console.log("Selected variant:", selectedVariant);
            console.log("Reservation ID:", reservationId);
            if (reservationId !== null) {
                payload.reservationId = reservationId;
            }
            else if (selectedVariant === 'Original') {
                payload.productId = selectedProduct;
            }
            else if (selectedVariant !== 'Original') {
                payload.productVariationId = selectedVariant;
            }

            console.log("Adding item to order:", editingOrderId, " ", payload);
    
            await addItemToOrder(token, editingOrderId, payload);

            setCreatedReservationId(null);

            await loadOrders(); // Refresh the orders
            setShowEditOrderModal(false); // Close the modal
        } catch (error) {
            console.error("Error adding item to order:", error);
            alert("Failed to add item to order.");
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
                employeeId: user.id,
                appointedAt: reservationSlot.startTime,
                firstName,
                lastName,
                phone
            };

            const response = await createReservation(token, reservationRequest);
            if (response && response.id) {
                // Store the newly created reservationId
                setSelectedQuantity(1);
                handleAddItemToOrder(response.id);
    
                setSuccessMessage("Reservation created successfully!");
                setErrorMessage("");
    
                // Optionally refresh slots and reservations
                await handleCheckSlots();
                await loadReservations();
    
                // Close modal after delay
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
            setErrorMessage(error.message || "Failed to create reservation. Please try again.");
        }
    };

    // Update Reservation handlers
    const handleOpenUpdateModal = (reservation) => {
        setUpdateReservationId(reservation.id);
        setUpdateData({
            firstName: reservation.firstName,
            lastName: reservation.lastName,
            phone: reservation.phone
        });
        setShowUpdateModal(true);
        setErrorMessage("");
        setSuccessMessage("");
    };

    const handleUpdateInputChange = (e) => {
        const { name, value } = e.target;
        setUpdateData(prev => ({ ...prev, [name]: value }));
    };

    const handleUpdateReservation = async (e) => {
        e.preventDefault();
        const { firstName, lastName, phone } = updateData;

        if (!firstName.trim() || !lastName.trim() || !phone.trim()) {
            setErrorMessage("All fields are required.");
            return;
        }

        try {
            const updatePayload = {
                serviceId: selectedServiceId,
                employeeId: user.id, // Assuming employee cannot change
                // If you want to allow changing the appointment time, you can include it here
                firstName,
                lastName,
                phone
            };

            const response = await updateReservation(token, updateReservationId, updatePayload);
            if (response && response.id) {
                setSuccessMessage("Reservation updated successfully!");
                setErrorMessage("");
                await loadReservations();
                // Close the modal after a short delay
                setTimeout(() => {
                    setShowUpdateModal(false);
                    setSuccessMessage("");
                    setUpdateData({ firstName: "", lastName: "", phone: "" });
                }, 2000);
            } else {
                setErrorMessage("Failed to update reservation. Please try again.");
            }
        } catch (error) {
            console.error("Error updating reservation:", error);
            setErrorMessage(error.message || "Failed to update reservation. Please try again.");
        }
    };

    const handleDeleteReservation = async (reservationId) => {
        const confirmDelete = window.confirm("Are you sure you want to cancel this reservation?");
        if (!confirmDelete) return;

        try {
            await cancelReservation(token, reservationId);
            setSuccessMessage("Reservation cancelled successfully!");
            setErrorMessage("");
            await loadReservations();
            // Optionally, refresh available slots
            await handleCheckSlots();
            // Hide the success message after some time
            setTimeout(() => {
                setSuccessMessage("");
            }, 2000);
        } catch (error) {
            console.error("Error cancelling reservation:", error);
            setErrorMessage(error.message || "Failed to cancel reservation. Please try again.");
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
    
                        {user.merchantId && (
                            <>
                                <div className="orders-section">
                                    <h2>Your Orders</h2>
                                    <button className="create-order-button" onClick={handleCreateOrder}>Create New Order</button>
                                    {loadingOrders ? (
                                        <p>Loading orders...</p>
                                    ) : (
                                        orders.length > 0 ? (
                                            <table className="reservations-table">
                                                <thead>
                                                    <tr>
                                                        <th>ID</th>
                                                        <th>Status</th>
                                                        <th>Items</th>
                                                        <th>Total Price</th>
                                                        <th>Created At</th>
                                                        <th>Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {orders.map((order) => (
                                                        <tr key={order.id}>
                                                            <td>{order.id.substring(0, 8)}</td>
                                                            <td>{order.status}</td>
                                                            <td>{order.items.length}</td>
                                                            <td>
                                                                {typeof order.totalPrice === "number"
                                                                    ? `$${order.totalPrice.toFixed(2)}`
                                                                    : "Calculating..."}
                                                            </td>
                                                            <td>{new Date(order.createdAt).toLocaleString()}</td>
                                                            <td>
                                                                <button
                                                                    className="details-button"
                                                                    onClick={() => handleOpenDetailsModal(order.id)}
                                                                >
                                                                    Details
                                                                </button>
                                                                <button
                                                                    className="edit-button"
                                                                    onClick={() => handleOpenEditOrderModal(order.id)}
                                                                >
                                                                    Edit
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                </tbody>
                                            </table>
                                        ) : (
                                            <p>No orders found.</p>
                                        )
                                    )}
                                </div>
                            </>
                        )}
                    </div>
                )
            )}
    
            {showDetailsModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Order Details</h2>
                        <p>Order ID: {selectedOrderId}</p>
    
                        {orderItems.length > 0 ? (
                            <table className="reservations-table">
                                <thead>
                                    <tr>
                                        <th>Product Name</th>
                                        <th>Price</th>
                                        <th>Quantity</th>
                                        <th>Created At</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {orderItems.map((item) => (
                                        <tr key={item.id}>
                                            <td>{item.name || "Unknown"}</td>
                                            <td>{item.price ? `$${item.price.toFixed(2)}` : "N/A"}</td>
                                            <td>
                                                <input
                                                    type="number"
                                                    value={item.quantity}
                                                    className="quantity-input"
                                                    onChange={(e) =>
                                                        handleQuantityChange(selectedOrderId, item.id, parseInt(e.target.value, 10))
                                                    }
                                                />
                                            </td>
                                            <td>{new Date(item.createdAt).toLocaleString()}</td>
                                            <td>
                                                <button
                                                    className="delete-button"
                                                    onClick={() => handleDeleteItem(selectedOrderId, item.id)}
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>No items in this order.</p>
                        )}

                        <button
                            className="add-item-button"
                            onClick={() => handleOpenEditOrderModal(selectedOrderId)}
                        >
                            Add New Item
                        </button>
    
                        <h3>Make a Reservation for This Order</h3>
                        {errorMessage && <p className="error-message">{errorMessage}</p>}
                        {successMessage && <p className="success-message">{successMessage}</p>}
    
                        <div className="controls">
                            <select
                                value={selectedServiceId}
                                onChange={(e) => setSelectedServiceId(e.target.value)}
                            >
                                <option value="">Select Service</option>
                                {services.map(s => (
                                    <option key={s.id} value={s.id}>
                                        {s.name} - ${s.price} USD, {Math.floor(s.duration / 60)}m {s.duration % 60}s
                                    </option>
                                ))}
                            </select>
    
                            <input
                                type="date"
                                value={selectedDate}
                                onChange={(e) => setSelectedDate(e.target.value)}
                            />
                            <button onClick={handleCheckSlots}>Check Slots</button>
                        </div>
    
                        {availableSlots.length > 0 && (
                            <div className="slots-section">
                                <h3>Available Slots</h3>
                                <ul>
                                    {availableSlots.map((slot, index) => (
                                        <li key={index}>
                                            <span>
                                                {new Date(slot.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} - {new Date(slot.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                            </span>
                                            <button className="book-button" onClick={() => handleOpenReservationModal(slot)}>Book</button>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
    
                        {hasCheckedSlots && availableSlots.length === 0 && selectedServiceId && selectedDate && (
                            <p>No available slots for the selected service and date.</p>
                        )}
    
                        <div className="modal-buttons">
                            <button onClick={() => setShowDetailsModal(false)}>Close</button>
                        </div>
                    </div>
                </div>
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

            {showEditOrderModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Edit Order</h2>
                        <p>Order ID: {editingOrderId}</p>
                        <div className="product-selection">
                            <label htmlFor="product">Select Product:</label>
                            <select
                                id="product"
                                value={selectedProduct}
                                onChange={(e) => handleProductSelection(e.target.value)}
                            >
                                <option value="">-- Choose a product --</option>
                                {availableProducts.map((product) => (
                                    <option key={product.id} value={product.id}>
                                        {product.name} (${product.price})
                                    </option>
                                ))}
                            </select>
                        </div>
    
                        {availableVariants && (
                            <div className="variant-selection">
                                <label htmlFor="variant">Select Variant:</label>
                                <select
                                    id="variant"
                                    value={selectedVariant}
                                    onChange={(e) => setSelectedVariant(e.target.value)}
                                >
                                    <option value="Original">Original</option>
                                    {availableVariants.map((variant) => (
                                        <option key={variant.id} value={variant.id}>
                                            {variant.name} (${variant.price})
                                        </option>
                                    ))}
                                </select>
                            </div>
                        )}
    
                        <div className="quantity-selection">
                            <label htmlFor="quantity">Quantity:</label>
                            <input
                                type="number"
                                id="quantity"
                                min="1"
                                value={selectedQuantity}
                                onChange={(e) => setSelectedQuantity(parseInt(e.target.value))}
                            />
                        </div>

                        <div className="modal-buttons">
                            <button onClick={() => handleAddItemToOrder(null)}>Add Item</button>
                            <button onClick={() => setShowEditOrderModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );    
};
