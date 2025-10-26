package dev.kadyko.mycurrency.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.kadyko.mycurrency.R
import dev.kadyko.mycurrency.presentation.theme.CurrencyAppTheme
import dev.kadyko.mycurrency.presentation.viewmodel.CurrencyViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyAppTheme {
                val navController = rememberNavController()
                val navItems = listOf(
                    NavItem("rub", stringResource(R.string.rub_label), Icons.Default.CurrencyRuble),
                    NavItem("usd", stringResource(R.string.usd_label), Icons.Default.AttachMoney),
                    NavItem("eur", stringResource(R.string.eur_label), Icons.Default.Euro)
                )

                val viewModel: CurrencyViewModel = hiltViewModel()
                val error by viewModel.errorState.collectAsStateWithLifecycle()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            navItems.forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = navController.currentDestination?.route == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (error != null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = stringResource(R.string.error_occurred))
                                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NavHost(
                                navController = navController,
                                startDestination = "rub"
                            ) {
                                composable("rub") { CurrencyScreen("RUB", viewModel) }
                                composable("usd") { CurrencyScreen("USD", viewModel) }
                                composable("eur") { CurrencyScreen("EUR", viewModel) }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun CurrencyScreen(currencyCode: String, viewModel: CurrencyViewModel) {
    val currency by when (currencyCode) {
        "RUB" -> viewModel.rubState.collectAsStateWithLifecycle()
        "USD" -> viewModel.usdState.collectAsStateWithLifecycle()
        "EUR" -> viewModel.eurState.collectAsStateWithLifecycle()
        else -> throw IllegalArgumentException("Unknown currency: $currencyCode")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.currency_rate, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        item {
            if (currency != null) {
                val c = currency
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.name, c!!.name))
                        Text(stringResource(R.string.quote, c!!.quotName))
                        Text(stringResource(R.string.scale, c!!.scale))
                        Text(stringResource(R.string.rate, c!!.officialRate))
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.loading))
                }
            }
        }
    }
}