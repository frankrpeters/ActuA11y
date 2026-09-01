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

package de.frpeters.actua11y.ui.topic.switchplatformvscustom

import androidx.compose.foundation.clickable
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
 * Naive implementation. Same Canvas-drawn switch track as [SwitchPlatformVsCustomBetter], same
 * layout, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun SwitchPlatformVsCustomNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.switch_platform_vs_custom_pane_title)
    var dndEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.switch_platform_vs_custom_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.switch_platform_vs_custom_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Do Not Disturb ────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.switch_platform_vs_custom_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.switch_platform_vs_custom_caption),
            style = MaterialTheme.typography.bodyMedium,
        )
        // NAIVE: Modifier.clickable does register a real click action here — Clickable.kt's
        // AbstractClickableNode.applySemantics() adds SemanticsActions.OnClick unconditionally,
        // regardless of what was drawn — so TalkBack does stop on this control. Nothing supplies
        // Role.Switch, a ToggleableState, or a label, though: none of that is inferred from a
        // Canvas draw call, and there is no Text composable in this subtree for a merge to pick
        // up. The result is a control TalkBack finds and can activate, but can say nothing about.
        CustomSwitchTrack(
            checked = dndEnabled,
            modifier = Modifier
                .clickable { dndEnabled = !dndEnabled }
                .testTag("switch_platform_vs_custom_toggle"),
        )

        HorizontalDivider()

        DeveloperNote(
            body = stringResource(R.string.switch_platform_vs_custom_developer_note_naive),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun SwitchPlatformVsCustomNaivePreview() {
    MaterialTheme {
        SwitchPlatformVsCustomNaive()
    }
}
