import { describe, expect, it } from 'vitest'
import DateUtils, { formatDate, formatDateTime, formatYearMonth } from './dateUtils'

describe('DateUtils', () => {
  const date = new Date(2026, 4, 6, 7, 8, 9)

  it('formats dates with the requested pattern', () => {
    expect(DateUtils.format(date, 'YYYY/MM/DD HH:mm:ss')).toBe('2026/05/06 07:08:09')
  })

  it('returns an empty string for invalid dates', () => {
    expect(DateUtils.format('not-a-date')).toBe('')
  })

  it('keeps helper exports consistent with the class methods', () => {
    expect(formatDate(date)).toBe('2026-05-06')
    expect(formatDateTime(date)).toBe('2026-05-06 07:08:09')
    expect(formatYearMonth(date)).toBe('2026-05')
  })
})
