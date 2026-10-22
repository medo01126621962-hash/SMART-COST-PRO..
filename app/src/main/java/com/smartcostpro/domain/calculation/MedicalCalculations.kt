package com.smartcostpro.domain.calculation

/**
 * Medical expense calculation — project rule:
 * medical = exam + medication
 */
object MedicalCalculations {

    fun medicalTotal(exam: Double, medication: Double): Double {
        val e = ExpenseCalculations.safeAmount(exam)
        val m = ExpenseCalculations.safeAmount(medication)
        return e + m
    }
}
