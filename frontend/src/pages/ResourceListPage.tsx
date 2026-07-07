import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { deleteResource, listResource } from '../api/resources'
import type { Row } from '../api/resources'
import { extractApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { BADGE_STYLES } from '../resources/config'
import type { ColumnDef, ResourceConfig } from '../resources/config'

function cellValue(row: Row, column: ColumnDef) {
  const value = row[column.key]
  if (value === null || value === undefined || value === '') return '—'
  if (column.kind === 'badge') {
    const text = String(value)
    return (
      <span
        className={`rounded-full px-2 py-0.5 text-xs font-medium ${
          BADGE_STYLES[text] ?? 'bg-slate-100 text-slate-600'
        }`}
      >
        {text}
      </span>
    )
  }
  return String(value)
}

export default function ResourceListPage({ config }: { config: ResourceConfig }) {
  const { hasRole } = useAuth()
  const [rows, setRows] = useState<Row[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    setError(null)
    listResource(config.resource)
      .then(setRows)
      .catch((err) => setError(extractApiError(err).message))
      .finally(() => setLoading(false))
  }, [config.resource])

  const handleDelete = async (row: Row) => {
    if (!window.confirm(`Delete ${config.singular.toLowerCase()} "${row.name ?? row.id}"?`)) return
    try {
      await deleteResource(config.resource, row.id)
      setRows((current) => current.filter((r) => r.id !== row.id))
    } catch (err) {
      setError(extractApiError(err).message)
    }
  }

  const canEdit = hasRole('ADMIN', 'MANAGER')
  const canDelete = hasRole('ADMIN')
  const columnCount = config.columns.length + (canEdit || canDelete ? 1 : 0)

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">{config.title}</h1>
        {canEdit && (
          <Link
            to={`/${config.route}/new`}
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            New {config.singular}
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
              {config.columns.map((column) => (
                <th key={column.key} className="px-4 py-3">
                  {column.label}
                </th>
              ))}
              {(canEdit || canDelete) && <th className="px-4 py-3 text-right">Actions</th>}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={columnCount} className="px-4 py-8 text-center text-slate-400">
                  Loading…
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={columnCount} className="px-4 py-8 text-center text-slate-400">
                  No {config.title.toLowerCase()} found.
                </td>
              </tr>
            ) : (
              rows.map((row) => (
                <tr key={row.id} className="hover:bg-slate-50">
                  {config.columns.map((column, index) => (
                    <td
                      key={column.key}
                      className={`px-4 py-3 ${index === 0 ? 'font-medium text-slate-900' : 'text-slate-600'}`}
                    >
                      {cellValue(row, column)}
                    </td>
                  ))}
                  {(canEdit || canDelete) && (
                    <td className="px-4 py-3 text-right">
                      <div className="flex justify-end gap-2">
                        {canEdit && (
                          <Link
                            to={`/${config.route}/${row.id}/edit`}
                            className="rounded-md border border-slate-300 px-2.5 py-1 text-xs text-slate-700 hover:bg-slate-100"
                          >
                            Edit
                          </Link>
                        )}
                        {canDelete && (
                          <button
                            onClick={() => handleDelete(row)}
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
