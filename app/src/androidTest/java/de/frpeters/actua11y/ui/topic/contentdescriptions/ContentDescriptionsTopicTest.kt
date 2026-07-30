package de.frpeters.actua11y.ui.topic.contentdescriptions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test showing that the Compose semantics tree can be asserted against directly,
 * without TalkBack running — this is the per-topic test template referenced in CLAUDE.md.
 *
 * Better must expose the meaningful image's and icon button's content descriptions; Naive
 * must expose neither, since it never sets them (see the NAIVE comments in
 * [ContentDescriptionsNaive]).
 */
@RunWith(AndroidJUnit4::class)
class ContentDescriptionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val meaningfulImageDesc get() = context.getString(R.string.meaningful_image_desc)
    private val iconButtonDesc get() = context.getString(R.string.icon_button_desc)

    @Test
    fun better_exposesContentDescriptions() {
        composeTestRule.setContent {
            MaterialTheme { ContentDescriptionsBetter() }
        }

        composeTestRule.onNodeWithContentDescription(meaningfulImageDesc).assertExists()
        composeTestRule.onNodeWithContentDescription(iconButtonDesc).assertExists()
    }

    @Test
    fun naive_exposesNeitherContentDescription() {
        composeTestRule.setContent {
            MaterialTheme { ContentDescriptionsNaive() }
        }

        composeTestRule.onNodeWithContentDescription(meaningfulImageDesc).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(iconButtonDesc).assertDoesNotExist()
    }
}
