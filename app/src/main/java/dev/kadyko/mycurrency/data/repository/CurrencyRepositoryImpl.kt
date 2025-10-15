package dev.kadyko.mycurrency.data.repository

class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao,
    private val preferences: DataStore<Preferences>
) : CurrencyRepository {

    override fun getCurrency(abbreviation: String): Flow<Currency?> {
        return currencyDao.getCurrency(abbreviation).map { it?.toCurrency() }
    }

    override suspend fun refreshCurrency(abbreviation: String) {
        try {
            val response = when (abbreviation.uppercase()) {
                "EUR" -> apiService.getEurCurrency()
                "RUB" -> apiService.getRubCurrency()
                "USD" -> apiService.getUsdCurrency()
                else -> throw IllegalArgumentException("Unknown currency: $abbreviation")
            }

            val currencyEntity = response.toEntity()
            currencyDao.insertCurrency(currencyEntity)
            updateLastRefreshTime(abbreviation)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun shouldRefreshData(abbreviation: String): Boolean {
        val lastRefresh = preferences.data
            .map { it[PreferencesKeys.getLastRefreshKey(abbreviation)] ?: 0L }
            .first()
        return System.currentTimeMillis() - lastRefresh > 3600000 // 1 час
    }

    private suspend fun updateLastRefreshTime(abbreviation: String) {
        preferences.edit { preferences ->
            preferences[PreferencesKeys.getLastRefreshKey(abbreviation)] = System.currentTimeMillis()
        }
    }
}

private object PreferencesKeys {
    fun getLastRefreshKey(abbreviation: String) = longPreferencesKey("last_refresh_$abbreviation")
}