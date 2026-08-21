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

package de.frpeters.actua11y.ui.topic.inputasbutton

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.flow.first

private val DEFAULT_DATE_MILLIS: Long =
    LocalDate.of(2026, 3, 14).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

// WHY: DatePickerState.selectedDateMillis is documented as UTC milliseconds for the selected
// calendar day, not local time — formatting in any other zone can shift the displayed date by a
// day near a zone boundary.
private fun formatDate(epochMillis: Long): String {
    val date = Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate()
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.getDefault()))
}

/**
 * Better implementation. Same appointment date field as [InputAsButtonNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputAsButtonBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.input_as_button_pane_title)

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
            text = stringResource(R.string.input_as_button_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.input_as_button_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Appointment date ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.input_as_button_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        DateButtonField(
            label = stringResource(R.string.input_as_button_field_label),
            initialDateMillis = DEFAULT_DATE_MILLIS,
            testTag = "input_as_button_field",
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.input_as_button_developer_note_better))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateButtonField(
    label: String,
    initialDateMillis: Long,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    var selectedMillis by remember { mutableLongStateOf(initialDateMillis) }
    var showDialog by remember { mutableStateOf(false) }
    var returnFocusToField by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current
    val inputModeManager = LocalInputModeManager.current
    val formatted = formatDate(selectedMillis)
    val fieldDescription = stringResource(R.string.input_as_button_field_description, label, formatted)

    fun closeDialog() {
        showDialog = false
        returnFocusToField = true
    }

    // BETTER: same two-step focus-return fix as the Focus After Navigation topic, unchanged —
    // see that topic's developer note and CLAUDE.md's "Compose Focus Behaviour" section for why
    // both steps are required together.
    LaunchedEffect(returnFocusToField) {
        if (returnFocusToField) {
            snapshotFlow { windowInfo.isWindowFocused }.first { it }
            inputModeManager.requestInputMode(InputMode.Keyboard)
            focusRequester.requestFocus()
            returnFocusToField = false
        }
    }

    // BETTER: not a TextField — OutlinedTextFieldDefaults.DecorationBox supplies only the visual
    // shell, with no editable-text core underneath to expose cursor or text-editing actions from.
    // The whole shape is one clickable target carrying Role.Button, with an explicit
    // contentDescription composed as "label, value" — TalkBack appends "button" itself from the
    // role, so the word is never written into the string by hand (see the Content Descriptions
    // topic's "Save button, button" finding for why that would double it).
    Box(
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .clickable { showDialog = true }
            .semantics(mergeDescendants = true) {
                contentDescription = fieldDescription
                role = Role.Button
            }
            .testTag(testTag),
    ) {
        OutlinedTextFieldDefaults.DecorationBox(
            value = formatted,
            innerTextField = { Text(text = formatted) },
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            label = { Text(text = label) },
            trailingIcon = { Text(text = "📅") },
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)
        DatePickerDialog(
            onDismissRequest = { closeDialog() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis = it }
                        closeDialog()
                    },
                ) {
                    Text(text = stringResource(R.string.input_as_button_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { closeDialog() }) {
                    Text(text = stringResource(R.string.input_as_button_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun InputAsButtonBetterPreview() {
    MaterialTheme {
        InputAsButtonBetter()
    }
}
