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
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private const val ITEM_COUNT = 24

/**
 * Better implementation. Same 24-item list as [OneDimensionalCollectionsNaive], same layout,
 * same behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun OneDimensionalCollectionsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.one_dimensional_collections_pane_title)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival, since content
            // swaps below the persistent app bar don't trigger a full navigation announcement.
            .semantics { paneTitle = paneTitleStr }
            // BETTER: LazyColumn attaches CollectionInfo to itself automatically, but always as
            // CollectionInfo(rowCount = -1, columnCount = 1) — "unknown" — regardless of whether
            // the real count is known (confirmed by reading LazyLayoutSemanticState.kt). This
            // list is 24 fixed items; the count is never in doubt, so it is overridden here with
            // the real value. Confirmed by instrumented test on a real device that this override
            // wins over LazyColumn's own internal default, not just assumed from the API.
            .semantics { collectionInfo = CollectionInfo(rowCount = ITEM_COUNT, columnCount = 1) }
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

        // BETTER: each item also carries its own CollectionItemInfo. No Lazy layout supplies
        // this automatically for any item, ever — confirmed by searching Compose Foundation's
        // own source for every place it sets collectionItemInfo, which is nowhere. A correct
        // container count does nothing for "item 8 of 24" without this on every item too.
        items(count = ITEM_COUNT) { index ->
            Card(
                onClick = { /* demonstration only */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("one_dimensional_collections_item_$index")
                    .semantics(mergeDescendants = true) {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = index,
                            rowSpan = 1,
                            columnIndex = 0,
                            columnSpan = 1,
                        )
                    },
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
                body = stringResource(R.string.one_dimensional_collections_developer_note_better),
            )
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun OneDimensionalCollectionsBetterPreview() {
    MaterialTheme {
        OneDimensionalCollectionsBetter()
    }
}
