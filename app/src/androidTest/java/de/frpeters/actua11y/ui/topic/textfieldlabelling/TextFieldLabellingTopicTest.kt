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

package de.frpeters.actua11y.ui.topic.textfieldlabelling

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for placeholder-as-label: confirms via the merged semantics text that Better's
 * field still carries its name once populated, while Naive's does not — not because the name was
 * hidden, but because OutlinedTextField never composes a placeholder once text is non-empty
 * (confirmed by reading TextFieldImpl.kt), so there is nothing there to merge in the first place.
 */
@RunWith(AndroidJUnit4::class)
class TextFieldLabellingTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fieldHint get() = context.getString(R.string.text_field_labelling_field_hint)

    @Test
    fun better_fieldRetainsItsNameOnceFilled() {
        composeTestRule.setContent {
            MaterialTheme { TextFieldLabellingBetter() }
        }

        val field = composeTestRule.onNodeWithTag("text_field_labelling_field").fetchSemanticsNode()
        val mergedText = field.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertTrue(mergedText?.contains(fieldHint) == true)
    }

    @Test
    fun naive_fieldLosesItsNameOnceFilled() {
        composeTestRule.setContent {
            MaterialTheme { TextFieldLabellingNaive() }
        }

        val field = composeTestRule.onNodeWithTag("text_field_labelling_field").fetchSemanticsNode()
        val mergedText = field.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertFalse(mergedText?.contains(fieldHint) == true)
    }
}
