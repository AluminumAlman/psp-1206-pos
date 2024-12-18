const BASE = 'http://localhost:8080';

export async function getOrderCharges(token, orderId, {offset=0, limit=20}={}) {
    const params = new URLSearchParams({ offset, limit });
    const res = await fetch(`${BASE}/order/${orderId}/charges?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createOrderCharge(token, orderId, data) {
    const res = await fetch(`${BASE}/order/${orderId}/charges`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}
