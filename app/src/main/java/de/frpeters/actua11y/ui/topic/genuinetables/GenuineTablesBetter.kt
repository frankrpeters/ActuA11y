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

package de.frpeters.actua11y.ui.topic.genuinetables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private const val COLUMN_COUNT = 3

/**
 * Better implementation. Same three-column inventory table as [GenuineTablesNaive], same
 * layout, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun GenuineTablesBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.genuine_tables_pane_title)
    val headers = listOf(
        stringResource(R.string.genuine_tables_header_product),
        stringResource(R.string.genuine_tables_header_price),
        stringResource(R.string.genuine_tables_header_stock),
    )
    val names = stringArrayResource(R.array.genuine_tables_product_names)
    val prices = stringArrayResource(R.array.genuine_tables_product_prices)
    val stock = stringArrayResource(R.array.genuine_tables_product_stock)
    val rowCount = names.size + 1

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
            text = stringResource(R.string.genuine_tables_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.genuine_tables_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Inventory table ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.genuine_tables_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: Compose has no table composable, so this grid is built by hand from Rows of
        // Text cells. A plain Column supplies no CollectionInfo of its own (unlike LazyColumn —
        // see the One-Dimensional Collections topic), so this has to be added from scratch: the
        // real rowCount/columnCount here, plus a CollectionItemInfo on every one of the 18
        // cells. Without the per-cell info, TalkBack has no way to relate "$9.99" back to the
        // row it came from.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("genuine_tables_table")
                .semantics {
                    collectionInfo = CollectionInfo(rowCount = rowCount, columnCount = COLUMN_COUNT)
                },
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                headers.forEachIndexed { column, label ->
                    TableCell(
                        text = label,
                        bold = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("genuine_tables_cell_0_$column")
                            .semantics {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = 0,
                                    rowSpan = 1,
                                    columnIndex = column,
                                    columnSpan = 1,
                                )
                            },
                    )
                }
            }
            names.forEachIndexed { i, name ->
                val row = i + 1
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(name, prices[i], stock[i]).forEachIndexed { column, text ->
                        TableCell(
                            text = text,
                            bold = false,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("genuine_tables_cell_${row}_$column")
                                .semantics {
                                    collectionItemInfo = CollectionItemInfo(
                                        rowIndex = row,
                                        rowSpan = 1,
                                        columnIndex = column,
                                        columnSpan = 1,
                                    )
                                },
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.genuine_tables_developer_note_better))
    }
}

@Composable
private fun TableCell(text: String, bold: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (bold) FontWeight.Bold else null,
        modifier = modifier.padding(8.dp),
    )
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun GenuineTablesBetterPreview() {
    MaterialTheme {
        GenuineTablesBetter()
    }
}
