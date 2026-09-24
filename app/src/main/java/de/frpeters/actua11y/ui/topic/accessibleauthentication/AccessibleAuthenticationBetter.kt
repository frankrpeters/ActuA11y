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

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same sign-in fields and button as [AccessibleAuthenticationNaive], plus
 * one element the Naive version does not have: a biometric sign-in button, offered as an
 * alternative that involves no memory test at all. See the WHY comment on that button for why
 * this topic departs from strict Naive/Better parity.
 */
@Composable
fun AccessibleAuthenticationBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.accessible_authentication_pane_title)
    val context = LocalContext.current
    val promptTitle = stringResource(R.string.accessible_authentication_biometric_prompt_title)
    val promptCancel = stringResource(R.string.accessible_authentication_biometric_prompt_cancel)
    val statusSucceeded = stringResource(R.string.accessible_authentication_biometric_succeeded)
    val statusFailed = stringResource(R.string.accessible_authentication_biometric_failed)
    val statusUnavailable = stringResource(R.string.accessible_authentication_biometric_unavailable)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var biometricStatus by remember { mutableStateOf("") }

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
        // BETTER: declares the field's purpose, so a password manager can offer to fill it.
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = stringResource(R.string.accessible_authentication_username_label)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentType = ContentType.Username }
                .testTag("accessible_authentication_username_field"),
        )
        // BETTER: accepts whatever arrives — typed, pasted, or filled by a password manager in
        // one step. ContentType.Password (an existing password, not NewPassword) tells the
        // password manager this is a sign-in rather than a sign-up.
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(R.string.accessible_authentication_password_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentType = ContentType.Password }
                .testTag("accessible_authentication_password_field"),
        )
        Button(onClick = { /* demonstration only */ }) {
            Text(text = stringResource(R.string.accessible_authentication_sign_in))
        }
        // WHY: the one deliberate exception to Naive/Better parity in this topic. Requirements
        // §3.8 asks for a biometric alternative "where the platform supports one", and there is no
        // way to offer an alternative without an element to offer it through. The button is
        // always shown, rather than only on devices with biometrics enrolled, so the layout does
        // not change from device to device; availability is checked when it is pressed, and
        // explained in the status line below if missing.
        OutlinedButton(
            onClick = {
                val activity = context.findFragmentActivity()
                val canAuthenticate = BiometricManager.from(context)
                    .canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
                if (activity == null || !canAuthenticate) {
                    biometricStatus = statusUnavailable
                    return@OutlinedButton
                }
                val prompt = BiometricPrompt(
                    activity,
                    ContextCompat.getMainExecutor(activity),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult,
                        ) {
                            biometricStatus = statusSucceeded
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            biometricStatus = statusFailed
                        }
                    },
                )
                prompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(promptTitle)
                        .setNegativeButtonText(promptCancel)
                        .setAllowedAuthenticators(BIOMETRIC_WEAK)
                        .build(),
                )
            },
            modifier = Modifier.testTag("accessible_authentication_biometric_button"),
        ) {
            Text(text = stringResource(R.string.accessible_authentication_biometric_button))
        }
        // BETTER: the outcome of the biometric attempt is announced politely when it changes,
        // rather than only shown. The system prompt itself is announced by the platform; this
        // covers what happens after it closes.
        // TODO(verify): confirm on a real device that TalkBack reads this status after the
        // system biometric prompt dismisses, and does not lose it in the window-focus change.
        Text(
            text = biometricStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .semantics { liveRegion = LiveRegionMode.Polite }
                .testTag("accessible_authentication_biometric_status"),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.accessible_authentication_developer_note_better))
    }
}

// WHY: LocalContext is not guaranteed to be the Activity itself — it can be wrapped (for example
// by a theme or locale override). BiometricPrompt needs the FragmentActivity underneath.
private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun AccessibleAuthenticationBetterPreview() {
    MaterialTheme {
        AccessibleAuthenticationBetter()
    }
}
