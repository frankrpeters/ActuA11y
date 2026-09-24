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

package de.frpeters.actua11y.ui.topic.voidparsing

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.frpeters.actua11y.navigation.TopicRegistry
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A content-only topic has no Naive/Better difference to assert. What remains testable is that
 * its structure is navigable — each section title is a real heading — and that the registry marks
 * it as having no naive counterpart, which is what disables the app-bar toggle.
 */
@RunWith(AndroidJUnit4::class)
class VoidParsingTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sectionTitlesAreHeadings() {
        composeTestRule.setContent {
            MaterialTheme { VoidParsingBetter() }
        }

        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assertCountEquals(3)
    }

    @Test
    fun registryMarksTopicAsHavingNoNaiveCounterpart() {
        val topic = TopicRegistry.all.single { it.id == "void_parsing" }
        assertFalse(topic.supportsNaive)
    }
}
