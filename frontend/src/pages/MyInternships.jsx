import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'

// ISO (UTC) -> <input type="datetime-local"> අගය (පරිගණකයේ වේලාවෙන්)
function toLocalInput(iso) {
    if (!iso) return ''
    const d = new Date(iso)
    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function show(iso) {
    return iso ? new Date(iso).toLocaleString() : '-'
}

export default function MyInternships() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')
    const [edits, setEdits] = useState({}) // noticeId -> datetime-local පෙළ

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
            setMessage(value ? 'Reminder saved.' : 'Reminder removed.')
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
            setMessage('Removed.')
            await load()
        } catch (err) {
            setError(err.message)
        }
    }

    if (loading) return <p>Loading...</p>

    return (
        <div style={{ display: 'grid', gap: 16, width: 520 }}>
            <h1>My Internships</h1>
            {error && <p style={{ color: 'tomato' }}>{error}</p>}
            {message && <p style={{ color: 'seagreen' }}>{message}</p>}
            {items.length === 0 && <p>You have not saved any internships yet.</p>}
            {items.map((i) => (
                <div key={i.noticeId} style={{ border: '1px solid #8884', borderRadius: 8, padding: 12, display: 'grid', gap: 8 }}>
                    <strong>{i.title}</strong>
                    <span>{i.company || '-'} · Deadline: {show(i.deadline)}</span>
                    <label style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
                        Reminder:
                        <input
                            type="datetime-local"
                            value={edits[i.noticeId] ?? ''}
                            onChange={(e) => setEdits((cur) => ({ ...cur, [i.noticeId]: e.target.value }))}
                        />
                        {i.reminderSent && <em>Reminder sent</em>}
                    </label>
                    <div style={{ display: 'flex', gap: 8 }}>
                        <button onClick={() => saveReminder(i)}>Save reminder</button>
                        <button onClick={() => remove(i)}>Remove</button>
                    </div>
                </div>
            ))}
            <p><Link to="/">Back</Link></p>
        </div>
    )
}