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

package de.frpeters.actua11y.ui.topic.keyboardfocusindicator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same chip row as [KeyboardFocusIndicatorBetter], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun KeyboardFocusIndicatorNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.keyboard_focus_indicator_pane_title)
    var lastAction by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.keyboard_focus_indicator_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.keyboard_focus_indicator_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Message actions ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.keyboard_focus_indicator_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipLabelResIds.forEachIndexed { index, labelRes ->
                val label = stringResource(labelRes)
                // NAIVE: Modifier.clickable is focusable, but draws no visible focus indicator of
                // its own by default. Tabbing here with an external keyboard moves focus, but
                // nothing on screen shows where it went.
                Text(
                    text = label,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(16.dp),
                        )
                        .clickable(role = Role.Button) { lastAction = label }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("keyboard_focus_indicator_chip_$index"),
                )
            }
        }
        Text(
            text = lastAction?.let { stringResource(R.string.keyboard_focus_indicator_last_action_format, it) }
                ?: stringResource(R.string.keyboard_focus_indicator_last_action_none),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.keyboard_focus_indicator_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun KeyboardFocusIndicatorNaivePreview() {
    MaterialTheme {
        KeyboardFocusIndicatorNaive()
    }
}
