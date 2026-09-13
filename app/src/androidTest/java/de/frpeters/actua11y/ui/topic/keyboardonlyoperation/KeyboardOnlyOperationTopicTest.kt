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

package de.frpeters.actua11y.ui.topic.keyboardonlyoperation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a gesture-only action that never reaches the semantics tree at all: the
 * Naive "Bookmark" action carries neither a click action nor a focus target, structurally
 * unreachable by keyboard Tab navigation, switch access, or TalkBack's own swipe traversal —
 * confirmed as an absence of both properties, not merely a different value for either.
 */
@RunWith(AndroidJUnit4::class)
class KeyboardOnlyOperationTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_bookmarkActionIsClickableAndFocusable() {
        composeTestRule.setContent {
            MaterialTheme { KeyboardOnlyOperationBetter() }
        }

        val bookmark = composeTestRule
            .onNodeWithTag("keyboard_only_operation_bookmark")
            .fetchSemanticsNode()
        assertNotNull(bookmark.config.getOrNull(SemanticsActions.OnClick))
        assertNotNull(bookmark.config.getOrNull(SemanticsActions.RequestFocus))
    }

    @Test
    fun naive_bookmarkActionIsNeitherClickableNorFocusable() {
        composeTestRule.setContent {
            MaterialTheme { KeyboardOnlyOperationNaive() }
        }

        val bookmark = composeTestRule
            .onNodeWithTag("keyboard_only_operation_bookmark")
            .fetchSemanticsNode()
        assertNull(bookmark.config.getOrNull(SemanticsActions.OnClick))
        assertNull(bookmark.config.getOrNull(SemanticsActions.RequestFocus))
    }

    @Test
    fun naive_shareAndMoreActionsRemainReachable() {
        composeTestRule.setContent {
            MaterialTheme { KeyboardOnlyOperationNaive() }
        }

        val share =
            composeTestRule.onNodeWithTag("keyboard_only_operation_share").fetchSemanticsNode()
        val more =
            composeTestRule.onNodeWithTag("keyboard_only_operation_more").fetchSemanticsNode()
        assertNotNull(share.config.getOrNull(SemanticsActions.OnClick))
        assertNotNull(more.config.getOrNull(SemanticsActions.OnClick))
    }
}
