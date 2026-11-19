import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'

export default function StaffAccounts() {
    const [staff, setStaff] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [busy, setBusy] = useState(false)

    async function load() {
        try {
            setStaff(await api('/api/admin/staff'))
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
    }, [])

    async function create(e) {
        e.preventDefault()
        setError('')
        setMessage('')
        if (password.length < 8) {
            setError('Password must be at least 8 characters.')
            return
        }
        setBusy(true)
        try {
            await api('/api/admin/staff', { method: 'POST', body: { name, email, password } })
            setMessage(`Staff account created for ${email}.`)
            setName('')
            setEmail('')
            setPassword('')
            await load()
        } catch (err) {
            setError(err.message)
        } finally {
            setBusy(false)
        }
    }

    async function setActive(s, active) {
        if (!active && !window.confirm(`Deactivate ${s.name}? They will not be able to sign in.`)) return
        setError('')
        setMessage('')
        try {
            await api(`/api/admin/staff/${s.id}/active`, { method: 'PATCH', body: { active } })
            setMessage(`${s.name} ${active ? 'reactivated' : 'deactivated'}.`)
            await load()
        } catch (err) {
            setError(err.message)
        }
    }

    if (loading) return <p>Loading...</p>

    return (
        <div style={{ display: 'grid', gap: 16, width: 560 }}>
            <h1>Staff accounts</h1>
            {error && <p style={{ color: 'tomato' }}>{error}</p>}
            {message && <p style={{ color: 'seagreen' }}>{message}</p>}

            <table style={{ borderCollapse: 'collapse', width: '100%' }}>
                <thead>
                <tr style={{ textAlign: 'left' }}>
                    <th>Name</th><th>Email</th><th>Status</th><th></th>
                </tr>
                </thead>
                <tbody>
                {staff.map((s) => (
                    <tr key={s.id}>
                        <td>{s.name}</td>
                        <td>{s.email}</td>
                        <td>{s.active ? 'Active' : 'Inactive'}</td>
                        <td>
                            {s.active
                                ? <button onClick={() => setActive(s, false)}>Deactivate</button>
                                : <button onClick={() => setActive(s, true)}>Reactivate</button>}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <form onSubmit={create} style={{ display: 'grid', gap: 8 }}>
                <h2>Add a staff member</h2>
                <input placeholder="Full name" value={name} onChange={(e) => setName(e.target.value)} required />
                <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                <input type="password" placeholder="Temporary password (min 8 characters)" value={password}
                       onChange={(e) => setPassword(e.target.value)} required />
                <button type="submit" disabled={busy}>{busy ? 'Creating...' : 'Create staff account'}</button>
            </form>

            <p><Link to="/">Back</Link></p>
        </div>
    )
}