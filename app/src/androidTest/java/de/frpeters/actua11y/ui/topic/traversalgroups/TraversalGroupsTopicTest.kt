package de.frpeters.actua11y.ui.topic.traversalgroups

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
 * Reference test showing that isTraversalGroup — an otherwise invisible geometry-sort hint —
 * can be asserted against directly in the semantics tree, without TalkBack running.
 *
 * Better must carry SemanticsProperties.IsTraversalGroup on the list; Naive must not, since it
 * never sets it (see the NAIVE comment in [TraversalGroupsNaive]).
 */
@RunWith(AndroidJUnit4::class)
class TraversalGroupsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun better_marksListAsTraversalGroup() {
        composeTestRule.setContent {
            MaterialTheme { TraversalGroupsBetter() }
        }

        val node = composeTestRule.onNodeWithTag("traversal_groups_list").fetchSemanticsNode()
        assertEquals(true, node.config.getOrNull(SemanticsProperties.IsTraversalGroup))
    }

    @Test
    fun naive_doesNotMarkListAsTraversalGroup() {
        composeTestRule.setContent {
            MaterialTheme { TraversalGroupsNaive() }
        }

        val node = composeTestRule.onNodeWithTag("traversal_groups_list").fetchSemanticsNode()
        assertNull(node.config.getOrNull(SemanticsProperties.IsTraversalGroup))
    }
}
