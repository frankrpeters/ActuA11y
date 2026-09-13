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

package de.frpeters.actua11y.ui.topic.modalsurfaces

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote
import kotlinx.coroutines.flow.first

/**
 * Better implementation. Same "More options" sheet as [ModalSurfacesNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSurfacesBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.modal_surfaces_pane_title)
    val sheetPaneTitleStr = stringResource(R.string.modal_surfaces_sheet_pane_title)
    var showSheet by remember { mutableStateOf(false) }
    var returnFocusToTrigger by remember { mutableStateOf(false) }
    val triggerFocusRequester = remember { FocusRequester() }
    val firstActionFocusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current
    val inputModeManager = LocalInputModeManager.current

    fun closeSheet() {
        showSheet = false
        returnFocusToTrigger = true
    }

    // BETTER: same two-step fix already established in FocusAfterNavigationBetter.kt — wait for
    // real platform window focus before requesting, then explicitly switch to keyboard input
    // mode, since Button's Focusability.SystemDefined otherwise refuses requestFocus() outright
    // whenever InputMode is Touch (the default on any touchscreen device, TalkBack included).
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
            text = stringResource(R.string.modal_surfaces_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.modal_surfaces_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Post options ──────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.modal_surfaces_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: ModalBottomSheetProperties() at its defaults, same as Naive — never disabled.
        // shouldDismissOnBackPress defaults to true, and the sheet's scrim wires
        // onDismissRequest unconditionally (not even configurable), confirmed by reading
        // ModalBottomSheet.android.kt. This is the one thing neither version of this topic is
        // allowed to be naive about, per requirements §4.6 — see the developer note.
        Button(
            onClick = { showSheet = true },
            modifier = Modifier
                .testTag("modal_surfaces_trigger_button")
                .focusRequester(triggerFocusRequester),
        ) {
            Text(text = stringResource(R.string.modal_surfaces_trigger_label))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.modal_surfaces_developer_note_better))
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { closeSheet() }) {
            // BETTER: requesting focus here, inside the sheet's own content scope, guarantees
            // this LaunchedEffect runs only after that content — including the FocusRequester
            // target below — has actually composed. Doing this in the outer composable instead,
            // keyed on showSheet, races the sheet's own (sub)composition: an earlier attempt
            // requested focus before the target node existed and crashed with "FocusRequester is
            // not initialized," confirmed by instrumented test failure, not assumed. The same
            // window-focus race documented for the return-to-trigger direction still applies once
            // the target exists — ModalBottomSheet hosts its content in its own platform Window,
            // same as AlertDialog — so the same two-step fix is still needed here too.
            LaunchedEffect(Unit) {
                snapshotFlow { windowInfo.isWindowFocused }.first { it }
                inputModeManager.requestInputMode(InputMode.Keyboard)
                firstActionFocusRequester.requestFocus()
            }
            Column(
                modifier = Modifier
                    .testTag("modal_surfaces_sheet_content")
                    // BETTER: announces the sheet's own appearance, since a bottom sheet sliding
                    // into place below the persistent app bar is not a full navigation event any
                    // more than a content swap is (same mechanism as the Pane Titles topic).
                    .semantics { paneTitle = sheetPaneTitleStr },
            ) {
                Text(
                    text = stringResource(R.string.modal_surfaces_action_share),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(firstActionFocusRequester)
                        .clickable { closeSheet() }
                        .padding(16.dp),
                )
                Text(
                    text = stringResource(R.string.modal_surfaces_action_report),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { closeSheet() }
                        .padding(16.dp),
                )
                Text(
                    text = stringResource(R.string.modal_surfaces_action_cancel),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { closeSheet() }
                        .padding(16.dp),
                )
            }
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ModalSurfacesBetterPreview() {
    MaterialTheme {
        ModalSurfacesBetter()
    }
}
