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

package de.frpeters.actua11y.ui.topic.switchplatformvscustom

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a hand-drawn control: the interesting assertion is not just "Better carries
 * Role.Switch", but that Naive's identical Canvas-drawn track still carries a real click action
 * (Modifier.clickable registers one unconditionally) while carrying none of Role, ToggleableState,
 * or ContentDescription — a control TalkBack can activate but say nothing about.
 */
@RunWith(AndroidJUnit4::class)
class SwitchPlatformVsCustomTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_toggleCarriesRoleStateAndDescription() {
        composeTestRule.setContent {
            MaterialTheme { SwitchPlatformVsCustomBetter() }
        }

        val toggle = composeTestRule
            .onNodeWithTag("switch_platform_vs_custom_toggle")
            .fetchSemanticsNode()
        assertEquals(Role.Switch, toggle.config.getOrNull(SemanticsProperties.Role))
        assertNotNull(toggle.config.getOrNull(SemanticsProperties.ToggleableState))
        assertNotNull(toggle.config.getOrNull(SemanticsProperties.ContentDescription))
        assertNotNull(toggle.config.getOrNull(SemanticsActions.OnClick))
    }

    @Test
    fun better_toggleStateFlipsOnClick() {
        composeTestRule.setContent {
            MaterialTheme { SwitchPlatformVsCustomBetter() }
        }

        val before = composeTestRule
            .onNodeWithTag("switch_platform_vs_custom_toggle")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.ToggleableState)

        composeTestRule.onNodeWithTag("switch_platform_vs_custom_toggle").performClick()

        val after = composeTestRule
            .onNodeWithTag("switch_platform_vs_custom_toggle")
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.ToggleableState)

        assertNotEquals(before, after)
    }

    @Test
    fun naive_toggleCarriesClickButNoRoleStateOrDescription() {
        composeTestRule.setContent {
            MaterialTheme { SwitchPlatformVsCustomNaive() }
        }

        val toggle = composeTestRule
            .onNodeWithTag("switch_platform_vs_custom_toggle")
            .fetchSemanticsNode()
        assertNotNull(toggle.config.getOrNull(SemanticsActions.OnClick))
        assertNull(toggle.config.getOrNull(SemanticsProperties.Role))
        assertNull(toggle.config.getOrNull(SemanticsProperties.ToggleableState))
        assertNull(toggle.config.getOrNull(SemanticsProperties.ContentDescription))
    }
}
