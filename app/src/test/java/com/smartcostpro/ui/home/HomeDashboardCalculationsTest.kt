package com.smartcostpro.ui.home

import com.smartcostpro.data.local.entity.ExpenseEntity
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeDashboardCalculationsTest {

    @Test
    fun totalForMonthOnlyIncludesCanonicalExpensesInSelectedMonth() {
        val expenses = listOf(
            expense("1", "house", 100.0, "2026-10-02"),
            expense("2", "medical", 25.5, "2026-10-10"),
            expense("3", "work", 80.0, "2026-09-30"),
            expense("4", "association", 500.0, "2026-10-14")
        )

        assertEquals(
            125.5,
            HomeDashboardCalculations.totalForMonth(expenses, YearMonth.of(2026, 10)),
            0.001
        )
    }

    @Test
    fun percentageIsZeroWhenTotalIsZeroAndIsBounded() {
        assertEquals(0, HomeDashboardCalculations.percentage(10.0, 0.0))
        assertEquals(50, HomeDashboardCalculations.percentage(50.0, 100.0))
        assertEquals(100, HomeDashboardCalculations.percentage(150.0, 100.0))
    }

    @Test
    fun forMonthKeepsOnlyRowsWithSelectedMonthPrefix() {
        val rows = listOf(
            expense("1", "house", 10.0, "2026-10-02"),
            expense("2", "house", 20.0, "2026-11-02")
        )

        val selected = HomeDashboardCalculations.forMonth(rows, YearMonth.of(2026, 10))

        assertEquals(1, selected.size)
        assertTrue(selected.all { it.id == "1" })
    }

    private fun expense(
        id: String,
        type: String,
        amount: Double,
        date: String
    ) = ExpenseEntity(
        id = id,
        title = "اختبار",
        amount = amount,
        type = type,
        category = type,
        date = date,
        paymentMethod = null,
        receiptUrl = null,
        notes = null,
        createdAt = date + "T00:00:00Z",
        updatedAt = date + "T00:00:00Z"
    )
}
