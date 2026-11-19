import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

// admin={true} නම් staff/admin login පිටුව
export default function Login({ admin = false }) {
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
        <div className="auth-page">
            <div className="brand">
                PathFinder CGU Internship Platform
                {admin && <small>Administrator access</small>}
            </div>
            <form className="auth-card" onSubmit={submit}>
                <div>
                    <h1>Login</h1>
                    <p className="hint">
                        {admin
                            ? 'Log in with your admin or staff account.'
                            : 'Log in with your university email and the password you registered with.'}
                    </p>
                </div>
                <div className="field">
                    <label htmlFor="email">{admin ? 'Email' : 'University Email'}</label>
                    <input id="email" type="email" value={email}
                           placeholder={admin ? 'Enter your email address' : 'Enter your university email address'}
                           onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className="field">
                    <label htmlFor="password">Password</label>
                    <input id="password" type="password" value={password}
                           placeholder={admin ? 'Enter your password' : 'Enter your registered password'}
                           onChange={(e) => setPassword(e.target.value)} required />
                </div>
                {notice && <p className="msg notice">{notice}</p>}
                {error && <p className="msg error">{error}</p>}
                <button className="btn" type="submit" disabled={busy}>
                    {busy ? 'Signing in...' : admin ? 'Login' : 'Log In'}
                </button>
                {admin
                    ? <p className="hint">Access is restricted to authorized CGU staff and administrators.</p>
                    : <p className="link-row">Don't have an account? <Link to="/register">Student Registration</Link></p>}
            </form>
        </div>
    )
}