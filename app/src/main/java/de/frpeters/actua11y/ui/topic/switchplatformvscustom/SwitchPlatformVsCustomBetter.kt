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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same Canvas-drawn switch track as [SwitchPlatformVsCustomNaive], same
 * layout, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun SwitchPlatformVsCustomBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.switch_platform_vs_custom_pane_title)
    val switchLabel = stringResource(R.string.switch_platform_vs_custom_switch_content_description)
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
        // BETTER: same Canvas-drawn track as Naive, same tap-handling family — but
        // Modifier.toggleable(role = Role.Switch) adds exactly what a Canvas draw call cannot
        // supply on its own: Role.Switch, a ToggleableState derived from the current value, and
        // (via contentDescription) the label this control has no Text composable to merge with.
        // Compare Topic 11 (Composite Controls), where a real, unmodified Switch() gets Role and
        // ToggleableState for free from the platform — only the label-merge step is left to add
        // by hand there. Here, replacing the platform composable with a hand-drawn one moves all
        // three responsibilities onto this code, not just the merge.
        CustomSwitchTrack(
            checked = dndEnabled,
            modifier = Modifier
                .toggleable(
                    value = dndEnabled,
                    onValueChange = { dndEnabled = it },
                    role = Role.Switch,
                )
                .semantics { contentDescription = switchLabel }
                .testTag("switch_platform_vs_custom_toggle"),
        )

        HorizontalDivider()

        DeveloperNote(
            body = stringResource(R.string.switch_platform_vs_custom_developer_note_better),
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun SwitchPlatformVsCustomBetterPreview() {
    MaterialTheme {
        SwitchPlatformVsCustomBetter()
    }
}
