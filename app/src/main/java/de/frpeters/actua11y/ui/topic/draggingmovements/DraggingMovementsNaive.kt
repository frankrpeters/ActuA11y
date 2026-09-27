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

package de.frpeters.actua11y.ui.topic.draggingmovements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

/**
 * Naive implementation. Same packing list, same rows, same long-press-and-drag reordering as
 * [DraggingMovementsBetter]. Dragging is the only way the order can be changed.
 */
@Composable
fun DraggingMovementsNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.dragging_movements_pane_title)
    val state = remember { ReorderState(PackingItemResIds) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.dragging_movements_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.dragging_movements_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Packing list ──────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.dragging_movements_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Column {
            state.items.forEachIndexed { index, item ->
                // WHY: keyed by item, so a row keeps its identity (and its drag gesture) while
                // it moves through the list.
                key(item) {
                    // NAIVE: dragging is the only way to change the order. There is no button, no
                    // custom action, nothing a keyboard, switch, or TalkBack user can reach.
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .dragToReorder(state, item)
                            .testTag("dragging_movements_row_$index"),
                    ) {
                        Row(
                            modifier = Modifier
                                .heightIn(min = 56.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Decorative grip, the same in both versions.
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                            Text(
                                text = stringResource(item),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.dragging_movements_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun DraggingMovementsNaivePreview() {
    MaterialTheme {
        DraggingMovementsNaive()
    }
}
