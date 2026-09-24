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

package de.frpeters.actua11y.ui.topic.wrappedview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. The same [LegacyStarRatingView] as [WrappedViewBetter], wrapped in
 * AndroidView the same way, looking and behaving identically for a sighted touch user. Nothing was
 * done about its accessibility.
 */
@Composable
fun WrappedViewNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.wrapped_view_pane_title)
    val filledColor = MaterialTheme.colorScheme.primary.toArgb()
    val emptyColor = MaterialTheme.colorScheme.outlineVariant.toArgb()

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.wrapped_view_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.wrapped_view_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Rate your delivery ────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.wrapped_view_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: the legacy View is dropped into AndroidView exactly as it is. It draws and
        // responds to taps, so a sighted touch user notices nothing. To an accessibility
        // service it is an anonymous View: no name, no role, no value, and no way to change the
        // rating except by tapping a precise spot — which TalkBack's own touch exploration
        // intercepts.
        AndroidView(
            factory = { context ->
                LegacyStarRatingView(context).apply { rating = INITIAL_RATING }
            },
            update = { view ->
                view.filledColor = filledColor
                view.emptyColor = emptyColor
                view.invalidate()
            },
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.wrapped_view_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun WrappedViewNaivePreview() {
    MaterialTheme {
        WrappedViewNaive()
    }
}
