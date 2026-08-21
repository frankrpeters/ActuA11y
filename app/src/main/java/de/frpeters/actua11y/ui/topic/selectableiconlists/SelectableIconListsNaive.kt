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

package de.frpeters.actua11y.ui.topic.selectableiconlists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private val SWATCH_COLORS = listOf(
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFF43A047),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA),
)
private val SWATCH_SIZE = 48.dp

/**
 * Naive implementation. Same five colour swatches as [SelectableIconListsBetter] — the
 * accessibility work was simply never done.
 */
@Composable
fun SelectableIconListsNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.selectable_icon_lists_pane_title)
    val colorNames = stringArrayResource(R.array.selectable_icon_lists_color_names)
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.selectable_icon_lists_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.selectable_icon_lists_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // ── Accent colour picker ──────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.selectable_icon_lists_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        // NAIVE: each swatch has a contentDescription naming its colour, so TalkBack does say
        // "Red", "Blue", and so on — the more obvious half of what an icon-only control needs.
        // What's missing is the other half: no Modifier.selectable, no Role.RadioButton, and no
        // selectableGroup() on the row, so TalkBack can never say which swatch is the current
        // choice or report a position like "2 of 5".
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            colorNames.forEachIndexed { index, name ->
                ColorSwatch(
                    color = SWATCH_COLORS[index],
                    selected = index == selectedIndex,
                    modifier = Modifier
                        .testTag("selectable_icon_lists_swatch_$index")
                        .clickable { selectedIndex = index }
                        .semantics { contentDescription = name },
                )
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.selectable_icon_lists_developer_note_naive))
    }
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(SWATCH_SIZE)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else {
                    Modifier
                },
            ),
    )
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun SelectableIconListsNaivePreview() {
    MaterialTheme {
        SelectableIconListsNaive()
    }
}
