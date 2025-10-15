package dev.kadyko.mycurrency.domain.repository

interface CurrencyRepository {
    fun getCurrency(abbreviation: String): Flow<Currency?>
    suspend fun refreshCurrency(abbreviation: String)
    suspend fun shouldRefreshData(abbreviation: String): Boolean
}