/*
 * Copyright 2026 Frank R. Peters
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.navigation.TopicCategory
import de.frpeters.actua11y.navigation.TopicRegistry

/**
 * Home screen: onboarding card, then all seven categories (requirements §5). Always the
 * accessible implementation — Home has no Naive counterpart, so there is nothing to toggle
 * here. Renders content only; the app bar lives in [de.frpeters.actua11y.ui.AppScaffold].
 */
@Composable
fun HomeScreen(onNavigateToCategory: (TopicCategory) -> Unit, modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.home_pane_title)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival.
            .semantics { paneTitle = paneTitleStr },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { OnboardingCard() }
        items(TopicCategory.entries) { category ->
            CategoryRow(category = category, onClick = { onNavigateToCategory(category) })
        }
    }
}

@Composable
private fun OnboardingCard() {
    val linkLabel = stringResource(R.string.onboarding_talkback_link_label)
    val linkUrl = stringResource(R.string.onboarding_talkback_link_url)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.titleMedium,
                // WHY: heading() lets TalkBack users jump straight past the onboarding card
                // with a swipe gesture once they've read it once.
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.onboarding_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            // WHY: LinkAnnotation.Url gives this text real link semantics — TalkBack announces
            // it as a link, and the visible label already states the destination, so the two
            // requirements (announced as a link, destination described) are met together.
            Text(
                text = buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            url = linkUrl,
                            styles = TextLinkStyles(
                                style = SpanStyle(textDecoration = TextDecoration.Underline)
                            ),
                        )
                    ) {
                        append(linkLabel)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CategoryRow(category: TopicCategory, onClick: () -> Unit) {
    val title = stringResource(category.titleRes)
    val topicCount = TopicRegistry.byCategory(category).size
    val itemDesc = if (topicCount > 0) {
        pluralStringResource(
            R.plurals.category_list_item_desc_with_topics,
            topicCount,
            title,
            topicCount
        )
    } else {
        stringResource(R.string.category_list_item_desc_empty, title)
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            // WHY: clearAndSetSemantics replaces the auto-derived semantics (title text plus
            // separate count text) with one sentence stating the category and whether it has
            // topics yet, so TalkBack announces a single complete description per row.
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (topicCount > 0) {
                        pluralStringResource(R.plurals.category_topic_count, topicCount, topicCount)
                    } else {
                        stringResource(R.string.category_empty)
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(onNavigateToCategory = {})
    }
}
