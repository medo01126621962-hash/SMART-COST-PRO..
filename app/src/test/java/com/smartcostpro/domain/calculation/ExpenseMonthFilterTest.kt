package com.smartcostpro.domain.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseMonthFilterTest {

    @Test
    fun isDateInMonth_day1_belongsToMonth() {
        assertTrue(ExpenseMonthFilter.isDateInMonth("2026-10-01", "2026-10"))
    }

    @Test
    fun isDateInMonth_day31_belongsToMonth() {
        assertTrue(ExpenseMonthFilter.isDateInMonth("2026-10-31", "2026-10"))
    }

    @Test
    fun isDateInMonth_midMonth_belongs() {
        assertTrue(ExpenseMonthFilter.isDateInMonth("2026-10-15", "2026-10"))
    }

    @Test
    fun isDateInMonth_wrongMonth_false() {
        assertFalse(ExpenseMonthFilter.isDateInMonth("2026-10-15", "2026-09"))
    }

    @Test
    fun isDateInMonth_emptyDate_false() {
        assertFalse(ExpenseMonthFilter.isDateInMonth("", "2026-10"))
        assertFalse(ExpenseMonthFilter.isDateInMonth(null, "2026-10"))
        assertFalse(ExpenseMonthFilter.isDateInMonth("   ", "2026-10"))
    }

    @Test
    fun isDateInMonth_shortDate_false() {
        assertFalse(ExpenseMonthFilter.isDateInMonth("2026-1", "2026-10"))
    }

    @Test
    fun currentMonthYyyyMm_january_zeroPadded() {
        assertEquals("2026-01", ExpenseMonthFilter.currentMonthYyyyMm(2026, 1))
    }

    @Test
    fun currentMonthYyyyMm_december() {
        assertEquals("2026-12", ExpenseMonthFilter.currentMonthYyyyMm(2026, 12))
    }

    @Test
    fun currentMonthYyyyMm_october() {
        assertEquals("2026-10", ExpenseMonthFilter.currentMonthYyyyMm(2026, 10))
    }
}
