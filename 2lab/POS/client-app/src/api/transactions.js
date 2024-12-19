const BASE = 'http://localhost:8080';

// Get paged transactions by orderId (if provided) or all
// GET /orders/transactions?limit=&offset=&orderId=&paymentMethodType=&status=&amount=
export async function getTransactions(token, {limit=20, offset=0, orderId, paymentMethodType, status, amount}={}) {
    const params = new URLSearchParams({ limit, offset });
    if (orderId) params.append('orderId', orderId);
    if (paymentMethodType) params.append('paymentMethodType', paymentMethodType);
    if (status) params.append('status', status);
    if (amount) params.append('amount', amount);

    const res = await fetch(`${BASE}/orders/transactions?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

// Get paged transactions for a specific order
// GET /orders/{orderId}/transactions?limit=&offset=
export async function getOrderTransactions(token, orderId, {limit=20, offset=0}={}) {
    const params = new URLSearchParams({ limit, offset });
    const res = await fetch(`${BASE}/orders/${orderId}/transactions?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

// Create transaction for an order
// POST /orders/{orderId}/transactions
export async function createTransaction(token, orderId, data) {
    const res = await fetch(`${BASE}/orders/${orderId}/transactions`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

// Get transaction details by orderId and transactionId
// GET /orders/{orderId}/transactions/{transactionId}
export async function getTransaction(token, orderId, transactionId) {
    const res = await fetch(`${BASE}/orders/${orderId}/transactions/${transactionId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

// Mark cash transaction as completed
// PATCH /orders/{orderId}/transactions/{transactionId}/complete
export async function completeTransaction(token, orderId, transactionId) {
    const res = await fetch(`${BASE}/orders/${orderId}/transactions/${transactionId}/complete`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

// Refund transaction
// PATCH /orders/{orderId}/transactions/{transactionId}/refund
export async function refundTransaction(token, orderId, transactionId) {
    const res = await fetch(`${BASE}/orders/${orderId}/transactions/${transactionId}/refund`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
