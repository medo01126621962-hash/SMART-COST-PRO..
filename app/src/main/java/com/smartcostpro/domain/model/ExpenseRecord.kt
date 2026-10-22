package com.smartcostpro.domain.model

/**
 * Canonical expense record — source: financeService.ts / Expenses-Logic-Only.txt §2
 */
data class ExpenseRecord(
    val id: String? = null,
    val type: ExpenseType,
    val title: String? = null,
    val amount: Double,
    val category: String? = null,
    /** ISO-like date string; month filter uses first 7 chars (YYYY-MM). */
    val date: String,
    val paymentMethod: String? = null,
    val receiptUrl: String? = null,
    val notes: String? = null,
    val createdAt: String? = null
)
