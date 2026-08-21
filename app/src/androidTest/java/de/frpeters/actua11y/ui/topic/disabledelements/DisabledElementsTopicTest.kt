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

package de.frpeters.actua11y.ui.topic.disabledelements

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a disabled control before its precondition is met: the interesting
 * assertion is not just "the Better button is marked disabled", but that it still carries
 * Role.Button and a click action alongside the Disabled marker, while the Naive button carries
 * none of the three — those semantics properties are absent, not present-and-false.
 */
@RunWith(AndroidJUnit4::class)
class DisabledElementsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_submitCarriesRoleAndDisabledTogetherBeforeAgreeing() {
        composeTestRule.setContent {
            MaterialTheme { DisabledElementsBetter() }
        }

        val submit =
            composeTestRule.onNodeWithTag("disabled_elements_submit").fetchSemanticsNode()
        assertEquals(Role.Button, submit.config.getOrNull(SemanticsProperties.Role))
        assertNotNull(submit.config.getOrNull(SemanticsProperties.Disabled))
        assertNotNull(submit.config.getOrNull(SemanticsActions.OnClick))
    }

    @Test
    fun better_submitLosesDisabledMarkerOnceAgreed() {
        composeTestRule.setContent {
            MaterialTheme { DisabledElementsBetter() }
        }

        composeTestRule.onNodeWithText("I have read and agree to the terms").performClick()

        val submit =
            composeTestRule.onNodeWithTag("disabled_elements_submit").fetchSemanticsNode()
        assertNull(submit.config.getOrNull(SemanticsProperties.Disabled))
        assertEquals(Role.Button, submit.config.getOrNull(SemanticsProperties.Role))
    }

    @Test
    fun naive_submitCarriesNoRoleClickOrDisabledMarkerBeforeAgreeing() {
        composeTestRule.setContent {
            MaterialTheme { DisabledElementsNaive() }
        }

        val submit =
            composeTestRule.onNodeWithTag("disabled_elements_submit").fetchSemanticsNode()
        assertNull(submit.config.getOrNull(SemanticsProperties.Role))
        assertNull(submit.config.getOrNull(SemanticsProperties.Disabled))
        assertNull(submit.config.getOrNull(SemanticsActions.OnClick))
    }

    @Test
    fun naive_submitGainsRoleAndClickOnceAgreed() {
        composeTestRule.setContent {
            MaterialTheme { DisabledElementsNaive() }
        }

        composeTestRule.onNodeWithText("I have read and agree to the terms").performClick()

        val submit =
            composeTestRule.onNodeWithTag("disabled_elements_submit").fetchSemanticsNode()
        assertEquals(Role.Button, submit.config.getOrNull(SemanticsProperties.Role))
        assertNotNull(submit.config.getOrNull(SemanticsActions.OnClick))
        assertNull(submit.config.getOrNull(SemanticsProperties.Disabled))
    }
}
