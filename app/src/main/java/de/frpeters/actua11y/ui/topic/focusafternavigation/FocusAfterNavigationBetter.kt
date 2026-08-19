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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalWindowInfo
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
import kotlinx.coroutines.flow.first

/**
 * Better implementation. Same picker as [FocusAfterNavigationNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun FocusAfterNavigationBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.focus_after_navigation_pane_title)
    var showDialog by remember { mutableStateOf(false) }
    var selectedFrequency by remember { mutableStateOf(FocusAfterNavigationFrequency.DAILY) }
    var returnFocusToTrigger by remember { mutableStateOf(false) }
    val triggerFocusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current
    val inputModeManager = LocalInputModeManager.current

    fun closeDialog() {
        showDialog = false
        returnFocusToTrigger = true
    }

    // BETTER: hands focus back to the button that opened the picker, once the picker closes —
    // otherwise Compose's focus system has nothing telling it where to go next once the
    // dialog's content disappears. Two things have to happen first, neither of which is
    // optional, both confirmed by instrumented testing on a real device (Pixel 9 Pro, API 37,
    // Compose BOM 2025.05.00) rather than assumed from reading the API alone:
    //
    // 1. Wait for LocalWindowInfo.current.isWindowFocused. AlertDialog opens a separate
    //    platform Window, which holds real platform window focus while shown; dismissing it
    //    hands that focus back to the Activity's window asynchronously. Requesting focus
    //    before that handover completes silently fails.
    // 2. Request keyboard input mode via InputModeManager.requestInputMode(). Button's click
    //    target uses Focusability.SystemDefined internally, which refuses requestFocus()
    //    outright whenever the platform's current InputMode is Touch — true on any touchscreen
    //    device by default, and TalkBack's touch-exploration gestures do not change it, so this
    //    is not merely a test-harness quirk. requestInputMode(Keyboard) calls the real
    //    View.requestFocusFromTouch(), which is the documented, public way to override this.
    LaunchedEffect(returnFocusToTrigger) {
        if (returnFocusToTrigger) {
            snapshotFlow { windowInfo.isWindowFocused }.first { it }
            inputModeManager.requestInputMode(InputMode.Keyboard)
            triggerFocusRequester.requestFocus()
            returnFocusToTrigger = false
        }
    }

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
            modifier = Modifier.semantics { heading() },
        )
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .testTag("focus_after_navigation_trigger_button")
                .focusRequester(triggerFocusRequester),
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

        DeveloperNote(body = stringResource(R.string.focus_after_navigation_developer_note_better))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
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
                                        closeDialog()
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
                TextButton(onClick = { closeDialog() }) {
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
private fun FocusAfterNavigationBetterPreview() {
    MaterialTheme {
        FocusAfterNavigationBetter()
    }
}
