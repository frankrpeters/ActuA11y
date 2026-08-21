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

package de.frpeters.actua11y.ui.topic.inputasbutton

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a picker field that must not expose editable-text semantics at all: the
 * interesting assertion is not just "Role.Button is present" but that the field carries no
 * EditableText, unlike a readOnly TextField which still does.
 */
@RunWith(AndroidJUnit4::class)
class InputAsButtonTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_fieldCarriesButtonRoleAndComposedDescription() {
        composeTestRule.setContent {
            MaterialTheme { InputAsButtonBetter() }
        }

        val field = composeTestRule.onNodeWithTag("input_as_button_field").fetchSemanticsNode()
        assertEquals(Role.Button, field.config.getOrNull(SemanticsProperties.Role))
        val description = field.config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull()
        assertNotNull(description)
        assertTrue(description!!.contains("Appointment date"))
        assertTrue(description.contains("2026"))
    }

    @Test
    fun better_fieldCarriesNoEditableTextCore() {
        composeTestRule.setContent {
            MaterialTheme { InputAsButtonBetter() }
        }

        val field = composeTestRule.onNodeWithTag("input_as_button_field").fetchSemanticsNode()
        assertNull(field.config.getOrNull(SemanticsProperties.EditableText))
    }

    @Test
    fun naive_fieldStillCarriesEditableTextCore() {
        composeTestRule.setContent {
            MaterialTheme { InputAsButtonNaive() }
        }

        val field = composeTestRule.onNodeWithTag("input_as_button_field").fetchSemanticsNode()
        assertNotNull(field.config.getOrNull(SemanticsProperties.EditableText))
    }

    @Test
    fun naive_fieldCarriesNoButtonRole() {
        composeTestRule.setContent {
            MaterialTheme { InputAsButtonNaive() }
        }

        val field = composeTestRule.onNodeWithTag("input_as_button_field").fetchSemanticsNode()
        assertNull(field.config.getOrNull(SemanticsProperties.Role))
    }
}
