package dev.kadyko.mycurrency.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalCurrencyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}