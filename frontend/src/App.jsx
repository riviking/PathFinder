import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './AuthContext'
import Login from './pages/Login'
import Home from './pages/Home'
import Register from './pages/Register'

// login වී නැත්නම් /login වෙත යවනවා
function RequireLogin({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <p>Loading...</p>
  return user ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
            <Route path="/" element={<RequireLogin><Home /></RequireLogin>} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  )
}