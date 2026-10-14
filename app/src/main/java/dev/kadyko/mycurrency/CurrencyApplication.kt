package dev.kadyko.mycurrency

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import dev.kadyko.mycurrency.notification.NotificationHelper
import javax.inject.Inject

@HiltAndroidApp
class CurrencyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        NotificationHelper.createNotificationChannel(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
