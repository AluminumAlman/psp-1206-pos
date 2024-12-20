const BASE = 'http://localhost:8080';

// Fetch Reservations with Filters and Pagination
export async function getReservations(token, { limit = 20, offset = 0, serviceName, customerName, customerEmail, customerPhone, appointedAt } = {}) {
    const params = new URLSearchParams({ limit, offset });
    if (serviceName) params.append('service-name', serviceName);
    if (customerName) params.append('customer-name', customerName);
    if (customerEmail) params.append('customer-email', customerEmail);
    if (customerPhone) params.append('customer-phone', customerPhone);
    if (appointedAt) params.append('appointedAt', appointedAt);

    const res = await fetch(`${BASE}/reservations?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch reservations.');
    }

    return res.json();
}

// Create a New Reservation
export async function createReservation(token, data) {
    const res = await fetch(`${BASE}/reservations`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to create reservation.');
    }

    return res.json();
}

// Get Reservation by ID
export async function getReservationById(token, reservationId) {
    const res = await fetch(`${BASE}/reservations/${reservationId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch reservation.');
    }

    return res.json();
}

// Cancel Reservation by ID
export async function cancelReservation(token, reservationId) {
    const res = await fetch(`${BASE}/reservations/${reservationId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to cancel reservation.');
    }

    return;
}

// Update Reservation by ID
export async function updateReservation(token, reservationId, data) {
    const res = await fetch(`${BASE}/reservations/${reservationId}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to update reservation.');
    }

    return res.json();
}
