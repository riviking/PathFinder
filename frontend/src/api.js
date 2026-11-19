const KEY = 'pathfinder_token'

export const getToken = () => sessionStorage.getItem(KEY)
export const setToken = (t) => sessionStorage.setItem(KEY, t)
export const clearToken = () => sessionStorage.removeItem(KEY)

// හැම API ඇමතුමක්ම මෙතනින්: token එක ඉබේම එකතු වෙනවා, වැරදි JSON පණිවිඩයෙන් error එකක් ලැබෙනවා
export async function api(path, { method = 'GET', body } = {}) {
    const headers = {}
    if (body) headers['Content-Type'] = 'application/json'
    const token = getToken()
    if (token) headers.Authorization = `Bearer ${token}`

    const res = await fetch(path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    })
    const data = res.status === 204 ? null : await res.json().catch(() => null)
    if (!res.ok) {
        // token තිබියදී 401 ආවොත් = session කල් ඉකුත් (වැරදි password ඇමතුමට token නෑ, ඒ නිසා බලපෑමක් නෑ)
        if (res.status === 401 && token) window.dispatchEvent(new Event('pathfinder:unauthorized'))
        const err = new Error(data?.message || `Request failed (${res.status})`)
        err.status = res.status
        throw err
    }
    return data
}