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

package de.frpeters.actua11y.ui.topic.traversalindex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same header layout as [TraversalIndexNaive], same visual order (the
 * dismiss button still sits to the left of the title), same behaviour for a sighted touch
 * user — the only difference is accessibility semantics.
 */
@Composable
fun TraversalIndexBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.traversal_index_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival, since content
            // swaps below the persistent app bar don't trigger a full navigation announcement.
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.traversal_index_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.traversal_index_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Card header with a dismiss button ─────────────────────────────────────
        Text(
            text = stringResource(R.string.traversal_index_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("traversal_index_header")
                // BETTER: required for the children's traversalIndex to have any effect at
                // all — it only reorders siblings within their nearest enclosing group.
                .semantics { isTraversalGroup = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { /* demonstration only */ },
                // BETTER: sorts after the title despite sitting to its left on screen.
                modifier = Modifier.semantics { traversalIndex = 1f },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.traversal_index_dismiss_desc),
                )
            }
            Text(
                text = stringResource(R.string.traversal_index_card_title),
                style = MaterialTheme.typography.titleSmall,
                // BETTER: 0f is the default; set explicitly so the two indices read as a pair.
                modifier = Modifier.semantics { traversalIndex = 0f },
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.traversal_index_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun TraversalIndexBetterPreview() {
    MaterialTheme {
        TraversalIndexBetter()
    }
}
