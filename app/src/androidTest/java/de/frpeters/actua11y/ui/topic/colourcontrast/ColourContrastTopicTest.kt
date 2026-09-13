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

package de.frpeters.actua11y.ui.topic.colourcontrast

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for "does this row say its status in words, or only in colour" — the one part
 * of this topic a Level 1 semantics-tree instrumented test can actually confirm (see
 * TESTING.md). Contrast-ratio compliance is a Level 2 (Accessibility Test Framework) concern
 * instead: this test can read a colour value back from the semantics tree, but that tree carries
 * no notion of perceptual contrast for it to assert against.
 */
@RunWith(AndroidJUnit4::class)
class ColourContrastTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun naive_rowMergedTextCarriesOrderIdButNotStatusWord() {
        composeTestRule.setContent {
            MaterialTheme { ColourContrastNaive() }
        }

        val row = composeTestRule
            .onNodeWithTag("colour_contrast_row_${OrderStatus.CANCELLED}")
            .fetchSemanticsNode()
        val mergedText = row.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertNotNull(mergedText)
        assertTrue(mergedText!!.contains("A1090"))
        assertFalse(mergedText.contains("Cancelled"))
    }

    @Test
    fun better_rowMergedTextCarriesBothOrderIdAndStatusWord() {
        composeTestRule.setContent {
            MaterialTheme { ColourContrastBetter() }
        }

        val row = composeTestRule
            .onNodeWithTag("colour_contrast_row_${OrderStatus.CANCELLED}")
            .fetchSemanticsNode()
        val mergedText = row.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertNotNull(mergedText)
        assertTrue(mergedText!!.contains("A1090"))
        assertTrue(mergedText.contains("Cancelled"))
    }
}
