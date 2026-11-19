import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../AuthContext'

export default function Register() {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [busy, setBusy] = useState(false)

    async function submit(e) {
        e.preventDefault()
        setError('')
        if (password.length < 8) {
            setError('Use at least 8 characters.')
            return
        }
        setBusy(true)
        try {
            await api('/api/auth/register', { method: 'POST', body: { email, password } })
            await login(email, password)
            navigate('/')
        } catch (err) {
            setError(err.message)
        } finally {
            setBusy(false)
        }
    }

    return (
        <div className="auth-page">
            <div className="brand">PathFinder CGU Internship Platform</div>
            <form className="auth-card" onSubmit={submit}>
                <div>
                    <h1>Student Registration</h1>
                    <p className="hint">Create your student account using your university email and a password.</p>
                </div>
                <div className="field">
                    <label htmlFor="email">University Email</label>
                    <input id="email" type="email" value={email}
                           placeholder="Enter your university email address"
                           onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className="field">
                    <label htmlFor="password">Password</label>
                    <input id="password" type="password" value={password}
                           placeholder="Create your password"
                           onChange={(e) => setPassword(e.target.value)} required />
                </div>
                {error && <p className="msg error">{error}</p>}
                <button className="btn" type="submit" disabled={busy}>
                    {busy ? 'Creating...' : 'Register'}
                </button>
                <p className="link-row">Already have an account? <Link to="/login">Log In</Link></p>
            </form>
        </div>
    )
}