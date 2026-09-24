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

package de.frpeters.actua11y.ui.topic.concatenateddescriptions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test reading a single content description back and checking its *structure*, not just
 * its presence: both versions carry a complete, non-empty description, and differ only in whether
 * the segments inside it are separated by line breaks.
 */
@RunWith(AndroidJUnit4::class)
class ConcatenatedDescriptionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun cardDescription(): String =
        composeTestRule.onNodeWithTag("concatenated_descriptions_card").fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]
            .single()

    @Test
    fun better_segmentsAreSeparatedByLineBreaks() {
        composeTestRule.setContent {
            MaterialTheme { ConcatenatedDescriptionsBetter() }
        }

        // Five segments, so four separators.
        assertEquals(4, cardDescription().count { it == '\n' })
    }

    @Test
    fun naive_segmentsRunTogether() {
        composeTestRule.setContent {
            MaterialTheme { ConcatenatedDescriptionsNaive() }
        }

        assertFalse(cardDescription().contains('\n'))
    }
}
