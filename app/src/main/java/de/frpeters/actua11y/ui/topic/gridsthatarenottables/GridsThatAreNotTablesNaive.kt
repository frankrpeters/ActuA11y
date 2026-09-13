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

package de.frpeters.actua11y.ui.topic.gridsthatarenottables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private val TileColors = listOf(
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
)

/**
 * Naive implementation. Same 12-photo grid as [GridsThatAreNotTablesBetter], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun GridsThatAreNotTablesNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.grids_that_are_not_tables_pane_title)

    // NAIVE: no collectionInfo override and no per-item collectionItemInfo. LazyVerticalGrid
    // still attaches its own CollectionInfo automatically — but that default is unconditionally
    // CollectionInfo(rowCount = -1, columnCount = -1), both dimensions "unknown," regardless of
    // this grid's real, fixed 12-item, 3-column layout (confirmed by reading LazySemantics.kt and
    // by an instrumented test reading this exact node back).
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .padding(16.dp)
            .testTag("grids_that_are_not_tables_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = FullWidthSpan) {
            Text(
                text = stringResource(R.string.grids_that_are_not_tables_intro),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item(span = FullWidthSpan) {
            Text(
                text = stringResource(R.string.grids_that_are_not_tables_what_to_try),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item(span = FullWidthSpan) { HorizontalDivider() }
        item(span = FullWidthSpan) {
            Text(
                text = stringResource(R.string.grids_that_are_not_tables_section_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
        }

        items(count = PHOTO_COUNT) { index ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(TileColors[index % TileColors.size], RoundedCornerShape(8.dp))
                    .testTag("grids_that_are_not_tables_item_$index"),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }
        }

        item(span = FullWidthSpan) { HorizontalDivider() }
        item(span = FullWidthSpan) {
            DeveloperNote(body = stringResource(R.string.grids_that_are_not_tables_developer_note_naive))
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun GridsThatAreNotTablesNaivePreview() {
    MaterialTheme {
        GridsThatAreNotTablesNaive()
    }
}
