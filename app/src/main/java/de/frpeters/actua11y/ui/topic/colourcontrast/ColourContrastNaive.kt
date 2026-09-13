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

package de.frpeters.actua11y.ui.topic.colourcontrast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Naive implementation. Same order list as [ColourContrastBetter], same layout, same behaviour
 * for a sighted user with typical colour vision — the only difference is that status here is
 * conveyed by colour alone, and that colour itself is low-contrast. See the developer note.
 */
@Composable
fun ColourContrastNaive(modifier: Modifier = Modifier) {
    val paneTitleStr = stringResource(R.string.colour_contrast_pane_title)

    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { paneTitle = paneTitleStr }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.colour_contrast_intro),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.colour_contrast_what_to_try),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.colour_contrast_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )

        // NAIVE: the dot's colour is the only signal of status — nothing in this row's merged
        // semantics text says "Delivered", "Pending", or "Cancelled" — and the colour itself
        // measures under 3:1 against the card background (see naiveDotColor's own comment),
        // so even a sighted user with low vision may not reliably tell the dots apart.
        SampleOrders.forEach { order ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColourContrastCardBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .semantics(mergeDescendants = true) {}
                    .testTag("colour_contrast_row_${order.status}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(naiveDotColor(order.status), CircleShape),
                )
                Text(text = order.id, style = MaterialTheme.typography.bodyLarge)
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.colour_contrast_developer_note_naive))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ColourContrastNaivePreview() {
    MaterialTheme {
        ColourContrastNaive()
    }
}
