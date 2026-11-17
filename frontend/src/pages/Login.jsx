import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'




export default function Login() {
    const { login, notice } = useAuth()
    const navigate = useNavigate()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [busy, setBusy] = useState(false)

    async function submit(e) {
        e.preventDefault()
        setError('')
        setBusy(true)
        try {
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
            <h1>PathFinder Login</h1>
            <input type="email" placeholder="Email" value={email}
                   onChange={(e) => setEmail(e.target.value)} required />
            <input type="password" placeholder="Password" value={password}
                   onChange={(e) => setPassword(e.target.value)} required />
            {notice && <p style={{ color: 'orange' }}>{notice}</p>}
            {error && <p style={{ color: 'tomato' }}>{error}</p>}
            <button type="submit" disabled={busy}>{busy ? 'Signing in...' : 'Sign in'}</button>
            <p>No account yet? <Link to="/register">Register</Link></p>
        </form>
    )
}