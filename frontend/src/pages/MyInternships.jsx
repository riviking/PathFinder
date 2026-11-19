import { useEffect, useState } from 'react'
import { api } from '../api'

function toLocalInput(iso) {
    if (!iso) return ''
    const d = new Date(iso)
    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function day(iso) {
    return iso ? new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }) : '-'
}

export default function MyInternships() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')
    const [edits, setEdits] = useState({})

    async function load() {
        try {
            const list = await api('/api/profile/saved')
            setItems(list)
            setEdits(Object.fromEntries(list.map((i) => [i.noticeId, toLocalInput(i.reminderAt)])))
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
    }, [])

    async function saveReminder(item) {
        setError('')
        setMessage('')
        const value = edits[item.noticeId]
        try {
            await api(`/api/saved/${item.noticeId}`, {
                method: 'PUT',
                body: {
                    title: item.title,
                    company: item.company,
                    deadline: item.deadline,
                    reminderAt: value ? new Date(value).toISOString() : null,
                },
            })
            setMessage(value ? 'Reminder saved.' : 'Reminder cleared.')
            await load()
        } catch (err) {
            setError(err.message)
        }
    }

    async function remove(item) {
        if (!window.confirm(`Remove "${item.title}" from My Internships?`)) return
        setError('')
        setMessage('')
        try {
            await api(`/api/saved/${item.noticeId}`, { method: 'DELETE' })
            setMessage('Removed from My Internships.')
            await load()
        } catch (err) {
            setError(err.message)
        }
    }

    return (
        <section className="card">
            <h2>My Internships</h2>
            <p className="hint">Internships you added from the notice page. Nearest deadline first. Only you can see this list.</p>
            {error && <p className="msg error">{error}</p>}
            {message && <p className="msg ok">{message}</p>}
            {loading && <p>Loading...</p>}
            {!loading && items.length === 0 && (
                <p className="hint">You have not added any internships yet. Open a notice and choose Add to My Internships.</p>
            )}
            {items.length > 0 && (
                <div className="table-wrap">
                    <table className="table">
                        <thead>
                        <tr><th>Internship</th><th>Apply by</th><th>Reminder</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                        {items.map((i) => (
                            <tr key={i.noticeId}>
                                <td>{i.title}<span className="sub">{i.company || '-'}</span></td>
                                <td>{day(i.deadline)}</td>
                                <td>
                                    <input type="datetime-local" value={edits[i.noticeId] ?? ''}
                                           onChange={(e) => setEdits((cur) => ({ ...cur, [i.noticeId]: e.target.value }))} />
                                    {i.reminderSent && <span className="sub">Reminder sent</span>}
                                </td>
                                <td>
                                    <div className="btn-row">
                                        <button className="btn small" onClick={() => saveReminder(i)}>Save reminder</button>
                                        <button className="btn danger small" onClick={() => remove(i)}>Remove</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    )
}