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

package de.frpeters.actua11y.ui.topic.autofillhints

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for autofill participation: confirms Better's fields declare the ContentType a
 * password manager needs to detect and fill them, and that Naive's identical-looking fields
 * declare none at all — the field type (password, via PasswordVisualTransformation) is present
 * either way; only the autofill hint is the naive/better difference.
 */
@RunWith(AndroidJUnit4::class)
class AutofillHintsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_usernameFieldDeclaresUsernameContentType() {
        composeTestRule.setContent {
            MaterialTheme { AutofillHintsBetter() }
        }

        val field =
            composeTestRule.onNodeWithTag("autofill_hints_username_field").fetchSemanticsNode()
        assertEquals(ContentType.Username, field.config.getOrNull(SemanticsProperties.ContentType))
    }

    @Test
    fun better_passwordFieldDeclaresNewPasswordContentType() {
        composeTestRule.setContent {
            MaterialTheme { AutofillHintsBetter() }
        }

        val field =
            composeTestRule.onNodeWithTag("autofill_hints_password_field").fetchSemanticsNode()
        assertEquals(
            ContentType.NewPassword,
            field.config.getOrNull(SemanticsProperties.ContentType),
        )
    }

    @Test
    fun naive_fieldsDeclareNoContentType() {
        composeTestRule.setContent {
            MaterialTheme { AutofillHintsNaive() }
        }

        val username =
            composeTestRule.onNodeWithTag("autofill_hints_username_field").fetchSemanticsNode()
        val password =
            composeTestRule.onNodeWithTag("autofill_hints_password_field").fetchSemanticsNode()
        assertNull(username.config.getOrNull(SemanticsProperties.ContentType))
        assertNull(password.config.getOrNull(SemanticsProperties.ContentType))
    }
}
