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

package de.frpeters.actua11y.ui.topic.headings

import androidx.compose.material3.MaterialTheme
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
 * Reference test showing that heading() — the property TalkBack's "Headings" reading control
 * relies on — can be asserted against directly, independent of visual styling.
 *
 * Better must mark all three section titles as headings; Naive must mark none, even though
 * both render with identical titleMedium styling (see the NAIVE comment in [HeadingsNaive]).
 */
@RunWith(AndroidJUnit4::class)
class HeadingsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sectionTags = listOf(
        "headings_section_1_title",
        "headings_section_2_title",
        "headings_section_3_title",
    )

    @Test
    fun better_marksAllThreeSectionTitlesAsHeadings() {
        composeTestRule.setContent {
            MaterialTheme { HeadingsBetter() }
        }

        sectionTags.forEach { tag ->
            val node = composeTestRule.onNodeWithTag(tag).fetchSemanticsNode()
            assertEquals(Unit, node.config.getOrNull(SemanticsProperties.Heading))
        }
    }

    @Test
    fun naive_marksNoSectionTitlesAsHeadings() {
        composeTestRule.setContent {
            MaterialTheme { HeadingsNaive() }
        }

        sectionTags.forEach { tag ->
            val node = composeTestRule.onNodeWithTag(tag).fetchSemanticsNode()
            assertNull(node.config.getOrNull(SemanticsProperties.Heading))
        }
    }
}
