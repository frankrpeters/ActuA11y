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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. The same bundled help page in the same WebView, the same simulated
 * failure and the same-looking error overlay as [WebViewScopeBetter]. The WebView was dropped in
 * with its defaults, and the error state was built for the eye only.
 */
@Composable
fun WebViewScopeNaive(modifier: Modifier = Modifier) {
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
                // NAIVE: the WebView stays exposed to accessibility services underneath the error
                // overlay. A sighted user sees only the error; a TalkBack user can swipe past the
                // error straight into the old page content behind it, with nothing to say it is
                // stale.
                // TODO(verify): with TalkBack, confirm the page behind the overlay is still
                // reachable by swiping here, and not in the Better version.
                FaqWebView(modifier = Modifier.fillMaxSize())
                if (failed) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // NAIVE: plain text. It appears silently — nothing tells a TalkBack user
                        // that the content they were reading has just been replaced by an error.
                        Text(
                            text = stringResource(R.string.webview_scope_error_message),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("webview_scope_error_message"),
                        )
                        // NAIVE: styled like a button, built from a clickable Text. TalkBack can
                        // activate it, but announces no role, so nothing says it is a control.
                        Text(
                            text = stringResource(R.string.webview_scope_retry),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { failed = false }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("webview_scope_retry"),
                        )
                    }
                }
            }
        }
        Button(onClick = { /* demonstration only */ }) {
            Text(text = stringResource(R.string.webview_scope_contact_support))
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.webview_scope_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun WebViewScopeNaivePreview() {
    MaterialTheme {
        WebViewScopeNaive()
    }
}
