package dev.kadyko.mycurrency.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kadyko.mycurrency.R
import dev.kadyko.mycurrency.data.preferences.UserPreferences
import dev.kadyko.mycurrency.data.worker.CurrencyUpdateWorker
import dev.kadyko.mycurrency.notification.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class SettingsUiState(
    val baseCurrency: String = "BYN",
    val language: String = "ru",
    val autoUpdateEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.baseCurrency,
        preferences.language,
        preferences.autoUpdate
    ) { base, lang, auto ->
        SettingsUiState(
            baseCurrency = base,
            language = lang,
            autoUpdateEnabled = auto
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setBaseCurrency(code: String) {
        viewModelScope.launch {
            preferences.setBaseCurrency(code)
        }
    }

    fun setLanguage(langCode: String) {
        viewModelScope.launch {
            preferences.setLanguage(langCode)
            val appLocales = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocales)
        }
    }

    fun setAutoUpdate(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoUpdate(enabled)
            val workManager = WorkManager.getInstance(context)
            if (enabled) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val workRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    "currency_periodic_update",
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
            } else {
                workManager.cancelUniqueWork("currency_periodic_update")
            }
        }
    }

    fun sendTestNotification() {
        val title = context.getString(R.string.rates_updated_notification_title)
        val message = context.getString(R.string.rates_updated_notification_text)
        NotificationHelper.sendRateUpdateNotification(context, title, message)
    }
}
