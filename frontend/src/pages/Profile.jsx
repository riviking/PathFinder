import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import { CATEGORIES } from '../categories'

export default function Profile() {
    const [selected, setSelected] = useState([])
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [message, setMessage] = useState('')
    const [error, setError] = useState('')

    useEffect(() => {
        api('/api/profile')
            .then((p) => setSelected(p.categoryIds))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    function toggle(id) {
        setMessage('')
        setSelected((cur) => (cur.includes(id) ? cur.filter((x) => x !== id) : [...cur, id]))
    }

    async function save() {
        setSaving(true)
        setError('')
        setMessage('')
        try {
            const p = await api('/api/profile/categories', {
                method: 'PUT',
                body: { categoryIds: selected },
            })
            setSelected(p.categoryIds)
            setMessage('Saved.')
        } catch (err) {
            setError(err.message)
        } finally {
            setSaving(false)
        }
    }

    if (loading) return <p>Loading...</p>

    return (
        <div style={{ display: 'grid', gap: 12, width: 360 }}>
            <h1>My interests</h1>
            <p>Pick the job categories you want to hear about. You will get an email when a matching internship is posted.</p>
            {CATEGORIES.map((c) => (
                <label key={c.id} style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                    <input type="checkbox" checked={selected.includes(c.id)} onChange={() => toggle(c.id)} />
                    {c.name}
                </label>
            ))}
            {error && <p style={{ color: 'tomato' }}>{error}</p>}
            {message && <p style={{ color: 'seagreen' }}>{message}</p>}
            <button onClick={save} disabled={saving}>{saving ? 'Saving...' : 'Save'}</button>
            <p><Link to="/">Back</Link></p>
        </div>
    )
}