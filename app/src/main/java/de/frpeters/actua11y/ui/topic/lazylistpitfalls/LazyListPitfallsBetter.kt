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

package de.frpeters.actua11y.ui.topic.lazylistpitfalls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
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

/**
 * Better implementation. Same alphabetically-grouped, sticky-header contacts list as
 * [LazyListPitfallsNaive], same layout, same behaviour for a sighted touch user — the only
 * difference is accessibility semantics.
 */
@Composable
fun LazyListPitfallsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.lazy_list_pitfalls_pane_title)
    val grouped = stringArrayResource(R.array.lazy_list_pitfalls_contact_names)
        .toList()
        .groupBy { it.first() }
    // WHY: precomputed once, before the LazyListScope body runs below, rather than derived from
    // the index parameter items() hands to each block — see the developer note for why that
    // parameter cannot be trusted as a whole-list position once a list is built from more than
    // one stickyHeader()/items() pair.
    val orderedContacts = grouped.values.flatten()
    val positionOf = orderedContacts.withIndex().associate { (i, name) -> name to i }
    val contactCount = orderedContacts.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival, since content
            // swaps below the persistent app bar don't trigger a full navigation announcement.
            .semantics { paneTitle = paneTitleStr }
            // BETTER: the real contact count only — headers are not rows in this collection.
            .semantics { collectionInfo = CollectionInfo(rowCount = contactCount, columnCount = 1) }
            .testTag("lazy_list_pitfalls_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.lazy_list_pitfalls_intro),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item {
            Text(
                text = stringResource(R.string.lazy_list_pitfalls_what_to_try),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.lazy_list_pitfalls_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
        }

        grouped.forEach { (letter, names) ->
            // BETTER: heading() lets TalkBack jump letter-to-letter, but deliberately no
            // CollectionItemInfo — this Compose version's CollectionItemInfo has no header
            // flag (see the Genuine Tables topic), so a header simply isn't a row here.
            stickyHeader(key = letter) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lazy_list_pitfalls_header_$letter"),
                ) {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(12.dp)
                            .semantics { heading() },
                    )
                }
            }
            items(names, key = { it }) { name ->
                // BETTER: rowIndex comes from the precomputed content-only position, not from
                // this items() block's own local index parameter — see the developer note.
                ContactRow(
                    name = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lazy_list_pitfalls_contact_${positionOf.getValue(name)}")
                        .semantics(mergeDescendants = true) {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = positionOf.getValue(name),
                                rowSpan = 1,
                                columnIndex = 0,
                                columnSpan = 1,
                            )
                        },
                )
            }
        }

        item { HorizontalDivider() }
        item {
            DeveloperNote(body = stringResource(R.string.lazy_list_pitfalls_developer_note_better))
        }
    }
}

@Composable
private fun ContactRow(name: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun LazyListPitfallsBetterPreview() {
    MaterialTheme {
        LazyListPitfallsBetter()
    }
}
