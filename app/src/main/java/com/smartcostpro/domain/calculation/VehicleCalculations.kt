package com.smartcostpro.domain.calculation

/**
 * Vehicle expense calculations — source: Expenses-Logic-Only.txt §5
 * Fuel:  cost = quantity × unit price
 * Maint: total = supplyPrice + laborPrice
 */
object VehicleCalculations {

    fun fuelCost(quantity: Double, rate: Double): Double {
        val q = ExpenseCalculations.safeAmount(quantity)
        val r = ExpenseCalculations.safeAmount(rate)
        return q * r
    }

    fun maintenanceTotal(supplyPrice: Double, laborPrice: Double): Double {
        val supply = ExpenseCalculations.safeAmount(supplyPrice)
        val labor = ExpenseCalculations.safeAmount(laborPrice)
        return supply + labor
    }

    fun vehicleTotal(fuelTotal: Double, maintTotal: Double, oilTotal: Double): Double =
        ExpenseCalculations.safeAmount(fuelTotal) +
            ExpenseCalculations.safeAmount(maintTotal) +
            ExpenseCalculations.safeAmount(oilTotal)
}
