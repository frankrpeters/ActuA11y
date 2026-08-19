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

package de.frpeters.actua11y.ui.topic.focusafternavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same picker, same layout, same behaviour for a sighted touch user as
 * [FocusAfterNavigationBetter] — the accessibility work was simply never done.
 */
@Composable
fun FocusAfterNavigationNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.focus_after_navigation_pane_title)
    var showDialog by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf(FocusAfterNavigationFrequency.DAILY) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.focus_after_navigation_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.focus_after_navigation_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Notification frequency picker ─────────────────────────────────────────
        Text(
            text = stringResource(R.string.focus_after_navigation_section_label),
            style = MaterialTheme.typography.titleMedium,
        )
        // NAIVE: no FocusRequester, no LaunchedEffect — once the dialog below closes, nothing
        // tells Compose's focus system where to go next.
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.testTag("focus_after_navigation_trigger_button"),
        ) {
            Text(text = stringResource(R.string.focus_after_navigation_trigger_label))
        }
        Text(
            text = stringResource(
                R.string.focus_after_navigation_current_value,
                stringResource(selectedFrequency.labelRes),
            ),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.focus_after_navigation_developer_note_naive))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.focus_after_navigation_dialog_title)) },
            text = {
                Column {
                    FocusAfterNavigationFrequency.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = option == selectedFrequency,
                                    onClick = {
                                        selectedFrequency = option
                                        showDialog = false
                                    },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = option == selectedFrequency, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(option.labelRes))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.focus_after_navigation_dialog_close))
                }
            },
        )
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun FocusAfterNavigationNaivePreview() {
    MaterialTheme {
        FocusAfterNavigationNaive()
    }
}
