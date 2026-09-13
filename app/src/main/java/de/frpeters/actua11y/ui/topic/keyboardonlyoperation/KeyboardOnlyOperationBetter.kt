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

package de.frpeters.actua11y.ui.topic.keyboardonlyoperation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same toolbar as [KeyboardOnlyOperationNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun KeyboardOnlyOperationBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.keyboard_only_operation_pane_title)
    var lastAction by remember { mutableStateOf<String?>(null) }
    val shareLabel = stringResource(R.string.keyboard_only_operation_action_share)
    val bookmarkLabel = stringResource(R.string.keyboard_only_operation_action_bookmark)
    val moreLabel = stringResource(R.string.keyboard_only_operation_action_more)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.keyboard_only_operation_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.keyboard_only_operation_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Article toolbar ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.keyboard_only_operation_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: all three actions use Modifier.clickable — including Bookmark, which Naive
        // built with a bare pointer gesture detector instead. Every action therefore
        // participates in keyboard Tab order, switch-access scanning, and TalkBack's swipe
        // traversal, all at once, since all three read from the same semantics tree.
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = shareLabel,
                modifier = Modifier
                    .clickable(role = Role.Button) { lastAction = shareLabel }
                    .padding(8.dp)
                    .testTag("keyboard_only_operation_share"),
            )
            Text(
                text = bookmarkLabel,
                modifier = Modifier
                    .clickable(role = Role.Button) { lastAction = bookmarkLabel }
                    .padding(8.dp)
                    .testTag("keyboard_only_operation_bookmark"),
            )
            Text(
                text = moreLabel,
                modifier = Modifier
                    .clickable(role = Role.Button) { lastAction = moreLabel }
                    .padding(8.dp)
                    .testTag("keyboard_only_operation_more"),
            )
        }
        Text(
            text = lastAction?.let { stringResource(R.string.keyboard_only_operation_last_action_format, it) }
                ?: stringResource(R.string.keyboard_only_operation_last_action_none),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.keyboard_only_operation_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun KeyboardOnlyOperationBetterPreview() {
    MaterialTheme {
        KeyboardOnlyOperationBetter()
    }
}
