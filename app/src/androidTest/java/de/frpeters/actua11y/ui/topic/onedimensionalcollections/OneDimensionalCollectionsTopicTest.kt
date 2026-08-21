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

package de.frpeters.actua11y.ui.topic.onedimensionalcollections

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test proving that a developer-supplied CollectionInfo overrides LazyColumn's own
 * internally-supplied default (always rowCount = -1, "unknown" — see LazyLayoutSemanticState.kt)
 * rather than being fought over or ignored, plus the per-item CollectionItemInfo contrast.
 */
@RunWith(AndroidJUnit4::class)
class OneDimensionalCollectionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_listReportsRealCountNotLazyColumnsInternalUnknown() {
        composeTestRule.setContent {
            MaterialTheme { OneDimensionalCollectionsBetter() }
        }

        val list =
            composeTestRule.onNodeWithTag("one_dimensional_collections_list").fetchSemanticsNode()
        val info = list.config.getOrNull(SemanticsProperties.CollectionInfo)
        assertEquals(24, info?.rowCount)
        assertEquals(1, info?.columnCount)
    }

    @Test
    fun better_firstItemReportsItsRowIndex() {
        composeTestRule.setContent {
            MaterialTheme { OneDimensionalCollectionsBetter() }
        }

        val item = composeTestRule.onNodeWithTag("one_dimensional_collections_item_0")
            .fetchSemanticsNode()
        val itemInfo = item.config.getOrNull(SemanticsProperties.CollectionItemInfo)
        assertEquals(0, itemInfo?.rowIndex)
    }

    @Test
    fun naive_listReportsLazyColumnsInternalUnknownCount() {
        composeTestRule.setContent {
            MaterialTheme { OneDimensionalCollectionsNaive() }
        }

        val list =
            composeTestRule.onNodeWithTag("one_dimensional_collections_list").fetchSemanticsNode()
        val info = list.config.getOrNull(SemanticsProperties.CollectionInfo)
        // NAIVE: this is not "no CollectionInfo" — LazyColumn always attaches one. It just
        // always says -1 (unknown), even though this list's 24 items are fixed and known.
        assertEquals(-1, info?.rowCount)
    }

    @Test
    fun naive_itemsCarryNoCollectionItemInfo() {
        composeTestRule.setContent {
            MaterialTheme { OneDimensionalCollectionsNaive() }
        }

        val item = composeTestRule.onNodeWithTag("one_dimensional_collections_item_0")
            .fetchSemanticsNode()
        assertNull(item.config.getOrNull(SemanticsProperties.CollectionItemInfo))
    }
}
