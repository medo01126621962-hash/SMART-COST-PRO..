package com.smartcostpro.domain.calculation

/**
 * Month filter helpers — source: SMART-COST-Android-Logic-Spec.md §4.1–4.2
 *
 * isDateInMonth(date, month) = date.slice(0, 7) == month
 * Empty date → false.
 */
object ExpenseMonthFilter {

    fun isDateInMonth(date: String?, month: String): Boolean {
        if (date.isNullOrBlank()) return false
        if (month.length < 7) return false
        return date.length >= 7 && date.substring(0, 7) == month
    }

    fun currentMonthYyyyMm(
        year: Int,
        month: Int
    ): String {
        require(month in 1..12) { "month must be 1..12" }
        return "%04d-%02d".format(year, month)
    }
}
