package dev.kadyko.mycurrency.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currencies ORDER BY isFavorite DESC, code ASC")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE isFavorite = 1 ORDER BY code ASC")
    fun getFavoriteCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getCurrencyByCode(code: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("UPDATE currencies SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavoriteStatus(code: String, isFavorite: Boolean)

    @Query("DELETE FROM currencies")
    suspend fun clearAll()
}
