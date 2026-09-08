import type { NamedRef } from '../types'

interface Props {
  label: string
  options: NamedRef[]
  selected: number[]
  onChange: (ids: number[]) => void
  disabled?: boolean
}

const labelClass = 'mb-1 block text-sm font-medium text-slate-700'

export default function MultiSelect({ label, options, selected, onChange, disabled }: Props) {
  const toggle = (id: number) => {
    onChange(selected.includes(id) ? selected.filter((s) => s !== id) : [...selected, id])
  }

  return (
    <fieldset disabled={disabled} className="disabled:opacity-50">
      <legend className={labelClass}>{label}</legend>
      <div className="max-h-48 overflow-y-auto rounded-md border border-slate-300">
        {options.length === 0 ? (
          <p className="px-3 py-2 text-sm text-slate-400">None available</p>
        ) : (
          options.map((option) => (
            <label
              key={option.id}
              className="flex items-center gap-2 border-b border-slate-100 px-3 py-2 text-sm text-slate-700 last:border-b-0 hover:bg-slate-50"
            >
              <input
                type="checkbox"
                checked={selected.includes(option.id)}
                onChange={() => toggle(option.id)}
                className="h-4 w-4 rounded border-slate-300"
              />
              {option.name}
            </label>
          ))
        )}
      </div>
    </fieldset>
  )
}
