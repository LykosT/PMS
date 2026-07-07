import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchDashboardStats } from '../api/employees'
import { useAuth } from '../auth/AuthContext'
import type { DashboardStats } from '../types'

const CARDS: { key: keyof DashboardStats; label: string; to: string }[] = [
  { key: 'employeeCount', label: 'Employees', to: '/employees' },
  { key: 'departmentCount', label: 'Departments', to: '/departments' },
  { key: 'projectCount', label: 'Projects', to: '/projects' },
  { key: 'teamCount', label: 'Teams', to: '/teams' },
  { key: 'taskCount', label: 'Tasks', to: '/tasks' },
  { key: 'businessRoleCount', label: 'Business Roles', to: '/roles' },
]

export default function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState<DashboardStats | null>(null)

  useEffect(() => {
    fetchDashboardStats().then(setStats).catch(console.error)
  }, [])

  return (
    <div>
      <h1 className="mb-1 text-2xl font-bold text-slate-900">Dashboard</h1>
      <p className="mb-8 text-sm text-slate-500">Welcome back, {user?.fullName}.</p>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {CARDS.map((card) => (
          <Link
            key={card.key}
            to={card.to}
            className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm transition-shadow hover:shadow-md"
          >
            <p className="text-sm font-medium text-slate-500">{card.label}</p>
            <p className="mt-2 text-3xl font-bold text-slate-900">{stats?.[card.key] ?? '—'}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
