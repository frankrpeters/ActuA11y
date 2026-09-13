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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.frpeters.actua11y.R

@Composable
fun ColourContrastTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        ColourContrastNaive(modifier)
    } else {
        ColourContrastBetter(modifier)
    }
}

internal enum class OrderStatus { DELIVERED, PENDING, CANCELLED }

internal data class Order(val id: String, val status: OrderStatus)

internal val SampleOrders = listOf(
    Order("Order #A1042", OrderStatus.DELIVERED),
    Order("Order #A1077", OrderStatus.PENDING),
    Order("Order #A1090", OrderStatus.CANCELLED),
)

// WHY: #F5F5F5 is the fixed (non-theme) card background both versions paint the status dot on,
// so the contrast figures quoted in the developer notes are reproducible numbers rather than
// "whatever MaterialTheme.colorScheme happens to resolve to" in light or dark mode.
internal val ColourContrastCardBackground = Color(0xFFF5F5F5)

// NAIVE: pastel tones chosen because they are realistic "looks fine at a glance" status colours
// — and because each one measures under 3:1 against ColourContrastCardBackground using the WCAG
// relative-luminance formula (contrast = (L_lighter + 0.05) / (L_darker + 0.05)), well below the
// 3:1 minimum WCAG 1.4.11 sets for non-text UI components: green ≈1.51:1, red ≈1.97:1,
// amber ≈1.03:1. The colour is also the *only* cue — nothing else on this row says which status
// it is.
internal fun naiveDotColor(status: OrderStatus): Color = when (status) {
    OrderStatus.DELIVERED -> Color(0xFFA5D6A7)
    OrderStatus.PENDING -> Color(0xFFFFF59D)
    OrderStatus.CANCELLED -> Color(0xFFEF9A9A)
}

// BETTER: saturated tones chosen because each measures at least 4.5:1 against
// ColourContrastCardBackground using the same formula: green ≈4.70:1, amber/brown ≈5.62:1,
// red ≈5.16:1 — but the colour is never the only cue here; see ColourContrastBetter.kt for the
// icon and text label that carry the status regardless of colour perception.
internal fun betterDotColor(status: OrderStatus): Color = when (status) {
    OrderStatus.DELIVERED -> Color(0xFF2E7D32)
    OrderStatus.PENDING -> Color(0xFF7A5E00)
    OrderStatus.CANCELLED -> Color(0xFFC62828)
}
