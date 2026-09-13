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

package de.frpeters.actua11y.ui.topic.selectablecopyabletext

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.frpeters.actua11y.R
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test for a copy affordance on a non-text element: confirms the order card carries
 * SemanticsActions.CopyText, and that invoking it directly (the same action an accessibility
 * service would invoke) actually copies — checked through the same visible status text a sighted
 * user would see, since there is no clipboard-content assertion in this project's test style.
 */
@RunWith(AndroidJUnit4::class)
class SelectableCopyableTextTopicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val copiedStatus get() = context.getString(R.string.selectable_copyable_text_copied_status)

    @Test
    fun orderCardCarriesCopyTextAction() {
        composeTestRule.setContent {
            MaterialTheme { SelectableCopyableTextBetter() }
        }

        val card = composeTestRule
            .onNodeWithTag("selectable_copyable_text_order_card")
            .fetchSemanticsNode()
        assertNotNull(card.config.getOrNull(SemanticsActions.CopyText))
    }

    @Test
    fun invokingCopyActionUpdatesStatus() {
        composeTestRule.setContent {
            MaterialTheme { SelectableCopyableTextBetter() }
        }

        val card = composeTestRule
            .onNodeWithTag("selectable_copyable_text_order_card")
            .fetchSemanticsNode()
        card.config.getOrNull(SemanticsActions.CopyText)?.action?.invoke()
        composeTestRule.waitForIdle()

        val status = composeTestRule
            .onNodeWithTag("selectable_copyable_text_copy_status")
            .fetchSemanticsNode()
        val text = status.config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        assertTrue(text == copiedStatus)
    }
}
