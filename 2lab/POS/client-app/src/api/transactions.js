const BASE = 'http://localhost:8080';

export async function getTransactions(token, {limit=10, offset=0, orderId}={}) {
    const params = new URLSearchParams({ limit, offset });
    if (orderId) params.append('orderId', orderId);

    const res = await fetch(`${BASE}/transactions?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
