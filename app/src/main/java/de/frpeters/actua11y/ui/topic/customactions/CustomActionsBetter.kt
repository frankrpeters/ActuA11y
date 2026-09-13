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

package de.frpeters.actua11y.ui.topic.customactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote
import kotlinx.coroutines.delay

/**
 * The sole implementation for this topic — see [CustomActionsTopic] for why there is no Naive
 * counterpart.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomActionsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.custom_actions_pane_title)
    val archiveLabel = stringResource(R.string.custom_actions_action_archive)
    val deleteLabel = stringResource(R.string.custom_actions_action_delete)
    var lastAction by remember { mutableStateOf<String?>(null) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> lastAction = archiveLabel
                SwipeToDismissBoxValue.EndToStart -> lastAction = deleteLabel
                SwipeToDismissBoxValue.Settled -> Unit
            }
            true
        },
    )

    // WHY: SwipeToDismissBox locks its own gesture once currentValue leaves Settled (see its
    // enabled = gesturesEnabled && currentValue == Settled). Resetting shortly after lets the row
    // be swiped again, so this single-item demo stays repeatable rather than a one-shot gesture.
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            delay(400)
            dismissState.reset()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.custom_actions_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        // WHY: requirements §4.5 requires a short, visible explanation of why this topic has no
        // naive counterpart, not just a disabled toggle — a naive version would just be a screen
        // with nothing here, which teaches nothing by comparison.
        Text(
            text = stringResource(R.string.custom_actions_no_naive_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.custom_actions_no_naive_explanation),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.custom_actions_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Inbox ──────────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.custom_actions_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: Archive and Delete are only reachable by swiping this row — there is no
        // persistent button for either. Modifier.semantics { customActions = ... } gives a
        // keyboard or TalkBack user a local-context-menu equivalent for the same two actions, so
        // reaching them never depends on performing the swipe gesture at all.
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF43A047)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                    SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceVariant
                }
                Box(modifier = Modifier.fillMaxSize().background(color))
            },
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_actions_message_row")
                    .semantics {
                        customActions = listOf(
                            CustomAccessibilityAction(archiveLabel) {
                                lastAction = archiveLabel
                                true
                            },
                            CustomAccessibilityAction(deleteLabel) {
                                lastAction = deleteLabel
                                true
                            },
                        )
                    },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.custom_actions_sender),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.custom_actions_preview),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        Text(
            text = lastAction?.let { stringResource(R.string.custom_actions_last_action_format, it) }
                ?: stringResource(R.string.custom_actions_last_action_none),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("custom_actions_last_action"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.custom_actions_developer_note))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun CustomActionsBetterPreview() {
    MaterialTheme {
        CustomActionsBetter()
    }
}
