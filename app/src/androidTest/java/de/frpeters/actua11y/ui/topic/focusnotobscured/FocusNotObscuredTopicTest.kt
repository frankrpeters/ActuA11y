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

package de.frpeters.actua11y.ui.topic.focusnotobscured

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a failure with no representation in the semantics tree at all: whether a
 * focused node is hidden behind another one is purely geometry. The test finds the first field
 * that starts out behind the pay bar, moves focus to it the way a keyboard would (RequestFocus —
 * text fields accept programmatic focus even in touch mode, see CLAUDE.md), and compares the
 * field's bounds with the bar's.
 */
@RunWith(AndroidJUnit4::class)
class FocusNotObscuredTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun fieldBottom(index: Int): Dp =
        composeTestRule.onNodeWithTag("focus_not_obscured_field_$index")
            .getUnclippedBoundsInRoot().bottom

    private fun barTop(): Dp =
        composeTestRule.onNodeWithTag("focus_not_obscured_pay_bar").getUnclippedBoundsInRoot().top

    /** Focuses the first field that starts out (at least partly) behind the bar; returns its index. */
    private fun focusFirstFieldBehindBar(content: @Composable () -> Unit): Int {
        composeTestRule.setContent { MaterialTheme { content() } }
        val index = PaymentFieldLabelResIds.indices.first { fieldBottom(it) > barTop() }
        composeTestRule.onNodeWithTag("focus_not_obscured_field_$index")
            .performSemanticsAction(SemanticsActions.RequestFocus)
        composeTestRule.waitForIdle()
        return index
    }

    @Test
    fun better_focusedFieldIsScrolledClearOfTheBar() {
        val index = focusFirstFieldBehindBar { FocusNotObscuredBetter() }

        // A fraction of a dp of tolerance for rounding between px and dp.
        assertTrue(fieldBottom(index) <= barTop() + 0.5.dp)
    }

    @Test
    fun naive_focusedFieldStaysBehindTheBar() {
        val index = focusFirstFieldBehindBar { FocusNotObscuredNaive() }

        assertTrue(fieldBottom(index) > barTop())
    }
}
