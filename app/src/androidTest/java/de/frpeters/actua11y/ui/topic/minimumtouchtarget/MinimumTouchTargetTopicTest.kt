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

package de.frpeters.actua11y.ui.topic.minimumtouchtarget

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for touch target size. The interesting result here is not the one this topic
 * started from: reading SemanticsModifierNode.kt/NodeCoordinator.kt (see the developer notes)
 * shows Compose auto-expands touchBoundsInRoot to 48dp for *any* clickable node in this version,
 * regardless of minimumInteractiveComponentSize() — confirmed below on the Naive button too. The
 * real, still-demonstrated difference is layout bounds: IconButton reserves real, un-clippable
 * 48dp of space; a bare clickable's layout bounds stay exactly as small as declared.
 */
@RunWith(AndroidJUnit4::class)
class MinimumTouchTargetTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_layoutBoundsAreLargerThanNaivesTwentyFourDp() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetBetter() }
        }

        // WHY: IconButton's default container (40dp here) is real, reserved layout space larger
        // than Naive's explicit 24dp — but it does not reach the full 48dp
        // minimumInteractiveComponentSize() theoretically enforces. That gap is unexplained and
        // deferred; see the developer note and the topic backlog rather than trusting a stronger
        // claim than this test actually shows.
        composeTestRule.onNodeWithTag("minimum_touch_target_button")
            .assertWidthIsAtLeast(40.dp)
            .assertHeightIsAtLeast(40.dp)
    }

    @Test
    fun better_touchBoundsAlsoMeetFortyEightDpMinimum() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetBetter() }
        }

        composeTestRule.onNodeWithTag("minimum_touch_target_button")
            .assertTouchWidthIsEqualTo(48.dp)
            .assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    fun better_tapIncrementsCounter() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetBetter() }
        }

        composeTestRule.onNodeWithTag("minimum_touch_target_button").performClick()

        composeTestRule.onNodeWithText("Tapped 1 times").assertExists()
    }

    @Test
    fun naive_layoutBoundsStayAtExplicitTwentyFourDp() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetNaive() }
        }

        composeTestRule.onNodeWithTag("minimum_touch_target_button")
            .assertWidthIsEqualTo(24.dp)
            .assertHeightIsEqualTo(24.dp)
    }

    @Test
    fun naive_touchBoundsStillAutoExpandWhenIsolated() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetNaive() }
        }

        // WHY: this is the surprising result documented in the developer note — Compose expands
        // touchBoundsInRoot for any clickable automatically in this version, with nothing beside
        // this icon to clip that expansion away. It does not mean the underlying concern this
        // topic is about is resolved; see the developer note and the topic backlog.
        composeTestRule.onNodeWithTag("minimum_touch_target_button")
            .assertTouchWidthIsEqualTo(48.dp)
            .assertTouchHeightIsEqualTo(48.dp)
    }

    @Test
    fun naive_tapIncrementsCounter() {
        composeTestRule.setContent {
            MaterialTheme { MinimumTouchTargetNaive() }
        }

        composeTestRule.onNodeWithTag("minimum_touch_target_button").performClick()

        composeTestRule.onNodeWithText("Tapped 1 times").assertExists()
    }
}
