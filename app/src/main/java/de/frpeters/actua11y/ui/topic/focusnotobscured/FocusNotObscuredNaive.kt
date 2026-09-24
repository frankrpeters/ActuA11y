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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same payment fields, same pinned pay bar, same look as
 * [FocusNotObscuredBetter]. The bar is laid over the form, and the keyboard is left to fall where
 * it falls.
 */
@Composable
fun FocusNotObscuredNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.focus_not_obscured_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            // NAIVE: nothing makes room for the on-screen keyboard. This app draws edge to edge,
            // so the window is not resized when the keyboard opens; it simply covers the bottom
            // of the screen, and whatever focused field was there.
            // TODO(verify): confirm on a real device that a payment field focused near the bottom
            // of the screen ends up behind the keyboard here, and stays visible in Better.
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
            // NAIVE: the bar is overlaid on the bottom of the scrolling form, and the form gets
            // bottom padding the height of the bar so its last field can still be scrolled clear
            // of it by hand. For a touch user that is enough. But the scroll viewport still runs
            // underneath the bar, and moving focus only scrolls a field into the *viewport* — a
            // field sitting behind the bar is already "in view" by that measure, so focus lands
            // on it and nothing moves. A keyboard user is left typing into a field they cannot
            // see.
            var barHeight by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp + barHeight),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PaymentFieldLabelResIds.forEachIndexed { index, labelRes ->
                        PaymentField(labelRes, index)
                    }
                }
                PayBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { barHeight = with(density) { it.height.toDp() } },
                )
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.focus_not_obscured_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun FocusNotObscuredNaivePreview() {
    MaterialTheme {
        FocusNotObscuredNaive()
    }
}
