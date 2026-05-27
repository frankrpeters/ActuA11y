package de.frpeters.actua11y.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.frpeters.actua11y.ui.home.HomeScreen
import de.frpeters.actua11y.ui.topic.contentdescriptions.ContentDescriptionsScreen

@Composable
fun ActuA11yNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.TopicContentDescriptions.route) {
            ContentDescriptionsScreen(onBack = { navController.popBackStack() })
        }
    }
}
