package de.frpeters.actua11y.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TopicContentDescriptions : Screen("topic/content_descriptions")
}
