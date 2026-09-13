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

package de.frpeters.actua11y.ui.topic.gridsthatarenottables

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
 * Reference test resolving requirements §10 open question #2: whether a developer-supplied
 * CollectionInfo cleanly overrides LazyVerticalGrid's own internally-supplied default, the way it
 * is already confirmed to for LazyColumn (One-Dimensional Collections topic). It does. Also
 * confirms the grid's real default directly — CollectionInfo(rowCount = -1, columnCount = -1),
 * both dimensions unknown, not the confident 2D report requirements §3.2.1 originally assumed.
 */
@RunWith(AndroidJUnit4::class)
class GridsThatAreNotTablesTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_gridReportsOneDimensionalOverrideNotGridsInternalUnknown() {
        composeTestRule.setContent {
            MaterialTheme { GridsThatAreNotTablesBetter() }
        }

        val grid =
            composeTestRule.onNodeWithTag("grids_that_are_not_tables_grid").fetchSemanticsNode()
        val info = grid.config.getOrNull(SemanticsProperties.CollectionInfo)
        assertEquals(PHOTO_COUNT, info?.rowCount)
        assertEquals(1, info?.columnCount)
    }

    @Test
    fun better_firstTileReportsItsFlattenedRowIndex() {
        composeTestRule.setContent {
            MaterialTheme { GridsThatAreNotTablesBetter() }
        }

        val tile = composeTestRule.onNodeWithTag("grids_that_are_not_tables_item_0")
            .fetchSemanticsNode()
        val itemInfo = tile.config.getOrNull(SemanticsProperties.CollectionItemInfo)
        assertEquals(0, itemInfo?.rowIndex)
        assertEquals(0, itemInfo?.columnIndex)
    }

    @Test
    fun naive_gridReportsGridsInternalUnknownOnBothDimensions() {
        composeTestRule.setContent {
            MaterialTheme { GridsThatAreNotTablesNaive() }
        }

        val grid =
            composeTestRule.onNodeWithTag("grids_that_are_not_tables_grid").fetchSemanticsNode()
        val info = grid.config.getOrNull(SemanticsProperties.CollectionInfo)
        // NAIVE: this is not "no CollectionInfo" — LazyVerticalGrid always attaches one. Both
        // rowCount and columnCount are unconditionally -1 (unknown), even though this grid's
        // 12-item, 3-column layout is fixed and known.
        assertEquals(-1, info?.rowCount)
        assertEquals(-1, info?.columnCount)
    }

    @Test
    fun naive_tilesCarryNoCollectionItemInfo() {
        composeTestRule.setContent {
            MaterialTheme { GridsThatAreNotTablesNaive() }
        }

        val tile = composeTestRule.onNodeWithTag("grids_that_are_not_tables_item_0")
            .fetchSemanticsNode()
        assertNull(tile.config.getOrNull(SemanticsProperties.CollectionItemInfo))
    }
}
