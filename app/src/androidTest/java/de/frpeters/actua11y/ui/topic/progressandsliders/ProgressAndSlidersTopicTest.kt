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

package de.frpeters.actua11y.ui.topic.progressandsliders

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a hand-built progress indicator: the interesting assertion is not just
 * "Better carries ProgressBarRangeInfo", but that its current value tracks the same state the
 * visual bar width is drawn from, confirmed across a real state change rather than one snapshot.
 */
@RunWith(AndroidJUnit4::class)
class ProgressAndSlidersTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_barReportsCurrentProgressValue() {
        composeTestRule.setContent {
            MaterialTheme { ProgressAndSlidersBetter() }
        }

        composeTestRule.onNodeWithTag("progress_and_sliders_advance_button").performClick()
        composeTestRule.onNodeWithTag("progress_and_sliders_advance_button").performClick()

        val bar = composeTestRule.onNodeWithTag("progress_and_sliders_bar").fetchSemanticsNode()
        val info = bar.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo)
        assertNotNull(info)
        assertEquals(2 * PROGRESS_STEP_PERCENT.toFloat(), info!!.current)
        assertEquals(0f..100f, info.range)
    }

    @Test
    fun better_barCarriesContentDescription() {
        composeTestRule.setContent {
            MaterialTheme { ProgressAndSlidersBetter() }
        }

        val bar = composeTestRule.onNodeWithTag("progress_and_sliders_bar").fetchSemanticsNode()
        assertNotNull(bar.config.getOrNull(SemanticsProperties.ContentDescription))
    }

    @Test
    fun naive_barCarriesNoProgressInfoOrDescription() {
        composeTestRule.setContent {
            MaterialTheme { ProgressAndSlidersNaive() }
        }

        composeTestRule.onNodeWithTag("progress_and_sliders_advance_button").performClick()

        val bar = composeTestRule.onNodeWithTag("progress_and_sliders_bar").fetchSemanticsNode()
        assertNull(bar.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo))
        assertNull(bar.config.getOrNull(SemanticsProperties.ContentDescription))
    }
}
