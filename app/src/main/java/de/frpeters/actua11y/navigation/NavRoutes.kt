package de.frpeters.actua11y.navigation

// WHY: only non-topic routes live here. Topic routes are derived from TopicRegistry
// (Topic.route) so there is exactly one place that declares them.
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Category : Screen("category/{categoryName}") {
        fun route(category: TopicCategory) = "category/${category.name}"
    }
}
