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

package de.frpeters.actua11y.ui.topic.keyboardfocusindicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.frpeters.actua11y.R

@Composable
fun KeyboardFocusIndicatorTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        KeyboardFocusIndicatorNaive(modifier)
    } else {
        KeyboardFocusIndicatorBetter(modifier)
    }
}

internal val ChipLabelResIds = listOf(
    R.string.keyboard_focus_indicator_chip_archive,
    R.string.keyboard_focus_indicator_chip_flag,
    R.string.keyboard_focus_indicator_chip_snooze,
)
