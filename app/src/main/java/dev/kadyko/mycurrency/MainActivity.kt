package dev.kadyko.mycurrency

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.kadyko.mycurrency.data.preferences.UserPreferences
import dev.kadyko.mycurrency.ui.navigation.BottomNavBar
import dev.kadyko.mycurrency.ui.navigation.NavGraph
import dev.kadyko.mycurrency.ui.theme.MyCurrencyTheme
import java.util.Locale
import javax.inject.Inject

class LocalizedContextWrapper(
    base: Context,
    private val localizedContext: Context
) : ContextWrapper(base) {
    override fun getResources(): Resources {
        return localizedContext.resources
    }
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            val currentLanguage by userPreferences.language.collectAsState(initial = "ru")
            val locale = remember(currentLanguage) { Locale(currentLanguage) }
            Locale.setDefault(locale)

            val currentConfig = LocalConfiguration.current
            val configuration = remember(currentLanguage, currentConfig) {
                Configuration(currentConfig).apply {
                    setLocale(locale)
                }
            }

            val currentContext = LocalContext.current
            val localizedWrapper = remember(currentLanguage, currentContext) {
                val localizedContext = currentContext.createConfigurationContext(configuration)
                LocalizedContextWrapper(currentContext, localizedContext)
            }

            CompositionLocalProvider(
                LocalContext provides localizedWrapper,
                LocalConfiguration provides configuration
            ) {
                MyCurrencyTheme {
                    MainAppScreen()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
