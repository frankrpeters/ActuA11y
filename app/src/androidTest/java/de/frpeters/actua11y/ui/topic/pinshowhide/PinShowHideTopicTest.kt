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

package de.frpeters.actua11y.ui.topic.pinshowhide

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a form field carrying (or not carrying) password semantics. Rather than
 * asserting a single node's properties, this asserts the shape of the semantics tree itself:
 * how many editable nodes exist for one conceptual PIN value, and whether they're marked as a
 * password field at all.
 */
@RunWith(AndroidJUnit4::class)
class PinShowHideTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val hasEditableText = SemanticsMatcher("has EditableText") {
        it.config.contains(SemanticsProperties.EditableText)
    }

    @Test
    fun better_exposesExactlyOneEditableFieldMarkedAsPassword() {
        composeTestRule.setContent {
            MaterialTheme { PinShowHideBetter() }
        }

        composeTestRule.onAllNodes(hasEditableText).assertCountEquals(1)

        val field = composeTestRule.onNodeWithTag("pin_show_hide_field").fetchSemanticsNode()
        assertTrue(field.config.contains(SemanticsProperties.Password))
    }

    @Test
    fun better_showHideControlExposesToggleableState() {
        composeTestRule.setContent {
            MaterialTheme { PinShowHideBetter() }
        }

        val toggle = composeTestRule.onNodeWithTag("pin_show_hide_toggle").fetchSemanticsNode()
        assertTrue(toggle.config.contains(SemanticsProperties.ToggleableState))
    }

    @Test
    fun naive_exposesSixEditableFieldsNoneMarkedAsPassword() {
        composeTestRule.setContent {
            MaterialTheme { PinShowHideNaive() }
        }

        val fields = composeTestRule.onAllNodes(hasEditableText)
        fields.assertCountEquals(6)

        fields.fetchSemanticsNodes().forEach { node ->
            assertNull(node.config.getOrNull(SemanticsProperties.Password))
        }
    }

    @Test
    fun naive_showHideControlExposesNoToggleableState() {
        composeTestRule.setContent {
            MaterialTheme { PinShowHideNaive() }
        }

        val toggle = composeTestRule.onNodeWithTag("pin_show_hide_toggle").fetchSemanticsNode()
        assertNull(toggle.config.getOrNull(SemanticsProperties.ToggleableState))
    }
}
