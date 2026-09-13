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

package de.frpeters.actua11y.ui.topic.imeactions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.text.input.ImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for IME actions actually doing what they visually promise. Real finding, not
 * the original premise: BasicTextField *does* surface KeyboardOptions.imeAction as
 * SemanticsProperties.ImeAction — confirmed by reading CoreTextFieldSemanticsModifier.kt's
 * `onImeAction(imeOptions.imeAction) { ... }` (a different file than BasicTextField.kt itself,
 * which is where an earlier grep looked and wrongly concluded there was no wiring at all) — and
 * confirmed empirically: performImeAction() refuses to run at all when a field's ImeAction is
 * Default ("Failed to assert: NOT (ImeAction = 'Default')"), which is exactly how Naive's fields
 * are caught below, without needing to call performImeAction() on them.
 */
@RunWith(AndroidJUnit4::class)
class ImeActionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_fieldsDeclareTheIntendedImeActions() {
        composeTestRule.setContent {
            MaterialTheme { ImeActionsBetter() }
        }

        val nameField =
            composeTestRule.onNodeWithTag("ime_actions_name_field").fetchSemanticsNode()
        val emailField =
            composeTestRule.onNodeWithTag("ime_actions_email_field").fetchSemanticsNode()
        assertEquals(ImeAction.Next, nameField.config.getOrNull(SemanticsProperties.ImeAction))
        assertEquals(ImeAction.Done, emailField.config.getOrNull(SemanticsProperties.ImeAction))
    }

    @Test
    fun better_nextActionAdvancesFocusToEmailField() {
        composeTestRule.setContent {
            MaterialTheme { ImeActionsBetter() }
        }

        composeTestRule.onNodeWithTag("ime_actions_name_field").performClick()
        composeTestRule.onNodeWithTag("ime_actions_name_field").performImeAction()

        composeTestRule.onNodeWithTag("ime_actions_email_field").assertIsFocused()
    }

    @Test
    fun better_doneActionShowsFormReadyStatus() {
        composeTestRule.setContent {
            MaterialTheme { ImeActionsBetter() }
        }

        composeTestRule.onNodeWithTag("ime_actions_email_field").performClick()
        composeTestRule.onNodeWithTag("ime_actions_email_field").performImeAction()

        composeTestRule.onNodeWithTag("ime_actions_form_ready_status").assertExists()
    }

    @Test
    fun naive_fieldsDeclareOnlyTheDefaultImeAction() {
        composeTestRule.setContent {
            MaterialTheme { ImeActionsNaive() }
        }

        val nameField =
            composeTestRule.onNodeWithTag("ime_actions_name_field").fetchSemanticsNode()
        val emailField =
            composeTestRule.onNodeWithTag("ime_actions_email_field").fetchSemanticsNode()
        assertEquals(ImeAction.Default, nameField.config.getOrNull(SemanticsProperties.ImeAction))
        assertEquals(ImeAction.Default, emailField.config.getOrNull(SemanticsProperties.ImeAction))
    }
}
