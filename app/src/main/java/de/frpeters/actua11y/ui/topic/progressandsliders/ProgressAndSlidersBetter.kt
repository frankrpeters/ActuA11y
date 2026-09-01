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

package de.frpeters.actua11y.ui.topic.progressandsliders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same upload progress bar as [ProgressAndSlidersNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun ProgressAndSlidersBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.progress_and_sliders_pane_title)
    val barLabel = stringResource(R.string.progress_and_sliders_bar_content_description)
    var progressPercent by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.progress_and_sliders_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.progress_and_sliders_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Upload ─────────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.progress_and_sliders_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: same two-Box bar as Naive — progressBarRangeInfo is what makes an accessibility
        // service treat this as a progress indicator at all, reporting the current value against
        // its range; contentDescription supplies the name the announcement is attached to, since
        // there is no Text composable here for a merge to pick up.
        UploadProgressBar(
            progressPercent = progressPercent,
            modifier = Modifier
                .semantics {
                    contentDescription = barLabel
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = progressPercent.toFloat(),
                        range = 0f..100f,
                    )
                }
                .testTag("progress_and_sliders_bar"),
        )
        Button(
            onClick = {
                progressPercent = (progressPercent + PROGRESS_STEP_PERCENT).coerceAtMost(100)
            },
            modifier = Modifier.testTag("progress_and_sliders_advance_button"),
        ) {
            Text(text = stringResource(R.string.progress_and_sliders_advance_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.progress_and_sliders_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ProgressAndSlidersBetterPreview() {
    MaterialTheme {
        ProgressAndSlidersBetter()
    }
}
