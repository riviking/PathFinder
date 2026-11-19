import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import './pages.css'

// හැම login වූ පිටුවකම header + මාතෘකාව
export default function Layout({ title, subtitle, back, children }) {
    const { user, logout } = useAuth()
    const navigate = useNavigate()
    const isStudent = user.role === 'STUDENT'

    function signOut() {
        logout()
        navigate(isStudent ? '/login' : '/admin/login')
    }

    return (
        <>
            <header className="app-header">
                <div className="inner">
                    <div className="brand">PathFinder CGU Internship Platform</div>
                    <nav>
                        <NavLink to="/" end>
                            {isStudent ? 'Student internship notices' : 'Notices'}
                        </NavLink>
                        {isStudent && <NavLink to="/profile">My Profile</NavLink>}
                        {user.role === 'ADMIN' && <NavLink to="/admin/staff">Staff accounts</NavLink>}
                        <span className="who">{user.role === 'ADMIN' ? 'Admin' : user.role === 'STAFF' ? 'Staff' : user.email}</span>
                        <button className="btn secondary small" onClick={signOut}>Logout</button>
                    </nav>
                </div>
            </header>
            <main className="container">
                {back && <Link className="back-link" to={back.to}>{'< '}{back.label}</Link>}
                <div className="page-head">
                    <h1>{title}</h1>
                    {subtitle && <p>{subtitle}</p>}
                </div>
                {children}
            </main>
        </>
    )
}