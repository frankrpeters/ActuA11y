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

package de.frpeters.actua11y.ui.topic.reducedmotion

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
 * Reference test in two parts. Duration itself is neither a semantics-tree property nor
 * something a rendered pixel can confirm the way Dark Mode's colour comparison could — an
 * animation's timing is a run-time detail `captureToImage()` cannot expose either. What can be
 * tested directly is [scaledAnimationDurationMillis] itself, the pure function both Better's
 * animation and this test agree on; the remaining tests confirm both versions still function
 * identically for a sighted touch user, which the "Naive and Better must be functionally
 * equivalent" rule (CLAUDE.md) requires regardless of animation duration.
 */
@RunWith(AndroidJUnit4::class)
class ReducedMotionTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val detailsText get() = context.getString(R.string.reduced_motion_details_text)

    @Test
    fun scaledAnimationDurationMillis_zeroScaleMeansInstant() {
        assertEquals(0, scaledAnimationDurationMillis(animatorDurationScale = 0f, baseDurationMillis = 400))
    }

    @Test
    fun scaledAnimationDurationMillis_normalScaleIsUnchanged() {
        assertEquals(400, scaledAnimationDurationMillis(animatorDurationScale = 1f, baseDurationMillis = 400))
    }

    @Test
    fun scaledAnimationDurationMillis_doubleScaleDoublesDuration() {
        assertEquals(800, scaledAnimationDurationMillis(animatorDurationScale = 2f, baseDurationMillis = 400))
    }

    @Test
    fun better_toggleRevealsDetails() {
        composeTestRule.setContent {
            MaterialTheme { ReducedMotionBetter() }
        }

        composeTestRule.onNodeWithTag("reduced_motion_toggle").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(detailsText).assertIsDisplayed()
    }

    @Test
    fun naive_toggleRevealsDetails() {
        composeTestRule.setContent {
            MaterialTheme { ReducedMotionNaive() }
        }

        composeTestRule.onNodeWithTag("reduced_motion_toggle").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(detailsText).assertIsDisplayed()
    }
}
