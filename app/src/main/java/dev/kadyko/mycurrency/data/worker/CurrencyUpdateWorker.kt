package dev.kadyko.mycurrency.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.kadyko.mycurrency.data.preferences.UserPreferences
import dev.kadyko.mycurrency.data.repository.CurrencyRepository
import dev.kadyko.mycurrency.notification.NotificationHelper
import kotlinx.coroutines.flow.first

@HiltWorker
class CurrencyUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CurrencyRepository,
    private val preferences: UserPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result {
        val baseCurrency = preferences.baseCurrency.first()
        val result = repository.refreshRates(baseCurrency)

        return if (result.isSuccess) {
            val title = context.getString(dev.kadyko.mycurrency.R.string.rates_updated_notification_title)
            val message = context.getString(dev.kadyko.mycurrency.R.string.rates_updated_notification_text)
            NotificationHelper.sendRateUpdateNotification(context, title, message)
            ListenableWorker.Result.success()
        } else {
            ListenableWorker.Result.retry()
        }
    }
}
