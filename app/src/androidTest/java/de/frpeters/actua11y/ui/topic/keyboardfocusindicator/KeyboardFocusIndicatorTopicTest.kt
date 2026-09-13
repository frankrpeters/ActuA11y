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

package de.frpeters.actua11y.ui.topic.keyboardfocusindicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a real limitation, stated rather than papered over: whether a visible focus
 * ring is actually drawn is a pixel-level fact outside the semantics tree entirely, so this test
 * can only confirm what it can confirm — that both versions' chips are genuinely focusable
 * (RequestFocus present, invoking it sets Focused) — not that Better draws a ring and Naive
 * doesn't. That half needs a sighted check, or a real external keyboard, not an instrumented test.
 */
@RunWith(AndroidJUnit4::class)
class KeyboardFocusIndicatorTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_chipIsFocusableAndBecomesFocusedOnRequest() {
        composeTestRule.setContent {
            MaterialTheme { KeyboardFocusIndicatorBetter() }
        }

        val chip =
            composeTestRule.onNodeWithTag("keyboard_focus_indicator_chip_0").fetchSemanticsNode()
        val requestFocus = chip.config.getOrNull(SemanticsActions.RequestFocus)
        assertNotNull(requestFocus)
        // WHY: focus requests must run on the UI thread — invoking the action directly from the
        // test thread throws CalledFromWrongThreadException, confirmed by an earlier failed run.
        composeTestRule.runOnIdle { requestFocus!!.action?.invoke() }
        composeTestRule.waitForIdle()

        val focusedAfter = composeTestRule
            .onNodeWithTag("keyboard_focus_indicator_chip_0")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Focused)
        assertTrue(focusedAfter == true)
    }

    @Test
    fun naive_chipIsAlsoFocusableAndBecomesFocusedOnRequest() {
        composeTestRule.setContent {
            MaterialTheme { KeyboardFocusIndicatorNaive() }
        }

        val chip =
            composeTestRule.onNodeWithTag("keyboard_focus_indicator_chip_0").fetchSemanticsNode()
        val requestFocus = chip.config.getOrNull(SemanticsActions.RequestFocus)
        assertNotNull(requestFocus)
        composeTestRule.runOnIdle { requestFocus!!.action?.invoke() }
        composeTestRule.waitForIdle()

        val focusedAfter = composeTestRule
            .onNodeWithTag("keyboard_focus_indicator_chip_0")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Focused)
        assertTrue(focusedAfter == true)
    }
}
