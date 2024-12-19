const BASE = 'http://localhost:8080';

export async function getDiscounts(token) {
    const res = await fetch(`${BASE}/discounts`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createDiscount(token, data) {
    const res = await fetch(`${BASE}/discounts`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getDiscount(token, discountId) {
    const res = await fetch(`${BASE}/discounts/${discountId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateDiscount(token, discountId, data) {
    const res = await fetch(`${BASE}/discounts/${discountId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteDiscount(token, discountId) {
    await fetch(`${BASE}/discounts/${discountId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}
