const BASE = 'http://localhost:8080';

// Fetch Services with Filters and Pagination
export async function getServices(token, { limit = 10, offset = 0, name, price, duration } = {}) {
    const params = new URLSearchParams({ limit, offset });
    if (name) params.append('name', name);
    if (price) params.append('price', price);
    if (duration) params.append('duration', duration);

    const res = await fetch(`${BASE}/services?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch services.');
    }

    return res.json();
}

// Create a New Service
export async function createService(token, data) {
    const res = await fetch(`${BASE}/services`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to create service.');
    }

    return res.json();
}

// Get Service by ID
export async function getServiceById(token, serviceId) {
    const res = await fetch(`${BASE}/services/${serviceId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch service.');
    }

    return res.json();
}

// Update Service by ID
export async function updateService(token, serviceId, data) {
    const res = await fetch(`${BASE}/services/${serviceId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to update service.');
    }

    return res.json();
}

// Delete Service by ID
export async function deleteService(token, serviceId) {
    const res = await fetch(`${BASE}/services/${serviceId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to delete service.');
    }
}

// Get Available Reservation Slots
export async function getAvailableSlots(token, serviceId, date, userId) {
    const params = new URLSearchParams({ date });
    if (userId) params.append('userId', userId);

    const res = await fetch(`${BASE}/services/${serviceId}/availableSlots?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to fetch available slots.');
    }

    return res.json();
}
