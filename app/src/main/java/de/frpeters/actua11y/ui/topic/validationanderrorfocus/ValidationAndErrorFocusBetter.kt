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

package de.frpeters.actua11y.ui.topic.validationanderrorfocus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same sign-in form as [ValidationAndErrorFocusNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun ValidationAndErrorFocusBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.validation_and_error_focus_pane_title)
    val emailErrorMessage = stringResource(R.string.validation_and_error_focus_email_error)
    val passwordErrorMessage = stringResource(R.string.validation_and_error_focus_password_error)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val emailInvalid = submitted && !email.contains("@")
    val passwordInvalid = submitted && password.length < 4
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.validation_and_error_focus_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.validation_and_error_focus_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Sign in ────────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.validation_and_error_focus_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = stringResource(R.string.validation_and_error_focus_email_label)) },
            isError = emailInvalid,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
                .then(
                    if (emailInvalid) {
                        Modifier.semantics { error(emailErrorMessage) }
                    } else {
                        Modifier
                    },
                )
                .testTag("validation_and_error_focus_email_field"),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(R.string.validation_and_error_focus_password_label)) },
            isError = passwordInvalid,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
                .then(
                    if (passwordInvalid) {
                        Modifier.semantics { error(passwordErrorMessage) }
                    } else {
                        Modifier
                    },
                )
                .testTag("validation_and_error_focus_password_field"),
        )
        // BETTER: same field marking as Naive, plus focus moves to the first invalid field on
        // submit. Unlike Button in the Focus After Navigation topic, BasicTextField's focus
        // target uses Modifier.focusable()'s default Focusability.Always, not
        // Focusability.SystemDefined — confirmed by reading Focusable.kt and BasicTextField.kt
        // directly — so requestFocus() is not gated on the platform's current InputMode the way
        // Button's is, and no dialog/new-window transition is involved here either. A direct
        // call is enough; the two-step fix established for AlertDialog does not apply to this
        // case, confirmed by instrumented test rather than assumed from the Button precedent.
        Button(
            onClick = {
                submitted = true
                when {
                    !email.contains("@") -> emailFocusRequester.requestFocus()
                    password.length < 4 -> passwordFocusRequester.requestFocus()
                }
            },
            modifier = Modifier.testTag("validation_and_error_focus_submit_button"),
        ) {
            Text(text = stringResource(R.string.validation_and_error_focus_submit_label))
        }
        if (submitted && (emailInvalid || passwordInvalid)) {
            Text(
                text = stringResource(R.string.validation_and_error_focus_summary_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.validation_and_error_focus_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ValidationAndErrorFocusBetterPreview() {
    MaterialTheme {
        ValidationAndErrorFocusBetter()
    }
}
