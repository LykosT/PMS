import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { createResource, getResource, listResource, updateResource } from '../api/resources'
import type { Row } from '../api/resources'
import { extractApiError } from '../api/client'
import type { FieldDef, ResourceConfig } from '../resources/config'

const inputClass =
  'w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none'
const labelClass = 'mb-1 block text-sm font-medium text-slate-700'

function toPayload(fields: FieldDef[], form: Record<string, string>): Record<string, unknown> {
  const payload: Record<string, unknown> = {}
  for (const field of fields) {
    const raw = (form[field.name] ?? '').trim()
    if (raw === '') {
      // Required empty values are sent as-is so the backend returns a field error;
      // optional ones are omitted entirely.
      if (field.required) payload[field.name] = field.optionsResource ? null : ''
      continue
    }
    payload[field.name] = field.optionsResource ? Number(raw) : raw
  }
  return payload
}

export default function ResourceFormPage({ config }: { config: ResourceConfig }) {
  const { id } = useParams()
  const isEdit = id !== undefined
  const navigate = useNavigate()

  const emptyForm = useMemo(
    () => Object.fromEntries(config.fields.map((field) => [field.name, ''])),
    [config],
  )

  const [form, setForm] = useState<Record<string, string>>(emptyForm)
  const [optionRows, setOptionRows] = useState<Record<string, Row[]>>({})
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(isEdit)
  const [submitting, setSubmitting] = useState(false)

  // Load dropdown choices for every relation field
  useEffect(() => {
    const resourceFields = config.fields.filter((field) => field.optionsResource)
    resourceFields.forEach((field) => {
      listResource(field.optionsResource!)
        .then((rows) => setOptionRows((current) => ({ ...current, [field.name]: rows })))
        .catch((err) => setError(extractApiError(err).message))
    })
  }, [config])

  useEffect(() => {
    if (!isEdit) {
      setForm(emptyForm)
      return
    }
    getResource(config.resource, Number(id))
      .then((item) =>
        setForm(
          Object.fromEntries(
            config.fields.map((field) => {
              const value = item[field.name]
              return [field.name, value === null || value === undefined ? '' : String(value)]
            }),
          ),
        ),
      )
      .catch((err) => setError(extractApiError(err).message))
      .finally(() => setLoading(false))
  }, [config, id, isEdit, emptyForm])

  const set = (name: string, value: string) =>
    setForm((current) => ({ ...current, [name]: value }))

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setFieldErrors({})
    setSubmitting(true)
    try {
      const payload = toPayload(config.fields, form)
      if (isEdit) {
        await updateResource(config.resource, Number(id), payload)
      } else {
        await createResource(config.resource, payload)
      }
      navigate(`/${config.route}`)
    } catch (err) {
      const apiError = extractApiError(err)
      setError(apiError.message)
      setFieldErrors(apiError.fieldErrors ?? {})
    } finally {
      setSubmitting(false)
    }
  }

  const renderField = (field: FieldDef) => {
    const value = form[field.name] ?? ''
    const common = {
      id: field.name,
      value,
      required: field.required,
      className: inputClass,
    }

    if (field.type === 'select') {
      const choices = field.options
        ? field.options.map((option) => ({ value: option, label: option }))
        : (optionRows[field.name] ?? []).map((row) => ({
            value: String(row.id),
            label: field.optionLabel ? field.optionLabel(row) : String(row.name),
          }))
      return (
        <select {...common} onChange={(e) => set(field.name, e.target.value)}>
          <option value="">{field.required ? 'Select…' : '—'}</option>
          {choices.map((choice) => (
            <option key={choice.value} value={choice.value}>
              {choice.label}
            </option>
          ))}
        </select>
      )
    }
    if (field.type === 'textarea') {
      return <textarea {...common} rows={3} onChange={(e) => set(field.name, e.target.value)} />
    }
    return (
      <input
        {...common}
        type={field.type === 'date' ? 'date' : 'text'}
        onChange={(e) => set(field.name, e.target.value)}
      />
    )
  }

  if (loading) {
    return <div className="py-8 text-center text-slate-400">Loading…</div>
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-bold text-slate-900">
        {isEdit ? `Edit ${config.singular}` : `New ${config.singular}`}
      </h1>

      {error && (
        <div className="mb-4 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            {config.fields.map((field) => (
              <div
                key={field.name}
                className={field.type === 'textarea' ? 'sm:col-span-2' : undefined}
              >
                <label htmlFor={field.name} className={labelClass}>
                  {field.label}
                </label>
                {renderField(field)}
                {fieldErrors[field.name] && (
                  <p className="mt-1 text-xs text-red-600">{fieldErrors[field.name]}</p>
                )}
              </div>
            ))}
          </div>
        </section>

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={submitting}
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
          >
            {submitting ? 'Saving…' : isEdit ? 'Save changes' : `Create ${config.singular.toLowerCase()}`}
          </button>
          <button
            type="button"
            onClick={() => navigate(`/${config.route}`)}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}
