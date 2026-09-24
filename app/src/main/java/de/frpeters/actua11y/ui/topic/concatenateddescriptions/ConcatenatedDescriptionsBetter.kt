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

package de.frpeters.actua11y.ui.topic.concatenateddescriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same order card as [ConcatenatedDescriptionsNaive], same visible lines,
 * same single TalkBack stop — the only difference is the separator the description is joined with.
 */
@Composable
fun ConcatenatedDescriptionsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.concatenated_descriptions_pane_title)
    val segments = listOf(
        stringResource(R.string.concatenated_descriptions_order_number),
        stringResource(R.string.concatenated_descriptions_status),
        pluralStringResource(R.plurals.concatenated_descriptions_item_count, ITEM_COUNT, ITEM_COUNT),
        stringResource(R.string.concatenated_descriptions_total),
        stringResource(R.string.concatenated_descriptions_delivery_date),
    )
    // BETTER: each segment is its own line of the description. TalkBack treats a line break
    // inside a contentDescription as a pause, so the listener hears five short statements rather
    // than one run-on sentence.
    // TODO(verify): confirm on a real device that TalkBack pauses at each \n, and whether the
    // pause is audibly longer than a comma's — the claim comes from requirements §3.9 and has not
    // been checked in this project yet.
    val cardDescription = segments.joinToString("\n")

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.concatenated_descriptions_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.concatenated_descriptions_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Latest order ──────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.concatenated_descriptions_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // WHY: clearAndSetSemantics replaces the five child Text nodes with one description, so
        // the whole card is a single TalkBack stop. That part is identical in both versions —
        // it is a reasonable choice for a summary card. What differs is only how the
        // description string itself was assembled, above.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("concatenated_descriptions_card")
                .clearAndSetSemantics { contentDescription = cardDescription },
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = segments[0], style = MaterialTheme.typography.titleMedium)
                segments.drop(1).forEach { segment ->
                    Text(text = segment, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.concatenated_descriptions_developer_note_better))
    }
}

private const val ITEM_COUNT = 3

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ConcatenatedDescriptionsBetterPreview() {
    MaterialTheme {
        ConcatenatedDescriptionsBetter()
    }
}
