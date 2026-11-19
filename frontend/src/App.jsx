import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import Profile from './pages/Profile'
import MyInternships from './pages/MyInternships'
import StaffAccounts from './pages/StaffAccounts'

// login වී නැත්නම් /login වෙත; role නොගැළපෙනවා නම් Home වෙත
function RequireLogin({ children, roles }) {
    const { user, loading } = useAuth()
    if (loading) return <p>Loading...</p>
    if (!user) return <Navigate to="/login" replace />
    if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />
    return children
}

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/" element={<RequireLogin><Home /></RequireLogin>} />
                    <Route path="/profile" element={<RequireLogin roles={['STUDENT']}><Profile /></RequireLogin>} />
                    <Route path="/saved" element={<RequireLogin roles={['STUDENT']}><MyInternships /></RequireLogin>} />
                    <Route path="/admin/staff" element={<RequireLogin roles={['ADMIN']}><StaffAccounts /></RequireLogin>} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}