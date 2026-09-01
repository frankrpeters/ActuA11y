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

package de.frpeters.actua11y.ui.topic.liveregions

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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same flight status refresh as [LiveRegionsBetter], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun LiveRegionsNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.live_regions_pane_title)
    var statusIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.live_regions_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.live_regions_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Flight Status ──────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.live_regions_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: this Text updates the same way visually on every refresh, but carries no
        // liveRegion at all. TalkBack has no reason to look at a node it is not currently focused
        // on, so the change is silent unless the user happens to swipe back onto it afterwards.
        Text(
            text = stringResource(FlightStatusResIds[statusIndex]),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("live_regions_status"),
        )
        Button(
            onClick = { statusIndex = (statusIndex + 1) % FlightStatusResIds.size },
            modifier = Modifier.testTag("live_regions_refresh_button"),
        ) {
            Text(text = stringResource(R.string.live_regions_refresh_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.live_regions_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun LiveRegionsNaivePreview() {
    MaterialTheme {
        LiveRegionsNaive()
    }
}
