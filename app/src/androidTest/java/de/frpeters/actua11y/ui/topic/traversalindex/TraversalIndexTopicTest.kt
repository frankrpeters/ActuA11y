package de.frpeters.actua11y.ui.topic.traversalindex

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for the "looks correct but silently does nothing" failure mode: both
 * implementations set identical traversalIndex values on the same two nodes. Only the presence
 * of isTraversalGroup on the enclosing Row makes those indices take effect, so that is the one
 * property this test can meaningfully distinguish between Better and Naive.
 */
@RunWith(AndroidJUnit4::class)
class TraversalIndexTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val cardTitle get() = context.getString(R.string.traversal_index_card_title)
    private val dismissDesc get() = context.getString(R.string.traversal_index_dismiss_desc)

    @Test
    fun better_marksHeaderAsTraversalGroupWithBothIndicesSet() {
        composeTestRule.setContent {
            MaterialTheme { TraversalIndexBetter() }
        }

        val header = composeTestRule.onNodeWithTag("traversal_index_header").fetchSemanticsNode()
        assertEquals(true, header.config.getOrNull(SemanticsProperties.IsTraversalGroup))

        val title = composeTestRule.onNodeWithText(cardTitle).fetchSemanticsNode()
        val dismiss = composeTestRule.onNodeWithContentDescription(dismissDesc).fetchSemanticsNode()
        assertEquals(0f, title.config.getOrNull(SemanticsProperties.TraversalIndex))
        assertEquals(1f, dismiss.config.getOrNull(SemanticsProperties.TraversalIndex))
    }

    @Test
    fun naive_setsSameIndicesButOmitsTraversalGroup() {
        composeTestRule.setContent {
            MaterialTheme { TraversalIndexNaive() }
        }

        val header = composeTestRule.onNodeWithTag("traversal_index_header").fetchSemanticsNode()
        assertNull(header.config.getOrNull(SemanticsProperties.IsTraversalGroup))

        // The indices themselves are indistinguishable from the Better version — set correctly,
        // just inert without the enclosing group. That is the point of this topic.
        val title = composeTestRule.onNodeWithText(cardTitle).fetchSemanticsNode()
        val dismiss = composeTestRule.onNodeWithContentDescription(dismissDesc).fetchSemanticsNode()
        assertEquals(0f, title.config.getOrNull(SemanticsProperties.TraversalIndex))
        assertEquals(1f, dismiss.config.getOrNull(SemanticsProperties.TraversalIndex))
    }
}
