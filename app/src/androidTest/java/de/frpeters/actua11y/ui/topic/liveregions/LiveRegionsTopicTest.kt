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

package de.frpeters.actua11y.ui.topic.liveregions

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
 * Reference test for automatic announcement on content change: the interesting assertion is not
 * just "Better carries LiveRegion", but the exact mode (Polite, not Assertive) this topic argues
 * for, versus Naive's status Text carrying no LiveRegion property at all.
 */
@RunWith(AndroidJUnit4::class)
class LiveRegionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_statusCarriesPoliteLiveRegion() {
        composeTestRule.setContent {
            MaterialTheme { LiveRegionsBetter() }
        }

        val status = composeTestRule.onNodeWithTag("live_regions_status").fetchSemanticsNode()
        assertEquals(LiveRegionMode.Polite, status.config.getOrNull(SemanticsProperties.LiveRegion))
    }

    @Test
    fun naive_statusCarriesNoLiveRegion() {
        composeTestRule.setContent {
            MaterialTheme { LiveRegionsNaive() }
        }

        val status = composeTestRule.onNodeWithTag("live_regions_status").fetchSemanticsNode()
        assertNull(status.config.getOrNull(SemanticsProperties.LiveRegion))
    }
}
