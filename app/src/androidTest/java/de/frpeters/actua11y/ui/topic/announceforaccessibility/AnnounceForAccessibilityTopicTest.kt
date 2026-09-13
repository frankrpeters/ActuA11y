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

package de.frpeters.actua11y.ui.topic.announceforaccessibility

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test contrasting the two mechanisms this topic is actually about. Note the
 * asymmetry: the live-region side has a real semantics property to assert on
 * (SemanticsProperties.LiveRegion); the announceForAccessibility side dispatches a raw
 * AccessibilityEvent with no semantics property behind it at all, so the only thing this test
 * can confirm about it is the *absence* of LiveRegion — not that an announcement actually fired,
 * which needs a real device with TalkBack (see the developer note's TODO(verify) items).
 */
@RunWith(AndroidJUnit4::class)
class AnnounceForAccessibilityTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun liveRegionStatusCarriesPoliteLiveRegion() {
        composeTestRule.setContent {
            MaterialTheme { AnnounceForAccessibilityBetter() }
        }

        val status = composeTestRule
            .onNodeWithTag("announce_for_accessibility_live_status")
            .fetchSemanticsNode()
        assertEquals(LiveRegionMode.Polite, status.config.getOrNull(SemanticsProperties.LiveRegion))
    }

    @Test
    fun announceStatusCarriesNoLiveRegion() {
        composeTestRule.setContent {
            MaterialTheme { AnnounceForAccessibilityBetter() }
        }

        val status = composeTestRule
            .onNodeWithTag("announce_for_accessibility_announce_status")
            .fetchSemanticsNode()
        assertNull(status.config.getOrNull(SemanticsProperties.LiveRegion))
    }
}
