const BASE = 'http://localhost:8080';

export async function getChargesByType(token, {limit=20, offset=0, chargeType}) {
    const params = new URLSearchParams({ limit, offset, chargeType });
    const res = await fetch(`${BASE}/charges?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createCharge(token, data) {
    const res = await fetch(`${BASE}/charges`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getChargeById(token, chargeId) {
    const res = await fetch(`${BASE}/charges/${chargeId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateCharge(token, chargeId, data) {
    const res = await fetch(`${BASE}/charges/${chargeId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deactivateCharge(token, chargeId) {
    await fetch(`${BASE}/charges/${chargeId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function reactivateCharge(token, chargeId) {
    const res = await fetch(`${BASE}/charges/${chargeId}/reactivate`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function getChargesByMerchant(token, {limit=10, offset=0, merchantId}) {
    const params = new URLSearchParams({ limit, offset, merchantId });
    const res = await fetch(`${BASE}/charges/merchant?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
