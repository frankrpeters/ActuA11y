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

package de.frpeters.actua11y.ui.topic.panetitles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 * Better implementation. Same view switcher as [PaneTitlesNaive], same layout, same behaviour
 * for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun PaneTitlesBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.pane_titles_pane_title)
    var selectedView by remember { mutableStateOf(PaneTitlesView.SUMMARY) }

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
            text = stringResource(R.string.pane_titles_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.pane_titles_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── View switcher ─────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.pane_titles_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedView == PaneTitlesView.SUMMARY,
                onClick = { selectedView = PaneTitlesView.SUMMARY },
                label = { Text(stringResource(R.string.pane_titles_tab_summary)) },
                modifier = Modifier.testTag("pane_titles_tab_summary"),
            )
            FilterChip(
                selected = selectedView == PaneTitlesView.DETAILS,
                onClick = { selectedView = PaneTitlesView.DETAILS },
                label = { Text(stringResource(R.string.pane_titles_tab_details)) },
                modifier = Modifier.testTag("pane_titles_tab_details"),
            )
        }

        val contentPaneTitleRes = if (selectedView == PaneTitlesView.SUMMARY) {
            R.string.pane_titles_summary_pane_title
        } else {
            R.string.pane_titles_details_pane_title
        }
        val bodyTextRes = if (selectedView == PaneTitlesView.SUMMARY) {
            R.string.pane_titles_summary_body
        } else {
            R.string.pane_titles_details_body
        }
        val contentPaneTitleStr = stringResource(contentPaneTitleRes)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pane_titles_content_pane")
                // BETTER: this pane's title tracks the selected view, so accessibility services
                // can announce the content swap even though nothing navigated — the swap happens
                // entirely within this one composable. See the developer note for why this
                // differs from the screen-level paneTitle above, which only fires once.
                .semantics { paneTitle = contentPaneTitleStr }
                .padding(vertical = 8.dp),
        ) {
            Text(text = stringResource(bodyTextRes))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.pane_titles_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun PaneTitlesBetterPreview() {
    MaterialTheme {
        PaneTitlesBetter()
    }
}
