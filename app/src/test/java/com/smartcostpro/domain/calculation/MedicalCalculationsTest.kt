package com.smartcostpro.domain.calculation

import org.junit.Assert.assertEquals
import org.junit.Test

class MedicalCalculationsTest {

    @Test
    fun medicalTotal_basic() {
        assertEquals(450.0, MedicalCalculations.medicalTotal(300.0, 150.0), 0.001)
        assertEquals(0.0, MedicalCalculations.medicalTotal(0.0, 0.0), 0.001)
    }

    @Test
    fun medicalTotal_nonFinite() {
        assertEquals(100.0, MedicalCalculations.medicalTotal(Double.NaN, 100.0), 0.001)
        assertEquals(50.0, MedicalCalculations.medicalTotal(50.0, Double.NEGATIVE_INFINITY), 0.001)
    }
}
