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

package de.frpeters.actua11y.ui.topic.liveregions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.frpeters.actua11y.R

@Composable
fun LiveRegionsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        LiveRegionsNaive(modifier)
    } else {
        LiveRegionsBetter(modifier)
    }
}

// WHY: shared across Naive and Better so both cycle through identical wording per CLAUDE.md's
// "same behaviour for a sighted touch user" invariant — only the modifier attached to the status
// Text differs between the two files.
internal val FlightStatusResIds: List<Int> = listOf(
    R.string.live_regions_status_on_time,
    R.string.live_regions_status_delayed,
    R.string.live_regions_status_boarding,
)
