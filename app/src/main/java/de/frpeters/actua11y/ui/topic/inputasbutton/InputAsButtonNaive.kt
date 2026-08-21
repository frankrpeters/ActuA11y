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

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
 * Naive implementation. Same appointment date field as [InputAsButtonBetter] — the accessibility
 * work was simply never done.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputAsButtonNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.input_as_button_pane_title)
    var selectedMillis by remember { mutableLongStateOf(DEFAULT_DATE_MILLIS) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
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
        // NAIVE: a real OutlinedTextField with readOnly = true still carries a real editable-text
        // core underneath — TalkBack offers cursor movement and text-selection actions that do
        // nothing useful on a fixed value, and nothing marks this as a button. The control that
        // actually opens the picker is a separate calendar icon button next to it, disconnected
        // from this field and from its current value.
        OutlinedTextField(
            value = formatDate(selectedMillis),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.input_as_button_field_label)) },
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Text(text = "📅")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_as_button_field"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.input_as_button_developer_note_naive))
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis = it }
                        showDialog = false
                    },
                ) {
                    Text(text = stringResource(R.string.input_as_button_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
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
private fun InputAsButtonNaivePreview() {
    MaterialTheme {
        InputAsButtonNaive()
    }
}
