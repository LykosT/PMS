import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { deleteEmployee, listEmployees } from '../api/employees'
import { extractApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import type { Employee } from '../types'

const statusStyles: Record<string, string> = {
  ACTIVE: 'bg-green-100 text-green-700',
  ON_LEAVE: 'bg-amber-100 text-amber-700',
  SICK_LEAVE: 'bg-amber-100 text-amber-700',
  RESIGNED: 'bg-slate-100 text-slate-600',
  TERMINATED: 'bg-red-100 text-red-700',
  RETIRED: 'bg-slate-100 text-slate-600',
}

export default function EmployeesPage() {
  const { hasRole } = useAuth()
  const [employees, setEmployees] = useState<Employee[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = () => {
    setLoading(true)
    listEmployees()
      .then(setEmployees)
      .catch((err) => setError(extractApiError(err).message))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const handleDelete = async (employee: Employee) => {
    if (!window.confirm(`Delete ${employee.firstName} ${employee.lastName}?`)) return
    try {
      await deleteEmployee(employee.id)
      setEmployees((current) => current.filter((e) => e.id !== employee.id))
    } catch (err) {
      setError(extractApiError(err).message)
    }
  }

  const canEdit = hasRole('ADMIN', 'MANAGER')
  const canDelete = hasRole('ADMIN')

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Employees</h1>
        {canEdit && (
          <Link
            to="/employees/new"
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            New Employee
          </Link>
        )}
      </div>

      {error && (
        <div className="mb-4 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
      )}

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Phone</th>
              <th className="px-4 py-3">Seniority</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Username</th>
              <th className="px-4 py-3">Role</th>
              {(canEdit || canDelete) && <th className="px-4 py-3 text-right">Actions</th>}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={8} className="px-4 py-8 text-center text-slate-400">
                  Loading…
                </td>
              </tr>
            ) : employees.length === 0 ? (
              <tr>
                <td colSpan={8} className="px-4 py-8 text-center text-slate-400">
                  No employees found.
                </td>
              </tr>
            ) : (
              employees.map((employee) => (
                <tr key={employee.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3 font-medium text-slate-900">
                    {employee.firstName} {employee.lastName}
                  </td>
                  <td className="px-4 py-3 text-slate-600">{employee.email}</td>
                  <td className="px-4 py-3 text-slate-600">{employee.phone ?? '—'}</td>
                  <td className="px-4 py-3 text-slate-600">{employee.seniorityLevel}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                        statusStyles[employee.employeeStatus] ?? 'bg-slate-100 text-slate-600'
                      }`}
                    >
                      {employee.employeeStatus}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-slate-600">{employee.username ?? '—'}</td>
                  <td className="px-4 py-3 text-slate-600">{employee.systemRole ?? '—'}</td>
                  {(canEdit || canDelete) && (
                    <td className="px-4 py-3 text-right">
                      <div className="flex justify-end gap-2">
                        {canEdit && (
                          <Link
                            to={`/employees/${employee.id}/edit`}
                            className="rounded-md border border-slate-300 px-2.5 py-1 text-xs text-slate-700 hover:bg-slate-100"
                          >
                            Edit
                          </Link>
                        )}
                        {canDelete && (
                          <button
                            onClick={() => handleDelete(employee)}
                            className="rounded-md border border-red-200 px-2.5 py-1 text-xs text-red-600 hover:bg-red-50"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
