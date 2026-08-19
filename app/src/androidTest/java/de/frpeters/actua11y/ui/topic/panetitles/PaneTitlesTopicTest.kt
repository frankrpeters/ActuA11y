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

package de.frpeters.actua11y.ui.topic.panetitles

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
 * Reference test for a dynamic pane title: unlike the screen-level paneTitle every topic sets
 * once on arrival, this content pane's title is expected to change every time the selected view
 * does, since the swap never triggers a full navigation event.
 */
@RunWith(AndroidJUnit4::class)
class PaneTitlesTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val summaryPaneTitle get() = context.getString(R.string.pane_titles_summary_pane_title)
    private val detailsPaneTitle get() = context.getString(R.string.pane_titles_details_pane_title)

    @Test
    fun better_contentPaneTitleTracksSelectedView() {
        composeTestRule.setContent {
            MaterialTheme { PaneTitlesBetter() }
        }

        val initial = composeTestRule.onNodeWithTag("pane_titles_content_pane").fetchSemanticsNode()
        assertEquals(summaryPaneTitle, initial.config.getOrNull(SemanticsProperties.PaneTitle))

        composeTestRule.onNodeWithTag("pane_titles_tab_details").performClick()

        val afterSwitch =
            composeTestRule.onNodeWithTag("pane_titles_content_pane").fetchSemanticsNode()
        assertEquals(detailsPaneTitle, afterSwitch.config.getOrNull(SemanticsProperties.PaneTitle))
    }

    @Test
    fun naive_contentPaneNeverSetsPaneTitle() {
        composeTestRule.setContent {
            MaterialTheme { PaneTitlesNaive() }
        }

        val initial = composeTestRule.onNodeWithTag("pane_titles_content_pane").fetchSemanticsNode()
        assertNull(initial.config.getOrNull(SemanticsProperties.PaneTitle))

        composeTestRule.onNodeWithTag("pane_titles_tab_details").performClick()

        val afterSwitch =
            composeTestRule.onNodeWithTag("pane_titles_content_pane").fetchSemanticsNode()
        assertNull(afterSwitch.config.getOrNull(SemanticsProperties.PaneTitle))
    }
}
