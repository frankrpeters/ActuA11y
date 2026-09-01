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

package de.frpeters.actua11y.ui.topic.switchplatformvscustom

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPlatformVsCustomTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        SwitchPlatformVsCustomNaive(modifier)
    } else {
        SwitchPlatformVsCustomBetter(modifier)
    }
}

// WHY: the track drawing lives here, shared by Naive and Better, rather than duplicated in each
// file — CLAUDE.md's second structural invariant requires the two to produce the same visual
// result, and a single shared draw call makes that true by construction instead of by care.
private val SwitchTrackWidth: Dp = 52.dp
private val SwitchTrackHeight: Dp = 32.dp
private val SwitchThumbRadius: Dp = 12.dp
private val SwitchThumbInset: Dp = 4.dp

@Composable
internal fun CustomSwitchTrack(checked: Boolean, modifier: Modifier = Modifier) {
    val trackColor = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val thumbColor = MaterialTheme.colorScheme.onPrimary
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) {
            SwitchTrackWidth - SwitchThumbRadius * 2 - SwitchThumbInset
        } else {
            SwitchThumbInset
        },
        label = "switch_platform_vs_custom_thumb_offset",
    )

    Canvas(modifier = modifier.size(width = SwitchTrackWidth, height = SwitchTrackHeight)) {
        drawRoundRect(
            color = trackColor,
            cornerRadius = CornerRadius(x = size.height / 2, y = size.height / 2),
        )
        drawCircle(
            color = thumbColor,
            radius = SwitchThumbRadius.toPx(),
            center = Offset(x = thumbOffset.toPx() + SwitchThumbRadius.toPx(), y = size.height / 2),
        )
    }
}
