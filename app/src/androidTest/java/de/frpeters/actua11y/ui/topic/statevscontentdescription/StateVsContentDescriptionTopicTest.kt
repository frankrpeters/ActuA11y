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

package de.frpeters.actua11y.ui.topic.statevscontentdescription

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for name-vs-state separation: the interesting assertion is not just "Better has
 * a StateDescription", but that Better's ContentDescription stays identical across both states
 * while Naive's ContentDescription itself changes and carries no StateDescription at all.
 */
@RunWith(AndroidJUnit4::class)
class StateVsContentDescriptionTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_contentDescriptionStaysConstantAcrossToggle() {
        composeTestRule.setContent {
            MaterialTheme { StateVsContentDescriptionBetter() }
        }

        val toggle = composeTestRule.onNodeWithTag("state_vs_content_description_toggle")
        val before = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)

        toggle.performClick()

        val after = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)
        assertEquals(before, after)
    }

    @Test
    fun better_stateDescriptionChangesAcrossToggle() {
        composeTestRule.setContent {
            MaterialTheme { StateVsContentDescriptionBetter() }
        }

        val toggle = composeTestRule.onNodeWithTag("state_vs_content_description_toggle")
        val before = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription)

        toggle.performClick()

        val after = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription)
        assertNotEquals(before, after)
    }

    @Test
    fun naive_contentDescriptionChangesAcrossToggleInstead() {
        composeTestRule.setContent {
            MaterialTheme { StateVsContentDescriptionNaive() }
        }

        val toggle = composeTestRule.onNodeWithTag("state_vs_content_description_toggle")
        val before = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)

        toggle.performClick()

        val after = toggle.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)
        assertNotEquals(before, after)
    }

    @Test
    fun naive_carriesNoStateDescriptionAtAll() {
        composeTestRule.setContent {
            MaterialTheme { StateVsContentDescriptionNaive() }
        }

        val toggle = composeTestRule
            .onNodeWithTag("state_vs_content_description_toggle")
            .fetchSemanticsNode()
        assertNull(toggle.config.getOrNull(SemanticsProperties.StateDescription))
    }
}
