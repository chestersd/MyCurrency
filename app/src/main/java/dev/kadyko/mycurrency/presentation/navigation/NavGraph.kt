package dev.kadyko.mycurrency.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.kadyko.mycurrency.presentation.screen.CurrencyScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "rub"
    ) {
        composable("rub") {
            CurrencyScreen(currencyType = "RUB")
        }
        composable("usd") {
            CurrencyScreen(currencyType = "USD")
        }
        composable("eur") {
            CurrencyScreen(currencyType = "EUR")
        }
    }
}
