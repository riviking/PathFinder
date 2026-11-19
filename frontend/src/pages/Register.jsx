import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../AuthContext'

export default function Register() {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirm, setConfirm] = useState('')
    const [error, setError] = useState('')
    const [busy, setBusy] = useState(false)

    async function submit(e) {
        e.preventDefault()
        setError('')
        if (password.length < 8) {
            setError('Password must be at least 8 characters.')
            return
        }
        if (password !== confirm) {
            setError('Passwords do not match.')
            return
        }
        setBusy(true)
        try {
            await api('/api/auth/register', { method: 'POST', body: { name, email, password } })
            await login(email, password)
            navigate('/')
        } catch (err) {
            setError(err.message)
        } finally {
            setBusy(false)
        }
    }

    return (
        <form onSubmit={submit} style={{ display: 'grid', gap: 12, width: 320 }}>
            <h1>Create account</h1>
            <input placeholder="Full name" value={name}
                   onChange={(e) => setName(e.target.value)} required />
            <input type="email" placeholder="Email" value={email}
                   onChange={(e) => setEmail(e.target.value)} required />
            <input type="password" placeholder="Password (min 8 characters)" value={password}
                   onChange={(e) => setPassword(e.target.value)} required />
            <input type="password" placeholder="Confirm password" value={confirm}
                   onChange={(e) => setConfirm(e.target.value)} required />
            {error && <p style={{ color: 'tomato' }}>{error}</p>}
            <button type="submit" disabled={busy}>{busy ? 'Creating...' : 'Register'}</button>
            <p>Already have an account? <Link to="/login">Sign in</Link></p>
        </form>
    )
}