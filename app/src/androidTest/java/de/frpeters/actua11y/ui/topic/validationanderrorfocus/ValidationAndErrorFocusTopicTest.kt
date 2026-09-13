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

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for moving focus to the first invalid field on submit. Confirms a real,
 * verified difference from the Focus After Navigation topic's precedent: BasicTextField's focus
 * target does not need the two-step InputMode dance a Button needs, since it uses
 * Focusability.Always rather than Focusability.SystemDefined (confirmed by reading
 * Focusable.kt/BasicTextField.kt) — a direct requestFocus() call, with no dialog or window
 * transition involved, is enough here.
 */
@RunWith(AndroidJUnit4::class)
class ValidationAndErrorFocusTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_movesFocusToFirstInvalidFieldOnSubmit() {
        composeTestRule.setContent {
            MaterialTheme { ValidationAndErrorFocusBetter() }
        }

        composeTestRule.onNodeWithTag("validation_and_error_focus_email_field")
            .assertIsNotFocused()

        composeTestRule.onNodeWithTag("validation_and_error_focus_submit_button").performClick()

        composeTestRule.onNodeWithTag("validation_and_error_focus_email_field").assertIsFocused()
    }

    @Test
    fun naive_leavesFocusUnsetOnInvalidSubmit() {
        composeTestRule.setContent {
            MaterialTheme { ValidationAndErrorFocusNaive() }
        }

        composeTestRule.onNodeWithTag("validation_and_error_focus_email_field")
            .assertIsNotFocused()

        composeTestRule.onNodeWithTag("validation_and_error_focus_submit_button").performClick()

        composeTestRule.onNodeWithTag("validation_and_error_focus_email_field")
            .assertIsNotFocused()
    }
}
