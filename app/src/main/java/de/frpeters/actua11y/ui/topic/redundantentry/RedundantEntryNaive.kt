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

package de.frpeters.actua11y.ui.topic.redundantentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * Naive implementation. Same two-step checkout, same fields, same buttons as
 * [RedundantEntryBetter]. Each step was built as its own form, and step 2 simply starts empty.
 */
@Composable
fun RedundantEntryNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.redundant_entry_pane_title)
    // WHY: both steps' values are hoisted to this level, above the step switch, in both versions.
    // The data needed to avoid asking twice is therefore available in the Naive version too —
    // redundant entry is rarely a missing-data problem, only a missing-connection one.
    // WHY: step 1 starts with sample values, so a reader can go straight to step 2.
    val sampleDelivery = AddressFormState(
        name = stringResource(R.string.redundant_entry_sample_name),
        street = stringResource(R.string.redundant_entry_sample_street),
        city = stringResource(R.string.redundant_entry_sample_city),
    )
    var step by remember { mutableIntStateOf(1) }
    var delivery by remember { mutableStateOf(sampleDelivery) }
    var billing by remember { mutableStateOf(AddressFormState()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.redundant_entry_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.redundant_entry_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Checkout ──────────────────────────────────────────────────────────────
        // WHY: focus is left wherever it was when the step changes, identically in both versions.
        // Moving it to the new step's heading is its own topic (Topic 5, Focus After Navigation)
        // and would blur what this one compares.
        if (step == 1) {
            Text(
                text = stringResource(R.string.redundant_entry_step1_heading),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            AddressFields(
                state = delivery,
                onChange = { delivery = it },
                tagPrefix = "redundant_entry_delivery",
            )
            Button(
                    onClick = {
                        // NAIVE: step 2 starts blank. The delivery address is still held in state a
                        // few lines above, but nothing carries it forward, so the user has to
                        // supply the same three values a second time.
                        step = 2
                    },
                modifier = Modifier.testTag("redundant_entry_continue"),
            ) {
                Text(text = stringResource(R.string.redundant_entry_continue))
            }
        } else {
            Text(
                text = stringResource(R.string.redundant_entry_step2_heading),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            AddressFields(
                state = billing,
                onChange = { billing = it },
                tagPrefix = "redundant_entry_billing",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { step = 1 }) {
                    Text(text = stringResource(R.string.redundant_entry_back))
                }
                Button(onClick = { /* demonstration only */ }) {
                    Text(text = stringResource(R.string.redundant_entry_place_order))
                }
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.redundant_entry_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun RedundantEntryNaivePreview() {
    MaterialTheme {
        RedundantEntryNaive()
    }
}
