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

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import de.frpeters.actua11y.R

@Composable
fun WebViewScopeTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) WebViewScopeNaive(modifier) else WebViewScopeBetter(modifier)
}

// WHY: a page bundled with the app rather than a live URL, so the topic needs no INTERNET
// permission and behaves the same offline. The page stands in for "whatever website is loaded".
internal const val FAQ_URL = "file:///android_asset/webview_scope/faq.html"

internal val WebViewHeight = 280.dp

/**
 * The embedded help page, identical in both versions apart from [update], through which the
 * Better version adjusts the WebView's accessibility. Previews cannot render a real WebView, so
 * they get an empty box of the same size instead.
 */
@Composable
internal fun FaqWebView(modifier: Modifier = Modifier, update: (WebView) -> Unit = {}) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier)
        return
    }
    AndroidView(
        factory = { context -> WebView(context).apply { loadUrl(FAQ_URL) } },
        update = update,
        modifier = modifier,
    )
}

/**
 * The control that simulates a failed page load, the same in both versions. A real failure would
 * come from WebViewClient.onReceivedError; simulating it keeps the topic deterministic and offline.
 */
@Composable
internal fun SimulateFailureRow(failed: Boolean, onFailedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = failed, role = Role.Switch, onValueChange = onFailedChange)
            .padding(vertical = 8.dp)
            .testTag("webview_scope_simulate_failure"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.webview_scope_simulate_failure),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = failed, onCheckedChange = null)
    }
}
