package com.smartcostpro.domain.model

/**
 * Canonical expense types — source: financeContract.ts / Expenses-Logic-Only.txt
 *
 * Exactly eight types. Do not add others without updating all consumers.
 */
enum class ExpenseType(val key: String) {
    HOUSE("house"),
    MEDICAL("medical"),
    PERSONAL("personal"),
    STUDENT("student"),
    VEHICLE_FUEL("vehicle_fuel"),
    VEHICLE_MAINT("vehicle_maint"),
    VEHICLE_OIL("vehicle_oil"),
    WORK("work");

    companion object {
        fun fromKey(key: String): ExpenseType? =
            entries.find { it.key.equals(key, ignoreCase = true) }

        /** All eight canonical keys in stable order. */
        val allKeys: List<String> = entries.map { it.key }
    }
}
