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

package de.frpeters.actua11y.ui.topic.focusnotobscured

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same payment fields, same pinned pay bar, same look as
 * [FocusNotObscuredNaive]. The bar is laid out beneath the form instead of over it, and the
 * screen makes room for the on-screen keyboard.
 */
@Composable
fun FocusNotObscuredBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.focus_not_obscured_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            // BETTER: the on-screen keyboard is the other thing that can cover a focused field.
            // This app draws edge to edge, so the window is not resized when the keyboard opens;
            // this padding shrinks the screen's scroll viewport by the keyboard's height instead.
            // Compose then scrolls the focused field back into view, because a scroll container
            // re-reveals a focused child that a viewport shrink has hidden (ContentInViewNode.kt,
            // onRemeasured). The system bars are excluded because AppScaffold's own padding
            // already accounts for them.
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.systemBars))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.focus_not_obscured_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.focus_not_obscured_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Payment ───────────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.focus_not_obscured_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(CheckoutFrameHeight),
        ) {
            // BETTER: the bar sits *below* the scrolling form rather than on top of it, so the
            // form's scroll viewport ends where the bar begins. Nothing more is needed: when a
            // field gains focus, Compose already scrolls it fully into its scroll container's
            // viewport (Focusable.kt, onFocusStateChange -> bringIntoView()). The fix is getting
            // the viewport's geometry right, not adding scrolling code.
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PaymentFieldLabelResIds.forEachIndexed { index, labelRes ->
                        PaymentField(labelRes, index)
                    }
                }
                PayBar()
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.focus_not_obscured_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun FocusNotObscuredBetterPreview() {
    MaterialTheme {
        FocusNotObscuredBetter()
    }
}
