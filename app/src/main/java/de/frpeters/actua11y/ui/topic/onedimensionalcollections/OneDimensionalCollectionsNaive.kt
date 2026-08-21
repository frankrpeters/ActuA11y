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

package de.frpeters.actua11y.ui.topic.onedimensionalcollections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private const val ITEM_COUNT = 24

/**
 * Naive implementation. Same 24-item list as [OneDimensionalCollectionsBetter] — the
 * accessibility work was simply never done.
 */
@Composable
fun OneDimensionalCollectionsNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.one_dimensional_collections_pane_title)

    // NAIVE: no collectionInfo override and no per-item collectionItemInfo. LazyColumn still
    // attaches its own CollectionInfo automatically — but that default is always
    // CollectionInfo(rowCount = -1, columnCount = 1), meaning "unknown", regardless of the real,
    // fixed count of 24 (confirmed by reading LazyLayoutSemanticState.kt). Nothing here is
    // missing outright; it is confidently wrong in a way that never crashes and looks fine.
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .testTag("one_dimensional_collections_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.one_dimensional_collections_intro),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item {
            Text(
                text = stringResource(R.string.one_dimensional_collections_what_to_try),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.one_dimensional_collections_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
        }

        items(count = ITEM_COUNT) { index ->
            Card(
                onClick = { /* demonstration only */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("one_dimensional_collections_item_$index"),
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.width(32.dp),
                    )
                    Text(
                        text = stringResource(
                            R.string.one_dimensional_collections_item_title,
                            index + 1,
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        item { HorizontalDivider() }
        item {
            DeveloperNote(
                body = stringResource(R.string.one_dimensional_collections_developer_note_naive),
            )
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun OneDimensionalCollectionsNaivePreview() {
    MaterialTheme {
        OneDimensionalCollectionsNaive()
    }
}
