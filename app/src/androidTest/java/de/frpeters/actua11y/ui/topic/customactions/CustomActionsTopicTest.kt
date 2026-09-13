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

package de.frpeters.actua11y.ui.topic.customactions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a swipe-only action's local-context-menu equivalent: the interesting
 * assertion is that both custom actions are present with the exact expected labels, giving a
 * keyboard or TalkBack user a way to reach Archive and Delete without ever performing the swipe
 * gesture the sighted, touch experience relies on.
 */
@RunWith(AndroidJUnit4::class)
class CustomActionsTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val archiveLabel get() = context.getString(R.string.custom_actions_action_archive)
    private val deleteLabel get() = context.getString(R.string.custom_actions_action_delete)

    @Test
    fun better_messageRowCarriesArchiveAndDeleteCustomActions() {
        composeTestRule.setContent {
            MaterialTheme { CustomActionsBetter() }
        }

        val row =
            composeTestRule.onNodeWithTag("custom_actions_message_row").fetchSemanticsNode()
        val actions = row.config.getOrNull(SemanticsActions.CustomActions)
        assertNotNull(actions)
        assertEquals(2, actions!!.size)
        assertEquals(archiveLabel, actions[0].label)
        assertEquals(deleteLabel, actions[1].label)
    }
}
