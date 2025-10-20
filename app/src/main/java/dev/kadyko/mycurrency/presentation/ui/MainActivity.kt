package dev.kadyko.mycurrency.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
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
                    NavItem("rub", "RUB", Icons.Default.Paid),
                    NavItem("usd", "USD", Icons.Default.AttachMoney),
                    NavItem("eur", "EUR", Icons.Default.Euro)
                )

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
                                            // Очищаем стек навигации, чтобы не создавать дубликаты
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
                        NavHost(
                            navController = navController,
                            startDestination = "rub"
                        ) {
                            composable("rub") { CurrencyScreen("RUB", hiltViewModel()) }
                            composable("usd") { CurrencyScreen("USD", hiltViewModel()) }
                            composable("eur") { CurrencyScreen("EUR", hiltViewModel()) }
                        }
                    }
                }
            }
        }
    }
}

// Вспомогательный класс для элементов навигации
data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

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
                text = "$currencyCode Курс",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        item {
            if (currency != null) {
                val c = currency // Локальная переменная для избежания ошибки "delegated property"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Название: ${c.name}")
                        Text("Квота: ${c.quotName}")
                        Text("Масштаб: ${c.scale}")
                        Text("Курс: ${c.officialRate} BYN")
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Загрузка...")
                }
            }
        }
    }
}

//package dev.kadyko.mycurrency.presentation.ui
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import dagger.hilt.android.AndroidEntryPoint
//import dev.kadyko.mycurrency.presentation.theme.CurrencyAppTheme
//import dev.kadyko.mycurrency.presentation.viewmodel.CurrencyViewModel
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            CurrencyAppTheme {
//                val navController = rememberNavController()
//                NavHost(
//                    navController = navController,
//                    startDestination = "rub"
//                ) {
//                    composable("rub") { CurrencyScreen("RUB", hiltViewModel()) }
//                    composable("usd") { CurrencyScreen("USD", hiltViewModel()) }
//                    composable("eur") { CurrencyScreen("EUR", hiltViewModel()) }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CurrencyScreen(currencyCode: String, viewModel: CurrencyViewModel) {
//    val currency by when (currencyCode) {
//        "RUB" -> viewModel.rubState.collectAsStateWithLifecycle()
//        "USD" -> viewModel.usdState.collectAsStateWithLifecycle()
//        "EUR" -> viewModel.eurState.collectAsStateWithLifecycle()
//        else -> throw IllegalArgumentException("Unknown currency: $currencyCode")
//    }
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        contentPadding = PaddingValues(16.dp)
//    ) {
//        item {
//            Text(
//                text = "$currencyCode Курс",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
//            )
//        }
//
//        item {
//            if (currency != null) {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // Используем !! для утверждения, что currency не null (поскольку проверили выше)
//                        Text("Название: ${currency!!.name}")     // <-- Добавлен !!
//                        Text("Квота: ${currency!!.quotName}")   // <-- Добавлен !!
//                        Text("Масштаб: ${currency!!.scale}")    // <-- Добавлен !!
//                        Text("Курс: ${currency!!.officialRate} BYN") // <-- Добавлен !!
//                    }
//                }
//            } else {
//                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                    Text("Загрузка...")
//                }
//            }
//        }
//    }
//}
//
