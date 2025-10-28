package dev.kadyko.mycurrency.data.repository

import dev.kadyko.mycurrency.data.local.CurrencyDao
import dev.kadyko.mycurrency.data.local.LocalCurrencyEntity
import dev.kadyko.mycurrency.data.remote.CurrencyApiService
import dev.kadyko.mycurrency.domain.model.Currency
import dev.kadyko.mycurrency.domain.repository.CurrencyRepository
import dev.kadyko.mycurrency.util.Constants // <-- Импорт
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao
) : CurrencyRepository {

    // Переместили id сюда
    override suspend fun fetchAndSaveCurrency(currencyCode: String) {
        val id = when (currencyCode) {
            "RUB" -> Constants.RUB_ID // <-- Используем константы
            "USD" -> Constants.USD_ID
            "EUR" -> Constants.EUR_ID
            else -> throw IllegalArgumentException("Unknown currency code: $currencyCode")
        }
        val dto = apiService.getCurrencyById(id)
        val entity = LocalCurrencyEntity(
            abbreviation = currencyCode,
            name = dto.curName,
            quotName = "${dto.curScale} ${dto.curName}",
            scale = dto.curScale,
            officialRate = dto.curOfficialRate
        )
        currencyDao.insertCurrency(entity)
    }

    override fun getCurrencyByAbbr(abbr: String): Flow<Currency?> {
        return currencyDao.getCurrencyByAbbr(abbr).map { it?.toDomain() }
    }

    private fun LocalCurrencyEntity.toDomain() = Currency(
        abbreviation = abbreviation,
        name = name,
        quotName = quotName,
        scale = scale,
        officialRate = officialRate
    )
}