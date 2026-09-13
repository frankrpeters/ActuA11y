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

package de.frpeters.actua11y.ui.topic.verbatimstrings

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.VerbatimTtsAnnotation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a verbatim-marked substring: the interesting assertion is that only the
 * code segment carries a VerbatimTtsAnnotation, read back via AnnotatedString.getTtsAnnotations,
 * not that the visible text differs from Naive at all — it doesn't.
 */
@RunWith(AndroidJUnit4::class)
class VerbatimStringsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val code get() = context.getString(R.string.verbatim_strings_reference_code)

    @Test
    fun better_referenceCodeCarriesVerbatimTtsAnnotation() {
        composeTestRule.setContent {
            MaterialTheme { VerbatimStringsBetter() }
        }

        val node =
            composeTestRule.onNodeWithTag("verbatim_strings_reference").fetchSemanticsNode()
        val text = node.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()
        val annotations = text?.getTtsAnnotations(0, text.length) ?: emptyList()
        assertTrue(annotations.any { it.item is VerbatimTtsAnnotation && (it.item as VerbatimTtsAnnotation).verbatim == code })
    }

    @Test
    fun naive_referenceCodeCarriesNoTtsAnnotation() {
        composeTestRule.setContent {
            MaterialTheme { VerbatimStringsNaive() }
        }

        val node =
            composeTestRule.onNodeWithTag("verbatim_strings_reference").fetchSemanticsNode()
        val text = node.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()
        val annotations = text?.getTtsAnnotations(0, text.length) ?: emptyList()
        assertEquals(0, annotations.size)
    }
}
