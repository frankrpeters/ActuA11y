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

package de.frpeters.actua11y.ui.topic.headings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * Better implementation. Same three sections as [HeadingsNaive], same styling, same behaviour
 * for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun HeadingsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.headings_pane_title)

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
            text = stringResource(R.string.headings_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.headings_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Account ────────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.headings_section_1_title),
            style = MaterialTheme.typography.titleMedium,
            // BETTER: marks this title so TalkBack's "Headings" reading control can stop on
            // it, letting a user jump straight here instead of swiping through every line.
            modifier = Modifier
                .testTag("headings_section_1_title")
                .semantics { heading() },
        )
        Text(text = stringResource(R.string.headings_section_1_body))

        HorizontalDivider()

        // ── Notifications ─────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.headings_section_2_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .testTag("headings_section_2_title")
                .semantics { heading() },
        )
        Text(text = stringResource(R.string.headings_section_2_body))

        HorizontalDivider()

        // ── Privacy ────────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.headings_section_3_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .testTag("headings_section_3_title")
                .semantics { heading() },
        )
        Text(text = stringResource(R.string.headings_section_3_body))

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.headings_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun HeadingsBetterPreview() {
    MaterialTheme {
        HeadingsBetter()
    }
}
