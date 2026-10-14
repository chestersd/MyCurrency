package dev.kadyko.mycurrency.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.kadyko.mycurrency.ui.auth.AuthScreen
import dev.kadyko.mycurrency.ui.auth.AuthViewModel
import dev.kadyko.mycurrency.ui.converter.ConverterScreen
import dev.kadyko.mycurrency.ui.converter.ConverterViewModel
import dev.kadyko.mycurrency.ui.favorites.FavoritesScreen
import dev.kadyko.mycurrency.ui.favorites.FavoritesViewModel
import dev.kadyko.mycurrency.ui.rates.RatesScreen
import dev.kadyko.mycurrency.ui.rates.RatesViewModel
import dev.kadyko.mycurrency.ui.settings.SettingsScreen
import dev.kadyko.mycurrency.ui.settings.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Rates.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Rates.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "mycurrency://rates" },
                navDeepLink { uriPattern = "mycurrency://app" }
            )
        ) {
            val viewModel: RatesViewModel = hiltViewModel()
            RatesScreen(
                viewModel = viewModel,
                onCurrencyClick = { currencyCode ->
                    navController.navigate("converter?from=USD&to=$currencyCode")
                }
            )
        }

        composable(
            route = Screen.Converter.route,
            arguments = listOf(
                navArgument("from") {
                    type = NavType.StringType
                    defaultValue = "USD"
                },
                navArgument("to") {
                    type = NavType.StringType
                    defaultValue = "EUR"
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "mycurrency://converter?from={from}&to={to}" },
                navDeepLink { uriPattern = "mycurrency://converter" }
            )
        ) {
            val viewModel: ConverterViewModel = hiltViewModel()
            ConverterScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.Favorites.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "mycurrency://favorites" }
            )
        ) {
            val viewModel: FavoritesViewModel = hiltViewModel()
            FavoritesScreen(
                viewModel = viewModel,
                onCurrencyClick = { currencyCode ->
                    navController.navigate("converter?from=USD&to=$currencyCode")
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "mycurrency://profile" }
            )
        ) {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.Settings.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "mycurrency://settings" }
            )
        ) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(viewModel = viewModel)
        }
    }
}
