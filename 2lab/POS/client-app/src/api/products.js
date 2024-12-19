const BASE = 'http://localhost:8080';

// Products
export async function getAllProducts(token, {name, price, categoryId, offset=0, limit=20}={}) {
    const params = new URLSearchParams({ offset, limit });
    if (name) params.append('name', name);
    if (price) params.append('price', price);
    if (categoryId) params.append('categoryId', categoryId);

    const res = await fetch(`${BASE}/products?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createProduct(token, data) {
    const res = await fetch(`${BASE}/products`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getProduct(token, id) {
    const res = await fetch(`${BASE}/products/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateProduct(token, id, data) {
    const res = await fetch(`${BASE}/products/${id}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteProduct(token, id) {
    await fetch(`${BASE}/products/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function adjustProductQuantity(token, id, adjustment) {
    const res = await fetch(`${BASE}/products/${id}/adjust-quantity`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify({ adjustment })
    });
    return res.json();
}

// Product Categories
export async function getAllProductCategories(token) {
    const res = await fetch(`${BASE}/productCategories`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createProductCategory(token, data) {
    const res = await fetch(`${BASE}/productCategories`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getProductCategoryById(token, id) {
    const res = await fetch(`${BASE}/productCategories/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateCategory(token, id, data) {
    const res = await fetch(`${BASE}/productCategories/${id}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteCategory(token, id) {
    await fetch(`${BASE}/productCategories/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

// Product Variations
export async function getProductVariations(token, productId) {
    const res = await fetch(`${BASE}/products/${productId}/variations`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function createProductVariation(token, productId, data) {
    const res = await fetch(`${BASE}/products/${productId}/variations`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getProductVariation(token, productId, variationId) {
    const res = await fetch(`${BASE}/products/${productId}/variations/${variationId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function updateProductVariation(token, productId, variationId, data) {
    const res = await fetch(`${BASE}/products/${productId}/variations/${variationId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function deleteProductVariation(token, productId, variationId) {
    await fetch(`${BASE}/products/${productId}/variations/${variationId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
}

export async function adjustProductVariationQuantity(token, productId, variationId, adjustment) {
    const res = await fetch(`${BASE}/products/${productId}/variations/${variationId}/adjust-quantity`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify({ adjustment })
    });
    return res.json();
}

// Inventory Logs
export async function createInventoryLog(token, data) {
    const res = await fetch(`${BASE}/inventoryLog`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json'
        },
        body: JSON.stringify(data)
    });
    return res.json();
}

export async function getInventoryLog(token, id) {
    const res = await fetch(`${BASE}/inventoryLog/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}

export async function getAllInventoryLogs(token, {offset=0, limit=20}={}) {
    // As per the given controller, filter is passed in request body (which is unusual for GET).
    // We'll call it without filter, just offset and limit.
    const params = new URLSearchParams({ offset, limit });
    const res = await fetch(`${BASE}/inventoryLog?${params}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    return res.json();
}
