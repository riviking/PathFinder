import { useAuth } from '../AuthContext'
import Layout from '../Layout'

export default function Home() {
    const { user } = useAuth()
    const isStudent = user.role === 'STUDENT'

    return (
        <Layout
            title={isStudent ? 'Internship Opportunities' : 'Notice Management'}
            subtitle={isStudent
                ? 'Browse published internships with upcoming application deadlines.'
                : 'Manage draft, published and closed internship notices.'}
        >
            <div className="card">
                <p>Signed in as <strong>{user.email}</strong></p>
                <p className="hint">The notice screens will appear here when they are ready.</p>
            </div>
        </Layout>
    )
}