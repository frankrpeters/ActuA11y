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

package de.frpeters.actua11y.ui.topic.contentdescriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

/**
 * Better implementation. Same three elements as [ContentDescriptionsNaive], same layout, same
 * behaviour for a sighted touch user — the only difference is accessibility semantics.
 */
@Composable
fun ContentDescriptionsBetter(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.content_descriptions_pane_title)

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
            text = stringResource(R.string.content_descriptions_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.content_descriptions_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Meaningful image ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.meaningful_image_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Image(
            painter = painterResource(R.drawable.ic_demo_smartphone),
            // BETTER: a meaningful contentDescription lets TalkBack tell the user what the
            // image shows, rather than skipping it or announcing only "Image".
            contentDescription = stringResource(R.string.meaningful_image_desc),
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally),
        )

        HorizontalDivider()

        // ── Icon-only button ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.icon_button_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        IconButton(
            onClick = { /* demonstration only */ },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                // BETTER: without this, an icon-only button has no accessible name at all —
                // TalkBack would announce it as just "Button".
                contentDescription = stringResource(R.string.icon_button_desc),
            )
        }

        HorizontalDivider()

        // ── Decorative image ──────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.decorative_image_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Image(
            painter = painterResource(R.drawable.ic_demo_wave),
            // BETTER: null removes this node from the accessibility tree entirely. Sighted
            // users still see the decoration; TalkBack users are not interrupted by it.
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.content_descriptions_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ContentDescriptionsBetterPreview() {
    MaterialTheme {
        ContentDescriptionsBetter()
    }
}
