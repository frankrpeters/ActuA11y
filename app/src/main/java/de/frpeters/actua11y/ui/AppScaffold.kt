package de.frpeters.actua11y.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.frpeters.actua11y.R
import de.frpeters.actua11y.navigation.ActuA11yNavHost
import de.frpeters.actua11y.navigation.TopicCategory
import de.frpeters.actua11y.navigation.TopicRegistry
import de.frpeters.actua11y.ui.components.NaiveToggle
import de.frpeters.actua11y.ui.theme.ActuA11yTheme

/**
 * The single [Scaffold] and [TopAppBar] for the whole app, composed once above the
 * [ActuA11yNavHost] (requirements §4.2, §4.6). Screen composables never introduce their own —
 * see the "Two Structural Invariants" in CLAUDE.md.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    // WHY: hoisted above the NavHost so it persists across navigation (§4.4). rememberSaveable
    // survives configuration change but is deliberately not backed by any persistent storage —
    // a cold start must always land on the Better version.
    var showNaive by rememberSaveable { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTopic = TopicRegistry.byRoute(currentRoute)
    val currentCategory = backStackEntry?.arguments?.getString("categoryName")
        ?.let { name -> TopicCategory.entries.firstOrNull { it.name == name } }

    val title = when {
        currentTopic != null -> stringResource(currentTopic.titleRes)
        currentCategory != null -> stringResource(currentCategory.titleRes)
        else -> stringResource(R.string.home_screen_title)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                // WHY: "back" alone is ambiguous; this clarifies the action.
                                contentDescription = stringResource(R.string.navigate_back),
                            )
                        }
                    }
                },
                actions = {
                    NaiveToggle(
                        showNaive = showNaive,
                        onToggle = { showNaive = it },
                        // WHY: Home and Category screens are always the accessible
                        // implementation (§4.4) — the toggle affects topic content only.
                        enabled = currentTopic?.supportsNaive == true,
                    )
                },
            )
        },
    ) { padding ->
        ActuA11yNavHost(
            navController = navController,
            showNaive = showNaive,
            modifier = Modifier.padding(padding),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun AppScaffoldPreview() {
    ActuA11yTheme {
        AppScaffold()
    }
}
