const BASE = 'http://localhost:8080';

export async function getServices(token, {limit=10, offset=0, name, price, duration}={}) {
    const params = new URLSearchParams({ limit, offset });
    if (name) params.append('name', name);
    if (price) params.append('price', price);
    if (duration) params.append('duration', duration);

    const res = await fetch(`${BASE}/services?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createService(token, data) {
    const res = await fetch(`${BASE}/services`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getServiceById(token, serviceId) {
    const res = await fetch(`${BASE}/services/${serviceId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateService(token, serviceId, data) {
    const res = await fetch(`${BASE}/services/${serviceId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteService(token, serviceId) {
    await fetch(`${BASE}/services/${serviceId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function getAvailableSlots(token, serviceId, date) {
    const params = new URLSearchParams({ date });
    const res = await fetch(`${BASE}/services/${serviceId}/availableSlots?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
