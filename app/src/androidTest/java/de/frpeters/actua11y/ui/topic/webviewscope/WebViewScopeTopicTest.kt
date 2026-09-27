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

package de.frpeters.actua11y.ui.topic.webviewscope

import android.view.View
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.frpeters.actua11y.ui.topic.wrappedview.findFirst
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Reference test spanning both accessibility layers: the native error state is checked in the
 * Compose semantics tree, while the WebView's own accessibility importance is read from the View
 * itself, since the Compose semantics tree has no view into an interop View.
 */
@RunWith(AndroidJUnit4::class)
class WebViewScopeTopicTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun simulateFailure() {
        composeTestRule.onNodeWithTag("webview_scope_simulate_failure").performClick()
        composeTestRule.waitForIdle()
    }

    private fun webViewImportance(): Int =
        requireNotNull(composeTestRule.activity.window.decorView.findFirst<WebView>())
            .importantForAccessibility

    @Test
    fun better_errorStateIsAnnouncedOperableAndHidesStalePage() {
        composeTestRule.setContent {
            MaterialTheme { WebViewScopeBetter() }
        }
        simulateFailure()

        val message = composeTestRule.onNodeWithTag("webview_scope_error_message")
            .fetchSemanticsNode()
        assertEquals(LiveRegionMode.Polite, message.config.getOrNull(SemanticsProperties.LiveRegion))
        val retry = composeTestRule.onNodeWithTag("webview_scope_retry").fetchSemanticsNode()
        assertEquals(Role.Button, retry.config.getOrNull(SemanticsProperties.Role))
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS, webViewImportance())
    }

    @Test
    fun naive_errorStateIsSilentRolelessAndLeavesStalePageExposed() {
        composeTestRule.setContent {
            MaterialTheme { WebViewScopeNaive() }
        }
        simulateFailure()

        val message = composeTestRule.onNodeWithTag("webview_scope_error_message")
            .fetchSemanticsNode()
        assertNull(message.config.getOrNull(SemanticsProperties.LiveRegion))
        val retry = composeTestRule.onNodeWithTag("webview_scope_retry").fetchSemanticsNode()
        assertNull(retry.config.getOrNull(SemanticsProperties.Role))
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, webViewImportance())
    }
}
