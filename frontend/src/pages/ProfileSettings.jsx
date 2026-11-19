import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../AuthContext'
import { CATEGORIES } from '../categories'
import Layout from '../Layout'

export default function ProfileSettings() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const [selected, setSelected] = useState([])
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')

    useEffect(() => {
        api('/api/profile')
            .then((p) => setSelected(p.categoryIds))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    function toggle(id) {
        setSelected((cur) => (cur.includes(id) ? cur.filter((x) => x !== id) : [...cur, id]))
    }

    async function save() {
        setSaving(true)
        setError('')
        try {
            await api('/api/profile/categories', { method: 'PUT', body: { categoryIds: selected } })
            navigate('/profile')
        } catch (err) {
            setError(err.message)
            setSaving(false)
        }
    }

    const names = CATEGORIES.filter((c) => selected.includes(c.id)).map((c) => c.name)

    return (
        <Layout title="Profile Settings"
                subtitle="Choose which internship notices are emailed to your university email."
                back={{ to: '/profile', label: 'Back to Student Profile' }}>
            {error && <p className="msg error">{error}</p>}
            {loading ? <p>Loading...</p> : (
                <>
                    <section className="card">
                        <h2>Account information</h2>
                        <div className="kv">
                            <span className="label">University Email</span>
                            <span className="value">{user.email}</span>
                        </div>
                        <p className="hint">Read only. This is the university email used to register your account.</p>
                    </section>

                    <section className="card">
                        <h2>Job categories</h2>
                        <p className="hint">Select all categories you want to receive emails about.</p>
                        <div className="checks">
                            {CATEGORIES.map((c) => (
                                <label key={c.id}>
                                    <input type="checkbox" checked={selected.includes(c.id)} onChange={() => toggle(c.id)} />
                                    {c.name}
                                </label>
                            ))}
                        </div>
                        <p className="hint">If you select no categories, you will not receive any internship emails.</p>
                    </section>

                    <section className="card">
                        <h2>Selection summary</h2>
                        <div className="kv">
                            <span className="label">Selected job categories ({names.length})</span>
                            <span className="value">{names.length ? names.join('; ') : 'None selected'}</span>
                        </div>
                        {names.length > 1 && (
                            <p className="hint">An internship in {names.join(' OR ')} qualifies. A match to any one selected category is enough.</p>
                        )}
                        <p className="hint">Use Save Changes to apply these preferences.</p>
                        <div className="btn-row">
                            <button className="btn" onClick={save} disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</button>
                            <button className="btn secondary" onClick={() => navigate('/profile')}>Cancel</button>
                        </div>
                        <p className="hint">Cancel returns to your profile without saving.</p>
                    </section>
                </>
            )}
        </Layout>
    )
}