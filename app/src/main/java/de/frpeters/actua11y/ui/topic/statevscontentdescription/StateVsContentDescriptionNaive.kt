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

package de.frpeters.actua11y.ui.topic.statevscontentdescription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same "Show details" disclosure as [StateVsContentDescriptionBetter], same
 * layout, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun StateVsContentDescriptionNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.state_vs_content_description_pane_title)
    var expanded by remember { mutableStateOf(false) }
    val label = stringResource(R.string.state_vs_content_description_toggle_label)
    val stateWord = if (expanded) {
        stringResource(R.string.state_vs_content_description_state_expanded)
    } else {
        stringResource(R.string.state_vs_content_description_state_collapsed)
    }
    // NAIVE: the changeable half of the announcement is folded into the same string as the
    // control's stable name, rebuilt on every recomposition.
    val bakedDescription = stringResource(
        R.string.state_vs_content_description_naive_content_description,
        label,
        stateWord,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.state_vs_content_description_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.state_vs_content_description_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Meeting location ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.state_vs_content_description_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: contentDescription is rebuilt as "$label, $stateWord" every time expanded
        // changes, folding the changeable half of the announcement into the same string as the
        // control's stable name. TalkBack hears an entirely new name for the control on toggle,
        // not the same control changing state — and there is no StateDescription property at all
        // for it to read separately.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .semantics(mergeDescendants = true) {
                    contentDescription = bakedDescription
                    role = Role.Button
                }
                .testTag("state_vs_content_description_toggle"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = if (expanded) "▲" else "▼")
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = stringResource(R.string.state_vs_content_description_details_text),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalDivider()

        DeveloperNote(
            body = stringResource(R.string.state_vs_content_description_developer_note_naive),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun StateVsContentDescriptionNaivePreview() {
    MaterialTheme {
        StateVsContentDescriptionNaive()
    }
}
