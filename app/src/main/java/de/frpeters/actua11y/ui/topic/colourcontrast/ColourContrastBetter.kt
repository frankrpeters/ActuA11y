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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frpeters.actua11y.R
import de.frpeters.actua11y.ui.components.DeveloperNote

private fun statusIcon(status: OrderStatus): ImageVector = when (status) {
    OrderStatus.DELIVERED -> Icons.Filled.CheckCircle
    OrderStatus.PENDING -> Icons.Filled.Info
    OrderStatus.CANCELLED -> Icons.Filled.Close
}

private fun statusLabelRes(status: OrderStatus): Int = when (status) {
    OrderStatus.DELIVERED -> R.string.colour_contrast_status_delivered
    OrderStatus.PENDING -> R.string.colour_contrast_status_pending
    OrderStatus.CANCELLED -> R.string.colour_contrast_status_cancelled
}

/**
 * Better implementation. Same order list as [ColourContrastNaive], same layout, same behaviour
 * for a sighted user with typical colour vision — the only difference is that status is also
 * carried by an icon and a text label, and the colour itself is higher-contrast. See the
 * developer note.
 */
@Composable
fun ColourContrastBetter(modifier: Modifier = Modifier) {
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

        // BETTER: status is carried by the icon shape and by the visible/accessible text label,
        // not by colour alone — a colour-blind or low-vision user reads the same status a fully
        // sighted user does. The icon's own contentDescription is left null: the adjacent Text
        // already states the status in the merged row, so giving the icon a description too
        // would only announce it twice.
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
                Icon(
                    imageVector = statusIcon(order.status),
                    contentDescription = null,
                    tint = betterDotColor(order.status),
                    modifier = Modifier.size(20.dp),
                )
                Text(text = order.id, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(statusLabelRes(order.status)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = betterDotColor(order.status),
                )
            }
        }

        HorizontalDivider()

        DeveloperNote(body = stringResource(R.string.colour_contrast_developer_note_better))
    }
}

@Preview(name = "Light 100%", showBackground = true)
@Preview(name = "Dark 100%", showBackground = true, uiMode = 0x20)
@Preview(name = "Light 200% font", showBackground = true, fontScale = 2.0f)
@Composable
private fun ColourContrastBetterPreview() {
    MaterialTheme {
        ColourContrastBetter()
    }
}
