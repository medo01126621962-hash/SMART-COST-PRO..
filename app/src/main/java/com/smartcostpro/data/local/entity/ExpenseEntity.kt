package com.smartcostpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartcostpro.domain.model.ExpenseRecord
import com.smartcostpro.domain.model.ExpenseType

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,
    val paymentMethod: String? = null,
    val receiptUrl: String? = null,
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: String
) {
    fun toDomain(): ExpenseRecord {
        val expenseType = ExpenseType.fromKey(type) ?: ExpenseType.HOUSE
        return ExpenseRecord(
            id = id,
            type = expenseType,
            title = title,
            amount = amount,
            category = category,
            date = date,
            paymentMethod = paymentMethod,
            receiptUrl = receiptUrl,
            notes = notes,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(record: ExpenseRecord, id: String, createdAt: String, updatedAt: String): ExpenseEntity {
            return ExpenseEntity(
                id = id,
                title = record.title?.takeIf { it.isNotBlank() }
                    ?: record.category?.takeIf { it.isNotBlank() }
                    ?: record.type.key,
                amount = record.amount,
                type = record.type.key,
                category = record.category?.takeIf { it.isNotBlank() } ?: record.type.key,
                date = record.date,
                paymentMethod = record.paymentMethod,
                receiptUrl = record.receiptUrl,
                notes = record.notes,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
