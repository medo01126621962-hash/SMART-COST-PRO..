package com.smartcostpro.domain.calculation

import com.smartcostpro.domain.model.ExpenseRecord
import com.smartcostpro.domain.model.ExpenseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseCalculationsTest {

    private fun expense(
        type: ExpenseType,
        amount: Double,
        date: String = "2026-10-15"
    ) = ExpenseRecord(
        id = "t-${type.key}",
        type = type,
        amount = amount,
        date = date,
        title = type.key
    )

    @Test
    fun sumByType_onlyMatchingType() {
        val list = listOf(
            expense(ExpenseType.HOUSE, 100.0),
            expense(ExpenseType.MEDICAL, 50.0),
            expense(ExpenseType.HOUSE, 25.0)
        )
        assertEquals(125.0, ExpenseCalculations.sumByType(list, ExpenseType.HOUSE), 0.001)
        assertEquals(50.0, ExpenseCalculations.sumByType(list, ExpenseType.MEDICAL), 0.001)
        assertEquals(0.0, ExpenseCalculations.sumByType(list, ExpenseType.WORK), 0.001)
    }

    @Test
    fun calculateMonthTotals_allEightTypes() {
        val list = listOf(
            expense(ExpenseType.HOUSE, 10.0),
            expense(ExpenseType.MEDICAL, 20.0),
            expense(ExpenseType.WORK, 30.0),
            expense(ExpenseType.PERSONAL, 40.0),
            expense(ExpenseType.VEHICLE_FUEL, 50.0),
            expense(ExpenseType.VEHICLE_MAINT, 60.0),
            expense(ExpenseType.VEHICLE_OIL, 70.0),
            expense(ExpenseType.STUDENT, 80.0),
            expense(ExpenseType.HOUSE, 999.0, date = "2026-09-01")
        )
        val totals = ExpenseCalculations.calculateMonthTotals(list, "2026-10")
        assertEquals(10.0, totals.houseTotal, 0.001)
        assertEquals(20.0, totals.medicalTotal, 0.001)
        assertEquals(30.0, totals.workTotal, 0.001)
        assertEquals(40.0, totals.personalTotal, 0.001)
        assertEquals(50.0, totals.fuelTotal, 0.001)
        assertEquals(60.0, totals.maintTotal, 0.001)
        assertEquals(70.0, totals.oilTotal, 0.001)
        assertEquals(80.0, totals.educationTotal, 0.001)
        assertEquals(30.0, totals.homeAndMedicalTotal, 0.001)
        assertEquals(180.0, totals.vehicleTotal, 0.001)
        assertEquals(360.0, totals.monthlyExpenses, 0.001)
    }

    @Test
    fun calculateMonthTotals_day1AndDay31Included() {
        val list = listOf(
            expense(ExpenseType.HOUSE, 5.0, date = "2026-10-01"),
            expense(ExpenseType.HOUSE, 7.0, date = "2026-10-31")
        )
        val totals = ExpenseCalculations.calculateMonthTotals(list, "2026-10")
        assertEquals(12.0, totals.houseTotal, 0.001)
    }

    @Test
    fun calculateMonthTotals_emptyDateExcluded() {
        val list = listOf(
            ExpenseRecord(type = ExpenseType.HOUSE, amount = 100.0, date = "")
        )
        val totals = ExpenseCalculations.calculateMonthTotals(list, "2026-10")
        assertEquals(0.0, totals.monthlyExpenses, 0.001)
    }

    @Test
    fun safeAmount_nonFiniteIsZero() {
        assertEquals(0.0, ExpenseCalculations.safeAmount(Double.NaN), 0.0)
        assertEquals(0.0, ExpenseCalculations.safeAmount(Double.POSITIVE_INFINITY), 0.0)
        assertEquals(0.0, ExpenseCalculations.safeAmount(null), 0.0)
        assertEquals(12.5, ExpenseCalculations.safeAmount(12.5), 0.0)
    }

    @Test
    fun netIncome_basic() {
        assertEquals(200.0, ExpenseCalculations.netIncome(1000.0, 800.0), 0.001)
        assertEquals(-100.0, ExpenseCalculations.netIncome(100.0, 200.0), 0.001)
    }

    @Test
    fun savingsRate_and_expenseRatio() {
        assertEquals(20, ExpenseCalculations.savingsRate(1000.0, 200.0))
        assertEquals(80, ExpenseCalculations.expenseRatio(1000.0, 800.0))
        assertEquals(0, ExpenseCalculations.savingsRate(0.0, 0.0))
        assertEquals(100, ExpenseCalculations.expenseRatio(0.0, 50.0))
        assertEquals(100, ExpenseCalculations.expenseRatio(100.0, 200.0))
    }

    @Test
    fun deficit_surplus_warning() {
        assertTrue(ExpenseCalculations.isDeficit(-1.0))
        assertFalse(ExpenseCalculations.isDeficit(0.0))
        assertTrue(ExpenseCalculations.isSurplus(0.0))
        assertTrue(ExpenseCalculations.isExpenseWarning(91))
        assertFalse(ExpenseCalculations.isExpenseWarning(90))
    }
}
