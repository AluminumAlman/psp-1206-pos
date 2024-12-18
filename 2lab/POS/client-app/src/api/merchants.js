const BASE = 'http://localhost:8080';

export async function getMerchants(token) {
    const res = await fetch(`${BASE}/merchants`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createMerchant(token, data) {
    const res = await fetch(`${BASE}/merchants`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
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
