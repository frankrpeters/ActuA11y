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

package de.frpeters.actua11y.ui.topic.draggingmovements

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for custom accessibility actions: reads each row's CustomActions back from the
 * semantics tree, then invokes one directly — the same call TalkBack makes from its actions
 * menu — and checks the list order changed. The drag gesture itself is not tested: it is
 * identical in both versions, and is not what either version is judged on.
 */
@RunWith(AndroidJUnit4::class)
class DraggingMovementsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val moveUp get() = context.getString(R.string.dragging_movements_action_move_up)
    private val moveDown get() = context.getString(R.string.dragging_movements_action_move_down)
    private val jacket get() = context.getString(R.string.dragging_movements_item_jacket)

    private fun actionLabels(index: Int): List<String> =
        composeTestRule.onNodeWithTag("dragging_movements_row_$index").fetchSemanticsNode()
            .config.getOrNull(SemanticsActions.CustomActions).orEmpty().map { it.label }

    @Test
    fun better_eachRowOffersOnlyThePossibleMoves() {
        composeTestRule.setContent {
            MaterialTheme { DraggingMovementsBetter() }
        }

        val lastIndex = PackingItemResIds.lastIndex
        assertEquals(listOf(moveDown), actionLabels(0))
        assertEquals(listOf(moveUp, moveDown), actionLabels(1))
        assertEquals(listOf(moveUp), actionLabels(lastIndex))
    }

    @Test
    fun better_customActionReordersTheList() {
        composeTestRule.setContent {
            MaterialTheme { DraggingMovementsBetter() }
        }

        // "Rain jacket" starts at index 2.
        composeTestRule.onNodeWithTag("dragging_movements_row_2").assertTextEquals(jacket)
        val moveUpAction = composeTestRule.onNodeWithTag("dragging_movements_row_2")
            .fetchSemanticsNode()
            .config[SemanticsActions.CustomActions]
            .single { it.label == moveUp }
        composeTestRule.runOnIdle { moveUpAction.action() }

        composeTestRule.onNodeWithTag("dragging_movements_row_1").assertTextEquals(jacket)
    }

    @Test
    fun better_arrowButtonReordersTheList() {
        composeTestRule.setContent {
            MaterialTheme { DraggingMovementsBetter() }
        }

        composeTestRule
            .onNodeWithContentDescription(
                context.getString(R.string.dragging_movements_move_up_desc, jacket),
            )
            .performClick()

        composeTestRule.onNodeWithTag("dragging_movements_row_1").assertTextEquals(jacket)
    }

    @Test
    fun naive_noRowOffersAnyAction() {
        composeTestRule.setContent {
            MaterialTheme { DraggingMovementsNaive() }
        }

        PackingItemResIds.indices.forEach { index ->
            assertNull(
                composeTestRule.onNodeWithTag("dragging_movements_row_$index").fetchSemanticsNode()
                    .config.getOrNull(SemanticsActions.CustomActions),
            )
        }
    }
}
