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

package de.frpeters.actua11y.ui.topic.minimumtouchtarget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same delete action as [MinimumTouchTargetNaive], same glyph, same
 * visual size, same behaviour for a sighted touch user — the only difference is accessibility
 * semantics.
 */
@Composable
fun MinimumTouchTargetBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.minimum_touch_target_pane_title)
    val description = stringResource(R.string.minimum_touch_target_button_description)
    var tapCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            // WHY: paneTitle announces the screen name to TalkBack on arrival, since content
            // swaps below the persistent app bar don't trigger a full navigation announcement.
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.minimum_touch_target_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.minimum_touch_target_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Delete action ─────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.minimum_touch_target_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // BETTER: a plain IconButton, nothing else. IconButton's own default implementation
        // already applies Modifier.minimumInteractiveComponentSize() internally, expanding the
        // measured touch target to at least 48dp x 48dp without changing what's drawn — the
        // glyph inside is exactly the same visual size as the Naive version.
        IconButton(
            onClick = { tapCount++ },
            modifier = Modifier
                .testTag("minimum_touch_target_button")
                .semantics { contentDescription = description },
        ) {
            Text(
                text = "🗑",
                fontSize = 20.sp,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
        Text(text = stringResource(R.string.minimum_touch_target_tap_count, tapCount))

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.minimum_touch_target_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun MinimumTouchTargetBetterPreview() {
    MaterialTheme {
        MinimumTouchTargetBetter()
    }
}
