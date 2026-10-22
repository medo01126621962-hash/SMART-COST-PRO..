package com.smartcostpro.domain.calculation

import com.smartcostpro.domain.model.ExpenseRecord
import com.smartcostpro.domain.model.ExpenseType

/**
 * Core expense totals — source: SMART-COST-Android-Logic-Spec.md §4.3, §4.9
 * and Expenses-Logic-Only.txt §3–4.
 *
 * monthlyExpenses = house + medical + work + personal + fuel + maint + oil + education
 * netIncome       = monthlyIncome - monthlyExpenses
 */
object ExpenseCalculations {

    fun safeAmount(amount: Double?): Double {
        if (amount == null) return 0.0
        if (!amount.isFinite()) return 0.0
        return amount
    }

    fun filterByMonth(
        expenses: List<ExpenseRecord>,
        month: String
    ): List<ExpenseRecord> =
        expenses.filter { ExpenseMonthFilter.isDateInMonth(it.date, month) }

    fun sumByType(
        expenses: List<ExpenseRecord>,
        type: ExpenseType
    ): Double =
        expenses
            .filter { it.type == type }
            .sumOf { safeAmount(it.amount) }

    data class MonthTotals(
        val houseTotal: Double,
        val medicalTotal: Double,
        val workTotal: Double,
        val personalTotal: Double,
        val fuelTotal: Double,
        val maintTotal: Double,
        val oilTotal: Double,
        val educationTotal: Double
    ) {
        val homeAndMedicalTotal: Double get() = houseTotal + medicalTotal
        val vehicleTotal: Double get() = fuelTotal + maintTotal + oilTotal

        val monthlyExpenses: Double
            get() = houseTotal + medicalTotal + workTotal + personalTotal +
                fuelTotal + maintTotal + oilTotal + educationTotal
    }

    fun calculateMonthTotals(
        expenses: List<ExpenseRecord>,
        month: String
    ): MonthTotals {
        val monthExpenses = filterByMonth(expenses, month)
        return MonthTotals(
            houseTotal = sumByType(monthExpenses, ExpenseType.HOUSE),
            medicalTotal = sumByType(monthExpenses, ExpenseType.MEDICAL),
            workTotal = sumByType(monthExpenses, ExpenseType.WORK),
            personalTotal = sumByType(monthExpenses, ExpenseType.PERSONAL),
            fuelTotal = sumByType(monthExpenses, ExpenseType.VEHICLE_FUEL),
            maintTotal = sumByType(monthExpenses, ExpenseType.VEHICLE_MAINT),
            oilTotal = sumByType(monthExpenses, ExpenseType.VEHICLE_OIL),
            educationTotal = sumByType(monthExpenses, ExpenseType.STUDENT)
        )
    }

    fun netIncome(monthlyIncome: Double, monthlyExpenses: Double): Double =
        safeAmount(monthlyIncome) - safeAmount(monthlyExpenses)

    fun savingsRate(monthlyIncome: Double, netIncome: Double): Int {
        val income = safeAmount(monthlyIncome)
        if (income <= 0.0) return 0
        val rate = kotlin.math.round(netIncome / income * 100.0).toInt()
        return rate.coerceIn(0, 100)
    }

    fun expenseRatio(monthlyIncome: Double, monthlyExpenses: Double): Int {
        val income = safeAmount(monthlyIncome)
        if (income <= 0.0) return 100
        val ratio = kotlin.math.round(monthlyExpenses / income * 100.0).toInt()
        return minOf(100, ratio)
    }

    fun isDeficit(netIncome: Double): Boolean = netIncome < 0.0
    fun isSurplus(netIncome: Double): Boolean = netIncome >= 0.0
    fun isExpenseWarning(expenseRatio: Int): Boolean = expenseRatio > 90
}
