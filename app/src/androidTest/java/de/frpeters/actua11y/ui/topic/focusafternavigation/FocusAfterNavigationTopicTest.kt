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

package de.frpeters.actua11y.ui.topic.focusafternavigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for returning focus to a trigger control after a picker dismisses. This
 * asserts against Compose's own focus system (SemanticsProperties.Focused, via
 * assertIsFocused/assertIsNotFocused) — the layer FocusRequester actually operates on — not
 * against TalkBack's accessibility focus, which needs a real device; see the on-device
 * TODO(verify) notes in both implementations' developer notes.
 */
@RunWith(AndroidJUnit4::class)
class FocusAfterNavigationTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val weeklyLabel get() = context.getString(R.string.focus_after_navigation_option_weekly)

    @Test
    fun better_returnsFocusToTriggerButtonAfterPickerCloses() {
        composeTestRule.setContent {
            MaterialTheme { FocusAfterNavigationBetter() }
        }

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").assertIsNotFocused()

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").performClick()
        composeTestRule.onNodeWithText(weeklyLabel).performClick()
        composeTestRule.onNodeWithText(weeklyLabel).assertDoesNotExist()

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").assertIsFocused()
    }

    @Test
    fun naive_leavesTriggerButtonFocusUnset() {
        composeTestRule.setContent {
            MaterialTheme { FocusAfterNavigationNaive() }
        }

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").assertIsNotFocused()

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").performClick()
        composeTestRule.onNodeWithText(weeklyLabel).performClick()

        composeTestRule.onNodeWithTag("focus_after_navigation_trigger_button").assertIsNotFocused()
    }
}
