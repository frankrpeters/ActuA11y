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

package de.frpeters.actua11y.ui.topic.accessibleauthentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same sign-in fields and button as [AccessibleAuthenticationBetter]. The
 * password field was deliberately hardened against pasting — the kind of change made in the name
 * of security, without considering who relies on paste to sign in at all.
 */
@Composable
fun AccessibleAuthenticationNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.accessible_authentication_pane_title)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.accessible_authentication_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.accessible_authentication_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Sign in ───────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.accessible_authentication_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: no contentType, so a password manager has nothing reliable to recognise this
        // field by. See Topic 27 (Autofill Hints) for that failure on its own.
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = stringResource(R.string.accessible_authentication_username_label)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accessible_authentication_username_field"),
        )
        // NAIVE: accepts at most one new character per change. That blocks pasting — and, as a
        // side effect nobody intended, it blocks password-manager autofill too, which also
        // inserts the whole value at once. What is left is typing a password from memory,
        // character by character: a cognitive function test with no assisting mechanism, which
        // is exactly what WCAG 2.2 SC 3.3.8 rules out.
        OutlinedTextField(
            value = password,
            onValueChange = { new -> if (new.length <= password.length + 1) password = new },
            label = { Text(text = stringResource(R.string.accessible_authentication_password_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accessible_authentication_password_field"),
        )
        Button(onClick = { /* demonstration only */ }) {
            Text(text = stringResource(R.string.accessible_authentication_sign_in))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.accessible_authentication_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun AccessibleAuthenticationNaivePreview() {
    MaterialTheme {
        AccessibleAuthenticationNaive()
    }
}
