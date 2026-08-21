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

package de.frpeters.actua11y.ui.topic.genuinetables

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
 * Reference test for hand-built table semantics: a plain Column (unlike LazyColumn) supplies no
 * CollectionInfo of its own, so unlike the One-Dimensional Collections topic, the Naive side
 * here is genuine absence rather than a present-but-wrong default.
 */
@RunWith(AndroidJUnit4::class)
class GenuineTablesTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_tableReportsRowAndColumnCounts() {
        composeTestRule.setContent {
            MaterialTheme { GenuineTablesBetter() }
        }

        val table =
            composeTestRule.onNodeWithTag("genuine_tables_table").fetchSemanticsNode()
        val info = table.config.getOrNull(SemanticsProperties.CollectionInfo)
        assertEquals(6, info?.rowCount)
        assertEquals(3, info?.columnCount)
    }

    @Test
    fun better_cellReportsItsRowAndColumnIndex() {
        composeTestRule.setContent {
            MaterialTheme { GenuineTablesBetter() }
        }

        val cell = composeTestRule.onNodeWithTag("genuine_tables_cell_2_1").fetchSemanticsNode()
        val itemInfo = cell.config.getOrNull(SemanticsProperties.CollectionItemInfo)
        assertEquals(2, itemInfo?.rowIndex)
        assertEquals(1, itemInfo?.columnIndex)
    }

    @Test
    fun naive_tableReportsNoCollectionInfo() {
        composeTestRule.setContent {
            MaterialTheme { GenuineTablesNaive() }
        }

        val table =
            composeTestRule.onNodeWithTag("genuine_tables_table").fetchSemanticsNode()
        assertNull(table.config.getOrNull(SemanticsProperties.CollectionInfo))
    }

    @Test
    fun naive_cellsCarryNoCollectionItemInfo() {
        composeTestRule.setContent {
            MaterialTheme { GenuineTablesNaive() }
        }

        val cell = composeTestRule.onNodeWithTag("genuine_tables_cell_2_1").fetchSemanticsNode()
        assertNull(cell.config.getOrNull(SemanticsProperties.CollectionItemInfo))
    }
}
