const BASE = 'http://localhost:8080';

export async function getReservations(token, {limit=20, offset=0, serviceName, customerName, customerEmail, customerPhone, appointedAt}={}) {
    const params = new URLSearchParams({ limit, offset });
    if (serviceName) params.append('service-name', serviceName);
    if (customerName) params.append('customer-name', customerName);
    if (customerEmail) params.append('customer-email', customerEmail);
    if (customerPhone) params.append('customer-phone', customerPhone);
    if (appointedAt) params.append('appointedAt', appointedAt);

    const res = await fetch(`${BASE}/reservations?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createReservation(token, data) {
    const res = await fetch(`${BASE}/reservations`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getReservationById(token, reservationId) {
    const res = await fetch(`${BASE}/reservations/${reservationId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function cancelReservation(token, reservationId) {
    await fetch(`${BASE}/reservations/${reservationId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function updateReservation(token, reservationId, data) {
    const res = await fetch(`${BASE}/reservations/${reservationId}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}
