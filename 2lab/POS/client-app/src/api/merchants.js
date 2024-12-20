const BASE = 'http://localhost:8080';

export async function getAllMerchants(token) {
    const res = await fetch(`${BASE}/merchants`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

// createMerchant function updated with schedule support:
export async function createMerchant(token, data) {
    const res = await fetch(`http://localhost:8080/merchants`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            name: data.name,
            phone: data.phone,
            email: data.email,
            currency: data.currency,
            address: data.address,
            city: data.city,
            country: data.country,
            postcode: data.postcode,
            schedule: data.schedule // Add the schedule to the request body
        }),
    });

    if (!res.ok) {
        throw new Error(`Failed to create merchant: ${res.statusText}`);
    }

    return res.json();
}

export async function getMerchant(token, merchantId) {
    const res = await fetch(`${BASE}/merchants/${merchantId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateMerchant(token, merchantId, data) {
    const res = await fetch(`${BASE}/merchants/${merchantId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteMerchant(token, merchantId) {
    await fetch(`${BASE}/merchants/${merchantId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}
