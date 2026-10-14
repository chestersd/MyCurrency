package dev.kadyko.mycurrency.data.repository

import dev.kadyko.mycurrency.data.local.CurrencyDao
import dev.kadyko.mycurrency.data.local.CurrencyEntity
import dev.kadyko.mycurrency.data.remote.CurrencyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao
) {

    val allCurrencies: Flow<List<CurrencyEntity>> = currencyDao.getAllCurrencies()
    val favoriteCurrencies: Flow<List<CurrencyEntity>> = currencyDao.getFavoriteCurrencies()

    suspend fun refreshRates(baseCurrency: String = "BYN"): Result<Unit> {
        return try {
            val response = currencyApi.getExchangeRates(baseCurrency)
            val ratesMap = response.rates ?: emptyMap()
            val timestamp = response.timeLastUpdateUnix ?: (System.currentTimeMillis() / 1000)

            val existingCurrencies = currencyDao.getAllCurrencies().first().associateBy { it.code }

            val entities = ratesMap.map { (code, rate) ->
                val existing = existingCurrencies[code]
                CurrencyEntity(
                    code = code,
                    name = getCurrencyName(code),
                    rate = rate,
                    baseCurrency = baseCurrency,
                    timestamp = timestamp,
                    isFavorite = existing?.isFavorite ?: (code == "USD" || code == "EUR" || code == "RUB" || code == "CNY")
                )
            }

            currencyDao.clearAll()
            currencyDao.insertCurrencies(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            // Seed local default database if empty or on error
            val currentList = currencyDao.getAllCurrencies().first()
            if (currentList.isEmpty()) {
                seedInitialData(baseCurrency)
            }
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(currencyCode: String, isFavorite: Boolean) {
        currencyDao.updateFavoriteStatus(currencyCode, isFavorite)
    }

    suspend fun convertCurrency(fromCode: String, toCode: String, amount: Double): Double {
        val fromEntity = currencyDao.getCurrencyByCode(fromCode)
        val toEntity = currencyDao.getCurrencyByCode(toCode)

        if (fromEntity == null || toEntity == null || fromEntity.rate == 0.0) {
            return 0.0
        }

        // Convert amount from `fromCode` to base, then base to `toCode`
        val inBase = amount / fromEntity.rate
        return inBase * toEntity.rate
    }

    private suspend fun seedInitialData(baseCurrency: String) {
        val defaults = mapOf(
            "BYN" to 1.0,
            "USD" to 0.305,
            "EUR" to 0.281,
            "RUB" to 28.50,
            "CNY" to 2.20,
            "GBP" to 0.241,
            "PLN" to 1.22,
            "TRY" to 10.50,
            "CHF" to 0.272,
            "JPY" to 47.50
        )

        val timestamp = System.currentTimeMillis() / 1000
        val entities = defaults.map { (code, rate) ->
            CurrencyEntity(
                code = code,
                name = getCurrencyName(code),
                rate = rate,
                baseCurrency = baseCurrency,
                timestamp = timestamp,
                isFavorite = code == "USD" || code == "EUR" || code == "RUB" || code == "CNY"
            )
        }

        currencyDao.insertCurrencies(entities)
    }

    private fun getCurrencyName(code: String): String {
        return when (code) {
            "BYN" -> "Belarusian Ruble"
            "USD" -> "US Dollar"
            "EUR" -> "Euro"
            "RUB" -> "Russian Ruble"
            "GBP" -> "British Pound"
            "CNY" -> "Chinese Yuan"
            "JPY" -> "Japanese Yen"
            "CAD" -> "Canadian Dollar"
            "CHF" -> "Swiss Franc"
            "AUD" -> "Australian Dollar"
            "KZT" -> "Kazakhstani Tenge"
            "PLN" -> "Polish Zloty"
            "TRY" -> "Turkish Lira"
            else -> "$code Currency"
        }
    }
}
