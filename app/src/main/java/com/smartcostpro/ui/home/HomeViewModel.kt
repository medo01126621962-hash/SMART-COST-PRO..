package com.smartcostpro.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartcostpro.SmartCostProApp
import com.smartcostpro.data.local.entity.ExpenseEntity
import com.smartcostpro.domain.model.ExpenseType
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeDashboardUiState(
    val allExpenses: List<ExpenseEntity> = emptyList(),
    val selectedMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val monthlyBudget: Double? = null
) {
    val monthKey: String get() = selectedMonth.toString()
    val monthExpenses: List<ExpenseEntity>
        get() = HomeDashboardCalculations.forMonth(allExpenses, selectedMonth)
    val monthlyTotal: Double
        get() = HomeDashboardCalculations.totalForMonth(allExpenses, selectedMonth)
}

object HomeDashboardCalculations {
    fun forMonth(expenses: List<ExpenseEntity>, month: YearMonth): List<ExpenseEntity> {
        val monthKey = month.toString()
        return expenses.filter { it.date.startsWith(monthKey) }
    }

    fun totalForMonth(expenses: List<ExpenseEntity>, month: YearMonth): Double =
        forMonth(expenses, month)
            .filter { ExpenseType.fromKey(it.type) != null }
            .sumOf { it.amount }

    fun totalForType(expenses: List<ExpenseEntity>, typeKey: String): Double =
        expenses.filter { it.type.equals(typeKey, ignoreCase = true) }
            .sumOf { it.amount }

    fun percentage(part: Double, total: Double): Int =
        if (total <= 0.0) 0 else ((part / total) * 100.0).toInt().coerceIn(0, 100)
}

/**
 * Keeps dashboard state tied to the existing Room database.
 * Income, association, trip-income, and accident records are intentionally not
 * fabricated here because those data sources are not part of the current Room schema.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = (application as SmartCostProApp).database.expenseDao()
    private val preferences = application.getSharedPreferences(
        "smart_cost_dashboard",
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(
        HomeDashboardUiState(
            monthlyBudget = readBudget(YearMonth.now())
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            expenseDao.observeAll()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "تعذّر قراءة المصروفات المحفوظة"
                        )
                    }
                }
                .collect { expenses ->
                    _uiState.update {
                        it.copy(
                            allExpenses = expenses,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun moveMonth(delta: Long) {
        _uiState.update { state ->
            val nextMonth = state.selectedMonth.plusMonths(delta)
            state.copy(
                selectedMonth = nextMonth,
                monthlyBudget = readBudget(nextMonth)
            )
        }
    }

    fun saveMonthlyBudget(amount: Double?) {
        val month = _uiState.value.selectedMonth
        val key = budgetKey(month)
        if (amount == null) {
            preferences.edit().remove(key).apply()
        } else {
            preferences.edit().putString(key, amount.toString()).apply()
        }
        _uiState.update { it.copy(monthlyBudget = amount) }
    }

    suspend fun addExpense(
        title: String,
        amount: Double,
        type: ExpenseType
    ): Result<Unit> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("اكتب وصف المصروف"))
        }
        if (!amount.isFinite() || amount <= 0.0) {
            return Result.failure(IllegalArgumentException("أدخل مبلغًا أكبر من صفر"))
        }

        return try {
            val now = Instant.now().toString()
            val selectedMonth = _uiState.value.selectedMonth
            val expenseDate = if (selectedMonth == YearMonth.now()) {
                LocalDate.now()
            } else {
                selectedMonth.atDay(1)
            }
            expenseDao.insert(
                ExpenseEntity(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    amount = amount,
                    type = type.key,
                    category = type.key,
                    date = expenseDate.toString(),
                    paymentMethod = null,
                    receiptUrl = null,
                    notes = null,
                    createdAt = now,
                    updatedAt = now
                )
            )
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun readBudget(month: YearMonth): Double? =
        preferences.getString(budgetKey(month), null)?.toDoubleOrNull()

    private fun budgetKey(month: YearMonth): String = "monthly_budget_" + month
}
