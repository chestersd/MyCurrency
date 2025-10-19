package dev.kadyko.mycurrency.domain.repository

import dev.kadyko.mycurrency.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getCurrency(abbreviation: String): Flow<Currency?>
    suspend fun refreshCurrency(abbreviation: String)
    suspend fun shouldRefreshData(abbreviation: String): Boolean
}