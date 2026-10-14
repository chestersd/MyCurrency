package dev.kadyko.mycurrency.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.kadyko.mycurrency.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Rates : Screen("rates", R.string.nav_rates, Icons.Default.Home)
    object Converter : Screen("converter?from={from}&to={to}", R.string.nav_converter, Icons.Default.Refresh)
    object Favorites : Screen("favorites", R.string.nav_favorites, Icons.Default.Favorite)
    object Profile : Screen("profile", R.string.nav_profile, Icons.Default.Person)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

val bottomNavScreens = listOf(
    Screen.Rates,
    Screen.Converter,
    Screen.Favorites,
    Screen.Profile,
    Screen.Settings
)
