package de.frpeters.actua11y.ui.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.navigation.Topic
import de.frpeters.actua11y.navigation.TopicCategory
import de.frpeters.actua11y.navigation.TopicRegistry

/**
 * Lists the topics within one category (requirements §4.1). Always the accessible
 * implementation — categories have no Naive counterpart. Renders content only; the app bar
 * lives in [de.frpeters.actua11y.ui.AppScaffold].
 */
@Composable
fun CategoryScreen(
    category: TopicCategory,
    onNavigateToTopic: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topics = TopicRegistry.byCategory(category)
    val paneTitleStr = stringResource(category.titleRes)

    Box(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the category name to TalkBack when this content swaps
            // in below the persistent app bar.
            .semantics { paneTitle = paneTitleStr },
    ) {
        if (topics.isEmpty()) {
            Text(
                text = stringResource(R.string.category_empty),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(topics) { topic ->
                    TopicRow(topic = topic, onClick = { onNavigateToTopic(topic.route) })
                }
            }
        }
    }
}

@Composable
private fun TopicRow(topic: Topic, onClick: () -> Unit) {
    val title = stringResource(topic.titleRes)
    val itemDesc = stringResource(R.string.topic_list_item_desc_template, title)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            // WHY: one generic template covers every topic row, so a new topic needs no new
            // string resource here — see CLAUDE.md "Adding a Topic".
            .clearAndSetSemantics {
                contentDescription = itemDesc
                role = Role.Button
                onClick { onClick(); true }
            },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp) // WHY: WCAG 2.5.8 minimum touch target size.
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
