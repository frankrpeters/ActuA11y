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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same three-column inventory table as [GenuineTablesBetter] — the
 * accessibility work was simply never done.
 */
@Composable
fun GenuineTablesNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.genuine_tables_pane_title)
    val headers = listOf(
        stringResource(R.string.genuine_tables_header_product),
        stringResource(R.string.genuine_tables_header_price),
        stringResource(R.string.genuine_tables_header_stock),
    )
    val names = stringArrayResource(R.array.genuine_tables_product_names)
    val prices = stringArrayResource(R.array.genuine_tables_product_prices)
    val stock = stringArrayResource(R.array.genuine_tables_product_stock)

    Column(
        modifier = modifier
            .fillMaxSize()
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
        // NAIVE: the same hand-built grid of Rows and Text cells, with no CollectionInfo and no
        // CollectionItemInfo anywhere. A plain Column supplies nothing on its own, so this is
        // genuine silence — TalkBack swipes through 18 unconnected text nodes with no signal
        // that any of them share a row.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("genuine_tables_table"),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                headers.forEachIndexed { column, label ->
                    TableCell(
                        text = label,
                        bold = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("genuine_tables_cell_0_$column"),
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
                                .testTag("genuine_tables_cell_${row}_$column"),
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.genuine_tables_developer_note_naive))
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
private fun GenuineTablesNaivePreview() {
    MaterialTheme {
        GenuineTablesNaive()
    }
}
