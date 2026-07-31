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
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same header layout, same visual order, same behaviour for a sighted
 * touch user as [TraversalIndexBetter] — the traversalIndex values below were set, but the
 * enclosing group that would make them work was not.
 */
@Composable
fun TraversalIndexNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.traversal_index_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
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
        // NAIVE: no isTraversalGroup on this Row. The traversalIndex values below are set
        // exactly as in the Better version, but traversalIndex only reorders siblings within
        // their nearest enclosing traversal group — without one, it is silently ignored and
        // TalkBack falls back to raw left-to-right order. The code looks correct; it isn't.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("traversal_index_header"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { /* demonstration only */ },
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
                modifier = Modifier.semantics { traversalIndex = 0f },
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.traversal_index_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun TraversalIndexNaivePreview() {
    MaterialTheme {
        TraversalIndexNaive()
    }
}
