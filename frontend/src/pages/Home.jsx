import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

export default function Home() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    return (
        <div>
            <h1>Hello, {user.name}</h1>
            <p>{user.email} ({user.role})</p>
            {user.role === 'STUDENT' && (
                <p>
                    <Link to="/profile">My interests</Link>{' · '}
                    <Link to="/saved">My Internships</Link>
                </p>
            )}
            {user.role === 'ADMIN' && (
                <p><Link to="/admin/staff">Staff accounts</Link></p>
            )}
            {user.role !== 'STUDENT' && (
                <p><em>Notice pages will appear here when the notice screens are ready.</em></p>
            )}
            <button onClick={() => { logout(); navigate('/login') }}>Log out</button>
        </div>
    )
}