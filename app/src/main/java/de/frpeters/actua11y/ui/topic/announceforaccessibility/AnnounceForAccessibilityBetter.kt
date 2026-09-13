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

package de.frpeters.actua11y.ui.topic.announceforaccessibility

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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private val SyncStatusResIds = listOf(
    R.string.announce_for_accessibility_sync_idle,
    R.string.announce_for_accessibility_sync_in_progress,
    R.string.announce_for_accessibility_sync_done,
)

/**
 * The sole implementation for this topic — see [AnnounceForAccessibilityTopic] for why there is
 * no Naive counterpart. Shows `View.announceForAccessibility` and a live region side by side,
 * updating the same kind of status text two different ways.
 */
@Composable
fun AnnounceForAccessibilityBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.announce_for_accessibility_pane_title)
    val announcedText = stringResource(R.string.announce_for_accessibility_announced_text)
    val view = LocalView.current
    var announceCount by remember { mutableIntStateOf(0) }
    var syncStatusIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.announce_for_accessibility_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.announce_for_accessibility_no_naive_label),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.announce_for_accessibility_no_naive_explanation),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.announce_for_accessibility_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Announcement (discouraged) ────────────────────────────────────────────
        Text(
            text = stringResource(R.string.announce_for_accessibility_announce_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER (of the two, in the sense of "less bad"): View.announceForAccessibility is
        // Google's own discouraged mechanism — it dispatches a raw AccessibilityEvent directly,
        // with no semantics property behind it at all, and is well documented to drop
        // announcements made during screen transitions. LocalView.current gives the host View
        // directly; no AndroidView wrapper is needed to reach it.
        Text(
            text = stringResource(R.string.announce_for_accessibility_announce_status_format, announceCount),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("announce_for_accessibility_announce_status"),
        )
        Button(
            onClick = {
                announceCount += 1
                // WHY: View.announceForAccessibility is itself @Deprecated in the Android SDK,
                // not merely discouraged by convention — using it here is deliberate, to
                // demonstrate the exact mechanism this topic argues against, not an oversight.
                @Suppress("DEPRECATION")
                view.announceForAccessibility(announcedText)
            },
            modifier = Modifier.testTag("announce_for_accessibility_announce_button"),
        ) {
            Text(text = stringResource(R.string.announce_for_accessibility_announce_button_label))
        }

        HorizontalDivider()

        // ── Live region (recommended) ─────────────────────────────────────────────
        Text(
            text = stringResource(R.string.announce_for_accessibility_live_region_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: the same mechanism established in the Live Regions topic — liveRegion =
        // LiveRegionMode.Polite announces this node's content automatically whenever it changes,
        // without a raw event the app has to dispatch itself, and without the transition-drop
        // risk named in requirements §3.4.
        Text(
            text = stringResource(SyncStatusResIds[syncStatusIndex]),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .semantics { liveRegion = LiveRegionMode.Polite }
                .testTag("announce_for_accessibility_live_status"),
        )
        Button(
            onClick = { syncStatusIndex = (syncStatusIndex + 1) % SyncStatusResIds.size },
            modifier = Modifier.testTag("announce_for_accessibility_refresh_button"),
        ) {
            Text(text = stringResource(R.string.announce_for_accessibility_refresh_button_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.announce_for_accessibility_developer_note))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun AnnounceForAccessibilityBetterPreview() {
    MaterialTheme {
        AnnounceForAccessibilityBetter()
    }
}
