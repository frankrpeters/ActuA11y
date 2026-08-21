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

package de.frpeters.actua11y.ui.topic.lazylistpitfalls

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a list built from several stickyHeader()/items() block pairs rather than a
 * single items(count = N) call: the interesting assertion is that row indices stay correct and
 * sequential *across* section boundaries, not just within one section.
 */
@RunWith(AndroidJUnit4::class)
class LazyListPitfallsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_listReportsRealContactCountNotHeaderInflatedTotal() {
        composeTestRule.setContent {
            MaterialTheme { LazyListPitfallsBetter() }
        }

        val list = composeTestRule.onNodeWithTag("lazy_list_pitfalls_list").fetchSemanticsNode()
        val info = list.config.getOrNull(SemanticsProperties.CollectionInfo)
        assertEquals(9, info?.rowCount)
    }

    @Test
    fun better_rowIndexStaysCorrectAcrossSectionBoundary() {
        composeTestRule.setContent {
            MaterialTheme { LazyListPitfallsBetter() }
        }

        // "Beth Baker" is the first contact in the B group — the second stickyHeader()/items()
        // block — and precomputed position 2 in the flattened, header-free contact list.
        composeTestRule
            .onNodeWithTag("lazy_list_pitfalls_list")
            .performScrollToNode(hasTestTag("lazy_list_pitfalls_contact_2"))
        val node = composeTestRule.onNodeWithTag("lazy_list_pitfalls_contact_2").fetchSemanticsNode()
        val itemInfo = node.config.getOrNull(SemanticsProperties.CollectionItemInfo)
        assertEquals(2, itemInfo?.rowIndex)
    }

    @Test
    fun better_headerCarriesHeadingButNoCollectionItemInfo() {
        composeTestRule.setContent {
            MaterialTheme { LazyListPitfallsBetter() }
        }

        val header =
            composeTestRule.onNodeWithTag("lazy_list_pitfalls_header_A").fetchSemanticsNode()
        assertNull(header.config.getOrNull(SemanticsProperties.CollectionItemInfo))
    }

    @Test
    fun naive_listReportsUnknownCollectionInfo() {
        composeTestRule.setContent {
            MaterialTheme { LazyListPitfallsNaive() }
        }

        val list = composeTestRule.onNodeWithTag("lazy_list_pitfalls_list").fetchSemanticsNode()
        val info = list.config.getOrNull(SemanticsProperties.CollectionInfo)
        assertEquals(-1, info?.rowCount)
    }

    @Test
    fun naive_contactsCarryNoCollectionItemInfo() {
        composeTestRule.setContent {
            MaterialTheme { LazyListPitfallsNaive() }
        }

        val node = composeTestRule.onNodeWithTag("lazy_list_pitfalls_contact_0").fetchSemanticsNode()
        assertNull(node.config.getOrNull(SemanticsProperties.CollectionItemInfo))
    }
}
