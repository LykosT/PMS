import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { createEmployee, getEmployee, updateEmployee } from '../api/employees'
import { extractApiError } from '../api/client'
import {
  EMPLOYEE_STATUSES,
  SENIORITY_LEVELS,
  SYSTEM_ROLES,
} from '../types'
import type { EmployeeStatus, SeniorityLevel, SystemRole } from '../types'

interface FormState {
  firstName: string
  lastName: string
  email: string
  phone: string
  seniorityLevel: SeniorityLevel
  employeeStatus: EmployeeStatus
  username: string
  password: string
  systemRole: SystemRole
  active: boolean
}

const emptyForm: FormState = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  seniorityLevel: 'JUNIOR',
  employeeStatus: 'ACTIVE',
  username: '',
  password: '',
  systemRole: 'USER',
  active: true,
}

const inputClass =
  'w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none'
const labelClass = 'mb-1 block text-sm font-medium text-slate-700'

export default function EmployeeFormPage() {
  const { id } = useParams()
  const isEdit = id !== undefined
  const navigate = useNavigate()

  const [form, setForm] = useState<FormState>(emptyForm)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(isEdit)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!isEdit) return
    getEmployee(Number(id))
      .then((employee) =>
        setForm({
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          phone: employee.phone ?? '',
          seniorityLevel: employee.seniorityLevel,
          employeeStatus: employee.employeeStatus,
          username: employee.username ?? '',
          password: '',
          systemRole: employee.systemRole ?? 'USER',
          active: employee.userActive ?? true,
        }),
      )
      .catch((err) => setError(extractApiError(err).message))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const set = <K extends keyof FormState>(key: K, value: FormState[K]) =>
    setForm((current) => ({ ...current, [key]: value }))

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setFieldErrors({})
    setSubmitting(true)
    try {
      const payload = {
        ...form,
        phone: form.phone.trim() === '' ? undefined : form.phone.trim(),
      }
      if (isEdit) {
        await updateEmployee(Number(id), {
          ...payload,
          password: form.password.trim() === '' ? undefined : form.password,
        })
      } else {
        await createEmployee(payload)
      }
      navigate('/employees')
    } catch (err) {
      const apiError = extractApiError(err)
      setError(apiError.message)
      setFieldErrors(apiError.fieldErrors ?? {})
    } finally {
      setSubmitting(false)
    }
  }

  const fieldError = (name: string) =>
    fieldErrors[name] ? <p className="mt-1 text-xs text-red-600">{fieldErrors[name]}</p> : null

  if (loading) {
    return <div className="py-8 text-center text-slate-400">Loading…</div>
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-bold text-slate-900">
        {isEdit ? 'Edit Employee' : 'New Employee'}
      </h1>

      {error && (
        <div className="mb-4 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
            Employee Information
          </h2>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label htmlFor="firstName" className={labelClass}>First name</label>
              <input
                id="firstName"
                value={form.firstName}
                onChange={(e) => set('firstName', e.target.value)}
                required
                className={inputClass}
              />
              {fieldError('firstName')}
            </div>
            <div>
              <label htmlFor="lastName" className={labelClass}>Last name</label>
              <input
                id="lastName"
                value={form.lastName}
                onChange={(e) => set('lastName', e.target.value)}
                required
                className={inputClass}
              />
              {fieldError('lastName')}
            </div>
            <div>
              <label htmlFor="email" className={labelClass}>Email</label>
              <input
                id="email"
                type="email"
                value={form.email}
                onChange={(e) => set('email', e.target.value)}
                required
                className={inputClass}
              />
              {fieldError('email')}
            </div>
            <div>
              <label htmlFor="phone" className={labelClass}>Phone (10 digits, optional)</label>
              <input
                id="phone"
                value={form.phone}
                onChange={(e) => set('phone', e.target.value)}
                pattern="\d{10}"
                className={inputClass}
              />
              {fieldError('phone')}
            </div>
            <div>
              <label htmlFor="seniorityLevel" className={labelClass}>Seniority level</label>
              <select
                id="seniorityLevel"
                value={form.seniorityLevel}
                onChange={(e) => set('seniorityLevel', e.target.value as SeniorityLevel)}
                className={inputClass}
              >
                {SENIORITY_LEVELS.map((level) => (
                  <option key={level} value={level}>{level}</option>
                ))}
              </select>
              {fieldError('seniorityLevel')}
            </div>
            <div>
              <label htmlFor="employeeStatus" className={labelClass}>Employee status</label>
              <select
                id="employeeStatus"
                value={form.employeeStatus}
                onChange={(e) => set('employeeStatus', e.target.value as EmployeeStatus)}
                className={inputClass}
              >
                {EMPLOYEE_STATUSES.map((status) => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
              {fieldError('employeeStatus')}
            </div>
          </div>
        </section>

        <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
            User Account
          </h2>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label htmlFor="username" className={labelClass}>Username</label>
              <input
                id="username"
                value={form.username}
                onChange={(e) => set('username', e.target.value)}
                required
                className={inputClass}
              />
              {fieldError('username')}
            </div>
            <div>
              <label htmlFor="password" className={labelClass}>
                Password {isEdit && <span className="text-slate-400">(leave blank to keep)</span>}
              </label>
              <input
                id="password"
                type="password"
                value={form.password}
                onChange={(e) => set('password', e.target.value)}
                required={!isEdit}
                minLength={6}
                className={inputClass}
              />
              {fieldError('password')}
            </div>
            <div>
              <label htmlFor="systemRole" className={labelClass}>System role</label>
              <select
                id="systemRole"
                value={form.systemRole}
                onChange={(e) => set('systemRole', e.target.value as SystemRole)}
                className={inputClass}
              >
                {SYSTEM_ROLES.map((role) => (
                  <option key={role} value={role}>{role}</option>
                ))}
              </select>
              {fieldError('systemRole')}
            </div>
            <div className="flex items-end pb-2">
              <label className="flex items-center gap-2 text-sm text-slate-700">
                <input
                  type="checkbox"
                  checked={form.active}
                  onChange={(e) => set('active', e.target.checked)}
                  className="h-4 w-4 rounded border-slate-300"
                />
                Account active
              </label>
            </div>
          </div>
        </section>

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={submitting}
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
          >
            {submitting ? 'Saving…' : isEdit ? 'Save changes' : 'Create employee'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/employees')}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}
