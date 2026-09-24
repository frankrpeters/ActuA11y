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

package de.frpeters.actua11y.ui.topic.consistentidentification

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a failure that exists only *between* two nodes: each save button is labelled
 * in both versions, so asserting on either one alone would pass. The test reads both accessible
 * names back from the merged semantics tree and compares them to each other.
 */
@RunWith(AndroidJUnit4::class)
class ConsistentIdentificationTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun descriptionOf(tag: String): List<String> =
        composeTestRule.onNodeWithTag(tag).fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]

    @Test
    fun better_bothSaveButtonsShareOneName() {
        composeTestRule.setContent {
            MaterialTheme { ConsistentIdentificationBetter() }
        }

        val expected = listOf(context.getString(R.string.consistent_identification_save_action))
        assertEquals(expected, descriptionOf("consistent_identification_list_save"))
        assertEquals(expected, descriptionOf("consistent_identification_details_save"))
    }

    @Test
    fun naive_saveButtonsAreLabelledDifferently() {
        composeTestRule.setContent {
            MaterialTheme { ConsistentIdentificationNaive() }
        }

        // Both are labelled — this is not a missing-label failure …
        val list = descriptionOf("consistent_identification_list_save")
        val details = descriptionOf("consistent_identification_details_save")
        // … the failure is that the two labels for one action disagree.
        assertNotEquals(list, details)
    }
}
