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

package de.frpeters.actua11y.ui.topic.disabledelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same checkbox-gated Submit button as [DisabledElementsNaive], same
 * layout, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun DisabledElementsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.disabled_elements_pane_title)
    var agreed by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

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
            text = stringResource(R.string.disabled_elements_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.disabled_elements_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Terms agreement ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.disabled_elements_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // Reuses the toggleable + mergeDescendants + Checkbox(onCheckedChange = null) pattern
        // already established by the Composite Controls topic — not this topic's subject.
        Row(
            modifier = Modifier
                .toggleable(
                    value = agreed,
                    onValueChange = { agreed = it; submitted = false },
                    role = Role.Checkbox,
                )
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = agreed, onCheckedChange = null)
            Text(text = stringResource(R.string.disabled_elements_agree_label))
        }

        // BETTER: always the same Modifier.clickable(enabled = agreed, role = Role.Button) —
        // agreed is a value passed to the modifier, never a reason to omit it. Reading
        // Clickable.kt's AbstractClickableNode.applySemantics() shows this registers onClick
        // unconditionally and only calls disabled() afterwards when enabled is false, so a
        // disabled button still carries its role and its (inert) action, not neither.
        Box(
            modifier = Modifier
                .clickable(enabled = agreed, role = Role.Button) { submitted = true }
                .background(
                    color = if (agreed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    },
                    shape = RoundedCornerShape(8.dp),
                )
                .testTag("disabled_elements_submit"),
        ) {
            Text(
                text = stringResource(R.string.disabled_elements_submit_label),
                color = if (agreed) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
        if (submitted) {
            Text(text = stringResource(R.string.disabled_elements_submitted_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.disabled_elements_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun DisabledElementsBetterPreview() {
    MaterialTheme {
        DisabledElementsBetter()
    }
}
