package dev.kadyko.mycurrency.domain.repository

import dev.kadyko.mycurrency.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun fetchAndSaveCurrency(currencyCode: String)
    fun getCurrencyByAbbr(abbr: String): Flow<Currency?>
}