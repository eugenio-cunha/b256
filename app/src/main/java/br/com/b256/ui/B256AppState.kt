package br.com.b256.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.b256.navigation.B256Destination
import kotlinx.coroutines.CoroutineScope
import androidx.navigation.NavDestination.Companion.hasRoute
import br.com.b256.feature.home.navigation.navigateToHome

@Composable
fun rememberB256AppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): B256AppState {
    return B256AppState(
        navController = navController,
        coroutineScope = coroutineScope,
    )
}

@Stable
class B256AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: B256Destination?
        @Composable get() {
            return B256Destination.entries.firstOrNull { destination ->
                currentDestination?.hasRoute(route = destination.route) ?: false
            }
        }

    fun navigateToHome() = navController.navigateToHome()
}
