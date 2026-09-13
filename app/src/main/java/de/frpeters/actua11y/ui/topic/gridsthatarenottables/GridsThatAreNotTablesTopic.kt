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

package de.frpeters.actua11y.ui.topic.gridsthatarenottables

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GridsThatAreNotTablesTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) {
        GridsThatAreNotTablesNaive(modifier)
    } else {
        GridsThatAreNotTablesBetter(modifier)
    }
}

internal const val PHOTO_COUNT = 12

// WHY: the whole screen is one LazyVerticalGrid, including the intro/heading/note blocks as
// full-width spanned items, rather than nesting the grid inside a scrollable Column — the same
// nested-scrollable footgun documented for One-Dimensional Collections' LazyColumn applies here.
// Shared here so both Naive and Better use the identical span rule.
internal val FullWidthSpan: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }
