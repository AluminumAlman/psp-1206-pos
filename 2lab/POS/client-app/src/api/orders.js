const BASE = 'http://localhost:8080';

//
// OrderController endpoints
//
export async function getOrders(token, {limit=20, offset=0, status, dateFrom, dateTo}={}) {
    const params = new URLSearchParams({ limit, offset });
    if (status) params.append('status', status);
    if (dateFrom) params.append('dateFrom', dateFrom);
    if (dateTo) params.append('dateTo', dateTo);

    const res = await fetch(`${BASE}/orders?${params.toString()}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createOrder(token, data) {
    const res = await fetch(`${BASE}/orders`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

//
// OrderChargeController endpoints
//
export async function getOrderCharges(token, orderId, {offset=0, limit=20}={}) {
    const params = new URLSearchParams({ offset, limit });
    const res = await fetch(`${BASE}/orders/${orderId}/charges?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createOrderCharge(token, orderId, data) {
    const res = await fetch(`${BASE}/orders/${orderId}/charges`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteOrderCharge(token, orderId, chargeId) {
    await fetch(`${BASE}/orders/${orderId}/charges/${chargeId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

//
// OrderItemController endpoints
//
export async function getOrderItems(token, orderId) {
    const res = await fetch(`${BASE}/orders/${orderId}/items`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function addItemToOrder(token, orderId, data) {
    const res = await fetch(`${BASE}/orders/${orderId}/items`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function updateOrderItem(token, orderId, orderItemId, data) {
    const res = await fetch(`${BASE}/orders/${orderId}/items/${orderItemId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getTotalOrderAmount(token, orderId) {
    try {
        const res = await fetch(`${BASE}/orders/${orderId}/totalPrice`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch total price for order ${orderId}: ${res.status}`);
        }

        const text = await res.text(); // Use text() to handle plain number responses
        return parseFloat(text); // Convert the response to a number
    } catch (error) {
        console.error("Error fetching total order amount:", error);
        return 0; // Return 0 in case of error
    }
}


export async function deleteOrderItem(token, orderId, orderItemId) {
    const res = await fetch(`${BASE}/orders/${orderId}/items/${orderItemId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
