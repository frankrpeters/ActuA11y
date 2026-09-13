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

package de.frpeters.actua11y.ui.topic.errorsemantics

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for marking a field as being in error. Real finding, not the original premise:
 * OutlinedTextField(isError = true) already applies its own semantics { error(...) } internally
 * (confirmed by reading TextFieldImpl.kt's defaultErrorSemantics) with a generic, locale-dependent
 * message — Naive is not silent, it is generically, unhelpfully loud. The actual contrast this
 * test confirms is generic-vs-specific, not absent-vs-present.
 */
@RunWith(AndroidJUnit4::class)
class ErrorSemanticsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val errorMessage get() = context.getString(R.string.error_semantics_error_message)

    @Test
    fun better_fieldCarriesTheSpecificErrorMessage() {
        composeTestRule.setContent {
            MaterialTheme { ErrorSemanticsBetter() }
        }

        val field = composeTestRule.onNodeWithTag("error_semantics_field").fetchSemanticsNode()
        assertEquals(errorMessage, field.config.getOrNull(SemanticsProperties.Error))
    }

    @Test
    fun naive_fieldCarriesMaterial3sGenericDefaultInstead() {
        composeTestRule.setContent {
            MaterialTheme { ErrorSemanticsNaive() }
        }

        val field = composeTestRule.onNodeWithTag("error_semantics_field").fetchSemanticsNode()
        val actual = field.config.getOrNull(SemanticsProperties.Error)
        assertNotNull(actual)
        assertNotEquals(errorMessage, actual)
    }
}
