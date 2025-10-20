package dev.kadyko.mycurrency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.kadyko.mycurrency.presentation.screen.CurrencyScreen
import dev.kadyko.mycurrency.presentation.theme.CurrencyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = "rub",
                            modifier = Modifier.padding(padding)
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
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem("rub", "RUB", Icons.Default.CurrencyRuble),
        NavItem("usd", "USD", Icons.Default.AttachMoney),
        NavItem("eur", "EUR", Icons.Default.Euro)
    )

    val currentRoute = currentRoute(navController)

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

data class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AttachMoney
//import androidx.compose.material.icons.filled.CurrencyRuble
//import androidx.compose.material.icons.filled.Euro
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import dev.kadyko.mycurrency.presentation.navigation.NavGraph
//import dev.kadyko.mycurrency.presentation.screen.CurrencyScreen
//import dev.kadyko.mycurrency.presentation.theme.CurrencyAppTheme
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            CurrencyAppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//
//                    Scaffold(
//                        bottomBar = {
//                            BottomNavigationBar(navController = navController)
//                        }
//                    ) { padding ->
//                        NavGraph(
//                            navController = navController,
//                            modifier = Modifier.padding(padding)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    val items = listOf(
//        NavItem("rub", "RUB", Icons.Default.CurrencyRuble),
//        NavItem("usd", "USD", Icons.Default.AttachMoney),
//        NavItem("eur", "EUR", Icons.Default.Euro)
//    )
//
//    val currentRoute = currentRoute(navController)
//
//    NavigationBar {
//        items.forEach { item ->
//            NavigationBarItem(
//                icon = { Icon(item.icon, contentDescription = item.title) },
//                label = { Text(item.title) },
//                selected = currentRoute == item.route,
//                onClick = {
//                    navController.navigate(item.route) {
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun currentRoute(navController: NavHostController): String? {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    return navBackStackEntry?.destination?.route
//}
//
//data class NavItem(
//    val route: String,
//    val title: String,
//    val icon: ImageVector
//)