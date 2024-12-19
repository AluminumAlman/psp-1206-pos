const BASE = 'http://localhost:8080';

export async function getCurrentUser(token) {
    const res = await fetch(`${BASE}/users/me`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function getUser(token, userId) {
    const res = await fetch(`${BASE}/users/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateUser(token, userId, data) {
    const res = await fetch(`${BASE}/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteUser(token, userId) {
    await fetch(`${BASE}/users/${userId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function getUsers(token, { firstname, lastname, email } = {}) {
    const params = new URLSearchParams();
    if (firstname) params.append('firstname', firstname);
    if (lastname) params.append('lastname', lastname);
    if (email) params.append('email', email);

    const res = await fetch(`${BASE}/users?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function assignMerchantToUser(token, userId, merchantId) {
    const res = await fetch(`${BASE}/users/${userId}/merchant`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ merchantId })
    });
    return res.json();
}

export async function switchMerchant(token, merchantId) {
    const params = new URLSearchParams();
    if (merchantId) params.append('merchantId', merchantId);

    const res = await fetch(`${BASE}/users/switch-merchant?${params}`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
