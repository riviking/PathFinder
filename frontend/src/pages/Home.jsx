import { useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

export default function Home() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    return (
        <div>
            <h1>Hello, {user.name}</h1>
            <p>{user.email} ({user.role})</p>
            <button onClick={() => { logout(); navigate('/login') }}>Log out</button>
        </div>
    )
}