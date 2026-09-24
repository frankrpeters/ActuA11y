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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. The same bundled help page in the same WebView, the same simulated
 * failure and the same-looking error overlay as [WebViewScopeNaive]. What changes is only the
 * native layer around the web content: the error state is announced and operable, and stale page
 * content is hidden from accessibility services while it is covered.
 */
@Composable
fun WebViewScopeBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.webview_scope_pane_title)
    var failed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.webview_scope_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.webview_scope_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Delivery help ─────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.webview_scope_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        SimulateFailureRow(failed = failed, onFailedChange = { failed = it })
        // WHY: the WebView and the native controls around it are composed in reading order in
        // both versions — heading, page, then the support button. Compose places an interop
        // View in accessibility traversal according to where it sits among its Compose
        // siblings, so no extra work was needed for its position in the order.
        // TODO(verify): with TalkBack, swipe from the heading through the web content and on to
        // "Contact support", and confirm the page is visited in that position rather than first
        // or last. Also check the order with a physical keyboard's Tab key.
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(WebViewHeight),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // BETTER: while the error overlay is shown, the page underneath is removed from the
                // accessibility tree entirely, so what TalkBack reads matches what is on screen.
                FaqWebView(
                    modifier = Modifier.fillMaxSize(),
                    update = { webView ->
                        webView.importantForAccessibility = if (failed) {
                            View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                        } else {
                            View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                        }
                    },
                )
                if (failed) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // BETTER: a polite live region, so the change from page to error is
                        // announced when it happens, without interrupting.
                        Text(
                            text = stringResource(R.string.webview_scope_error_message),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .semantics { liveRegion = LiveRegionMode.Polite }
                                .testTag("webview_scope_error_message"),
                        )
                        // BETTER: a real TextButton — the same look as the Naive version's
                        // clickable text, with the Button role and minimum touch target that come
                        // with it.
                        TextButton(
                            onClick = { failed = false },
                            modifier = Modifier.testTag("webview_scope_retry"),
                        ) {
                            Text(text = stringResource(R.string.webview_scope_retry))
                        }
                    }
                }
            }
        }
        Button(onClick = { /* demonstration only */ }) {
            Text(text = stringResource(R.string.webview_scope_contact_support))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.webview_scope_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun WebViewScopeBetterPreview() {
    MaterialTheme {
        WebViewScopeBetter()
    }
}
