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

package de.frpeters.actua11y.ui.topic.compositecontrols

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a row that must merge into one control: the interesting assertion is not
 * just "the row carries Role.Switch", but that the inner Switch — read directly from the
 * unmerged tree — carries no click action of its own once onCheckedChange = null.
 */
@RunWith(AndroidJUnit4::class)
class CompositeControlsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_rowMergesLabelDescriptionAndSwitchState() {
        composeTestRule.setContent {
            MaterialTheme { CompositeControlsBetter() }
        }

        val row = composeTestRule.onNodeWithTag("composite_controls_row").fetchSemanticsNode()
        assertEquals(Role.Switch, row.config.getOrNull(SemanticsProperties.Role))
        assertNotNull(row.config.getOrNull(SemanticsProperties.ToggleableState))
        val mergedText = row.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertNotNull(mergedText)
        assertTrue(mergedText!!.contains("Wi-Fi"))
        assertTrue(mergedText.contains("Connect automatically"))
    }

    @Test
    fun better_innerSwitchCarriesNoClickActionOfItsOwn() {
        composeTestRule.setContent {
            MaterialTheme { CompositeControlsBetter() }
        }

        val switch = composeTestRule
            .onNodeWithTag("composite_controls_switch", useUnmergedTree = true)
            .fetchSemanticsNode()
        assertNull(switch.config.getOrNull(SemanticsActions.OnClick))
    }

    @Test
    fun naive_rowCarriesNoRoleOrMergedState() {
        composeTestRule.setContent {
            MaterialTheme { CompositeControlsNaive() }
        }

        val row = composeTestRule.onNodeWithTag("composite_controls_row").fetchSemanticsNode()
        assertNull(row.config.getOrNull(SemanticsProperties.Role))
        assertNull(row.config.getOrNull(SemanticsProperties.ToggleableState))
    }

    @Test
    fun naive_switchIsIndependentlyClickableWithNoLabel() {
        composeTestRule.setContent {
            MaterialTheme { CompositeControlsNaive() }
        }

        val switch = composeTestRule
            .onNodeWithTag("composite_controls_switch", useUnmergedTree = true)
            .fetchSemanticsNode()
        assertNotNull(switch.config.getOrNull(SemanticsActions.OnClick))
        assertNull(switch.config.getOrNull(SemanticsProperties.ContentDescription))
    }
}
