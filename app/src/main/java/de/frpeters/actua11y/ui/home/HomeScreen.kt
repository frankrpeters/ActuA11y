package de.frpeters.actua11y.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val paneTitleStr = stringResource(R.string.home_pane_title)
    val itemDescStr = stringResource(R.string.content_descriptions_list_item_desc)

    Scaffold(
        // WHY: paneTitle announces the screen name to TalkBack when navigation lands here,
        // giving screen-reader users immediate context about where they are.
        modifier = Modifier.semantics { paneTitle = paneTitleStr },
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.home_screen_title)) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.home_intro_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.home_intro_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                // WHY: heading() lets TalkBack users jump between categories with a
                // swipe gesture, rather than having to move through every list item.
                Text(
                    text = stringResource(R.string.home_talkback_category),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .semantics { heading() }
                )
            }
            item {
                Surface(
                    onClick = { onNavigate(Screen.TopicContentDescriptions.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        // WHY: clearAndSetSemantics replaces the auto-derived semantics
                        // (just the label text) with a complete sentence that tells TalkBack
                        // users what the item is and where it leads before they activate it.
                        .clearAndSetSemantics {
                            contentDescription = itemDescStr
                            role = Role.Button
                            onClick { onNavigate(Screen.TopicContentDescriptions.route); true }
                        },
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            // WHY: 48 dp is the WCAG minimum touch target size.
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.content_descriptions_label),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
