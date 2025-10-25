package dev.kadyko.mycurrency.domain.repository

import dev.kadyko.mycurrency.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun fetchAndSaveCurrency(currencyCode: String) // <-- Изменили сигнатуру
    fun getCurrencyByAbbr(abbr: String): Flow<Currency?>
}