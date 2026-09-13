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

package de.frpeters.actua11y.ui.topic.darkmode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Naive implementation. Same banner card as [DarkModeBetter], same layout, same behaviour for a
 * sighted user in light mode — the only difference is that this card ignores dark mode entirely.
 * See the developer note.
 */
@Composable
fun DarkModeNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.dark_mode_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.dark_mode_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.dark_mode_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.dark_mode_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )

        // NAIVE: Color.White and Color.Black are hardcoded instead of reading
        // MaterialTheme.colorScheme, so this card renders exactly the same regardless of which
        // theme is active. In light mode that happens to look fine; in dark mode it renders as a
        // bright white box in an otherwise dark screen. This is a deliberate, documented
        // exception to this project's own mandatory dark-theme preview (see CLAUDE.md,
        // Previews) — the mismatch here is the entire point of the topic, not an oversight.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .testTag("dark_mode_card"),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.dark_mode_banner_text),
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.dark_mode_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun DarkModeNaivePreview() {
    MaterialTheme {
        DarkModeNaive()
    }
}
