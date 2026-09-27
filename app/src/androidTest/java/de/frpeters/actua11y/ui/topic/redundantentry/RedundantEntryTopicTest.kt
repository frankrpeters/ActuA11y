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

package de.frpeters.actua11y.ui.topic.redundantentry

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a multi-step flow: it drives the form from step 1 to step 2 the way a user
 * would, then reads each billing field's EditableText back from the semantics tree. The failure
 * is not visible on either step alone — only in what step 2 contains after step 1.
 */
@RunWith(AndroidJUnit4::class)
class RedundantEntryTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun fieldText(tag: String): String =
        composeTestRule.onNodeWithTag(tag).fetchSemanticsNode()
            .config[SemanticsProperties.EditableText].text

    @Test
    fun better_billingFieldsArePrefilledFromDelivery() {
        composeTestRule.setContent {
            MaterialTheme { RedundantEntryBetter() }
        }

        // WHY: the Continue button sits below the fold on the test host's default window size —
        // three text fields plus the intro copy push it out of the initial viewport. performClick()
        // alone dispatches to the node's actual on-screen coordinates and silently does nothing if
        // those fall outside the visible window; performScrollTo() first is what makes the click
        // land, the same as a real user having to scroll before they can tap it.
        composeTestRule.onNodeWithTag("redundant_entry_continue").performScrollTo().performClick()

        assertEquals(
            context.getString(R.string.redundant_entry_sample_name),
            fieldText("redundant_entry_billing_name"),
        )
        assertEquals(
            context.getString(R.string.redundant_entry_sample_street),
            fieldText("redundant_entry_billing_street"),
        )
        assertEquals(
            context.getString(R.string.redundant_entry_sample_city),
            fieldText("redundant_entry_billing_city"),
        )
    }

    @Test
    fun naive_billingFieldsStartEmpty() {
        composeTestRule.setContent {
            MaterialTheme { RedundantEntryNaive() }
        }

        composeTestRule.onNodeWithTag("redundant_entry_continue").performScrollTo().performClick()

        assertEquals("", fieldText("redundant_entry_billing_name"))
        assertEquals("", fieldText("redundant_entry_billing_street"))
        assertEquals("", fieldText("redundant_entry_billing_city"))
    }
}
