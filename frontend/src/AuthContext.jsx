import { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react'
import { api, clearToken, getToken, setToken } from './api'

// විනාඩි 30 (පරීක්ෂා කිරීමට .env.local හි VITE_IDLE_MINUTES වෙනස් කරන්න)
const IDLE_MINUTES = Number(import.meta.env.VITE_IDLE_MINUTES ?? 30)
const ACTIVITY_EVENTS = ['mousemove', 'mousedown', 'keydown', 'scroll', 'touchstart']

const AuthContext = createContext(null)
export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(!!getToken())
    const [notice, setNotice] = useState('')
    const lastActivity = useRef(Date.now())

    const logout = useCallback((reason = '') => {
        clearToken()
        setUser(null)
        setNotice(typeof reason === 'string' ? reason : '')
    }, [])

    // පිටුව refresh කළාම: token තියෙනවා නම් "මම කවුද" නැවත ඉල්ලනවා
    useEffect(() => {
        if (!getToken()) return
        api('/api/users/me')
            .then(setUser)
            .catch(() => clearToken())
            .finally(() => setLoading(false))
    }, [])

    // ඕනෑම API ඇමතුමකින් 401 ආවොත් logout
    useEffect(() => {
        const onUnauthorized = () => logout('Your session has expired. Please sign in again.')
        window.addEventListener('pathfinder:unauthorized', onUnauthorized)
        return () => window.removeEventListener('pathfinder:unauthorized', onUnauthorized)
    }, [logout])

    // idle logout: login වී සිටිය දී පමණක්
    useEffect(() => {
        if (!user) return
        lastActivity.current = Date.now()
        const mark = () => { lastActivity.current = Date.now() }
        ACTIVITY_EVENTS.forEach((e) => window.addEventListener(e, mark, { passive: true }))
        const timer = setInterval(() => {
            if (Date.now() - lastActivity.current > IDLE_MINUTES * 60 * 1000) {
                logout(`You were signed out after ${IDLE_MINUTES} minutes of inactivity.`)
            }
        }, 5000)
        return () => {
            ACTIVITY_EVENTS.forEach((e) => window.removeEventListener(e, mark))
            clearInterval(timer)
        }
    }, [user, logout])

    async function login(email, password) {
        const res = await api('/api/auth/login', { method: 'POST', body: { email, password } })
        setToken(res.token)
        setNotice('')
        setUser({ id: res.userId, email: res.email, name: res.name, role: res.role })
        return res.role
    }

    return (
        <AuthContext.Provider value={{ user, loading, notice, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}