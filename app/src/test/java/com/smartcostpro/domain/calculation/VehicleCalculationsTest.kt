package com.smartcostpro.domain.calculation

import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleCalculationsTest {

    @Test
    fun fuelCost_basic() {
        assertEquals(111.25, VehicleCalculations.fuelCost(5.0, 22.25), 0.001)
        assertEquals(0.0, VehicleCalculations.fuelCost(0.0, 22.25), 0.001)
    }

    @Test
    fun fuelCost_nonFinite() {
        assertEquals(0.0, VehicleCalculations.fuelCost(Double.NaN, 10.0), 0.0)
        assertEquals(0.0, VehicleCalculations.fuelCost(10.0, Double.POSITIVE_INFINITY), 0.0)
    }

    @Test
    fun maintenanceTotal_basic() {
        assertEquals(350.0, VehicleCalculations.maintenanceTotal(200.0, 150.0), 0.001)
        assertEquals(200.0, VehicleCalculations.maintenanceTotal(200.0, 0.0), 0.001)
    }

    @Test
    fun vehicleTotal_sum() {
        assertEquals(180.0, VehicleCalculations.vehicleTotal(50.0, 60.0, 70.0), 0.001)
    }
}
