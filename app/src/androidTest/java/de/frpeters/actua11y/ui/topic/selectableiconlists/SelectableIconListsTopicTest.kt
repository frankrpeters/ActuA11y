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

package de.frpeters.actua11y.ui.topic.selectableiconlists

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for an icon-only selectable list: the interesting assertion is that Better's
 * swatches carry both a description and a selected state together, while Naive supplies only
 * the description — the "usually supply one" gap this topic is about.
 */
@RunWith(AndroidJUnit4::class)
class SelectableIconListsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_swatchCarriesDescriptionRoleAndSelectedState() {
        composeTestRule.setContent {
            MaterialTheme { SelectableIconListsBetter() }
        }

        val swatch =
            composeTestRule.onNodeWithTag("selectable_icon_lists_swatch_1").fetchSemanticsNode()
        assertEquals("Orange", swatch.config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull())
        assertEquals(Role.RadioButton, swatch.config.getOrNull(SemanticsProperties.Role))
        assertEquals(false, swatch.config.getOrNull(SemanticsProperties.Selected))
    }

    @Test
    fun better_selectingASwatchUpdatesSelectedState() {
        composeTestRule.setContent {
            MaterialTheme { SelectableIconListsBetter() }
        }

        composeTestRule.onNodeWithTag("selectable_icon_lists_swatch_2").performClick()

        val selected =
            composeTestRule.onNodeWithTag("selectable_icon_lists_swatch_2").fetchSemanticsNode()
        assertEquals(true, selected.config.getOrNull(SemanticsProperties.Selected))

        val deselected =
            composeTestRule.onNodeWithTag("selectable_icon_lists_swatch_0").fetchSemanticsNode()
        assertEquals(false, deselected.config.getOrNull(SemanticsProperties.Selected))
    }

    @Test
    fun naive_swatchCarriesDescriptionButNoSelectedStateOrRole() {
        composeTestRule.setContent {
            MaterialTheme { SelectableIconListsNaive() }
        }

        val swatch =
            composeTestRule.onNodeWithTag("selectable_icon_lists_swatch_1").fetchSemanticsNode()
        assertEquals("Orange", swatch.config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull())
        assertNull(swatch.config.getOrNull(SemanticsProperties.Selected))
        assertNull(swatch.config.getOrNull(SemanticsProperties.Role))
    }
}
