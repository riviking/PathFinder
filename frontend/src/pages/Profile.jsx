import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../AuthContext'
import { CATEGORIES } from '../categories'
import Layout from '../Layout'
import MyInternships from './MyInternships'

export default function Profile() {
    const { user } = useAuth()
    const [selected, setSelected] = useState([])
    const [error, setError] = useState('')

    useEffect(() => {
        api('/api/profile')
            .then((p) => setSelected(p.categoryIds))
            .catch((err) => setError(err.message))
    }, [])

    const names = CATEGORIES.filter((c) => selected.includes(c.id)).map((c) => c.name)

    return (
        <Layout title="Student Profile" subtitle="View your account and internship email preferences.">
            {error && <p className="msg error">{error}</p>}

            <section className="card">
                <h2>Account information</h2>
                <div className="kv">
                    <span className="label">University Email</span>
                    <span className="value">{user.email}</span>
                </div>
                <p className="hint">Your account is identified by the university email you registered with.</p>
            </section>

            <section className="card">
                <h2>Internship email preferences</h2>
                <div className="kv">
                    <span className="label">Selected job categories ({names.length})</span>
                    <span className="value">{names.length ? names.join('; ') : 'None selected'}</span>
                </div>
                <p className="hint">You will receive internship emails only for your selected job categories.</p>
                {names.length > 1 && (
                    <p className="hint">An internship in {names.join(' OR ')} qualifies. Notices in other categories will not be emailed to you.</p>
                )}
                {names.length === 0 && (
                    <p className="hint">No categories selected means no internship emails. Update your choices in Profile Settings.</p>
                )}
                <div className="btn-row">
                    <Link className="btn" to="/profile/settings" style={{ textDecoration: 'none' }}>Profile Settings</Link>
                </div>
            </section>

            <MyInternships />
        </Layout>
    )
}