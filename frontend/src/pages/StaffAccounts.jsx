import { useEffect, useState } from 'react'
import { api } from '../api'
import Layout from '../Layout'

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
            setError('Use at least 8 characters.')
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

    return (
        <Layout title="Staff Accounts" subtitle="Only an Admin can create, deactivate and reactivate staff accounts.">
            {error && <p className="msg error">{error}</p>}
            {message && <p className="msg ok">{message}</p>}

            <section className="card">
                {loading ? <p>Loading...</p> : (
                    <div className="table-wrap">
                        <table className="table">
                            <thead>
                            <tr><th>Name</th><th>Email</th><th>Status</th><th>Actions</th></tr>
                            </thead>
                            <tbody>
                            {staff.length === 0 && (
                                <tr><td colSpan="4" className="hint">No staff accounts yet.</td></tr>
                            )}
                            {staff.map((s) => (
                                <tr key={s.id}>
                                    <td>{s.name}</td>
                                    <td>{s.email}</td>
                                    <td><span className={`badge ${s.active ? 'ok' : 'off'}`}>{s.active ? 'Active' : 'Deactivated'}</span></td>
                                    <td>
                                        {s.active
                                            ? <button className="btn danger small" onClick={() => setActive(s, false)}>Deactivate</button>
                                            : <button className="btn secondary small" onClick={() => setActive(s, true)}>Reactivate</button>}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            <form className="card" onSubmit={create}>
                <h2>Create staff account</h2>
                <div className="form-grid">
                    <div className="field">
                        <label htmlFor="sname">Name</label>
                        <input id="sname" type="text" value={name} onChange={(e) => setName(e.target.value)} required />
                    </div>
                    <div className="field">
                        <label htmlFor="semail">Email</label>
                        <input id="semail" type="email" value={email} autoComplete="off" onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <div className="field">
                        <label htmlFor="spass">Temporary password</label>
                        <input id="spass" type="password" value={password} autoComplete="new-password" onChange={(e) => setPassword(e.target.value)} required />
                    </div>
                    <div className="btn-row">
                        <button className="btn" type="submit" disabled={busy}>{busy ? 'Creating...' : 'Create staff account'}</button>
                    </div>
                </div>
                <p className="hint">Staff cannot register themselves.</p>
            </form>
        </Layout>
    )
}