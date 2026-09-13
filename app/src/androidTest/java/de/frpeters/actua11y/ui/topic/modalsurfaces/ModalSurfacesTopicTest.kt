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

package de.frpeters.actua11y.ui.topic.modalsurfaces

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a modal surface's own accessibility: this asserts against Compose's own
 * focus system (SemanticsProperties.Focused, via assertIsFocused/assertIsNotFocused) — the same
 * approach already established in FocusAfterNavigationTopicTest — and against the sheet content's
 * paneTitle. Real TalkBack behaviour (does its accessibility-focus cursor actually follow, is the
 * appearance actually announced) needs a real device; see the TODO(verify) notes in both
 * implementations' developer notes.
 */
@RunWith(AndroidJUnit4::class)
class ModalSurfacesTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val cancelLabel get() = context.getString(R.string.modal_surfaces_action_cancel)
    private val sheetPaneTitle get() = context.getString(R.string.modal_surfaces_sheet_pane_title)

    @Test
    fun better_sheetContentCarriesPaneTitle() {
        composeTestRule.setContent {
            MaterialTheme { ModalSurfacesBetter() }
        }

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").performClick()

        val sheet =
            composeTestRule.onNodeWithTag("modal_surfaces_sheet_content").fetchSemanticsNode()
        assertEquals(sheetPaneTitle, sheet.config.getOrNull(SemanticsProperties.PaneTitle))
    }

    @Test
    fun better_returnsFocusToTriggerButtonAfterSheetCloses() {
        composeTestRule.setContent {
            MaterialTheme { ModalSurfacesBetter() }
        }

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").assertIsNotFocused()

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").performClick()
        composeTestRule.onNodeWithText(cancelLabel).performClick()
        composeTestRule.onNodeWithText(cancelLabel).assertDoesNotExist()

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").assertIsFocused()
    }

    @Test
    fun naive_sheetContentCarriesNoPaneTitle() {
        composeTestRule.setContent {
            MaterialTheme { ModalSurfacesNaive() }
        }

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").performClick()

        val sheet =
            composeTestRule.onNodeWithTag("modal_surfaces_sheet_content").fetchSemanticsNode()
        assertNull(sheet.config.getOrNull(SemanticsProperties.PaneTitle))
    }

    @Test
    fun naive_leavesTriggerButtonFocusUnset() {
        composeTestRule.setContent {
            MaterialTheme { ModalSurfacesNaive() }
        }

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").assertIsNotFocused()

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").performClick()
        composeTestRule.onNodeWithText(cancelLabel).performClick()

        composeTestRule.onNodeWithTag("modal_surfaces_trigger_button").assertIsNotFocused()
    }
}
