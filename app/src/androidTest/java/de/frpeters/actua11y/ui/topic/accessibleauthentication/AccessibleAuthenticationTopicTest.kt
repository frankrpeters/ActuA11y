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

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for paste-blocking. performTextInput delivers its whole string as one edit —
 * the same shape of change a paste or a password manager's fill produces — so it can stand in
 * for both without touching the real clipboard or an autofill service.
 *
 * The biometric path is not tested here: BiometricPrompt shows a system window outside this
 * app's composition, which a Compose test cannot drive. Only its entry point is asserted.
 */
@RunWith(AndroidJUnit4::class)
class AccessibleAuthenticationTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun fieldLength(tag: String): Int =
        composeTestRule.onNodeWithTag(tag).fetchSemanticsNode()
            .config[SemanticsProperties.EditableText].length

    @Test
    fun better_passwordFieldAcceptsWholeValueInOneStep() {
        composeTestRule.setContent {
            MaterialTheme { AccessibleAuthenticationBetter() }
        }

        composeTestRule.onNodeWithTag("accessible_authentication_password_field")
            .performTextInput(PASSWORD)

        // EditableText holds the masked (transformed) text, so compare lengths, not content.
        assertEquals(PASSWORD.length, fieldLength("accessible_authentication_password_field"))
    }

    @Test
    fun better_fieldsDeclareSignInContentTypes() {
        composeTestRule.setContent {
            MaterialTheme { AccessibleAuthenticationBetter() }
        }

        val username = composeTestRule.onNodeWithTag("accessible_authentication_username_field")
            .fetchSemanticsNode()
        val password = composeTestRule.onNodeWithTag("accessible_authentication_password_field")
            .fetchSemanticsNode()
        assertEquals(ContentType.Username, username.config.getOrNull(SemanticsProperties.ContentType))
        assertEquals(ContentType.Password, password.config.getOrNull(SemanticsProperties.ContentType))
    }

    @Test
    fun better_offersBiometricAlternative() {
        composeTestRule.setContent {
            MaterialTheme { AccessibleAuthenticationBetter() }
        }

        composeTestRule.onNodeWithTag("accessible_authentication_biometric_button")
            .assertHasClickAction()
    }

    @Test
    fun naive_passwordFieldRejectsWholeValueInOneStep() {
        composeTestRule.setContent {
            MaterialTheme { AccessibleAuthenticationNaive() }
        }

        composeTestRule.onNodeWithTag("accessible_authentication_password_field")
            .performTextInput(PASSWORD)

        assertEquals(0, fieldLength("accessible_authentication_password_field"))
    }

    @Test
    fun naive_fieldsDeclareNoContentType() {
        composeTestRule.setContent {
            MaterialTheme { AccessibleAuthenticationNaive() }
        }

        val password = composeTestRule.onNodeWithTag("accessible_authentication_password_field")
            .fetchSemanticsNode()
        assertNull(password.config.getOrNull(SemanticsProperties.ContentType))
    }

    private companion object {
        const val PASSWORD = "correct-horse-battery-staple"
    }
}
