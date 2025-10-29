package dev.kadyko.mycurrency.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: LocalCurrencyEntity)

    @Query("SELECT * FROM currencies WHERE abbreviation = :abbr")
    fun getCurrencyByAbbr(abbr: String): Flow<LocalCurrencyEntity?>

    @Query("SELECT * FROM currencies WHERE name = :name")
    fun getCurrencyByName(name: String): Flow<LocalCurrencyEntity?>

    @Query("SELECT * FROM currencies WHERE name = :scale")
    fun getCurrencyByName(scale: Int): Flow<LocalCurrencyEntity?>
}