package de.frpeters.actua11y.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.frpeters.actua11y.ui.category.CategoryScreen
import de.frpeters.actua11y.ui.home.HomeScreen

@Composable
fun ActuA11yNavHost(
    navController: NavHostController,
    showNaive: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCategory = { category ->
                    navController.navigate(Screen.Category.route(category))
                },
            )
        }
        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            val category = TopicCategory.entries.firstOrNull { it.name == categoryName }
            if (category != null) {
                CategoryScreen(
                    category = category,
                    onNavigateToTopic = { route -> navController.navigate(route) },
                )
            }
        }
        // WHY: destinations are derived from TopicRegistry — no route is declared by hand.
        TopicRegistry.all.forEach { topic ->
            composable(topic.route) {
                topic.content(showNaive, Modifier)
            }
        }
    }
}
