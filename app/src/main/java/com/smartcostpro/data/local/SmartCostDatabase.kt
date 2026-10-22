package com.smartcostpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartcostpro.data.local.dao.ExpenseDao
import com.smartcostpro.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SmartCostDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "smartcost.db"
    }
}
