package dev.kadyko.mycurrency.data.local.dao

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies WHERE abbreviation = :abbreviation")
    fun getCurrency(abbreviation: String): Flow<CurrencyEntity?>

    @Query("SELECT * FROM currencies")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: CurrencyEntity)

    @Query("DELETE FROM currencies WHERE abbreviation = :abbreviation")
    suspend fun deleteCurrency(abbreviation: String)
}

