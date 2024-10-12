package br.com.b256.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.b256.feature.home.HomeScreen

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(options: NavOptions? = null) = navigate(HOME_ROUTE, options)

fun NavGraphBuilder.homeScreen() {
    composable(route = HOME_ROUTE) {
        HomeScreen()
    }
}
