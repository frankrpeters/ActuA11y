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

package de.frpeters.actua11y.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * NaiveToggle is the most-used control in the app (requirements §4.4). This asserts its
 * composite-control semantics directly: one clickable node carrying an accessible-vs-naive
 * stateDescription, rather than the meaningless default on/off.
 */
@RunWith(AndroidJUnit4::class)
class NaiveToggleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val label get() = context.getString(R.string.naive_toggle_label)
    private val betterState get() = context.getString(R.string.naive_toggle_state_better)
    private val naiveState get() = context.getString(R.string.naive_toggle_state_naive)

    @Test
    fun toggle_hasClickActionAndTracksStateDescription() {
        composeTestRule.setContent {
            var showNaive by remember { mutableStateOf(false) }
            MaterialTheme {
                NaiveToggle(showNaive = showNaive, onToggle = { showNaive = it }, enabled = true)
            }
        }

        // WHY: label and switch are merged into one node (mergeDescendants = true), so a
        // single onNodeWithText(label) finds the whole composite control.
        val toggleNode = composeTestRule.onNodeWithText(label)
        toggleNode.assertHasClickAction()
        assertEquals(
            betterState,
            toggleNode.fetchSemanticsNode().config[SemanticsProperties.StateDescription],
        )

        toggleNode.performClick()

        assertEquals(
            naiveState,
            toggleNode.fetchSemanticsNode().config[SemanticsProperties.StateDescription],
        )
    }

    @Test
    fun toggle_disabledWhenTopicHasNoNaiveCounterpart() {
        composeTestRule.setContent {
            MaterialTheme {
                NaiveToggle(showNaive = false, onToggle = {}, enabled = false)
            }
        }

        // WHY: semantics { disabled() } plus the `enabled` parameter together make TalkBack
        // announce this control as disabled, rather than silently skipping it (topic 14).
        composeTestRule.onNodeWithText(label).assertIsNotEnabled()
    }
}
