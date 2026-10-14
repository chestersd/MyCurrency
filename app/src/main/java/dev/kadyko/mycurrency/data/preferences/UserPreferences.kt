package dev.kadyko.mycurrency.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val baseCurrencyKey = stringPreferencesKey("base_currency")
    private val languageKey = stringPreferencesKey("language")
    private val autoUpdateKey = booleanPreferencesKey("auto_update")

    val baseCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[baseCurrencyKey] ?: "BYN"
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[languageKey] ?: "ru"
    }

    val autoUpdate: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[autoUpdateKey] ?: true
    }

    suspend fun setBaseCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[baseCurrencyKey] = currencyCode
        }
    }

    suspend fun setLanguage(langCode: String) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = langCode
        }
    }

    suspend fun setAutoUpdate(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[autoUpdateKey] = enabled
        }
    }
}
